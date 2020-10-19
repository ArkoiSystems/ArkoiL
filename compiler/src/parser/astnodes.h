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
    std::shared_ptr<SymbolTable> m_Scope;
    std::shared_ptr<ASTNode> m_Parent;
    bool mb_Failed;
    ASTKind m_Kind;

public:
    ASTNode();

    ASTNode &operator=(const ASTNode &) = delete;

    virtual ~ASTNode() = default;

protected:
    ASTNode(const ASTNode &other);

public:
    virtual ASTNode *clone(const std::shared_ptr<ASTNode> parent,
                           const std::shared_ptr<SymbolTable> symbolTable) const;

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
    const std::shared_ptr<SymbolTable> &getScope() const;

    void setScope(const std::shared_ptr<SymbolTable> &scope);

    [[nodiscard]]
    const std::shared_ptr<ASTNode> &getParent() const;

    void setParent(const std::shared_ptr<ASTNode> &parent);

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
    std::vector<std::shared_ptr<ASTNode>> m_Nodes;
    std::string m_SourcePath, m_SourceCode;

public:
    RootNode();

    RootNode &operator=(const RootNode &) = delete;

protected:
    RootNode(const RootNode &other);

public:
    RootNode *clone(const std::shared_ptr<ASTNode> parent,
                    const std::shared_ptr<SymbolTable> symbolTable) const override;

    std::vector<std::shared_ptr<RootNode>> getImportedRoots();

    void getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots);

    std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>>
    searchWithImports(const std::string &id,
                      const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate);

    void addNode(const std::shared_ptr<ASTNode> &node);

    void removeNode(const std::shared_ptr<ASTNode> &node);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<ASTNode>> &getNodes() const;

    [[nodiscard]]
    const std::string &getSourcePath() const;

    void setSourcePath(const std::string &sourcePath);

    [[nodiscard]]
    const std::string &getSourceCode() const;

    void setSourceCode(const std::string &sourceCode);

};

class TypedNode : public ASTNode {

private:
    std::shared_ptr<ASTNode> m_TargetNode;
    std::shared_ptr<TypeNode> m_Type;
    bool mb_TypeResolved;
    bool mb_Accessed;

public:
    TypedNode();

    TypedNode &operator=(const TypedNode &) = delete;

protected:
    TypedNode(const TypedNode &other);

public:
    TypedNode *clone(const std::shared_ptr<ASTNode> parent,
                     const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<ASTNode> &getTargetNode() const;

    void setTargetNode(const std::shared_ptr<ASTNode> &targetNode);

    [[nodiscard]]
    const std::shared_ptr<TypeNode> &getType() const;

    void setType(const std::shared_ptr<TypeNode> &type);

    [[nodiscard]]
    bool isTypeResolved() const;

    void setTypeResolved(bool typeResolved);

    bool isAccessed() const;

    void setAccessed(bool accessed);

};

class ImportNode : public ASTNode {

private:
    std::shared_ptr<RootNode> m_Target;
    std::shared_ptr<Token> m_Path;

public:
    ImportNode();

    ImportNode &operator=(const ImportNode &) = delete;

protected:
    ImportNode(const ImportNode &other);

public:
    ImportNode *clone(const std::shared_ptr<ASTNode> parent,
                      const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<RootNode> &getTarget() const;

    void setTarget(const std::shared_ptr<RootNode> &target);

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
    ParameterNode *clone(const std::shared_ptr<ASTNode> parent,
                         const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class BlockNode : public TypedNode {

private:
    std::vector<std::shared_ptr<ASTNode>> m_Nodes;
    bool mb_Inlined;

public:
    BlockNode();

    BlockNode &operator=(const BlockNode &) = delete;

protected:
    BlockNode(const BlockNode &other);

public:
    BlockNode *clone(const std::shared_ptr<ASTNode> parent,
                     const std::shared_ptr<SymbolTable> symbolTable) const override;

    void addNode(const std::shared_ptr<ASTNode> &node);

    void removeNode(const std::shared_ptr<ASTNode> &node);

    void insertNode(const std::shared_ptr<ASTNode> &node, int index);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<ASTNode>> &getNodes() const;

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
    OperableNode *clone(const std::shared_ptr<ASTNode> parent,
                        const std::shared_ptr<SymbolTable> symbolTable) const override;

};

class VariableNode : public TypedNode {

private:
    std::shared_ptr<OperableNode> m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_Constant, mb_Local;

public:
    VariableNode();

    VariableNode &operator=(const VariableNode &) = delete;

protected:
    VariableNode(const VariableNode &other);

public:
    VariableNode *clone(const std::shared_ptr<ASTNode> parent,
                        const std::shared_ptr<SymbolTable> symbolTable) const override;

    bool isGlobal();

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

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
    std::shared_ptr<OperableNode> m_Lhs, m_Rhs;
    BinaryKind m_OperatorKind;

public:
    BinaryNode();

    BinaryNode &operator=(const BinaryNode &) = delete;

protected:
    BinaryNode(const BinaryNode &other);

public:
    BinaryNode *clone(const std::shared_ptr<ASTNode> parent,
                      const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getLHS() const;

    void setLHS(const std::shared_ptr<OperableNode> &lhs);

    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getRHS() const;

    void setRHS(const std::shared_ptr<OperableNode> &rhs);

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
    std::shared_ptr<OperableNode> m_Expression;
    UnaryKind m_OperatorKind;

public:
    UnaryNode();

    UnaryNode &operator=(const UnaryNode &) = delete;

protected:
    UnaryNode(const UnaryNode &other);

public:
    UnaryNode *clone(const std::shared_ptr<ASTNode> parent,
                     const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

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
    std::shared_ptr<OperableNode> m_Expression;

public:
    ParenthesizedNode();

    ParenthesizedNode &operator=(const ParenthesizedNode &) = delete;

protected:
    ParenthesizedNode(const ParenthesizedNode &other);

public:
    ParenthesizedNode *clone(const std::shared_ptr<ASTNode> parent,
                             const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

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
    NumberNode *clone(const std::shared_ptr<ASTNode> parent,
                      const std::shared_ptr<SymbolTable> symbolTable) const override;

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
    StringNode *clone(const std::shared_ptr<ASTNode> parent,
                      const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getString() const;

    void setString(const std::shared_ptr<Token> &string);

};

class IdentifierNode : public OperableNode {

private:
    std::shared_ptr<IdentifierNode> m_NextIdentifier, m_LastIdentifier;
    std::shared_ptr<Token> m_Identifier;
    bool mb_Pointer, mb_Dereference;

public:
    IdentifierNode();

    IdentifierNode &operator=(const IdentifierNode &) = delete;

protected:
    IdentifierNode(const IdentifierNode &other);

public:
    IdentifierNode *clone(const std::shared_ptr<ASTNode> parent,
                          const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getNextIdentifier() const;

    void setNextIdentifier(const std::shared_ptr<IdentifierNode> &nextIdentifier);

    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getLastIdentifier() const;

    void setLastIdentifier(const std::shared_ptr<IdentifierNode> &lastIdentifier);

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
    std::shared_ptr<IdentifierNode> m_StartIdentifier, m_EndIdentifier;
    std::shared_ptr<OperableNode> m_Expression;

public:
    AssignmentNode();

    AssignmentNode &operator=(const AssignmentNode &) = delete;

protected:
    AssignmentNode(const AssignmentNode &other);

public:
    AssignmentNode *clone(const std::shared_ptr<ASTNode> parent,
                          const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getStartIdentifier() const;

    void setStartIdentifier(const std::shared_ptr<IdentifierNode> &startIdentifier);

    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getEndIdentifier() const;

    void setEndIdentifier(const std::shared_ptr<IdentifierNode> &endIdentifier);

    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

};

class ReturnNode : public TypedNode {

private:
    std::shared_ptr<OperableNode> m_Expression;

public:
    ReturnNode();

    ReturnNode &operator=(const ReturnNode &) = delete;

protected:
    ReturnNode(const ReturnNode &other);

public:
    ReturnNode *clone(const std::shared_ptr<ASTNode> parent,
                      const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

};

class StructNode : public TypedNode {

private:
    std::vector<std::shared_ptr<VariableNode>> m_Variables;
    std::shared_ptr<Token> m_Name;

public:
    StructNode();

    StructNode &operator=(const StructNode &) = delete;

protected:
    StructNode(const StructNode &other);

public:
    StructNode *clone(const std::shared_ptr<ASTNode> parent,
                      const std::shared_ptr<SymbolTable> symbolTable) const override;

    void addVariable(const std::shared_ptr<VariableNode> &variable);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<VariableNode>> &getVariables() const;

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class TypeNode : public OperableNode {

private:
    std::shared_ptr<StructNode> m_TargetStruct;
    unsigned int m_PointerLevel, m_Bits;
    std::shared_ptr<Token> m_TypeToken;
    bool mb_Signed, mb_Floating;

public:
    TypeNode();

    TypeNode &operator=(const TypeNode &) = delete;

protected:
    TypeNode(const TypeNode &other);

public:
    TypeNode *clone(const std::shared_ptr<ASTNode> parent,
                    const std::shared_ptr<SymbolTable> symbolTable) const override;

    [[nodiscard]]
    bool isNumeric() const;

public:
    [[nodiscard]]
    const std::shared_ptr<StructNode> &getTargetStruct() const;

    void setTargetStruct(const std::shared_ptr<StructNode> &targetStruct);

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
    friend std::ostream &operator<<(std::ostream &out, const std::shared_ptr<TypeNode> &typeNode);

    bool operator==(const TypeNode &other) const;

    bool operator!=(const TypeNode &other) const;

};

class FunctionNode : public TypedNode {

private:
    std::vector<std::shared_ptr<ParameterNode>> m_Parameters;
    std::set<std::string> m_Annotations;
    std::shared_ptr<BlockNode> m_Block;
    std::shared_ptr<Token> m_Name;
    bool mb_Variadic, mb_Native;

public:
    FunctionNode();

    FunctionNode &operator=(const FunctionNode &) = delete;

protected:
    FunctionNode(const FunctionNode &other);

public:
    FunctionNode *clone(const std::shared_ptr<ASTNode> parent,
                        const std::shared_ptr<SymbolTable> symbolTable) const override;

    bool hasAnnotation(const std::string &annotation);

    void addParameter(const std::shared_ptr<ParameterNode> &parameterNode);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<ParameterNode>> &getParameters() const;

    void setAnnotations(const std::set<std::string> &annotations);

    [[nodiscard]]
    const std::shared_ptr<BlockNode> &getBlock() const;

    void setBlock(const std::shared_ptr<BlockNode> &block);

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
    std::shared_ptr<OperableNode> m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_DontCopy;

public:
    StructArgumentNode();

    StructArgumentNode &operator=(const StructArgumentNode &) = delete;

protected:
    StructArgumentNode(const StructArgumentNode &other);

public:
    StructArgumentNode *clone(const std::shared_ptr<ASTNode> parent,
                              const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    bool isDontCopy() const;

    void setDontCopy(bool mbDontCopy);

    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class StructCreateNode : public OperableNode {

private:
    std::vector<std::shared_ptr<StructArgumentNode>> m_Arguments;
    std::shared_ptr<IdentifierNode> m_Identifier;
    bool mb_Unnamed;

public:
    StructCreateNode();

    StructCreateNode &operator=(const StructCreateNode &) = delete;

protected:
    StructCreateNode(const StructCreateNode &other);

public:
    StructCreateNode *clone(const std::shared_ptr<ASTNode> parent,
                            const std::shared_ptr<SymbolTable> symbolTable) const override;

    void addArgument(const std::shared_ptr<StructArgumentNode> &argumentNode);

    void removeArgument(const std::shared_ptr<StructArgumentNode> &argumentNode);

    void insertArgument(int index, const std::shared_ptr<StructArgumentNode> &argumentNode);

public:
    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getIdentifier() const;

    void setIdentifier(const std::shared_ptr<IdentifierNode> &mIdentifier);

    [[nodiscard]]
    const std::vector<std::shared_ptr<StructArgumentNode>> &getArguments() const;

    [[nodiscard]]
    bool isUnnamed() const;

    void setUnnamed(bool unnamed);

};

class FunctionArgumentNode : public TypedNode {

private:
    std::shared_ptr<OperableNode> m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_TypeWhitelisted;

public:
    FunctionArgumentNode();

    FunctionArgumentNode &operator=(const FunctionArgumentNode &) = delete;

protected:
    FunctionArgumentNode(const FunctionArgumentNode &other);

public:
    FunctionArgumentNode *clone(const std::shared_ptr<ASTNode> parent,
                                const std::shared_ptr<SymbolTable> symbolTable) const override;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

    [[nodiscard]]
    bool isTypeWhitelisted() const;

    void setTypeWhitelisted(bool typeWhitelisted);

};

class FunctionCallNode : public IdentifierNode {

private:
    std::vector<std::shared_ptr<FunctionArgumentNode>> m_Arguments;

public:
    FunctionCallNode();

    FunctionCallNode &operator=(const FunctionCallNode &) = delete;

protected:
    FunctionCallNode(const FunctionCallNode &other);

public:
    FunctionCallNode *clone(const std::shared_ptr<ASTNode> parent,
                            const std::shared_ptr<SymbolTable> symbolTable) const override;

    bool getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                            std::vector<std::shared_ptr<FunctionArgumentNode>> &sortedArguments);

    void addArgument(const std::shared_ptr<FunctionArgumentNode> &argumentNode);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<FunctionArgumentNode>> &getArguments() const;

};