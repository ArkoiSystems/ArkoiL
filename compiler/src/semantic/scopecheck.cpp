//
// Created by timo on 8/10/20.
//

#include "scopecheck.h"

#include "../parser/symboltable.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "../utils/utils.h"

void ScopeCheck::visit(const SharedASTNode &node) {
    if (node->getKind() == ASTNode::ROOT) {
        ScopeCheck::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::IMPORT) {
        ScopeCheck::visit(std::static_pointer_cast<ImportNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        ScopeCheck::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_CALL) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->getKind() == ASTNode::IDENTIFIER) {
        ScopeCheck::visit(std::static_pointer_cast<IdentifierNode>(node));
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
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionArgumentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        ScopeCheck::visit(std::static_pointer_cast<StructArgumentNode>(node));
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        ScopeCheck::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        ScopeCheck::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        ScopeCheck::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() != ASTNode::TYPE && node->getKind() != ASTNode::NUMBER &&
               node->getKind() != ASTNode::STRING) {
        THROW_NODE_ERROR(node, "ScopeCheck: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }
}

void ScopeCheck::visit(const SharedRootNode &rootNode) {
    for (const auto &node : rootNode->getNodes())
        ScopeCheck::visit(node);
}

void ScopeCheck::visit(const SharedImportNode &importNode) {
    auto rootNode = importNode->findNodeOfParents<RootNode>();
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

void ScopeCheck::visit(const SharedFunctionNode &functionNode) {
    if (functionNode->getBlock() != nullptr) {
        ScopeCheck::visit(functionNode->getBlock());

        std::vector<SharedASTNode> returns;
        for (const auto &node : functionNode->getBlock()->getNodes()) {
            if (node->getKind() != ASTNode::RETURN)
                continue;

            returns.emplace_back(node);
        }

        if (returns.empty() && functionNode->getType()->getBits() != 0) {
            THROW_NODE_ERROR(functionNode,
                             "Non-void functions need to have at least one return statement.")
            return;
        }
    }

    auto scopeCheck = [functionNode](const SharedASTNode &node) {
        if (node->getKind() != ASTNode::FUNCTION)
            return false;

        auto foundFunction = std::static_pointer_cast<FunctionNode>(node);
        return *foundFunction == *functionNode;
    };

    Symbols foundNodes;
    auto rootNode = functionNode->findNodeOfParents<RootNode>();
    rootNode->searchWithImports(foundNodes, functionNode->getName()->getContent(), scopeCheck);

    if (foundNodes.size() > 1) {
        THROW_NODE_ERROR(functionNode, "There already exists a similar function.")
        return;
    }

    for (const auto &parameter : functionNode->getParameters())
        ScopeCheck::visit(parameter);
}

void ScopeCheck::visit(const SharedBlockNode &blockNode) {
    std::vector<SharedASTNode> returns;
    for (const auto &node : blockNode->getNodes()) {
        if (node->getKind() != ASTNode::RETURN)
            continue;

        returns.emplace_back(node);
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

void ScopeCheck::visit(const SharedParameterNode &parameterNode) {
    auto scopeCheck = [](const SharedASTNode &node) {
        return node->getKind() == ASTNode::PARAMETER;
    };

    Symbols foundNodes;
    parameterNode->getScope()->scope(foundNodes, parameterNode->getName()->getContent(),
                                     scopeCheck);
    if (foundNodes.size() > 1) {
        THROW_NODE_ERROR(parameterNode, "There already exists a similar parameter.")
        return;
    }
}

void ScopeCheck::visit(const SharedVariableNode &variableNode) {
    if (variableNode->getName() == "_") {
        if (variableNode->getExpression() != nullptr)
            ScopeCheck::visit(variableNode->getExpression());
        return;
    }

    auto scopeCheck = [](const SharedASTNode &node) {
        return node->getKind() == ASTNode::VARIABLE;
    };

    Symbols foundNodes;
    variableNode->getScope()->scope(foundNodes, variableNode->getName()->getContent(), scopeCheck);

    auto blockNode = variableNode->findNodeOfParents<BlockNode>();
    if (foundNodes.empty() && blockNode != nullptr) {
        foundNodes.clear();
        variableNode->getScope()->all(foundNodes, variableNode->getName()->getContent(),
                                      scopeCheck);

        if (foundNodes.size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    } else if (foundNodes.empty()) {
        auto rootNode = variableNode->findNodeOfParents<RootNode>();

        foundNodes.clear();
        rootNode->searchWithImports(foundNodes, variableNode->getName()->getContent(), scopeCheck);

        if (foundNodes.size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    }

    if (foundNodes.size() > 1) {
        THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
        return;
    }

    if (variableNode->getExpression() != nullptr)
        ScopeCheck::visit(variableNode->getExpression());
}

void ScopeCheck::visit(const SharedIdentifierNode &identifierNode) {
    if (identifierNode->getTargetNode() == nullptr ||
        identifierNode->getTargetNode()->getKind() != ASTNode::VARIABLE)
        return;
    if (identifierNode->getLastIdentifier() != nullptr)
        return;

    auto targetVariable = std::static_pointer_cast<VariableNode>(identifierNode->getTargetNode());
    SharedVariableNode variableParent;

    if (auto argumentNode = identifierNode->findNodeOfParents<FunctionArgumentNode>()) {
        variableParent = std::static_pointer_cast<VariableNode>(argumentNode->getTargetNode());
    } else if (auto variableNode = identifierNode->findNodeOfParents<VariableNode>())
        variableParent = variableNode;

    if (variableParent == nullptr)
        return;
    if (targetVariable->isGlobal())
        return;

    int variableParentIndex;
    int targetVariableIndex;
    if (auto structNode = variableParent->findNodeOfParents<StructNode>()) {
        variableParentIndex = Utils::indexOf(structNode->getVariables(), variableParent).second;
        targetVariableIndex = Utils::indexOf(structNode->getVariables(), targetVariable).second;
    } else if (auto blockNode = variableParent->findNodeOfParents<BlockNode>()) {
        variableParentIndex = Utils::indexOf(blockNode->getNodes(), variableParent).second;
        targetVariableIndex = Utils::indexOf(blockNode->getNodes(), targetVariable).second;
    } else if (auto rootNode = variableParent->findNodeOfParents<RootNode>()) {
        variableParentIndex = Utils::indexOf(rootNode->getNodes(), variableParent).second;
        targetVariableIndex = Utils::indexOf(rootNode->getNodes(), targetVariable).second;
    } else {
        THROW_NODE_ERROR(identifierNode, "Case not implemented for the \"loaded variable\" check.")
        exit(EXIT_FAILURE);
    }

    if (variableParentIndex == -1 || targetVariableIndex == -1) {
        THROW_NODE_ERROR(identifierNode, "Couldn't find the indices for the identifier.")
        return;
    }

    if (targetVariableIndex > variableParentIndex) {
        THROW_NODE_ERROR(identifierNode,
                         "Couldn't target the variable because it's not created yet.")
        return;
    }
}

void ScopeCheck::visit(const SharedBinaryNode &binaryNode) {
    ScopeCheck::visit(binaryNode->getLHS());
    ScopeCheck::visit(binaryNode->getRHS());
}

void ScopeCheck::visit(const SharedUnaryNode &unaryNode) {
    ScopeCheck::visit(unaryNode->getExpression());
}

void ScopeCheck::visit(const SharedParenthesizedNode &parenthesizedNode) {
    ScopeCheck::visit(parenthesizedNode->getExpression());
}

void ScopeCheck::visit(const SharedStructCreateNode &structCreateNode) {
    for (const auto &argument : structCreateNode->getArguments())
        ScopeCheck::visit(argument);
}

void ScopeCheck::visit(const SharedStructArgumentNode &structArgumentNode) {
    if (structArgumentNode->getName() == "_")
        return;

    auto scopeCheck = [](const SharedASTNode &node) {
        return node->getKind() == ASTNode::STRUCT_ARGUMENT;
    };

    Symbols foundNodes;
    structArgumentNode->getScope()->scope(foundNodes, structArgumentNode->getName()->getContent(),
                                          scopeCheck);
    if (foundNodes.size() > 1) {
        THROW_NODE_ERROR(structArgumentNode, "There already exists a similar argument.")
        return;
    }

    if (structArgumentNode->getExpression() != nullptr)
        ScopeCheck::visit(structArgumentNode->getExpression());
}

void ScopeCheck::visit(const SharedFunctionArgumentNode &functionArgumentNode) {
    if (functionArgumentNode->getName() == nullptr) {
        ScopeCheck::visit(functionArgumentNode->getExpression());
        return;
    }

    auto scopeCheck = [](const SharedASTNode &node) {
        return node->getKind() == ASTNode::FUNCTION_ARGUMENT;
    };

    Symbols foundNodes;
    functionArgumentNode->getScope()->scope(
            foundNodes, functionArgumentNode->getName()->getContent(), scopeCheck);
    if (foundNodes.size() > 1) {
        THROW_NODE_ERROR(functionArgumentNode, "There already exists a similar argument.")
        return;
    }

    ScopeCheck::visit(functionArgumentNode->getExpression());
}

void ScopeCheck::visit(const SharedFunctionCallNode &functionCallNode) {
    for (const auto &argument : functionCallNode->getArguments())
        ScopeCheck::visit(argument);
}

void ScopeCheck::visit(const SharedAssignmentNode &assignmentNode) {
    ScopeCheck::visit(assignmentNode->getExpression());
}

void ScopeCheck::visit(const SharedReturnNode &returnNode) {
    if (returnNode->getExpression() != nullptr)
        ScopeCheck::visit(returnNode->getExpression());
}

void ScopeCheck::visit(const SharedStructNode &structNode) {
    for (const auto &variable : structNode->getVariables())
        ScopeCheck::visit(variable);
}