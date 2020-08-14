//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_ASTNODES_H
#define ARKOICOMPILER_ASTNODES_H

#include <vector>
#include <ostream>
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

    template<typename Type>
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

    void getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots) {
        for (const auto node : nodes) {
            if (node->kind != AST_IMPORT)
                continue;

            auto importNode = std::static_pointer_cast<ImportNode>(node);
            importNode->target->getImportedRoots(importedRoots);
            importedRoots.push_back(importNode->target);
        }
    }

    template<typename Function>
    void searchGlobally(std::vector<std::shared_ptr<ASTNode>> &foundNodes, const std::string &id,
                        Function predicate) {
        std::vector<std::shared_ptr<RootNode>> importedRoots;
        getImportedRoots(importedRoots);

        auto currentFounds = scope->scope(id, predicate);
        if(currentFounds != nullptr && !currentFounds->empty())
            foundNodes.insert(foundNodes.end(), currentFounds->begin(), currentFounds->end());

        for (const auto &importedRoot : importedRoots) {
            auto importedFounds = importedRoot->scope->scope(id, predicate);
            if (importedFounds == nullptr || importedFounds->empty())
                continue;

            foundNodes.insert(foundNodes.end(), importedFounds->begin(), importedFounds->end());
        }
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
    bool isInlined;

    BlockNode() : nodes({}), isInlined(false) {
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
    bool isConstant;

    VariableNode() : expression({}), name({}), isConstant(false) {
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
    bool isBuiltin;

    StructNode() : variables({}), name({}), isBuiltin(false) {
        kind = AST_STRUCT;
    }

};

struct TypeNode : public ASTNode {

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

    [[nodiscard]] bool isBoolean() const {
        return targetStruct == nullptr && bits == 1 && isSigned && !isFloating;
    }

    friend std::ostream &operator<<(std::ostream &os, const std::shared_ptr<TypeNode> &typeNode) {
        os << "targetStruct: " << typeNode->targetStruct
           << ", pointerLevel: " << typeNode->pointerLevel
           << ", bits: " << typeNode->bits
           << ", isSigned: " << std::boolalpha << typeNode->isSigned << std::dec
           << ", isFloating: " << std::boolalpha << typeNode->isFloating << std::dec;
        return os;
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
    bool isVariadic, isBuiltin, isNative;
    std::shared_ptr<BlockNode> block;
    std::shared_ptr<Token> name;

    FunctionNode() : parameters({}), isVariadic(false), isBuiltin(false), isNative(false),
                     block({}), name({}) {
        kind = AST_FUNCTION;
    }

    bool operator==(const FunctionNode &other) const {
        if (std::strcmp(name->content.c_str(), other.name->content.c_str()) != 0)
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

    std::vector<std::shared_ptr<ArgumentNode>> getSortedArguments() {
        auto structNode = std::static_pointer_cast<StructNode>(targetNode);

        std::vector<std::shared_ptr<ArgumentNode>> sortedArguments(arguments);
        for (const auto &argument : arguments) {
            std::shared_ptr<VariableNode> foundVariable;
            for (auto index = 0; index < structNode->variables.size(); index++) {
                auto variable = structNode->variables.at(index);
                if (std::strcmp(variable->name->content.c_str(),
                                argument->name->content.c_str()) == 0) {
                    foundVariable = variable;
                    break;
                }
            }

            if (foundVariable == nullptr) {
                std::cout << "StructCreateNode: Couldn't find the variable." << std::endl;
                exit(EXIT_FAILURE);
            }

            auto variableIndex = Utils::indexOf(structNode->variables, foundVariable).second;
            auto argumentIndex = Utils::indexOf(arguments, argument).second;
            sortedArguments.erase(sortedArguments.begin() + argumentIndex);
            sortedArguments.insert(sortedArguments.begin() + variableIndex, argument);
        }

        return sortedArguments;
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

    std::vector<std::shared_ptr<ArgumentNode>> getSortedArguments() {
        auto functionNode = std::static_pointer_cast<FunctionNode>(targetNode);

        std::vector<std::shared_ptr<ArgumentNode>> sortedArguments(arguments);
        for (const auto &argument : arguments) {
            if (argument->name == nullptr)
                continue;

            std::shared_ptr<ParameterNode> foundParameter;
            for (auto index = 0; index < functionNode->parameters.size(); index++) {
                auto parameter = functionNode->parameters.at(index);
                if (std::strcmp(parameter->name->content.c_str(),
                                argument->name->content.c_str()) == 0) {
                    foundParameter = parameter;
                    break;
                }
            }

            if (foundParameter == nullptr) {
                std::cout << "FunctionCallNode: Couldn't find the parameter." << std::endl;
                exit(EXIT_FAILURE);
            }

            auto parameterIndex = Utils::indexOf(functionNode->parameters, foundParameter).second;
            auto argumentIndex = Utils::indexOf(arguments, argument).second;
            sortedArguments.erase(sortedArguments.begin() + argumentIndex);
            sortedArguments.insert(sortedArguments.begin() + parameterIndex, argument);
        }

        return sortedArguments;
    }

};

#endif //ARKOICOMPILER_ASTNODES_H
