//
// Created by timo on 8/10/20.
//

#include "scopecheck.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"
#include "../utils.h"

void ScopeCheck::visitNode(const std::shared_ptr<ASTNode> &node) {
    if (node->kind == AST_ROOT) {
        ScopeCheck::visitRoot(std::static_pointer_cast<RootNode>(node));
    } else if (node->kind == AST_IMPORT) {
        ScopeCheck::visitImport(std::static_pointer_cast<ImportNode>(node));
    } else if (node->kind == AST_FUNCTION) {
        ScopeCheck::visitFunction(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->kind == AST_PARAMETER) {
        ScopeCheck::visitParameter(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->kind == AST_VARIABLE) {
        ScopeCheck::visitVariable(std::static_pointer_cast<VariableNode>(node));
    } else if (node->kind == AST_BINARY) {
        ScopeCheck::visitBinary(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->kind == AST_UNARY) {
        ScopeCheck::visitUnary(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->kind == AST_PARENTHESIZED) {
        ScopeCheck::visitParenthesized(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->kind == AST_STRUCT_CREATE) {
        ScopeCheck::visitStructCreate(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->kind == AST_ARGUMENT) {
        ScopeCheck::visitArgument(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->kind == AST_FUNCTION_CALL) {
        ScopeCheck::visitFunctionCall(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->kind == AST_ASSIGNMENT) {
        ScopeCheck::visitAssignment(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->kind == AST_RETURN) {
        ScopeCheck::visitReturn(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->kind == AST_STRUCT) {
        ScopeCheck::visitStruct(std::static_pointer_cast<StructNode>(node));
    } else if (node->kind != AST_TYPE && node->kind != AST_NUMBER &&
               node->kind != AST_STRING && node->kind != AST_IDENTIFIER) {
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
        if (node->kind != AST_FUNCTION)
            return false;

        auto foundFunction = std::static_pointer_cast<FunctionNode>(node);
        return *foundFunction == *functionNode;
    };

    auto rootNode = functionNode->getParent<RootNode>();
    std::vector<std::shared_ptr<ASTNode>> foundNodes;
    rootNode->searchGlobally(foundNodes, functionNode->name->content, scopeCheck);

    if (foundNodes.size() > 1) {
        THROW_NODE_ERROR(functionNode, "There already exists a similar function.")
        return;
    }

    for (const auto &parameter : functionNode->parameters)
        ScopeCheck::visitParameter(parameter);
}

void ScopeCheck::visitBlock(const std::shared_ptr<BlockNode> &blockNode) {
    std::vector<std::shared_ptr<ASTNode>> returns;
    for (const auto &node : blockNode->nodes) {
        if (node->kind != AST_RETURN)
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
        ScopeCheck::visitNode(node);
}

void ScopeCheck::visitParameter(const std::shared_ptr<ParameterNode> &parameterNode) {
    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->kind == AST_PARAMETER;
    };

    auto foundNodes = parameterNode->scope->scope(parameterNode->name->content, scopeCheck);
    if (foundNodes->size() > 1) {
        THROW_NODE_ERROR(parameterNode, "There already exists a similar parameter.")
        return;
    }
}

void ScopeCheck::visitVariable(const std::shared_ptr<VariableNode> &variableNode) {
    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
        return node->kind == AST_VARIABLE;
    };

    auto blockNode = variableNode->getParent<BlockNode>();
    if (blockNode != nullptr) {
        auto foundNodes = variableNode->scope->general(variableNode->name->content, scopeCheck);
        if (foundNodes->size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    } else {
        auto rootNode = variableNode->getParent<RootNode>();
        std::vector<std::shared_ptr<ASTNode>> foundNodes;
        rootNode->searchGlobally(foundNodes, variableNode->name->content, scopeCheck);

        if (foundNodes.size() > 1) {
            THROW_NODE_ERROR(variableNode, "There already exists a similar variable.")
            return;
        }
    }

    if (variableNode->expression != nullptr)
        ScopeCheck::visitNode(variableNode->expression);
}

void ScopeCheck::visitBinary(const std::shared_ptr<BinaryNode> &binaryNode) {
    ScopeCheck::visitNode(binaryNode->lhs);
    ScopeCheck::visitNode(binaryNode->rhs);
}

void ScopeCheck::visitUnary(const std::shared_ptr<UnaryNode> &unaryNode) {
    ScopeCheck::visitNode(unaryNode->operable);
}

void ScopeCheck::visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    ScopeCheck::visitNode(parenthesizedNode->expression);
}

void ScopeCheck::visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->arguments)
        ScopeCheck::visitArgument(argument);
}

void ScopeCheck::visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode) {
    if (argumentNode->name == nullptr) {
        ScopeCheck::visitNode(argumentNode->expression);
        return;
    }

    // TODO: Check if there already any argument with the same name.

//    auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
//        std::cout << node->kind << std::endl;
//        return node->kind == AST_ARGUMENT;
//    };

//    std::cout << argumentNode->name->content << " ~~~~" << std::endl;
//    for(const auto &test : argumentNode->scope->table)
//        std::cout << test.first << std::endl;
//
//    auto foundNodes = argumentNode->scope->scope(argumentNode->name->content, scopeCheck);
//    std::cout << foundNodes << std::endl;
//    if (foundNodes->size() > 1) {
//        THROW_NODE_ERROR(argumentNode, "There already exists a similar argument.")
//        return;
//    }

    ScopeCheck::visitNode(argumentNode->expression);
}

void ScopeCheck::visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (const auto &argument : functionCallNode->arguments)
        ScopeCheck::visitArgument(argument);
}

void ScopeCheck::visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    ScopeCheck::visitNode(assignmentNode->expression);
}

void ScopeCheck::visitReturn(const std::shared_ptr<ReturnNode> &returnNode) {
    if (returnNode->expression != nullptr)
        ScopeCheck::visitNode(returnNode->expression);
}

void ScopeCheck::visitStruct(const std::shared_ptr<StructNode> &structNode) {
    for(const auto &variable : structNode->variables)
        ScopeCheck::visitVariable(variable);
}
