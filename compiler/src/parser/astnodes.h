//
// Created by timo on 7/30/20.
//

#pragma once

#include <functional>
#include <ostream>
#include <vector>
#include <memory>
#include <set>

#include <llvm/IR/BasicBlock.h>

#include "allnodes.h"

class SymbolTable;

class Token;

class ASTNode {

public:
    enum ASTKind {
        NONE,
        ROOT,
        IMPORT,
        FUNCTION,
        PARAMETER,
        TYPE,
        BLOCK,
        VARIABLE,
        BINARY,
        UNARY,
        PARENTHESIZED,
        NUMBER,
        STRING,
        IDENTIFIER,
        FUNCTION_ARGUMENT,
        FUNCTION_CALL,
        STRUCT_ARGUMENT,
        STRUCT_CREATE,
        ASSIGNMENT,
        RETURN,
        STRUCT,
        OPERABLE,
    };

private:
    std::shared_ptr<Token> m_StartToken, m_EndToken;
    SharedSymbolTable m_Scope;
    SharedASTNode m_Parent;
    bool mb_Failed;
    ASTKind m_Kind;

public:
    ASTNode();

    ASTNode &operator=(const ASTNode &) = delete;

    virtual ~ASTNode() = default;

protected:
    ASTNode(const ASTNode &other);

public:
    [[nodiscard]]
    virtual ASTNode *clone(SharedASTNode parent,
                           SharedSymbolTable symbolTable) const = 0;

    template<typename Type = ASTNode>
    std::shared_ptr<Type> findNodeOfParents() {
        if (m_Parent == nullptr)
            return nullptr;

        if (auto result = std::dynamic_pointer_cast<Type>(getParent()))
            return result;
        return m_Parent->findNodeOfParents<Type>();
    }

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getStartToken() const;

    void setStartToken(const std::shared_ptr<Token> &startToken);

    [[nodiscard]]
    const std::shared_ptr<Token> &getEndToken() const;

    void setEndToken(const std::shared_ptr<Token> &endToken);

    [[nodiscard]]
    const SharedSymbolTable &getScope() const;

    void setScope(const SharedSymbolTable &scope);

    [[nodiscard]]
    const SharedASTNode &getParent() const;

    void setParent(const SharedASTNode &parent);

    [[nodiscard]]
    bool isFailed() const;

    void setFailed(bool failed);

    [[nodiscard]]
    std::string getKindAsString() const;

    [[nodiscard]]
    ASTKind getKind() const;

    void setKind(ASTKind kind);

public:
    friend std::ostream &operator<<(std::ostream &os, const ASTKind &kind);

};

class RootNode : public ASTNode {

private:
    std::vector<SharedASTNode> m_Nodes;
    std::string m_SourcePath, m_SourceCode;

public:
    RootNode();

    RootNode &operator=(const RootNode &) = delete;

protected:
    RootNode(const RootNode &other);

public:
    [[nodiscard]]
    RootNode *clone(SharedASTNode parent,
                    SharedSymbolTable symbolTable) const override;

    std::vector<SharedRootNode> getImportedRoots();

    void getImportedRoots(std::vector<SharedRootNode> &importedRoots);

    std::shared_ptr<std::vector<SharedASTNode>>
    searchWithImports(const std::string &id,
                      const std::function<bool(const SharedASTNode &)> &predicate);

    void addNode(const SharedASTNode &node);

    void removeNode(const SharedASTNode &node);

public:
    [[nodiscard]]
    const std::vector<SharedASTNode> &getNodes() const;

    [[nodiscard]]
    const std::string &getSourcePath() const;

    void setSourcePath(const std::string &sourcePath);

    [[nodiscard]]
    const std::string &getSourceCode() const;

    void setSourceCode(const std::string &sourceCode);

};

class TypedNode : public ASTNode {

private:
    SharedASTNode m_TargetNode;
    SharedTypeNode m_Type;
    bool mb_TypeResolved;
    bool mb_Accessed;

public:
    TypedNode();

    TypedNode &operator=(const TypedNode &) = delete;

protected:
    TypedNode(const TypedNode &other);

public:
    [[nodiscard]]
    TypedNode *clone(SharedASTNode parent,
                     SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedASTNode &getTargetNode() const;

    void setTargetNode(const SharedASTNode &targetNode);

    [[nodiscard]]
    const SharedTypeNode &getType() const;

    void setType(const SharedTypeNode &type);

    [[nodiscard]]
    bool isTypeResolved() const;

    void setTypeResolved(bool typeResolved);

    [[nodiscard]]
    bool isAccessed() const;

    void setAccessed(bool accessed);

};

class ImportNode : public ASTNode {

private:
    SharedRootNode m_Target;
    std::shared_ptr<Token> m_Path;

public:
    ImportNode();

    ImportNode &operator=(const ImportNode &) = delete;

protected:
    ImportNode(const ImportNode &other);

public:
    [[nodiscard]]
    ImportNode *clone(SharedASTNode parent,
                      SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedRootNode &getTarget() const;

    void setTarget(const SharedRootNode &target);

    [[nodiscard]]
    const std::shared_ptr<Token> &getPath() const;

    void setPath(const std::shared_ptr<Token> &path);

};

class ParameterNode : public TypedNode {

private:
    std::shared_ptr<Token> m_Name;

public:
    ParameterNode();

    ParameterNode &operator=(const ParameterNode &) = delete;

protected:
    ParameterNode(const ParameterNode &other);

public:
    [[nodiscard]]
    ParameterNode *clone(SharedASTNode parent,
                         SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class BlockNode : public TypedNode {

private:
    std::vector<SharedASTNode> m_Nodes;
    bool mb_Inlined;

public:
    BlockNode();

    BlockNode &operator=(const BlockNode &) = delete;

protected:
    BlockNode(const BlockNode &other);

public:
    [[nodiscard]]
    BlockNode *clone(SharedASTNode parent,
                     SharedSymbolTable symbolTable) const override;

    void addNode(const SharedASTNode &node);

    void removeNode(const SharedASTNode &node);

    void insertNode(const SharedASTNode &node, int index);

public:
    [[nodiscard]]
    const std::vector<SharedASTNode> &getNodes() const;

    [[nodiscard]]
    bool isInlined() const;

    void setInlined(bool inlined);

};

class OperableNode : public TypedNode {

public:
    OperableNode();

    OperableNode &operator=(const OperableNode &) = delete;

protected:
    OperableNode(const OperableNode &other);

public:
    [[nodiscard]]
    OperableNode *clone(SharedASTNode parent,
                        SharedSymbolTable symbolTable) const override;

};

class VariableNode : public TypedNode {

private:
    SharedOperableNode m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_Constant, mb_Local;

public:
    VariableNode();

    VariableNode &operator=(const VariableNode &) = delete;

protected:
    VariableNode(const VariableNode &other);

public:
    [[nodiscard]]
    VariableNode *clone(SharedASTNode parent,
                        SharedSymbolTable symbolTable) const override;

    bool isGlobal();

public:
    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

    [[nodiscard]]
    bool isConstant() const;

    void setConstant(bool constant);

    [[nodiscard]]
    bool isLocal() const;

    void setLocal(bool local);

};

class BinaryNode : public OperableNode {

public:
    enum BinaryKind {
        NONE,

        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        REMAINING,

        LESS_THAN,
        GREATER_THAN,
        LESS_EQUAL_THAN,
        GREATER_EQUAL_THAN,
        EQUAL,
        NOT_EQUAL,

        BIT_CAST
    };

private:
    SharedOperableNode m_Lhs, m_Rhs;
    BinaryKind m_OperatorKind;

public:
    BinaryNode();

    BinaryNode &operator=(const BinaryNode &) = delete;

protected:
    BinaryNode(const BinaryNode &other);

public:
    [[nodiscard]]
    BinaryNode *clone(SharedASTNode parent,
                      SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedOperableNode &getLHS() const;

    void setLHS(const SharedOperableNode &lhs);

    [[nodiscard]]
    const SharedOperableNode &getRHS() const;

    void setRHS(const SharedOperableNode &rhs);

    [[nodiscard]]
    std::string getOperatorKindAsString() const;

    [[nodiscard]]
    BinaryKind getOperatorKind() const;

    void setOperatorKind(BinaryKind operatorKind);

public:
    friend std::ostream &operator<<(std::ostream &os, const BinaryKind &kind);

};

class UnaryNode : public OperableNode {

public:
    enum UnaryKind {
        NONE,

        NEGATE
    };

private:
    SharedOperableNode m_Expression;
    UnaryKind m_OperatorKind;

public:
    UnaryNode();

    UnaryNode &operator=(const UnaryNode &) = delete;

protected:
    UnaryNode(const UnaryNode &other);

public:
    [[nodiscard]]
    UnaryNode *clone(SharedASTNode parent,
                     SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

    [[nodiscard]]
    std::string getOperatorKindAsString() const;

    [[nodiscard]]
    UnaryKind getOperatorKind() const;

    void setOperatorKind(UnaryKind operatorKind);

public:
    friend std::ostream &operator<<(std::ostream &os, const UnaryKind &kind);

};

class ParenthesizedNode : public OperableNode {

private:
    SharedOperableNode m_Expression;

public:
    ParenthesizedNode();

    ParenthesizedNode &operator=(const ParenthesizedNode &) = delete;

protected:
    ParenthesizedNode(const ParenthesizedNode &other);

public:
    [[nodiscard]]
    ParenthesizedNode *clone(SharedASTNode parent,
                             SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

};

class NumberNode : public OperableNode {

private:
    std::shared_ptr<Token> m_Number;

public:
    NumberNode();

    NumberNode &operator=(const NumberNode &) = delete;

protected:
    NumberNode(const NumberNode &other);

public:
    [[nodiscard]]
    NumberNode *clone(SharedASTNode parent,
                      SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getNumber() const;

    void setNumber(const std::shared_ptr<Token> &number);

};

class StringNode : public OperableNode {

private:
    std::shared_ptr<Token> m_String;

public:
    StringNode();

    StringNode &operator=(const StringNode &) = delete;

protected:
    StringNode(const StringNode &other);

public:
    [[nodiscard]]
    StringNode *clone(SharedASTNode parent,
                      SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getString() const;

    void setString(const std::shared_ptr<Token> &string);

};

class IdentifierNode : public OperableNode {

private:
    SharedIdentifierNode m_NextIdentifier, m_LastIdentifier;
    std::shared_ptr<Token> m_Identifier;
    bool mb_Pointer, mb_Dereference;

public:
    IdentifierNode();

    IdentifierNode &operator=(const IdentifierNode &) = delete;

protected:
    IdentifierNode(const IdentifierNode &other);

public:
    [[nodiscard]]
    IdentifierNode *clone(SharedASTNode parent,
                          SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedIdentifierNode &getNextIdentifier() const;

    void setNextIdentifier(const SharedIdentifierNode &nextIdentifier);

    [[nodiscard]]
    const SharedIdentifierNode &getLastIdentifier() const;

    void setLastIdentifier(const SharedIdentifierNode &lastIdentifier);

    [[nodiscard]]
    const std::shared_ptr<Token> &getIdentifier() const;

    void setIdentifier(const std::shared_ptr<Token> &identifier);

    [[nodiscard]]
    bool isPointer() const;

    void setPointer(bool pointer);

    [[nodiscard]]
    bool isDereference() const;

    void setDereference(bool dereference);

};

class AssignmentNode : public OperableNode {

private:
    SharedIdentifierNode m_StartIdentifier, m_EndIdentifier;
    SharedOperableNode m_Expression;

public:
    AssignmentNode();

    AssignmentNode &operator=(const AssignmentNode &) = delete;

protected:
    AssignmentNode(const AssignmentNode &other);

public:
    [[nodiscard]]
    AssignmentNode *clone(SharedASTNode parent,
                          SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedIdentifierNode &getStartIdentifier() const;

    void setStartIdentifier(const SharedIdentifierNode &startIdentifier);

    [[nodiscard]]
    const SharedIdentifierNode &getEndIdentifier() const;

    void setEndIdentifier(const SharedIdentifierNode &endIdentifier);

    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

};

class ReturnNode : public TypedNode {

private:
    SharedOperableNode m_Expression;

public:
    ReturnNode();

    ReturnNode &operator=(const ReturnNode &) = delete;

protected:
    ReturnNode(const ReturnNode &other);

public:
    [[nodiscard]]
    ReturnNode *clone(SharedASTNode parent,
                      SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

};

class StructNode : public TypedNode {

private:
    std::vector<SharedVariableNode> m_Variables;
    std::shared_ptr<Token> m_Name;

public:
    StructNode();

    StructNode &operator=(const StructNode &) = delete;

protected:
    StructNode(const StructNode &other);

public:
    [[nodiscard]]
    StructNode *clone(SharedASTNode parent,
                      SharedSymbolTable symbolTable) const override;

    void addVariable(const SharedVariableNode &variable);

public:
    [[nodiscard]]
    const std::vector<SharedVariableNode> &getVariables() const;

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class TypeNode : public OperableNode {

private:
    SharedStructNode m_TargetStruct;
    unsigned int m_PointerLevel, m_Bits;
    std::shared_ptr<Token> m_TypeToken;
    bool mb_Signed, mb_Floating;

public:
    TypeNode();

    TypeNode &operator=(const TypeNode &) = delete;

protected:
    TypeNode(const TypeNode &other);

public:
    [[nodiscard]]
    TypeNode *clone(SharedASTNode parent,
                    SharedSymbolTable symbolTable) const override;

    bool isVoid() const;

    [[nodiscard]]
    bool isNumeric() const;

public:
    [[nodiscard]]
    const SharedStructNode &getTargetStruct() const;

    void setTargetStruct(const SharedStructNode &targetStruct);

    [[nodiscard]]
    unsigned int getPointerLevel() const;

    void setPointerLevel(unsigned int pointerLevel);

    [[nodiscard]]
    unsigned int getBits() const;

    void setBits(unsigned int bits);

    [[nodiscard]]
    const std::shared_ptr<Token> &getTypeToken() const;

    void setTypeToken(const std::shared_ptr<Token> &typeToken);

    [[nodiscard]]
    bool isSigned() const;

    void setSigned(bool isSigned);

    [[nodiscard]]
    bool isFloating() const;

    void setFloating(bool floating);

public:
    friend std::ostream &operator<<(std::ostream &out, const SharedTypeNode &typeNode);

    bool operator==(const TypeNode &other) const;

    bool operator!=(const TypeNode &other) const;

};

class FunctionNode : public TypedNode {

private:
    std::vector<SharedParameterNode> m_Parameters;
    std::set<std::string> m_Annotations;
    SharedBlockNode m_Block;
    std::shared_ptr<Token> m_Name;
    bool mb_Variadic, mb_Native;

public:
    FunctionNode();

    FunctionNode &operator=(const FunctionNode &) = delete;

protected:
    FunctionNode(const FunctionNode &other);

public:
    [[nodiscard]]
    FunctionNode *clone(SharedASTNode parent,
                        SharedSymbolTable symbolTable) const override;

    bool hasAnnotation(const std::string &annotation);

    void addParameter(const SharedParameterNode &parameterNode);

public:
    [[nodiscard]]
    const std::vector<SharedParameterNode> &getParameters() const;

    void setAnnotations(const std::set<std::string> &annotations);

    [[nodiscard]]
    const SharedBlockNode &getBlock() const;

    void setBlock(const SharedBlockNode &block);

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

    [[nodiscard]]
    bool isVariadic() const;

    void setVariadic(bool variadic);

    [[nodiscard]]
    bool isNative() const;

    void setNative(bool native);

public:
    bool operator==(const FunctionNode &other) const;

    bool operator!=(const FunctionNode &other) const;

};

class StructArgumentNode : public TypedNode {

private:
    SharedOperableNode m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_DontCopy;

public:
    StructArgumentNode();

    StructArgumentNode &operator=(const StructArgumentNode &) = delete;

protected:
    StructArgumentNode(const StructArgumentNode &other);

public:
    [[nodiscard]]
    StructArgumentNode *clone(SharedASTNode parent,
                              SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    bool isDontCopy() const;

    void setDontCopy(bool mbDontCopy);

    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class StructCreateNode : public OperableNode {

private:
    std::vector<SharedStructArgumentNode> m_Arguments;
    SharedIdentifierNode m_Identifier;
    bool mb_Unnamed;

public:
    StructCreateNode();

    StructCreateNode &operator=(const StructCreateNode &) = delete;

protected:
    StructCreateNode(const StructCreateNode &other);

public:
    [[nodiscard]]
    StructCreateNode *clone(SharedASTNode parent,
                            SharedSymbolTable symbolTable) const override;

    void addArgument(const SharedStructArgumentNode &argumentNode);

    void removeArgument(const SharedStructArgumentNode &argumentNode);

    void insertArgument(int index, const SharedStructArgumentNode &argumentNode);

public:
    [[nodiscard]]
    const SharedIdentifierNode &getIdentifier() const;

    void setIdentifier(const SharedIdentifierNode &mIdentifier);

    [[nodiscard]]
    const std::vector<SharedStructArgumentNode> &getArguments() const;

    [[nodiscard]]
    bool isUnnamed() const;

    void setUnnamed(bool unnamed);

};

class FunctionArgumentNode : public TypedNode {

private:
    SharedOperableNode m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_TypeWhitelisted;

public:
    FunctionArgumentNode();

    FunctionArgumentNode &operator=(const FunctionArgumentNode &) = delete;

protected:
    FunctionArgumentNode(const FunctionArgumentNode &other);

public:
    [[nodiscard]]
    FunctionArgumentNode *clone(SharedASTNode parent,
                                SharedSymbolTable symbolTable) const override;

public:
    [[nodiscard]]
    const SharedOperableNode &getExpression() const;

    void setExpression(const SharedOperableNode &expression);

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

    [[nodiscard]]
    bool isTypeWhitelisted() const;

    void setTypeWhitelisted(bool typeWhitelisted);

};

class FunctionCallNode : public IdentifierNode {

private:
    std::vector<SharedFunctionArgumentNode> m_Arguments;

public:
    FunctionCallNode();

    FunctionCallNode &operator=(const FunctionCallNode &) = delete;

protected:
    FunctionCallNode(const FunctionCallNode &other);

public:
    [[nodiscard]]
    FunctionCallNode *clone(SharedASTNode parent,
                            SharedSymbolTable symbolTable) const override;

    bool getSortedArguments(const SharedFunctionNode &functionNode,
                            std::vector<SharedFunctionArgumentNode> &sortedArguments);

    void addArgument(const SharedFunctionArgumentNode &argumentNode);

public:
    [[nodiscard]]
    const std::vector<SharedFunctionArgumentNode> &getArguments() const;

};