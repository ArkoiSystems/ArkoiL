//
// Created by timo on 8/10/20.
//

#include "scopecheck.h"

#include "../parser/symboltable.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "../utils.h"

void ScopeCheck::visit(const std::shared_ptr<ASTNode> &node) {
    if (node->getKind() == ASTNode::ROOT) {
        ScopeCheck::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::IMPORT) {
        ScopeCheck::visit(std::static_pointer_cast<ImportNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::PARAMETER) {
        ScopeCheck::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        ScopeCheck::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        ScopeCheck::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        ScopeCheck::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        ScopeCheck::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        ScopeCheck::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() == ASTNode::ARGUMENT) {
        ScopeCheck::visit(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_CALL) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        ScopeCheck::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        ScopeCheck::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        ScopeCheck::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() != ASTNode::TYPE && node->getKind() != ASTNode::NUMBER &&
               node->getKind() != ASTNode::STRING && node->getKind() != ASTNode::IDENTIFIER) {
        std::cout << "ScopeCheck: Unsupported node. " << node->getKind() << std::endl;
        exit(EXIT_FAILURE);
    }
}

void ScopeCheck::visit(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->getNodes())
        ScopeCheck::visit(node);
}

void ScopeCheck::visit(const std::shared_ptr<ImportNode> &importNode) {
    auto rootNode = importNode->getParentNode<RootNode>();
    for (const auto &node : rootNode->getNodes()) {
        if (node->getKind() != ASTNode::IMPORT || node == importNode)
            continue;

        auto rootImport = std::static_pointer_cast<ImportNode>(node);
        if (rootImport->getPath()->getContent() == importNode->getPath()->getContent()) {
            THROW_NODE_ERROR(importNode, "There is already another import with the same path.")
            return;
        }
    }
}

void ScopeCheck::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    if (functionNode->getBlock() != nullptr) {
        ScopeCheck::visit(functionNode->getBlock());

        std::vector<std::shared_ptr<ASTNode>> returns;
        for (const auto &node : functionNode->getBlock()->getNodes()) {
            if (node->getKind() != ASTNode::RETURN)
                continue;
            returns.push_back(node);
        }

        if (returns.empty() && functionNode->getType()->getBits() != 0) {
            THROW_NODE_ERROR(functionNode,
                             "Non-void functions need to have at least one return statement.")
            return;
        }
    }

    auto scopeCheck = [functionNode](const std::shared_ptr<ASTNode> &node) {
        if (node->getKind() != ASTNode::FUNCTION)
            return false;

        auto foundFunction = std::static_pointer_cast<FunctionNode>(node);
        return *foundFunction == *functionNode;
    };

    auto rootNode = functionNode->getParentNode<RootNode>();
    auto foundNodes = rootNode->searchWithImports(functionNode->getName()->getContent(), scopeCheck);

    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(functionNode, "There already exists a similar function.")
        return;
    }

    for (const auto &parameter : functionNode->getParameters())
        ScopeCheck::visit(parameter);
}

void ScopeCheck::visit(const std::shared_ptr<BlockNode> &blockNode) {
    std::vector<std::shared_ptr<ASTNode>> returns;
    for (const auto &node : blockNode->getNodes()) {
        if (node->getKind() != ASTNode::RETURN)
            continue;
        returns.push_back(node);
    }

    if (!returns.empty()) {
        auto targetReturn = returns.at(0);
        auto returnIndex = Utils::indexOf(blockNode->getNodes(), targetReturn);
        if (returnIndex.second != blockNode->getNodes().size() - 1) {
            THROW_NODE_ERROR(targetReturn, "Everything after a return statement is unreachable.")
            return;
        }
    }

    for (const auto &node : blockNode->getNodes())
        ScopeCheck::visit(node);
}

void ScopeCheck::visit(const std::shared_ptr<ParameterNode> &parameterNode) {
    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->getKind() == ASTNode::PARAMETER;
    };

    auto foundNodes = parameterNode->getScope()->scope(parameterNode->getName()->getContent(), scopeCheck);
    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(parameterNode, "There already exists a similar parameter.")
        return;
    }
}

void ScopeCheck::visit(const std::shared_ptr<VariableNode> &variableNode) {
    if (variableNode->getName() == "_") {
        if (variableNode->getExpression() != nullptr)
            ScopeCheck::visit(variableNode->getExpression());
        return;
    }

    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->getKind() == ASTNode::VARIABLE;
    };

    auto foundNodes = variableNode->getScope()->scope(variableNode->getName()->getContent(), scopeCheck);
    auto blockNode = variableNode->getParentNode<BlockNode>();
    if (foundNodes->empty() && blockNode != nullptr) {
        foundNodes = variableNode->getScope()->all(variableNode->getName()->getContent(), scopeCheck);
        if (foundNodes->size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    } else if (foundNodes->empty()) {
        auto rootNode = variableNode->getParentNode<RootNode>();
        foundNodes = rootNode->searchWithImports(variableNode->getName()->getContent(), scopeCheck);

        if (foundNodes->size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    }

    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
        return;
    }

    if (variableNode->getExpression() != nullptr)
        ScopeCheck::visit(variableNode->getExpression());
}

void ScopeCheck::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    ScopeCheck::visit(binaryNode->getLHS());
    ScopeCheck::visit(binaryNode->getRHS());
}

void ScopeCheck::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    ScopeCheck::visit(unaryNode->getExpression());
}

void ScopeCheck::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    ScopeCheck::visit(parenthesizedNode->getExpression());
}

void ScopeCheck::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->getArguments())
        ScopeCheck::visit(argument);
}

void ScopeCheck::visit(const std::shared_ptr<ArgumentNode> &argumentNode) {
    if (argumentNode->getName() == nullptr) {
        ScopeCheck::visit(argumentNode->getExpression());
        return;
    }

    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->getKind() == ASTNode::ARGUMENT;
    };

    auto foundNodes = argumentNode->getScope()->scope(argumentNode->getName()->getContent(), scopeCheck);
    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(argumentNode, "There already exists a similar argument.")
        return;
    }

    ScopeCheck::visit(argumentNode->getExpression());
}

void ScopeCheck::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (const auto &argument : functionCallNode->getArguments())
        ScopeCheck::visit(argument);
}

void ScopeCheck::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    ScopeCheck::visit(assignmentNode->getExpression());
}

void ScopeCheck::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    if (returnNode->getExpression() != nullptr)
        ScopeCheck::visit(returnNode->getExpression());
}

void ScopeCheck::visit(const std::shared_ptr<StructNode> &structNode) {
    for (const auto &variable : structNode->getVariables())
        ScopeCheck::visit(variable);
}
