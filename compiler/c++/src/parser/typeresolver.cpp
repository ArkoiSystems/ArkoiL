//
// Created by timo on 8/3/20.
//

#include "typeresolver.h"
#include "astnodes.h"
#include "../compiler/error.h"

void TypeResolver::visitNode(const std::shared_ptr<ASTNode> &node) {
    switch (node->kind) {
        case AST_TYPE:
            visitType(std::static_pointer_cast<TypeNode>(node));
            break;
        case AST_STRUCT:
            visitStruct(std::static_pointer_cast<StructNode>(node));
            break;
        case AST_FUNCTION:
            visitFunction(std::static_pointer_cast<FunctionNode>(node));
            break;
        case AST_FUNCTION_CALL:
            visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
            break;
        case AST_STRUCT_CREATE:
            visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
            break;
        case AST_IDENTIFIER:
            visitIdentifier(std::static_pointer_cast<IdentifierNode>(node));
            break;
        case AST_NUMBER:
            visitNumber(std::static_pointer_cast<NumberNode>(node));
            break;
        case AST_PARAMETER:
            visitParameter(std::static_pointer_cast<ParameterNode>(node));
            break;
        case AST_BLOCK:
            visitBlock(std::static_pointer_cast<BlockNode>(node));
            break;
        case AST_STRING:
            visitString(std::static_pointer_cast<StringNode>(node));
            break;
        case AST_ROOT:
            visitRoot(std::static_pointer_cast<RootNode>(node));
            break;
        case AST_VARIABLE:
            visitVariable(std::static_pointer_cast<VariableNode>(node));
            break;
        case AST_RETURN:
            visitReturn(std::static_pointer_cast<ReturnNode>(node));
            break;
        case AST_ARGUMENT:
            visitArgument(std::static_pointer_cast<ArgumentNode>(node));
            break;
        case AST_ASSIGNMENT:
            visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
            break;
        case AST_UNARY:
            visitUnary(std::static_pointer_cast<UnaryNode>(node));
            break;
        case AST_BINARY:
            visitBinary(std::static_pointer_cast<BinaryNode>(node));
            break;
        case AST_PARENTHESIZED:
            visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
            break;
        case AST_IMPORT:
            break;
        default:
            std::cout << "Unsupported node. " << node->kind << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeResolver::visitRoot(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->nodes)
        visitNode(node);
}

void TypeResolver::visitFunction(const std::shared_ptr<FunctionNode> &functionNode) {
    if(functionNode->isTypeResolved)
        return;

    visitType(functionNode->type);

    for (const auto &parameter : functionNode->parameters)
        visitParameter(parameter);

    if (!functionNode->isNative && !functionNode->isBuiltin)
        visitBlock(functionNode->block);

    functionNode->isTypeResolved = true;
}

void TypeResolver::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {
    for (const auto &node : blockNode->nodes)
        visitNode(node);
}

void TypeResolver::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
    if(variableNode->isTypeResolved)
        return;

    if (variableNode->type != nullptr)
        visitType(variableNode->type);

    visitOperable(variableNode->expression, variableNode->type);
    variableNode->type = variableNode->expression->type;

    variableNode->isTypeResolved = true;
}

void TypeResolver::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
    if(binaryNode->isTypeResolved)
        return;

    visitOperable(binaryNode->lhs);
    visitOperable(binaryNode->rhs, binaryNode->lhs->type);
    binaryNode->type = binaryNode->lhs->type;

    binaryNode->isTypeResolved = true;
}

void TypeResolver::visitUnary(const std::shared_ptr<UnaryNode> &unaryNode) {
    if(unaryNode->isTypeResolved)
        return;

    visitOperable(unaryNode->operable);
    unaryNode->type = unaryNode->operable->type;

    unaryNode->isTypeResolved = true;
}

void TypeResolver::visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    if(parenthesizedNode->isTypeResolved)
        return;

    visitOperable(parenthesizedNode->expression);
    parenthesizedNode->type = parenthesizedNode->expression->type;

    parenthesizedNode->isTypeResolved = true;
}

void TypeResolver::visitNumber(const std::shared_ptr<NumberNode> &numberNode) {
    if(numberNode->isTypeResolved)
        return;

    numberNode->type = std::make_shared<TypeNode>();
    numberNode->type->isFloating = numberNode->number->content.find('.') != std::string::npos;
    numberNode->type->bits = 32;

    if (!numberNode->type->isFloating)
        numberNode->type->isSigned = true;

    numberNode->isTypeResolved = true;
}

void TypeResolver::visitString(const std::shared_ptr<StringNode> &stringNode) {
    if(stringNode->isTypeResolved)
        return;

    stringNode->type = std::make_shared<TypeNode>();
    stringNode->type->isSigned = false;
    stringNode->type->pointerLevel = 1;
    stringNode->type->bits = 8;

    stringNode->isTypeResolved = true;
}

void TypeResolver::visitIdentifier(const std::shared_ptr<IdentifierNode> &identifierNode) {
    if (identifierNode->isTypeResolved)
        return;

    auto scopeCheck = [identifierNode](const std::shared_ptr<ASTNode> &node) {
        if (identifierNode->kind == AST_FUNCTION_CALL && node->kind != AST_FUNCTION)
            return false;

        auto name = std::string();
        if (node->kind == AST_VARIABLE) {
            auto variable = std::static_pointer_cast<VariableNode>(node);
            name = variable->name->content;
            TypeResolver::visitVariable(variable);
        } else if (node->kind == AST_PARAMETER) {
            auto parameter = std::static_pointer_cast<ParameterNode>(node);
            name = parameter->name->content;
            TypeResolver::visitParameter(parameter);
        } else if (node->kind == AST_FUNCTION) {
            auto function = std::static_pointer_cast<FunctionNode>(node);
            name = function->name->content;
            TypeResolver::visitFunction(function);
        } else if (node->kind == AST_STRUCT) {
            auto structNode = std::static_pointer_cast<StructNode>(node);
            name = structNode->name->content;
            TypeResolver::visitStruct(structNode);
        }

        auto sameName = strcmp(identifierNode->identifier->content.c_str(), name.c_str()) == 0;
        if (sameName && identifierNode->kind != AST_FUNCTION_CALL)
            return sameName;

        auto functionCall = std::static_pointer_cast<FunctionCallNode>(identifierNode);
        auto function = std::static_pointer_cast<FunctionNode>(node);
        if (sameName) {
            if (!function->isVariadic &&
                (function->parameters.size() != functionCall->arguments.size()))
                return false;

            for (auto index = 0; index < functionCall->arguments.size(); index++) {
                if (index >= function->parameters.size())
                    break;

                auto argument = functionCall->arguments.at(index);
                TypeResolver::visitArgument(argument);
                auto parameter = function->parameters.at(index);
                TypeResolver::visitParameter(parameter);

                if (*parameter->type != *argument->type)
                    return false;
            }

            return true;
        }

        return false;
    };

    auto nodes = identifierNode->scope->general(identifierNode->identifier->content, scopeCheck);
    if (nodes == nullptr) {
        for (const auto &node : identifierNode->getParent<RootNode>()->nodes) {
            if (node->kind != AST_IMPORT)
                continue;

            auto import = std::static_pointer_cast<ImportNode>(node);
            nodes = import->target->scope->scope(identifierNode->identifier->content, scopeCheck);
            if (nodes != nullptr && !nodes->empty())
                break;
        }
    }

    if (nodes == nullptr || nodes->empty()) {
        THROW_NODE_ERROR(identifierNode, "Couldn't find the identifier \"{}\".",
                         identifierNode->identifier->content)
        return;
    }

    auto foundNode = nodes->at(0);
    auto typedNode = std::dynamic_pointer_cast<TypedNode>(foundNode);
    if (typedNode == nullptr) {
        THROW_NODE_ERROR(foundNode, "The found identifier is not a valid node.")
        return;
    }

    if (typedNode->type == nullptr) {
        THROW_NODE_ERROR(typedNode, "The found identifier has no type.")
        return;
    }

    identifierNode->type = std::make_shared<TypeNode>(*typedNode->type);

    if (identifierNode->isPointer)
        identifierNode->type->pointerLevel += 1;
    else if (identifierNode->isDereference)
        identifierNode->type->pointerLevel -= 1;

    if (identifierNode->nextIdentifier != nullptr &&
        identifierNode->type->targetStruct == nullptr) {
        THROW_NODE_ERROR(identifierNode,
                         "The identifier has no struct, so there can't be a following identifier.")
        return;
    }

    if (identifierNode->nextIdentifier != nullptr) {
        identifierNode->nextIdentifier->scope = identifierNode->type->targetStruct->scope;
        TypeResolver::visitIdentifier(identifierNode->nextIdentifier);
    }

    identifierNode->isTypeResolved = true;
}

void TypeResolver::visitParameter(const std::shared_ptr<ParameterNode> &parameterNode) {
    if (parameterNode->isTypeResolved)
        return;

    TypeResolver::visitType(parameterNode->type);

    parameterNode->isTypeResolved = true;
}

void TypeResolver::visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
    if (argumentNode->isTypeResolved)
        return;

    TypeResolver::visitOperable(argumentNode->expression);
    argumentNode->type = argumentNode->expression->type;

    argumentNode->isTypeResolved = true;
}

void TypeResolver::visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    if (functionCallNode->isTypeResolved)
        return;

    for (const auto &argument : functionCallNode->arguments)
        TypeResolver::visitArgument(argument);
    TypeResolver::visitIdentifier(functionCallNode);

    functionCallNode->isTypeResolved = true;
}

void TypeResolver::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->arguments)
        TypeResolver::visitArgument(argument);
    TypeResolver::visitIdentifier(structCreateNode->startIdentifier);
    structCreateNode->type = structCreateNode->startIdentifier->type;
}

void TypeResolver::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    if (assignmentNode->isTypeResolved)
        return;

    TypeResolver::visitIdentifier(assignmentNode->startIdentifier);
    assignmentNode->type = assignmentNode->startIdentifier->type;

    TypeResolver::visitOperable(assignmentNode->expression, assignmentNode->type);
    assignmentNode->type = assignmentNode->expression->type;

    assignmentNode->isTypeResolved = true;
}

void TypeResolver::visitReturn(const std::shared_ptr<ReturnNode> &returnNode) {
    if (returnNode->isTypeResolved)
        return;

    auto function = returnNode->getParent<FunctionNode>();
    if (function == nullptr) {
        std::cout << "Return node is not inside function." << std::endl;
        exit(EXIT_FAILURE);
    }

    TypeResolver::visitOperable(returnNode->expression, function->type);
    returnNode->type = returnNode->expression->type;

    returnNode->isTypeResolved = true;
}

void TypeResolver::visitStruct(const std::shared_ptr<StructNode> &structNode) {
    if (structNode->isTypeResolved)
        return;

    structNode->type = std::make_shared<TypeNode>();
    structNode->type->targetStruct = structNode;

    for (const auto &variable : structNode->variables)
        TypeResolver::visitVariable(variable);

    structNode->isTypeResolved = true;
}

void TypeResolver::visitOperable(const std::shared_ptr<OperableNode> &operableNode,
                                 const std::shared_ptr<TypeNode> &targetType) {
    if(operableNode->isTypeResolved)
        return;

    TypeResolver::visitNode(operableNode);

    // TODO: Promote to target.

    operableNode->isTypeResolved = true;
}

void TypeResolver::visitType(const std::shared_ptr<TypeNode> &typeNode) {
    if (typeNode->typeToken == TOKEN_TYPE &&
        (std::strncmp(typeNode->typeToken->content.c_str(), "i", 1) == 0 ||
         std::strncmp(typeNode->typeToken->content.c_str(), "u", 1) == 0)) {
        auto bits = std::stoi(typeNode->typeToken->content.substr(1));
        auto isSigned = std::strncmp(typeNode->typeToken->content.c_str(), "i", 1) == 0;
        typeNode->isSigned = isSigned;
        typeNode->isFloating = false;
        typeNode->bits = bits;
    } else if (typeNode->typeToken == "bool") {
        typeNode->isFloating = false;
        typeNode->isSigned = true;
        typeNode->bits = 1;
    } else if (typeNode->typeToken == "float") {
        typeNode->isFloating = true;
        typeNode->isSigned = false;
        typeNode->bits = 32;
    } else if (typeNode->typeToken == "double") {
        typeNode->isFloating = true;
        typeNode->isSigned = false;
        typeNode->bits = 64;
    } else if (typeNode->typeToken == "void") {
        typeNode->isFloating = false;
        typeNode->isSigned = false;
        typeNode->bits = 0;
    } else if (typeNode->typeToken == TOKEN_IDENTIFIER) {
        auto root = typeNode->getParent<RootNode>();
        if (root == nullptr) {
            std::cout << "Root cannot be null." << std::endl;
            exit(EXIT_FAILURE);
        }

        auto scopeCheck = [typeNode](const std::shared_ptr<ASTNode> &node) {
            if (node->kind != AST_STRUCT)
                return false;

            auto structNode = std::static_pointer_cast<StructNode>(node);
            return strcmp(structNode->name->content.c_str(),
                          typeNode->typeToken->content.c_str()) == 0;
        };

        auto structNodes = root->scope->scope(typeNode->typeToken->content, scopeCheck);
        if (structNodes != nullptr)
            typeNode->targetStruct = std::static_pointer_cast<StructNode>(structNodes->at(0));
        else {
            for (const auto &node : root->nodes) {
                if (node->kind != AST_IMPORT)
                    continue;

                auto import = std::static_pointer_cast<ImportNode>(node);
                structNodes = import->target->scope->scope(typeNode->typeToken->content,
                                                           scopeCheck);

                if (structNodes != nullptr && !structNodes->empty()) {
                    typeNode->targetStruct = std::static_pointer_cast<StructNode>(
                            structNodes->at(0));
                    break;
                }
            }
        }

        if (typeNode->targetStruct == nullptr) {
            std::cout << "Couldn't find the struct for the searched identifier." << std::endl;
            exit(EXIT_FAILURE);
        }

        TypeResolver::visitStruct(typeNode->targetStruct);
    }
}