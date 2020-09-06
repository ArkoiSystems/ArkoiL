//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_ASTNODES_H
#define ARKOICOMPILER_ASTNODES_H

#include <vector>
#include <ostream>
#include <functional>
#include <set>
#include "../lexer/token.h"
#include "symboltable.h"
#include "../utils.h"

enum ASTKind {
    AST_NONE,
    AST_ROOT,
    AST_IMPORT,
    AST_FUNCTION,
    AST_PARAMETER,
    AST_TYPE,
    AST_BLOCK,
    AST_VARIABLE,
    AST_BINARY,
    AST_UNARY,
    AST_PARENTHESIZED,
    AST_NUMBER,
    AST_STRING,
    AST_IDENTIFIER,
    AST_ARGUMENT,
    AST_FUNCTION_CALL,
    AST_STRUCT_CREATE,
    AST_ASSIGNMENT,
    AST_RETURN,
    AST_STRUCT,
    AST_OPERABLE,
};

class TypeNode;

class RootNode;

struct ASTNode {

    std::shared_ptr<Token> startToken, endToken;
    std::shared_ptr<SymbolTable> scope;
    std::shared_ptr<ASTNode> parent;
    bool isFailed;
    ASTKind kind;

    ASTNode() : startToken({}), endToken({}), scope({}), parent({}),
                isFailed(false), kind(AST_NONE) {}

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

struct TypedNode : public ASTNode {

    std::shared_ptr<ASTNode> targetNode;
    std::shared_ptr<TypeNode> type;
    bool isTypeResolved;

    TypedNode() : type({}), isTypeResolved(false) {}

    TypedNode(const TypedNode &other) = default;

};

struct ImportNode : public ASTNode {

    std::shared_ptr<RootNode> target;
    std::shared_ptr<Token> path;

    ImportNode() : target({}), path({}) {
        kind = AST_IMPORT;
    }

};

struct RootNode : public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;
    std::string sourcePath, sourceCode;

    RootNode() : nodes({}), sourcePath({}), sourceCode({}) {
        kind = AST_ROOT;
    }

    std::vector<std::shared_ptr<RootNode>> getImportedRoots() {
        std::vector<std::shared_ptr<RootNode>> importedRoots;
        getImportedRoots(importedRoots);
        return importedRoots;
    }

    void getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots) {
        for (const auto node : nodes) {
            if (node->kind != AST_IMPORT)
                continue;

            auto importNode = std::static_pointer_cast<ImportNode>(node);
            importNode->target->getImportedRoots(importedRoots);
            importedRoots.push_back(importNode->target);
        }
    }

    std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>>
    searchWithImports(const std::string &id,
                      const std::function<bool(const std::shared_ptr<ASTNode> &)> &predicate) {
        auto foundNodes = std::make_shared<std::vector<std::shared_ptr<ASTNode>>>();

        auto currentFounds = scope->scope(id, predicate);
        if (currentFounds != nullptr && !currentFounds->empty())
            foundNodes->insert(foundNodes->end(), currentFounds->begin(), currentFounds->end());

        auto importedRoots = getImportedRoots();
        for (const auto &importedRoot : importedRoots) {
            auto importedFounds = importedRoot->scope->scope(id, predicate);
            if (importedFounds == nullptr || importedFounds->empty())
                continue;

            foundNodes->insert(foundNodes->end(), importedFounds->begin(), importedFounds->end());
        }

        return foundNodes;
    }

};

struct ParameterNode : public TypedNode {

    std::shared_ptr<Token> name;

    ParameterNode() : name({}) {
        kind = AST_PARAMETER;
    }

};

struct BlockNode : public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;

    BlockNode() : nodes({}) {
        kind = AST_BLOCK;
    }

};

struct OperableNode : public TypedNode {

    OperableNode() {
        kind = AST_OPERABLE;
    }

    OperableNode(const OperableNode &other) : TypedNode(other) {
        kind = AST_OPERABLE;
    }

};

struct VariableNode : public TypedNode {

    std::shared_ptr<OperableNode> expression;
    std::shared_ptr<Token> name;
    bool isConstant, isLocal;

    VariableNode() : expression({}), name({}), isConstant(false), isLocal(false) {
        kind = AST_VARIABLE;
    }

};

enum BinaryKind {
    BINARY_NONE,

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

struct BinaryNode: public OperableNode {

    std::shared_ptr<OperableNode> lhs, rhs;
    BinaryKind operatorKind;

    BinaryNode() : lhs({}), rhs({}), operatorKind(BINARY_NONE) {
        kind = AST_BINARY;
    }

};

enum UnaryKind {
    UNARY_NONE,
    NEGATE
};

struct UnaryNode: public OperableNode {

    std::shared_ptr<OperableNode> operable;
    UnaryKind operatorKind;

    UnaryNode() : operable({}), operatorKind(UNARY_NONE) {
        kind = AST_UNARY;
    }

};

struct ParenthesizedNode: public OperableNode {

    std::shared_ptr<OperableNode> expression;

    ParenthesizedNode() : expression({}) {
        kind = AST_PARENTHESIZED;
    }

};

struct NumberNode : public OperableNode {

    std::shared_ptr<Token> number;

    NumberNode() : number({}) {
        kind = AST_NUMBER;
    }

};

struct StringNode : public OperableNode {

    std::shared_ptr<Token> string;

    StringNode() : string({}) {
        kind = AST_STRING;
    }

};

struct ArgumentNode : public TypedNode {

    std::shared_ptr<OperableNode> expression;
    std::shared_ptr<Token> name;

    ArgumentNode() : expression({}), name({}) {
        kind = AST_ARGUMENT;
    }

};

struct IdentifierNode : public OperableNode {

    std::shared_ptr<IdentifierNode> nextIdentifier, lastIdentifier;
    std::shared_ptr<Token> identifier;
    bool isPointer, isDereference;

    IdentifierNode() : nextIdentifier({}), lastIdentifier({}), identifier({}),
                       isPointer(false), isDereference(false) {
        kind = AST_IDENTIFIER;
    }

    IdentifierNode(const IdentifierNode &other) : OperableNode(other) {
        nextIdentifier = other.nextIdentifier;
        isDereference = other.isDereference;
        identifier = other.identifier;
        isPointer = other.isPointer;
        kind = AST_IDENTIFIER;
    }

};

struct AssignmentNode : public OperableNode {

    std::shared_ptr<IdentifierNode> startIdentifier, endIdentifier;
    std::shared_ptr<OperableNode> expression;

    AssignmentNode() : startIdentifier({}), endIdentifier({}), expression({}) {
        kind = AST_ASSIGNMENT;
    }

};

struct ReturnNode : public TypedNode {

    std::shared_ptr<OperableNode> expression;

    ReturnNode() : expression({}) {
        kind = AST_RETURN;
    }

};

struct StructNode : public TypedNode {

    std::vector<std::shared_ptr<VariableNode>> variables;
    std::shared_ptr<Token> name;

    StructNode() : variables({}), name({}) {
        kind = AST_STRUCT;
    }

};

struct TypeNode : public OperableNode {

    std::shared_ptr<StructNode> targetStruct;
    std::shared_ptr<Token> typeToken;
    unsigned int pointerLevel, bits;
    bool isSigned, isFloating;

    TypeNode() : targetStruct({}), typeToken({}), pointerLevel(0), bits(0),
                 isSigned(false), isFloating(false) {
        kind = AST_TYPE;
    }

    TypeNode(const TypeNode &other) = default;

    [[nodiscard]] bool isNumeric() const {
        if ((isSigned || isFloating) && bits > 0)
            return true;
        return bits > 0 && targetStruct == nullptr;
    }

    friend std::ostream &operator<<(std::ostream &out, const std::shared_ptr<TypeNode> &typeNode) {
        if(typeNode == nullptr) {
            out << "null";
            return out;
        }

        out << "targetStruct: " << typeNode->targetStruct
            << ", pointerLevel: " << typeNode->pointerLevel
            << ", bits: " << typeNode->bits
            << ", isSigned: " << std::boolalpha << typeNode->isSigned << std::dec
            << ", isFloating: " << std::boolalpha << typeNode->isFloating << std::dec;
        return out;
    }

    bool operator==(const TypeNode &other) const {
        return (targetStruct == other.targetStruct) &&
               (pointerLevel == other.pointerLevel) &&
               (bits == other.bits) &&
               (isSigned == other.isSigned) &&
               (isFloating == other.isFloating);
    }

    bool operator!=(const TypeNode &other) const {
        return !(other == *this);
    }

};

struct FunctionNode : public TypedNode {

    std::vector<std::shared_ptr<ParameterNode>> parameters;
    std::set<std::string> annotations;
    std::shared_ptr<BlockNode> block;
    std::shared_ptr<Token> name;
    bool isVariadic, isNative;

    FunctionNode() : parameters({}), isVariadic(false), isNative(false), block({}), name({}) {
        kind = AST_FUNCTION;
    }

    bool isInlined() {
        return annotations.find("inlined") != annotations.end();
    }

    bool operator==(const FunctionNode &other) const {
        if (name->content != other.name->content)
            return false;

        if (parameters.size() != other.parameters.size())
            return false;

        for (auto index = 0; index < other.parameters.size(); index++) {
            auto otherParameter = other.parameters[index];
            auto ownParameter = parameters[index];

            if (*otherParameter->type != *ownParameter->type)
                return false;
        }

        return true;
    }

    bool operator!=(const FunctionNode &other) const {
        return !(other == *this);
    }

};

struct StructCreateNode : public OperableNode {

    std::shared_ptr<IdentifierNode> startIdentifier, endIdentifier;
    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    StructCreateNode() : startIdentifier({}), endIdentifier({}), arguments({}) {
        kind = AST_STRUCT_CREATE;
    }

    bool getFilledExpressions(const std::shared_ptr<StructNode> &structNode,
                              std::vector<std::shared_ptr<OperableNode>> &expressions) {
        for (const auto &variable : structNode->variables) {
            std::shared_ptr<ArgumentNode> foundNode;
            for (const auto &argument : arguments) {
                if (variable->name->content == argument->name->content) {
                    foundNode = argument;
                    break;
                }
            }

            if (foundNode == nullptr) {
                expressions.emplace_back(variable->expression);
            } else {
                expressions.emplace_back(foundNode->expression);
            }
        }

        return false;
    }

};

struct FunctionCallNode : public IdentifierNode {

    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    FunctionCallNode() : arguments({}) {
        kind = AST_FUNCTION_CALL;
    }

    explicit FunctionCallNode(const IdentifierNode &other) : IdentifierNode(other) {
        kind = AST_FUNCTION_CALL;
    }

    bool getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                            std::vector<std::shared_ptr<ArgumentNode>> &sortedArguments) {
        auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
            return node->kind == AST_PARAMETER;
        };

        for (const auto &argument : arguments) {
            if (argument->name == nullptr)
                continue;

            auto foundParameters = functionNode->scope->scope(argument->name->content, scopeCheck);
            if (foundParameters == nullptr)
                return false;

            auto foundParameter = std::static_pointer_cast<ParameterNode>(foundParameters->at(0));
            auto parameterIndex = Utils::indexOf(functionNode->parameters, foundParameter).second;
            auto argumentIndex = Utils::indexOf(sortedArguments, argument).second;
            sortedArguments.erase(sortedArguments.begin() + argumentIndex);
            sortedArguments.insert(sortedArguments.begin() + parameterIndex, argument);
        }

        return true;
    }

};

#endif //ARKOICOMPILER_ASTNODES_H
