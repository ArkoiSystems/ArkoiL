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

class SymbolTable;

class Token;

class TypeNode;

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
        ARGUMENT,
        FUNCTION_CALL,
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

    ASTNode(const ASTNode &other) = default;

    ASTNode &operator=(const ASTNode &) = delete;

    virtual ~ASTNode() = default;

public:
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
    ASTKind getKind() const;

    void setKind(ASTKind kind);

};

class RootNode : public ASTNode {

private:
    std::vector<std::shared_ptr<ASTNode>> m_Nodes;
    std::string m_SourcePath, m_SourceCode;

public:
    RootNode();

    RootNode(const RootNode &other) = delete;

    RootNode &operator=(const RootNode &) = delete;

public:
    std::vector<std::shared_ptr<RootNode>> getImportedRoots();

    void getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots);

    std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>>
    searchWithImports(const std::string &id,
                      const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate);

    void addNode(const std::shared_ptr<ASTNode> &node);

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

public:
    TypedNode();

    TypedNode(const TypedNode &other) = default;

    TypedNode &operator=(const TypedNode &) = delete;

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

};

class ImportNode : public ASTNode {

private:
    std::shared_ptr<RootNode> m_Target;
    std::shared_ptr<Token> m_Path;

public:
    ImportNode();

    ImportNode(const ImportNode &other) = delete;

    ImportNode &operator=(const ImportNode &) = delete;

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

    ParameterNode(const ParameterNode &other) = delete;

    ParameterNode &operator=(const ParameterNode &) = delete;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getName() const;

    void setName(const std::shared_ptr<Token> &name);

};

class BlockNode : public ASTNode {

private:
    std::vector<std::shared_ptr<ASTNode>> m_Nodes;

public:
    BlockNode();

    BlockNode(const BlockNode &other) = delete;

    BlockNode &operator=(const BlockNode &) = delete;

public:
    void addNode(const std::shared_ptr<ASTNode> &node);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<ASTNode>> &getNodes() const;

};

class OperableNode : public TypedNode {

public:
    OperableNode();

    OperableNode(const OperableNode &other);

    OperableNode &operator=(const OperableNode &) = delete;

};

class VariableNode : public TypedNode {

private:
    std::shared_ptr<OperableNode> m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_Constant, mb_Local;

public:
    VariableNode();

    VariableNode(const VariableNode &other) = delete;

    VariableNode &operator=(const VariableNode &) = delete;

public:
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

    BinaryNode(const BinaryNode &other) = delete;

    BinaryNode &operator=(const BinaryNode &) = delete;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getLHS() const;

    void setLHS(const std::shared_ptr<OperableNode> &lhs);

    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getRHS() const;

    void setRHS(const std::shared_ptr<OperableNode> &rhs);

    [[nodiscard]]
    BinaryKind getOperatorKind() const;

    void setOperatorKind(BinaryKind operatorKind);

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

    UnaryNode(const UnaryNode &other) = delete;

    UnaryNode &operator=(const UnaryNode &) = delete;

public:
    [[nodiscard]]
    const std::shared_ptr<OperableNode> &getExpression() const;

    void setExpression(const std::shared_ptr<OperableNode> &expression);

    [[nodiscard]]
    UnaryKind getOperatorKind() const;

    void setOperatorKind(UnaryKind operatorKind);

};

class ParenthesizedNode : public OperableNode {

private:
    std::shared_ptr<OperableNode> m_Expression;

public:
    ParenthesizedNode();

    ParenthesizedNode(const ParenthesizedNode &other) = delete;

    ParenthesizedNode &operator=(const ParenthesizedNode &) = delete;

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

    NumberNode(const NumberNode &other) = delete;

    NumberNode &operator=(const NumberNode &) = delete;

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

    StringNode(const StringNode &other) = delete;

    StringNode &operator=(const StringNode &) = delete;

public:
    [[nodiscard]]
    const std::shared_ptr<Token> &getString() const;

    void setString(const std::shared_ptr<Token> &string);

};

class ArgumentNode : public TypedNode {

private:
    std::shared_ptr<OperableNode> m_Expression;
    std::shared_ptr<Token> m_Name;
    bool mb_TypeWhitelisted;

public:
    ArgumentNode();

    ArgumentNode(const ArgumentNode &other) = delete;

    ArgumentNode &operator=(const ArgumentNode &) = delete;

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

class IdentifierNode : public OperableNode {

private:
    std::shared_ptr<IdentifierNode> m_NextIdentifier, m_LastIdentifier;
    std::shared_ptr<Token> m_Identifier;
    bool mb_Pointer, mb_Dereference;

public:
    IdentifierNode();

    IdentifierNode(const IdentifierNode &other);

    IdentifierNode &operator=(const IdentifierNode &) = delete;

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

    AssignmentNode(const AssignmentNode &other) = delete;

    AssignmentNode &operator=(const AssignmentNode &) = delete;

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

    ReturnNode(const ReturnNode &other) = delete;

    ReturnNode &operator=(const ReturnNode &) = delete;

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

    StructNode(const StructNode &other) = delete;

    StructNode &operator=(const StructNode &) = delete;

public:
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

    TypeNode(const TypeNode &other) = default;

    TypeNode &operator=(const TypeNode &) = delete;

public:
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

class FunctionCallNode;

class FunctionNode : public TypedNode {

private:
    std::vector<std::shared_ptr<ParameterNode>> m_Parameters;
    std::shared_ptr<FunctionCallNode> m_InlinedFunctionCall;
    std::set<std::string> m_Annotations;
    std::shared_ptr<BlockNode> m_Block;
    llvm::BasicBlock *m_EntryBlock;
    std::shared_ptr<Token> m_Name;
    bool mb_Variadic, mb_Native;

public:
    FunctionNode();

    FunctionNode(const FunctionNode &other) = delete;

    FunctionNode &operator=(const FunctionNode &) = delete;

public:
    bool hasAnnotation(const std::string &annotation);

    void addParameter(const std::shared_ptr<ParameterNode> &parameterNode);

public:
    [[nodiscard]]
    const std::shared_ptr<FunctionCallNode> &getInlinedFunctionCall() const;

    void setInlinedFunctionCall(const std::shared_ptr<FunctionCallNode> &inlinedFunctionCall);

    [[nodiscard]]
    const std::vector<std::shared_ptr<ParameterNode>> &getParameters() const;

    void setAnnotations(const std::set<std::string> &annotations);

    [[nodiscard]]
    const std::shared_ptr<BlockNode> &getBlock() const;

    void setBlock(const std::shared_ptr<BlockNode> &block);

    [[nodiscard]]
    llvm::BasicBlock *getEntryBlock() const;

    void setEntryBlock(llvm::BasicBlock *entryBlock);

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

class StructCreateNode : public OperableNode {

private:
    std::shared_ptr<IdentifierNode> m_StartIdentifier, m_EndIdentifier;
    std::vector<std::shared_ptr<ArgumentNode>> m_Arguments;
    bool mb_Unnamed;

public:
    StructCreateNode();

    StructCreateNode(const StructCreateNode &other) = delete;

    StructCreateNode &operator=(const StructCreateNode &) = delete;

public:
    bool getFilledExpressions(const std::shared_ptr<StructNode> &structNode,
                              std::vector<std::shared_ptr<OperableNode>> &expressions);

    void addArgument(const std::shared_ptr<ArgumentNode> &argumentNode);

public:
    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getStartIdentifier() const;

    void setStartIdentifier(const std::shared_ptr<IdentifierNode> &startIdentifier);

    [[nodiscard]]
    const std::shared_ptr<IdentifierNode> &getEndIdentifier() const;

    void setEndIdentifier(const std::shared_ptr<IdentifierNode> &endIdentifier);

    [[nodiscard]]
    const std::vector<std::shared_ptr<ArgumentNode>> &getArguments() const;

    [[nodiscard]]
    bool isUnnamed() const;

    void setUnnamed(bool unnamed);

};

class FunctionCallNode : public IdentifierNode {

private:
    std::vector<std::shared_ptr<ArgumentNode>> m_Arguments;

public:
    FunctionCallNode();

    FunctionCallNode(const FunctionCallNode &other) = delete;

    FunctionCallNode &operator=(const FunctionCallNode &) = delete;

    explicit FunctionCallNode(const IdentifierNode &other);

public:
    bool getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                            std::vector<std::shared_ptr<ArgumentNode>> &sortedArguments);

    void addArgument(const std::shared_ptr<ArgumentNode> &argumentNode);

public:
    [[nodiscard]]
    const std::vector<std::shared_ptr<ArgumentNode>> &getArguments() const;

};