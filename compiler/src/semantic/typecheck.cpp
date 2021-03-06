//
// Created by timo on 8/10/20.
//

#include "../../include/semantic/typecheck.h"

#include <fmt/core.h>

#include "../../include/parser/symboltable.h"
#include "../../include/parser/astnodes.h"
#include "../../include/compiler/error.h"
#include "../../include/lexer/token.h"
#include "../../include/utils/utils.h"

void TypeCheck::visit(const SharedASTNode &node) {
    if (node->getKind() == ASTNode::ROOT) {
        TypeCheck::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        TypeCheck::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        TypeCheck::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (auto identifierNode = std::dynamic_pointer_cast<IdentifierNode>(node)) {
        auto firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier())
            firstIdentifier = firstIdentifier->getLastIdentifier();

        return TypeCheck::visit(firstIdentifier);
    } else if (node->getKind() == ASTNode::BLOCK) {
        TypeCheck::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        TypeCheck::visit(std::static_pointer_cast<FunctionNode>(node));
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
    } else if (node->getKind() == ASTNode::PARAMETER) {
        TypeCheck::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->getKind() != ASTNode::IMPORT &&
               node->getKind() != ASTNode::STRING && node->getKind() != ASTNode::NUMBER &&
               node->getKind() != ASTNode::TYPE) {
        throwNode(Error::ERROR, node, "TypeCheck: Unsupported node: " + node->getKindAsString());
        exit(EXIT_FAILURE);
    }
}

void TypeCheck::visit(const SharedRootNode &rootNode) {
    for (const auto &node : rootNode->getNodes())
        TypeCheck::visit(node);
}

void TypeCheck::visit(const SharedStructNode &structNode) {
    for (const auto &variable : structNode->getVariables())
        TypeCheck::visit(variable);
}

void TypeCheck::visit(const SharedVariableNode &variableNode) {
    if (variableNode->getType()->isVoid()) {
        throwNode(Error::ERROR, variableNode, "You can't declare a variable with void as a type.");
        return;
    }

    if (!variableNode->getExpression() && variableNode->isConstant()) {
        throwNode(Error::ERROR, variableNode, "Constant variables need an expression.");
        return;
    }

    if (!variableNode->getExpression() && !variableNode->getType()) {
        throwNode(Error::ERROR, variableNode, "There must be specified a return type if no "
                                              "expression exists.");
        return;
    }

    if (!variableNode->getExpression())
        return;

    TypeCheck::visit(variableNode->getExpression());

    if (*variableNode->getExpression()->getType() != *variableNode->getType()) {
        throwNode(Error::ERROR, variableNode, "The expression type doesn't match that of the "
                                              "variable.");
        return;
    }
}

void TypeCheck::visit(const SharedFunctionNode &functionNode) {
    for (auto const &parameter : functionNode->getParameters())
        TypeCheck::visit(parameter);

    if (functionNode->getBlock())
        TypeCheck::visit(functionNode->getBlock());
}

void TypeCheck::visit(const SharedBlockNode &blockNode) {
    for (const auto &node : blockNode->getNodes())
        TypeCheck::visit(node);
}

void TypeCheck::visit(const SharedIdentifierNode &identifierNode) {
    if (identifierNode->getKind() == ASTNode::FUNCTION_CALL)
        TypeCheck::visit(std::static_pointer_cast<FunctionCallNode>(identifierNode));

    if (identifierNode->getType()->getPointerLevel() > 0
        && !identifierNode->isDereference() && identifierNode->getNextIdentifier()) {
        throwNode(Error::ERROR, identifierNode, "You can't access data from a not dereferenced "
                                                "variable.");
        return;
    }

    if (identifierNode->getNextIdentifier())
        TypeCheck::visit(identifierNode->getNextIdentifier());
}

void TypeCheck::visit(const SharedFunctionCallNode &functionCallNode) {
    for (auto const &argument : functionCallNode->getArguments())
        TypeCheck::visit(argument);
}

void TypeCheck::visit(const SharedFunctionArgumentNode &functionArgumentNode) {
    TypeCheck::visit(functionArgumentNode->getExpression());
}

void TypeCheck::visit(const SharedStructArgumentNode &structArgumentNode) {
    if (structArgumentNode->getExpression())
        TypeCheck::visit(structArgumentNode->getExpression());
}

void TypeCheck::visit(const SharedReturnNode &returnNode) {
    auto functionNode = returnNode->findNodeOfParents<FunctionNode>();
    if (!returnNode->getExpression() && functionNode->getType()->getBits() != 0) {
        throwNode(Error::ERROR, returnNode, "You can't return void on a non-void function.");
        return;
    }

    if (!returnNode->getExpression())
        return;

    if (*returnNode->getExpression()->getType() != *functionNode->getType()) {
        throwNode(Error::ERROR, returnNode, "The return statement uses a different type than the "
                                            "function.");
        return;
    }

    TypeCheck::visit(returnNode->getExpression());
}

void TypeCheck::visit(const SharedAssignmentNode &assignmentNode) {
    if (*assignmentNode->getType() != *assignmentNode->getExpression()->getType()) {
        throwNode(Error::ERROR, assignmentNode, "The assignment expression uses a different type"
                                                " than the variable.");
        return;
    }

    auto checkIdentifier = assignmentNode->getStartIdentifier();
    while (checkIdentifier->getNextIdentifier()) {
        if (checkIdentifier->getTargetNode()->getKind() == ASTNode::VARIABLE) {
            auto variableNode = std::static_pointer_cast<VariableNode>(
                    checkIdentifier->getTargetNode());
            if (variableNode->isConstant()) {
                throwNode(Error::ERROR, checkIdentifier, "Constant variables can't be reassigned.");
                return;
            }
        }

        checkIdentifier = checkIdentifier->getNextIdentifier();
    }

    TypeCheck::visit(assignmentNode->getExpression());
}

void TypeCheck::visit(const SharedStructCreateNode &structCreateNode) {
    if (!structCreateNode->getType()->getTargetStruct()) {
        throwNode(Error::ERROR, structCreateNode, "Struct creation has no target struct.");
        return;
    }

    auto targetStruct = structCreateNode->getType()->getTargetStruct();
    for (const auto &argument : structCreateNode->getArguments()) {
        TypeCheck::visit(argument);

        auto variableIndex = utils::indexOf(structCreateNode->getArguments(), argument).second;
        auto variable = targetStruct->getVariables().at(variableIndex);
        if (*argument->getType() != *variable->getType()) {
            throwNode(Error::ERROR, argument, "The struct create argument uses a different type "
                                              "than the variable.");
            return;
        }

        if (variable->isConstant()) {
            throwNode(Error::ERROR, argument, "Constant variables can't be reassigned.");
            return;
        }
    }
}

void TypeCheck::visit(const SharedBinaryNode &binaryNode) {
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
                throwNode(Error::ERROR, binaryNode->getLHS(), "Left side of the binary expression "
                                                              "is not numeric.");
            if (!binaryNode->getRHS()->getType()->isNumeric() ||
                binaryNode->getRHS()->getType()->getPointerLevel() != 0)
                throwNode(Error::ERROR, binaryNode->getRHS(), "Right side of the binary expression "
                                                              "is not numeric.");
            break;

        case BinaryNode::BIT_CAST:
            break;

        default:
            std::cout << "TypeCheck: Binary operator not supported." << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeCheck::visit(const SharedUnaryNode &unaryNode) {
    TypeCheck::visit(unaryNode->getExpression());

    switch (unaryNode->getOperatorKind()) {
        case UnaryNode::NEGATE:
            if (!unaryNode->getExpression()->getType()->isNumeric() ||
                unaryNode->getExpression()->getType()->getPointerLevel() != 0)
                throwNode(Error::ERROR, unaryNode->getExpression(), "Unary expression is not "
                                                                    "numeric.");
            break;

        default:
            std::cout << "TypeCheck: Unary operator not supported." << std::endl;
            exit(EXIT_FAILURE);
    }
}

void TypeCheck::visit(const SharedParenthesizedNode &parenthesizedNode) {
    TypeCheck::visit(parenthesizedNode->getExpression());
}

void TypeCheck::visit(const SharedParameterNode &parameterNode) {
    if (parameterNode->getType()->isVoid()) {
        throwNode(Error::ERROR, parameterNode, "You can't declare a parameter with void "
                                               "as a type.");
        return;
    }
}

template<class... Args>
void TypeCheck::throwNode(unsigned int errorType, const SharedASTNode &node, Args... args) {
    std::cout << Error((Error::ErrorType) errorType,
                       node->findNodeOfParents<RootNode>()->getSourcePath(),
                       node->findNodeOfParents<RootNode>()->getSourceCode(),
                       node->getStartToken()->getLineNumber(),
                       node->getEndToken()->getLineNumber(),
                       node->getStartToken()->getStartChar(),
                       node->getEndToken()->getEndChar(),
                       fmt::format(args...));
}