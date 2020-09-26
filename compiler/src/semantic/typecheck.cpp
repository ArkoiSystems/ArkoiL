//
// Created by timo on 8/10/20.
//

#include "typecheck.h"

#include "../parser/symboltable.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"
#include "../lexer/token.h"
#include "../utils.h"

void TypeCheck::visit(const std::shared_ptr<ASTNode> &node) {
    if (node->getKind() == ASTNode::ROOT) {
        TypeCheck::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        TypeCheck::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        TypeCheck::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        TypeCheck::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::BLOCK) {
        TypeCheck::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_CALL) {
        TypeCheck::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        TypeCheck::visit(std::static_pointer_cast<FunctionArgumentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        TypeCheck::visit(std::static_pointer_cast<StructArgumentNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        TypeCheck::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        TypeCheck::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        TypeCheck::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        TypeCheck::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        TypeCheck::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        TypeCheck::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->getKind() != ASTNode::IDENTIFIER && node->getKind() != ASTNode::IMPORT &&
               node->getKind() != ASTNode::STRING && node->getKind() != ASTNode::NUMBER &&
               node->getKind() != ASTNode::TYPE && node->getKind() != ASTNode::PARAMETER) {
        THROW_NODE_ERROR(node, "TypeCheck: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }
}

void TypeCheck::visit(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->getNodes())
        TypeCheck::visit(node);
}

void TypeCheck::visit(const std::shared_ptr<StructNode> &structNode) {
    for (const auto &variable : structNode->getVariables())
        TypeCheck::visit(variable);
}

void TypeCheck::visit(const std::shared_ptr<VariableNode> &variableNode) {
    if (variableNode->getExpression() == nullptr && variableNode->isConstant()) {
        THROW_NODE_ERROR(variableNode, "Constant variables need an expression.")
        return;
    }

    if (variableNode->getExpression() == nullptr && variableNode->getType() == nullptr) {
        THROW_NODE_ERROR(variableNode,
                         "There must be specified a return type if no expression exists.")
        return;
    }

    if (variableNode->getExpression() == nullptr)
        return;

    TypeCheck::visit(variableNode->getExpression());

    if (*variableNode->getExpression()->getType() != *variableNode->getType()) {
        THROW_NODE_ERROR(variableNode, "The expression type doesn't match that of the variable.")
        return;
    }
}

void TypeCheck::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    if (functionNode->getBlock() != nullptr)
        TypeCheck::visit(functionNode->getBlock());
}

void TypeCheck::visit(const std::shared_ptr<BlockNode> &blockNode) {
    for (const auto &node : blockNode->getNodes())
        TypeCheck::visit(node);
}

void TypeCheck::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (auto const &argument : functionCallNode->getArguments())
        TypeCheck::visit(argument);
}

void TypeCheck::visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode) {
    TypeCheck::visit(functionArgumentNode->getExpression());
}

void TypeCheck::visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode) {
    if (structArgumentNode->getExpression() != nullptr)
        TypeCheck::visit(structArgumentNode->getExpression());
}

void TypeCheck::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    auto functionNode = returnNode->findNodeOfParents<FunctionNode>();

    if (returnNode->getExpression() == nullptr && functionNode->getType()->getBits() != 0) {
        THROW_NODE_ERROR(returnNode, "You can't return void on a non-void function.")
        return;
    }

    if (*returnNode->getExpression()->getType() != *functionNode->getType()) {
        THROW_NODE_ERROR(returnNode, "The return statement uses a different type than the function.")
        return;
    }

    TypeCheck::visit(returnNode->getExpression());
}

void TypeCheck::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    if (*assignmentNode->getType() != *assignmentNode->getExpression()->getType()) {
        THROW_NODE_ERROR(assignmentNode, "The assignment expression uses a different type than the variable.")
        return;
    }

    if (assignmentNode->getTargetNode()->getKind() == ASTNode::VARIABLE) {
        auto variableNode = std::static_pointer_cast<VariableNode>(assignmentNode->getTargetNode());
        if (variableNode->isConstant()) {
            THROW_NODE_ERROR(assignmentNode, "Constant variables can't be reassigned.")
            return;
        }
    }

    TypeCheck::visit(assignmentNode->getExpression());
}

void TypeCheck::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    if (structCreateNode->getType()->getTargetStruct() == nullptr) {
        THROW_NODE_ERROR(structCreateNode, "Struct creation has no target struct.")
        return;
    }

    auto targetStruct = structCreateNode->getType()->getTargetStruct();
    for (const auto &argument : structCreateNode->getArguments()) {
        TypeCheck::visit(argument);

        auto variable = targetStruct->getVariables().at(Utils::indexOf(structCreateNode->getArguments(),
                                                                       argument).second);
        if (*argument->getType() != *variable->getType()) {
            THROW_NODE_ERROR(argument, "The struct create argument uses a different type than the variable.")
            return;
        }

        if (variable->isConstant()) {
            THROW_NODE_ERROR(argument, "Constant variables can't be reassigned.")
            return;
        }
    }
}

void TypeCheck::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    TypeCheck::visit(binaryNode->getLHS());
    TypeCheck::visit(binaryNode->getRHS());

    switch (binaryNode->getOperatorKind()) {
        case BinaryNode::LESS_EQUAL_THAN:
        case BinaryNode::LESS_THAN:
        case BinaryNode::GREATER_EQUAL_THAN:
        case BinaryNode::GREATER_THAN:
        case BinaryNode::EQUAL:
        case BinaryNode::NOT_EQUAL:

        case BinaryNode::ADDITION:
        case BinaryNode::MULTIPLICATION:
        case BinaryNode::SUBTRACTION:
        case BinaryNode::DIVISION:
        case BinaryNode::REMAINING:
            if (!binaryNode->getLHS()->getType()->isNumeric() ||
                binaryNode->getLHS()->getType()->getPointerLevel() != 0)
                THROW_NODE_ERROR(binaryNode->getLHS(), "Left side of the binary expression is not numeric.")
            if (!binaryNode->getRHS()->getType()->isNumeric() ||
                binaryNode->getRHS()->getType()->getPointerLevel() != 0)
                THROW_NODE_ERROR(binaryNode->getRHS(), "Right side of the binary expression is not numeric.")
            break;

        case BinaryNode::BIT_CAST:
            break;

        default:
            std::cout << "TypeCheck: Binary operator not supported." << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeCheck::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    TypeCheck::visit(unaryNode->getExpression());

    switch (unaryNode->getOperatorKind()) {
        case UnaryNode::NEGATE:
            if (!unaryNode->getExpression()->getType()->isNumeric() ||
                unaryNode->getExpression()->getType()->getPointerLevel() != 0)
                THROW_NODE_ERROR(unaryNode->getExpression(), "Unary expression is not numeric.")
                break;

        default:
            std::cout << "TypeCheck: Unary operator not supported." << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeCheck::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    TypeCheck::visit(parenthesizedNode->getExpression());
}