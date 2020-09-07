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
    if (node->kind == ASTNode::ROOT) {
        ScopeCheck::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == ASTNode::IMPORT) {
        ScopeCheck::visit(std::static_pointer_cast<ImportNode>(node));
    } else if (node->kind == ASTNode::FUNCTION) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == ASTNode::PARAMETER) {
        ScopeCheck::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->kind == ASTNode::VARIABLE) {
        ScopeCheck::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->kind == ASTNode::BINARY) {
        ScopeCheck::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == ASTNode::UNARY) {
        ScopeCheck::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == ASTNode::PARENTHESIZED) {
        ScopeCheck::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind == ASTNode::STRUCT_CREATE) {
        ScopeCheck::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind == ASTNode::ARGUMENT) {
        ScopeCheck::visit(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == ASTNode::FUNCTION_CALL) {
        ScopeCheck::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == ASTNode::ASSIGNMENT) {
        ScopeCheck::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == ASTNode::RETURN) {
        ScopeCheck::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == ASTNode::STRUCT) {
        ScopeCheck::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->kind != ASTNode::TYPE && node->kind != ASTNode::NUMBER &&
               node->kind != ASTNode::STRING && node->kind != ASTNode::IDENTIFIER) {
        std::cout << "ScopeCheck: Unsupported node. " << node->kind << std::endl;
        exit(EXIT_FAILURE);
    }
}

void ScopeCheck::visit(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->nodes)
        ScopeCheck::visit(node);
}

void ScopeCheck::visit(const std::shared_ptr<ImportNode> &importNode) {
    auto rootNode = importNode->getParent<RootNode>();
    for (const auto &node : rootNode->nodes) {
        if (node->kind != ASTNode::IMPORT || node == importNode)
            continue;

        auto rootImport = std::static_pointer_cast<ImportNode>(node);
        if(rootImport->path->content == importNode->path->content)  {
            THROW_NODE_ERROR(importNode, "There is already another import with the same path.")
            return;
        }
    }
}

void ScopeCheck::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    if (functionNode->block != nullptr) {
        ScopeCheck::visit(functionNode->block);

        std::vector<std::shared_ptr<ASTNode>> returns;
        for (const auto &node : functionNode->block->nodes) {
            if (node->kind != ASTNode::RETURN)
                continue;
            returns.push_back(node);
        }

        if (returns.empty() && functionNode->type->bits != 0) {
            THROW_NODE_ERROR(functionNode,
                             "Non-void functions need to have at least one return statement.")
            return;
        }
    }

    auto scopeCheck = [functionNode](const std::shared_ptr<ASTNode> &node) {
        if (node->kind != ASTNode::FUNCTION)
            return false;

        auto foundFunction = std::static_pointer_cast<FunctionNode>(node);
        return *foundFunction == *functionNode;
    };

    auto rootNode = functionNode->getParent<RootNode>();
    auto foundNodes = rootNode->searchWithImports(functionNode->name->content, scopeCheck);

    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(functionNode, "There already exists a similar function.")
        return;
    }

    for (const auto &parameter : functionNode->parameters)
        ScopeCheck::visit(parameter);
}

void ScopeCheck::visit(const std::shared_ptr<BlockNode> &blockNode) {
    std::vector<std::shared_ptr<ASTNode>> returns;
    for (const auto &node : blockNode->nodes) {
        if (node->kind != ASTNode::RETURN)
            continue;
        returns.push_back(node);
    }

    if (!returns.empty()) {
        auto targetReturn = returns.at(0);
        auto returnIndex = Utils::indexOf(blockNode->nodes, targetReturn);
        if (returnIndex.second != blockNode->nodes.size() - 1) {
            THROW_NODE_ERROR(targetReturn, "Everything after a return statement is unreachable.")
            return;
        }
    }

    for (const auto &node : blockNode->nodes)
        ScopeCheck::visit(node);
}

void ScopeCheck::visit(const std::shared_ptr<ParameterNode> &parameterNode) {
    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->kind == ASTNode::PARAMETER;
    };

    auto foundNodes = parameterNode->scope->scope(parameterNode->name->content, scopeCheck);
    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(parameterNode, "There already exists a similar parameter.")
        return;
    }
}

void ScopeCheck::visit(const std::shared_ptr<VariableNode> &variableNode) {
    if(variableNode->name == "_") {
        if (variableNode->expression != nullptr)
            ScopeCheck::visit(variableNode->expression);
        return;
    }

    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->kind == ASTNode::VARIABLE;
    };

    auto foundNodes = variableNode->scope->scope(variableNode->name->content, scopeCheck);
    auto blockNode = variableNode->getParent<BlockNode>();
    if (foundNodes->empty() && blockNode != nullptr) {
        foundNodes = variableNode->scope->all(variableNode->name->content, scopeCheck);
        if (foundNodes->size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    } else if(foundNodes->empty()) {
        auto rootNode = variableNode->getParent<RootNode>();
        foundNodes = rootNode->searchWithImports(variableNode->name->content, scopeCheck);

        if (foundNodes->size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    }

    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
        return;
    }

    if (variableNode->expression != nullptr)
        ScopeCheck::visit(variableNode->expression);
}

void ScopeCheck::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    ScopeCheck::visit(binaryNode->lhs);
    ScopeCheck::visit(binaryNode->rhs);
}

void ScopeCheck::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    ScopeCheck::visit(unaryNode->operable);
}

void ScopeCheck::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    ScopeCheck::visit(parenthesizedNode->expression);
}

void ScopeCheck::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->arguments)
        ScopeCheck::visit(argument);
}

void ScopeCheck::visit(const std::shared_ptr<ArgumentNode> &argumentNode) {
    if (argumentNode->name == nullptr) {
        ScopeCheck::visit(argumentNode->expression);
        return;
    }

    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->kind == ASTNode::ARGUMENT;
    };

    auto foundNodes = argumentNode->scope->scope(argumentNode->name->content, scopeCheck);
    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(argumentNode, "There already exists a similar argument.")
        return;
    }

    ScopeCheck::visit(argumentNode->expression);
}

void ScopeCheck::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (const auto &argument : functionCallNode->arguments)
        ScopeCheck::visit(argument);
}

void ScopeCheck::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    ScopeCheck::visit(assignmentNode->expression);
}

void ScopeCheck::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    if (returnNode->expression != nullptr)
        ScopeCheck::visit(returnNode->expression);
}

void ScopeCheck::visit(const std::shared_ptr<StructNode> &structNode) {
    for(const auto &variable : structNode->variables)
        ScopeCheck::visit(variable);
}
