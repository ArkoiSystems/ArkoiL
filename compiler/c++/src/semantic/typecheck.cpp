//
// Created by timo on 8/10/20.
//

#include "typecheck.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"

void TypeCheck::visitNode(const std::shared_ptr<ASTNode> &node) {
    switch (node->kind) {
        case AST_ROOT:
            TypeCheck::visitRoot(std::static_pointer_cast<RootNode>(node));
            break;
        case AST_STRUCT:
            TypeCheck::visitStruct(std::static_pointer_cast<StructNode>(node));
            break;
        case AST_VARIABLE:
            TypeCheck::visitVariable(std::static_pointer_cast<VariableNode>(node));
            break;
        case AST_FUNCTION:
            TypeCheck::visitFunction(std::static_pointer_cast<FunctionNode>(node));
            break;
        case AST_BLOCK:
            TypeCheck::visitBlock(std::static_pointer_cast<BlockNode>(node));
            break;
        case AST_FUNCTION_CALL:
            TypeCheck::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
            break;
        case AST_ARGUMENT:
            TypeCheck::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
            break;
        case AST_RETURN:
            TypeCheck::visitReturn(std::static_pointer_cast<ReturnNode>(node));
            break;
        case AST_ASSIGNMENT:
            TypeCheck::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
            break;
        case AST_STRUCT_CREATE:
            TypeCheck::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
            break;
        case AST_IDENTIFIER:
        case AST_IMPORT:
        case AST_STRING:
        case AST_NUMBER:
            break;
        default:
            std::cout << "Unsupported node. " << node->kind << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeCheck::visitRoot(const std::shared_ptr<RootNode> &root) {
    for (const auto &node : root->nodes)
        TypeCheck::visitNode(node);
}

void TypeCheck::visitStruct(const std::shared_ptr<StructNode> &structNode) {
    for (const auto &variable : structNode->variables)
        TypeCheck::visitVariable(variable);
}

void TypeCheck::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
    if (variableNode->expression == nullptr && variableNode->isConstant) {
        THROW_NODE_ERROR(variableNode, "Constant variables need an expression.")
        return;
    }

    if (variableNode->expression == nullptr && variableNode->type == nullptr) {
        THROW_NODE_ERROR(variableNode,
                         "There must be specified a return type if no expression exists.")
        return;
    }

    if (variableNode->expression == nullptr)
        return;

    TypeCheck::visitNode(variableNode->expression);

    if (*variableNode->expression->type != *variableNode->type) {
        THROW_NODE_ERROR(variableNode, "The expression type doesn't match that of the variable.")
        return;
    }
}

void TypeCheck::visitFunction(const std::shared_ptr<FunctionNode> &functionNode) {
    if (functionNode->block != nullptr)
        TypeCheck::visitBlock(functionNode->block);
}

void TypeCheck::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {
    for (const auto &node : blockNode->nodes)
        TypeCheck::visitNode(node);
}

void TypeCheck::visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (auto const &argument : functionCallNode->arguments)
        TypeCheck::visitArgument(argument);
}

void TypeCheck::visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
    TypeCheck::visitNode(argumentNode->expression);
}

void TypeCheck::visitReturn(const std::shared_ptr<ReturnNode> &returnNode) {
    auto functionNode = returnNode->getParent<FunctionNode>();

    if (returnNode->expression == nullptr && functionNode->type->bits != 0) {
        THROW_NODE_ERROR(returnNode, "You can't return void on a non-void function.")
        return;
    }

    TypeCheck::visitNode(returnNode->expression);
}

void TypeCheck::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    std::cout << assignmentNode->type << std::endl;
    std::cout << assignmentNode->expression->type << std::endl;

    if (*assignmentNode->type != *assignmentNode->expression->type) {
        THROW_NODE_ERROR(assignmentNode,
                         "The assignment expression uses a different type than the variable.")
        return;
    }

    TypeCheck::visitNode(assignmentNode->expression);
}

void TypeCheck::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    if (structCreateNode->type->targetStruct == nullptr) {
        THROW_NODE_ERROR(structCreateNode, "Struct creation has no target struct.")
        return;
    }

    for (const auto &argument : structCreateNode->arguments) {
        visitArgument(argument);

        for (const auto &variable : structCreateNode->type->targetStruct->variables) {

        }
    }
}
