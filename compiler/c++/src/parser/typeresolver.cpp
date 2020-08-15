//
// Created by timo on 8/3/20.
//

#include "typeresolver.h"
#include "astnodes.h"
#include "../compiler/error.h"

void TypeResolver::visitNode(const std::shared_ptr<ASTNode> &node) {
    if (node->kind == AST_TYPE) {
        TypeResolver::visitType(std::static_pointer_cast<TypeNode>(node));
    } else if (node->kind == AST_STRUCT) {
        TypeResolver::visitStruct(std::static_pointer_cast<StructNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        TypeResolver::visitFunction(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == AST_FUNCTION_CALL) {
        TypeResolver::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == AST_STRUCT_CREATE) {
        TypeResolver::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind == AST_IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->lastIdentifier != nullptr)
            firstIdentifier = firstIdentifier->lastIdentifier;

        TypeResolver::visitIdentifier(firstIdentifier);
    } else if (node->kind == AST_NUMBER) {
        TypeResolver::visitNumber(std::static_pointer_cast<NumberNode>(node));
    } else if (node->kind == AST_PARAMETER) {
        TypeResolver::visitParameter(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->kind == AST_BLOCK) {
        TypeResolver::visitBlock(std::static_pointer_cast<BlockNode>(node));
    } else if (node->kind == AST_STRING) {
        TypeResolver::visitString(std::static_pointer_cast<StringNode>(node));
    } else if (node->kind == AST_ROOT) {
        TypeResolver::visitRoot(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == AST_VARIABLE) {
        TypeResolver::visitVariable(std::static_pointer_cast<VariableNode>(node));
    } else if (node->kind == AST_RETURN) {
        TypeResolver::visitReturn(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == AST_ARGUMENT) {
        TypeResolver::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == AST_ASSIGNMENT) {
        TypeResolver::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == AST_BINARY) {
        TypeResolver::visitBinary(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == AST_UNARY) {
        TypeResolver::visitUnary(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == AST_PARENTHESIZED) {
        TypeResolver::visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind != AST_IMPORT) {
        std::cout << "TypeResolver: Unsupported node. " << node->kind << std::endl;
        exit(EXIT_FAILURE);
    }
}

void TypeResolver::visitRoot(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->nodes)
        TypeResolver::visitNode(node);
}

void TypeResolver::visitFunction(const std::shared_ptr<FunctionNode> &functionNode) {
    if(functionNode->isTypeResolved)
        return;

    TypeResolver::visitType(functionNode->type);

    for (const auto &parameter : functionNode->parameters)
        TypeResolver::visitParameter(parameter);

    if (!functionNode->isNative && !functionNode->isBuiltin)
        TypeResolver::visitBlock(functionNode->block);

    functionNode->isTypeResolved = true;
}

void TypeResolver::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {
    for (const auto &node : blockNode->nodes)
        TypeResolver::visitNode(node);
}

void TypeResolver::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
    if (variableNode->isTypeResolved)
        return;

    variableNode->isLocal = variableNode->getParent<BlockNode>() != nullptr;

    if (variableNode->type != nullptr)
        TypeResolver::visitType(variableNode->type);

    if (variableNode->expression != nullptr) {
        TypeResolver::visitOperable(variableNode->expression, variableNode->type);

        if (variableNode->type == nullptr)
            variableNode->type = variableNode->expression->type;
    }

    variableNode->isTypeResolved = true;
}

void TypeResolver::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
    if (binaryNode->isTypeResolved)
        return;

    TypeResolver::visitOperable(binaryNode->lhs);
    TypeResolver::visitOperable(binaryNode->rhs, binaryNode->lhs->type);
    binaryNode->type = binaryNode->lhs->type;

    binaryNode->isTypeResolved = true;
}

void TypeResolver::visitUnary(const std::shared_ptr<UnaryNode> &unaryNode) {
    if(unaryNode->isTypeResolved)
        return;

    TypeResolver::visitOperable(unaryNode->operable);
    unaryNode->type = unaryNode->operable->type;

    unaryNode->isTypeResolved = true;
}

void TypeResolver::visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    if(parenthesizedNode->isTypeResolved)
        return;

    TypeResolver::visitOperable(parenthesizedNode->expression);
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

        if (node->kind != AST_VARIABLE && node->kind != AST_PARAMETER &&
            node->kind != AST_FUNCTION && node->kind != AST_STRUCT)
            return false;

        TypeResolver::visitNode(node);

        if (identifierNode->kind != AST_FUNCTION_CALL)
            return true;

        auto functionCall = std::static_pointer_cast<FunctionCallNode>(identifierNode);
        auto function = std::static_pointer_cast<FunctionNode>(node);

        if (!function->isVariadic &&
            (function->parameters.size() != functionCall->arguments.size()))
            return false;

        auto sortedArguments(functionCall->arguments);
        if (!functionCall->getSortedArguments(function, sortedArguments))
            return false;

        for (auto index = 0; index < functionCall->arguments.size(); index++) {
            if (index >= function->parameters.size())
                break;

            auto argument = sortedArguments.at(index);
            TypeResolver::visitArgument(argument);
            auto parameter = function->parameters.at(index);
            TypeResolver::visitParameter(parameter);

            if (*parameter->type != *argument->type)
                return false;
        }

        return true;
    };

    auto nodes = identifierNode->scope->all(identifierNode->identifier->content, scopeCheck);
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

    auto targetNode = nodes->at(0);
    TypeResolver::visitNode(targetNode);

    auto typedNode = std::static_pointer_cast<TypedNode>(targetNode);
    if (typedNode->type == nullptr) {
        THROW_NODE_ERROR(typedNode, "The found identifier has no type.")
        return;
    }

    identifierNode->targetNode = targetNode;
    identifierNode->type = std::make_shared<TypeNode>(*typedNode->type);

    if (identifierNode->isDereference && identifierNode->type->pointerLevel <= 0) {
        THROW_NODE_ERROR(identifierNode, "Can't dereference a non-pointer type.")
        return;
    }

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

    std::shared_ptr<IdentifierNode> firstIdentifier = functionCallNode;
    while (firstIdentifier->lastIdentifier != nullptr)
        firstIdentifier = firstIdentifier->lastIdentifier;
    TypeResolver::visitIdentifier(firstIdentifier);

    functionCallNode->isTypeResolved = true;
}

void TypeResolver::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->arguments)
        TypeResolver::visitArgument(argument);

    TypeResolver::visitIdentifier(structCreateNode->startIdentifier);

    structCreateNode->targetNode = structCreateNode->endIdentifier->targetNode;
    structCreateNode->type = structCreateNode->endIdentifier->type;
}

void TypeResolver::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    if (assignmentNode->isTypeResolved)
        return;

    TypeResolver::visitIdentifier(assignmentNode->startIdentifier);

    assignmentNode->targetNode = assignmentNode->endIdentifier->targetNode;
    assignmentNode->type = assignmentNode->endIdentifier->type;

    TypeResolver::visitOperable(assignmentNode->expression, assignmentNode->type);

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

    if(returnNode->expression != nullptr) {
        TypeResolver::visitOperable(returnNode->expression, function->type);
        returnNode->type = returnNode->expression->type;
    }

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
        auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
            return node->kind == AST_STRUCT;
        };

        auto rootNode = typeNode->getParent<RootNode>();
        auto foundNodes = rootNode->searchWithImports(typeNode->typeToken->content, scopeCheck);

        if (foundNodes.empty()) {
            std::cout << "Couldn't find the struct for the searched identifier." << std::endl;
            exit(EXIT_FAILURE);
        }

        typeNode->targetStruct = std::static_pointer_cast<StructNode>(foundNodes.at(0));
        TypeResolver::visitStruct(typeNode->targetStruct);
    }
}