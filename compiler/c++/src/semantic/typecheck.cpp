//
// Created by timo on 8/10/20.
//

#include "typecheck.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"

void TypeCheck::visitNode(const std::shared_ptr<ASTNode> &node) {
    if (node->kind == AST_ROOT) {
        TypeCheck::visitRoot(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == AST_STRUCT) {
        TypeCheck::visitStruct(std::static_pointer_cast<StructNode>(node));
    } else if (node->kind == AST_VARIABLE) {
        TypeCheck::visitVariable(std::static_pointer_cast<VariableNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        TypeCheck::visitFunction(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == AST_BLOCK) {
        TypeCheck::visitBlock(std::static_pointer_cast<BlockNode>(node));
    } else if (node->kind == AST_FUNCTION_CALL) {
        TypeCheck::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == AST_ARGUMENT) {
        TypeCheck::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == AST_RETURN) {
        TypeCheck::visitReturn(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == AST_ASSIGNMENT) {
        TypeCheck::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == AST_STRUCT_CREATE) {
        TypeCheck::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind == AST_BINARY) {
        TypeCheck::visitBinary(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == AST_UNARY) {
        TypeCheck::visitUnary(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == AST_PARENTHESIZED) {
        TypeCheck::visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind != AST_IDENTIFIER && node->kind != AST_IMPORT &&
               node->kind != AST_STRING && node->kind != AST_NUMBER &&
               node->kind != AST_TYPE && node->kind != AST_PARAMETER) {
        std::cout << "TypeCheck: Unsupported node. " << node->kind << std::endl;
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
        TypeCheck::visitArgument(argument);

        std::shared_ptr<VariableNode> foundVariable;
        for (const auto &variable : structCreateNode->type->targetStruct->variables) {
            if (strcmp(variable->name->content.c_str(), argument->name->content.c_str()) == 0) {
                foundVariable = variable;
                break;
            }
        }

        if (foundVariable == nullptr) {
            THROW_NODE_ERROR(argument, "Struct creation has an unknown argument.")
            return;
        }

        if (*argument->type != *foundVariable->type) {
            THROW_NODE_ERROR(argument,
                             "The struct create argument uses a different type than the variable.")
            return;
        }
    }
}

void TypeCheck::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
    visitNode(binaryNode->lhs);
    visitNode(binaryNode->rhs);

    switch (binaryNode->operatorKind) {
        case LESS_EQUAL_THAN:
        case LESS_THAN:
        case GREATER_EQUAL_THAN:
        case GREATER_THAN:
        case EQUAL:
        case NOT_EQUAL:

        case ADDITION:
        case MULTIPLICATION:
        case SUBTRACTION:
        case DIVISION:
        case REMAINING:
            if (!binaryNode->lhs->type->isNumeric())
                THROW_NODE_ERROR(binaryNode->lhs,
                                 "Left side of the binary expression is not numeric.");
            if (!binaryNode->rhs->type->isNumeric())
                THROW_NODE_ERROR(binaryNode->rhs,
                                 "Right side of the binary expression is not numeric.");
            break;
        default:
            std::cout << "TypeCheck: Binary operator not supported." << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeCheck::visitUnary(const std::shared_ptr<UnaryNode>& unaryNode) {
    // TODO: Make later checks.
    TypeCheck::visitNode(unaryNode->operable);
}

void TypeCheck::visitParenthesized(const std::shared_ptr<ParenthesizedNode>& parenthesizedNode) {
    TypeCheck::visitNode(parenthesizedNode->expression);
}
