//
// Created by timo on 8/10/20.
//

#include "scopecheck.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"

void ScopeCheck::visitNode(const std::shared_ptr<ASTNode> &node) {
    if (node->kind == AST_ROOT) {
        ScopeCheck::visitRoot(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == AST_IMPORT) {
        ScopeCheck::visitImport(std::static_pointer_cast<ImportNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        ScopeCheck::visitFunction(std::static_pointer_cast<FunctionNode>(node));
    } else {
        std::cout << "ScopeCheck: Unsupported node. " << node->kind << std::endl;
        exit(EXIT_FAILURE);
    }
}

void ScopeCheck::visitRoot(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->nodes)
        ScopeCheck::visitNode(node);
}

void ScopeCheck::visitImport(const std::shared_ptr<ImportNode> &importNode) {
    auto rootNode = importNode->getParent<RootNode>();
    for (const auto &node : rootNode->nodes) {
        if (node->kind != AST_IMPORT || node == importNode)
            continue;

        auto rootImport = std::static_pointer_cast<ImportNode>(node);
        if (std::strcmp(rootImport->path->content.c_str(),
                        importNode->path->content.c_str()) == 0) {
            THROW_NODE_ERROR(importNode, "There is already another import with the same path.")
            return;
        }
    }
}

void ScopeCheck::visitFunction(const std::shared_ptr<FunctionNode> &functionNode) {
    if (functionNode->block != nullptr) {
        ScopeCheck::visitBlock(functionNode->block);

        std::vector<std::shared_ptr<ASTNode>> returns;
        for (const auto &node : functionNode->block->nodes) {
            if (node->kind != AST_RETURN)
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
        if (node->kind != AST_FUNCTION || node == functionNode)
            return false;

        auto foundFunction = std::static_pointer_cast<FunctionNode>(node);
        return *foundFunction == *functionNode;
    };

    auto rootNode = functionNode->getParent<RootNode>();
    std::vector<std::shared_ptr<ASTNode>> foundNodes;
    rootNode->searchGlobally(foundNodes, functionNode->name->content, scopeCheck);

    if (!foundNodes.empty()) {
        THROW_NODE_ERROR(functionNode, "There already exists a similar function.")
        return;
    }
}

void ScopeCheck::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {

}
