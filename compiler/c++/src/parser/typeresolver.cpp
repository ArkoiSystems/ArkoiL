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
        } else return false;

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

                if (argument->type == nullptr || parameter->type == nullptr)
                    return false;

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

    auto typedNode = std::static_pointer_cast<TypedNode>(nodes->at(0));
    if (typedNode->type == nullptr) {
        THROW_NODE_ERROR(typedNode, "The found identifier has no type.")
        return;
    }

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

    std::shared_ptr<IdentifierNode> firstIdentifier = structCreateNode->endIdentifier;
    while (firstIdentifier->lastIdentifier != nullptr)
        firstIdentifier = firstIdentifier->lastIdentifier;
    TypeResolver::visitIdentifier(firstIdentifier);

    structCreateNode->type = structCreateNode->endIdentifier->type;
}

void TypeResolver::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    if (assignmentNode->isTypeResolved)
        return;

    std::shared_ptr<IdentifierNode> firstIdentifier = assignmentNode->endIdentifier;
    while (firstIdentifier->lastIdentifier != nullptr)
        firstIdentifier = firstIdentifier->lastIdentifier;
    TypeResolver::visitIdentifier(firstIdentifier);

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