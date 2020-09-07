//
// Created by timo on 9/7/20.
//

#include "astnodes.h"

#include "../lexer/token.h"
#include "symboltable.h"
#include "../utils.h"

ASTNode::ASTNode() {
    isFailed = false;
    startToken = {};
    endToken = {};
    parent = {};
    scope = {};

    kind = ASTNode::NONE;
}

TypedNode::TypedNode() {
    isTypeResolved = false;
    type = {};
}

ImportNode::ImportNode() {
    target = {};
    path = {};

    kind = ASTNode::IMPORT;
}

RootNode::RootNode() {
    sourcePath = {};
    sourceCode = {};

    kind = ASTNode::ROOT;
}

std::vector<std::shared_ptr<RootNode>> RootNode::getImportedRoots() {
    std::vector<std::shared_ptr<RootNode>> importedRoots;
    getImportedRoots(importedRoots);
    return importedRoots;
}

void RootNode::getImportedRoots(std::vector<std::shared_ptr<RootNode>> &importedRoots) {
    for (const auto &node : nodes) {
        if (node->kind != IMPORT)
            continue;

        auto importNode = std::static_pointer_cast<ImportNode>(node);
        importNode->target->getImportedRoots(importedRoots);
        importedRoots.push_back(importNode->target);
    }
}

std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>>
RootNode::searchWithImports(const std::string &id,
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

ParameterNode::ParameterNode() {
    name = {};

    kind = ASTNode::PARAMETER;
}

BlockNode::BlockNode() {
    kind = ASTNode::BLOCK;
}

OperableNode::OperableNode() {
    kind = ASTNode::OPERABLE;
}

OperableNode::OperableNode(const OperableNode &other) : TypedNode(other) {
    kind = ASTNode::OPERABLE;
}

VariableNode::VariableNode() : expression({}), name({}), isConstant(false), isLocal(false) {
    isConstant = false;
    isLocal = false;
    expression = {};
    name = {};

    kind = ASTNode::VARIABLE;
}

BinaryNode::BinaryNode() {
    operatorKind = BinaryNode::NONE;
    lhs = {};
    rhs = {};

    kind = ASTNode::BINARY;
}

UnaryNode::UnaryNode() {
    operatorKind = UnaryNode::NONE;
    operable = {};

    kind = ASTNode::UNARY;
}

ParenthesizedNode::ParenthesizedNode() {
    expression = {};

    kind = ASTNode::PARENTHESIZED;
}

NumberNode::NumberNode() {
    number = {};

    kind = ASTNode::NUMBER;
}

StringNode::StringNode() {
    string = {};

    kind = ASTNode::STRING;
}

ArgumentNode::ArgumentNode() {
    expression = {};
    name = {};

    kind = ASTNode::ARGUMENT;
}

IdentifierNode::IdentifierNode() {
    isDereference = false;
    isPointer = false;
    nextIdentifier = {};
    lastIdentifier = {};
    identifier = {};

    kind = ASTNode::IDENTIFIER;
}

IdentifierNode::IdentifierNode(const IdentifierNode &other) : OperableNode(other) {
    nextIdentifier = other.nextIdentifier;
    isDereference = other.isDereference;
    identifier = other.identifier;
    isPointer = other.isPointer;

    kind = ASTNode::IDENTIFIER;
}

AssignmentNode::AssignmentNode() {
    startIdentifier = {};
    endIdentifier = {};
    expression = {};

    kind = ASTNode::ASSIGNMENT;
}

ReturnNode::ReturnNode() {
    expression = {};

    kind = ASTNode::RETURN;
}

StructNode::StructNode() {
    variables = {};
    name = {};

    kind = ASTNode::STRUCT;
}

TypeNode::TypeNode() {
    isFloating = false;
    isSigned = false;
    targetStruct = {};
    typeToken = {};
    pointerLevel = 0;
    bits = 0;

    kind = ASTNode::TYPE;
}

bool TypeNode::isNumeric() const {
    if ((isSigned || isFloating) && bits > 0)
        return true;
    return bits > 0 && targetStruct == nullptr;
}

std::ostream &operator<<(std::ostream &out, const std::shared_ptr<TypeNode> &typeNode) {
    if (typeNode == nullptr) {
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

bool TypeNode::operator==(const TypeNode &other) const {
    return (targetStruct == other.targetStruct) &&
           (pointerLevel == other.pointerLevel) &&
           (bits == other.bits) &&
           (isSigned == other.isSigned) &&
           (isFloating == other.isFloating);
}

bool TypeNode::operator!=(const TypeNode &other) const {
    return !(other == *this);
}

FunctionNode::FunctionNode() : parameters({}), isVariadic(false), isNative(false), block({}), name({}) {
    isVariadic = false;
    isNative = false;
    parameters = {};
    block = {};
    name = {};

    kind = ASTNode::FUNCTION;
}

bool FunctionNode::hasAnnotation(const std::string &annotation) {
    return annotations.find(annotation) != annotations.end();
}

bool FunctionNode::operator==(const FunctionNode &other) const {
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

bool FunctionNode::operator!=(const FunctionNode &other) const {
    return !(other == *this);
}

StructCreateNode::StructCreateNode() {
    startIdentifier = {};
    endIdentifier = {};
    arguments = {};

    kind = ASTNode::STRUCT_CREATE;
}

bool StructCreateNode::getFilledExpressions(const std::shared_ptr<StructNode> &structNode,
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

FunctionCallNode::FunctionCallNode() {
    arguments = {};

    kind = ASTNode::FUNCTION_CALL;
}

FunctionCallNode::FunctionCallNode(const IdentifierNode &other) : IdentifierNode(other) {
    kind = ASTNode::FUNCTION_CALL;
}

bool FunctionCallNode::getSortedArguments(const std::shared_ptr<FunctionNode> &functionNode,
                                          std::vector<std::shared_ptr<ArgumentNode>> &sortedArguments) {
    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->kind == PARAMETER;
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
