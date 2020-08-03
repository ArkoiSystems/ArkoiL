#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-pragmas"
#pragma ide diagnostic ignored "readability-convert-member-functions-to-static"

//
// Created by timo on 8/3/20.
//

#include "typeresolver.h"
#include "../../deps/dbg-macro/dbg.h"

void TypeResolver::visitRoot(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->nodes) {
        switch (node->kind) {
            case AST_FUNCTION:
                visitFunction(std::dynamic_pointer_cast<FunctionNode>(node));
                break;
            case AST_VARIABLE:
                visitVariable(std::dynamic_pointer_cast<VariableNode>(node));
                break;
            case AST_STRUCT:
                visitStruct(std::dynamic_pointer_cast<StructNode>(node));
                break;
            default:
                break;
        }
    }
}

void TypeResolver::visitFunction(const std::shared_ptr<FunctionNode> &functionNode) {
    visitBlock(functionNode->block);
}

void TypeResolver::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {
    for (const auto &node : blockNode->nodes) {
        switch (node->kind) {
            case AST_VARIABLE:
                visitVariable(std::dynamic_pointer_cast<VariableNode>(node));
                break;
            case AST_FUNCTION_CALL:
                visitFunctionCall(std::dynamic_pointer_cast<FunctionCallNode>(node));
                break;
            case AST_IDENTIFIER:
                visitIdentifier(std::dynamic_pointer_cast<IdentifierNode>(node));
                break;
            case AST_RETURN:
                visitReturn(std::dynamic_pointer_cast<ReturnNode>(node));
                break;
            default:
                break;
        }
    }
}

void TypeResolver::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
    visitOperable(variableNode->expression);

    // TODO: Promote value.
    variableNode->type = variableNode->expression->type;
}

void TypeResolver::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
    visitOperable(binaryNode->lhs);
    visitOperable(binaryNode->rhs);

    // TODO: Promote values.
    binaryNode->type = binaryNode->lhs->type;
}

void TypeResolver::visitUnary(const std::shared_ptr<UnaryNode> &unaryNode) {
    visitOperable(unaryNode->operable);
    unaryNode->type = unaryNode->operable->type;
}

void TypeResolver::visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    visitOperable(parenthesizedNode->expression);
    parenthesizedNode->type = parenthesizedNode->expression->type;
}

void TypeResolver::visitNumber(const std::shared_ptr<NumberNode> &numberNode) {
    // TODO: Change for future number types.

    numberNode->type = std::make_shared<TypeNode>();
    numberNode->type->isFloating = numberNode->number->content.find('.') != std::string::npos;
    numberNode->type->bits = 32;

    if (!numberNode->type->isFloating)
        numberNode->type->isSigned = true;
}

void TypeResolver::visitString(const std::shared_ptr<StringNode> &stringNode) {
    stringNode->type = std::make_shared<TypeNode>();
    stringNode->type->isSigned = false;
    stringNode->type->pointerLevel = 1;
    stringNode->type->bits = 8;
}

void TypeResolver::visitIdentifier(const std::shared_ptr<IdentifierNode> &identifierNode) {
    // TODO: Search for the identifier.

    if (identifierNode->nextIdentifier != nullptr)
        visitOperable(identifierNode->nextIdentifier);
}

void TypeResolver::visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
    visitOperable(argumentNode->expression);
}

void TypeResolver::visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (const auto &argument : functionCallNode->arguments)
        visitArgument(argument);

    // TODO: Search for the function and set the type.
}

void TypeResolver::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->arguments)
        visitArgument(argument);

    // TODO: Search for the struct and set the type.
}

void TypeResolver::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    visitOperable(assignmentNode->expression);

    // TODO: Promote value.
    assignmentNode->type = assignmentNode->expression->type;
}

void TypeResolver::visitReturn(const std::shared_ptr<ReturnNode> &returnNode) {
    visitOperable(returnNode->expression);

    // TODO: Promote value.
    returnNode->type = returnNode->expression->type;
}

void TypeResolver::visitStruct(const std::shared_ptr<StructNode> &structNode) {
    for (const auto &variable : structNode->variables)
        visitVariable(variable);
}

void TypeResolver::visitOperable(const std::shared_ptr<OperableNode> &operableNode) {
    switch (operableNode->kind) {
        case AST_STRING:
            visitString(std::dynamic_pointer_cast<StringNode>(operableNode));
            break;
        case AST_ASSIGNMENT:
            visitAssignment(std::dynamic_pointer_cast<AssignmentNode>(operableNode));
            break;
        case AST_BINARY:
            visitBinary(std::dynamic_pointer_cast<BinaryNode>(operableNode));
            break;
        case AST_FUNCTION_CALL:
            visitFunctionCall(std::dynamic_pointer_cast<FunctionCallNode>(operableNode));
            break;
        case AST_IDENTIFIER:
            visitIdentifier(std::dynamic_pointer_cast<IdentifierNode>(operableNode));
            break;
        case AST_NUMBER:
            visitNumber(std::dynamic_pointer_cast<NumberNode>(operableNode));
            break;
        case AST_PARENTHESIZED:
            visitParenthesized(std::dynamic_pointer_cast<ParenthesizedNode>(operableNode));
            break;
        case AST_STRUCT_CREATE:
            visitStructCreate(std::dynamic_pointer_cast<StructCreateNode>(operableNode));
            break;
        case AST_UNARY:
            visitUnary(std::dynamic_pointer_cast<UnaryNode>(operableNode));
            break;
        default:
            break;
    }
}

#pragma clang diagnostic pop