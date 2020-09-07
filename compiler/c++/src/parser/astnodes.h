//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_ASTNODES_H
#define ARKOICOMPILER_ASTNODES_H

#include <functional>
#include <ostream>
#include <vector>
#include <memory>
#include <set>

class SymbolTable;

class Token;

class TypeNode;

struct ASTNode {

    std::shared_ptr<Token> startToken, endToken;
    std::shared_ptr<SymbolTable> scope;
    std::shared_ptr<ASTNode> parent;
    bool isFailed;
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
    } kind;

    ASTNode();

    ASTNode(const ASTNode &other) = default;

    virtual ~ASTNode() = default;

    template<typename Type = ASTNode>
    std::shared_ptr<Type> getParent() {
        if (parent == nullptr)
            return nullptr;

        if (auto result = std::dynamic_pointer_cast<Type>(parent))
            return result;
        return parent->getParent<Type>();
    }

};

struct RootNode : public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;
    std::string sourcePath, sourceCode;

    RootNode();

    std::vector<std::shared_ptr<RootNode>> getImportedRoots();

    void getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots);

    std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>>
    searchWithImports(const std::string &id,
                      const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate);

};

struct TypedNode : public ASTNode {

    std::shared_ptr<ASTNode> targetNode;
    std::shared_ptr<TypeNode> type;
    bool isTypeResolved;

    TypedNode();

    TypedNode(const TypedNode &other) = default;

};

struct ImportNode : public ASTNode {

    std::shared_ptr<RootNode> target;
    std::shared_ptr<Token> path;

    ImportNode();

};

struct ParameterNode : public TypedNode {

    std::shared_ptr<Token> name;

    ParameterNode();

};

struct BlockNode : public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;

    BlockNode();

};

struct OperableNode : public TypedNode {

    OperableNode();

    OperableNode(const OperableNode &other);

};

struct VariableNode : public TypedNode {

    std::shared_ptr<OperableNode> expression;
    std::shared_ptr<Token> name;
    bool isConstant, isLocal;

    VariableNode();

};


struct BinaryNode: public OperableNode {

    std::shared_ptr<OperableNode> lhs, rhs;
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
    } operatorKind;

    BinaryNode();

};

struct UnaryNode: public OperableNode {

    std::shared_ptr<OperableNode> operable;
    enum UnaryKind {
        NONE,

        NEGATE
    } operatorKind;

    UnaryNode();

};

struct ParenthesizedNode: public OperableNode {

    std::shared_ptr<OperableNode> expression;

    ParenthesizedNode();

};

struct NumberNode : public OperableNode {

    std::shared_ptr<Token> number;

    NumberNode();

};

struct StringNode : public OperableNode {

    std::shared_ptr<Token> string;

    StringNode();

};

struct ArgumentNode : public TypedNode {

    std::shared_ptr<OperableNode> expression;
    std::shared_ptr<Token> name;

    ArgumentNode();

};

struct IdentifierNode : public OperableNode {

    std::shared_ptr<IdentifierNode> nextIdentifier, lastIdentifier;
    std::shared_ptr<Token> identifier;
    bool isPointer, isDereference;

    IdentifierNode();

    IdentifierNode(const IdentifierNode &other);

};

struct AssignmentNode : public OperableNode {

    std::shared_ptr<IdentifierNode> startIdentifier, endIdentifier;
    std::shared_ptr<OperableNode> expression;

    AssignmentNode();

};

struct ReturnNode : public TypedNode {

    std::shared_ptr<OperableNode> expression;

    ReturnNode();

};

struct StructNode : public TypedNode {

    std::vector<std::shared_ptr<VariableNode>> variables;
    std::shared_ptr<Token> name;

    StructNode();

};

struct TypeNode : public OperableNode {

    std::shared_ptr<StructNode> targetStruct;
    std::shared_ptr<Token> typeToken;
    unsigned int pointerLevel, bits;
    bool isSigned, isFloating;

    TypeNode();

    TypeNode(const TypeNode &other) = default;

    [[nodiscard]] bool isNumeric() const;

    friend std::ostream &operator<<(std::ostream &out, const std::shared_ptr<TypeNode> &typeNode);

    bool operator==(const TypeNode &other) const;

    bool operator!=(const TypeNode &other) const;

};

struct FunctionNode : public TypedNode {

    std::vector<std::shared_ptr<ParameterNode>> parameters;
    std::set<std::string> annotations;
    std::shared_ptr<BlockNode> block;
    std::shared_ptr<Token> name;
    bool isVariadic, isNative;

    FunctionNode();

    bool hasAnnotation(const std::string &annotation);

    bool operator==(const FunctionNode &other) const;

    bool operator!=(const FunctionNode &other) const;

};

struct StructCreateNode : public OperableNode {

    std::shared_ptr<IdentifierNode> startIdentifier, endIdentifier;
    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    StructCreateNode();

    bool getFilledExpressions(const std::shared_ptr<StructNode> &structNode,
                              std::vector<std::shared_ptr<OperableNode>> &expressions);

};

struct FunctionCallNode : public IdentifierNode {

    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    FunctionCallNode();

    explicit FunctionCallNode(const IdentifierNode &other);

    bool getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                            std::vector<std::shared_ptr<ArgumentNode>> &sortedArguments);

};

#endif //ARKOICOMPILER_ASTNODES_H
