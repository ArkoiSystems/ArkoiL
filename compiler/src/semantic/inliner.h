//
// Created by timo on 10/15/20.
//

#pragma once

#include <memory>

#include "../parser/allnodes.h"

class SymbolTable;

class Inliner {

    typedef std::shared_ptr<VariableNode> ReturnVariable;
    typedef std::shared_ptr<IdentifierNode> ReturnIdentifier;

public:
    Inliner();

    Inliner(const Inliner &other) = delete;

    Inliner &operator=(const Inliner &) = delete;

public:
    ReturnVariable visit(const std::shared_ptr<ASTNode> &node);

    ReturnVariable visit(const std::shared_ptr<RootNode> &rootNode);

    ReturnVariable visit(const std::shared_ptr<FunctionNode> &functionNode);

    ReturnVariable visit(const std::shared_ptr<BlockNode> &blockNode);

    ReturnVariable visit(const std::shared_ptr<VariableNode> &variableNode);

    ReturnVariable visit(const std::shared_ptr<BinaryNode> &binaryNode);

    ReturnVariable visit(const std::shared_ptr<UnaryNode> &unaryNode);

    ReturnVariable visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    ReturnVariable visit(const std::shared_ptr<IdentifierNode> &identifierNode);

    ReturnVariable visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode);

    ReturnVariable visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode);

    ReturnVariable visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    ReturnVariable visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    ReturnVariable visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    ReturnVariable visit(const std::shared_ptr<ReturnNode> &returnNode);

    ReturnVariable visit(const std::shared_ptr<StructNode> &structNode);

    ReturnVariable generate(const std::shared_ptr<FunctionNode> &targetFunction,
                            const std::shared_ptr<IdentifierNode> &identifierNode,
                            const std::shared_ptr<BlockNode> &insertBlock, int insertIndex);

    ReturnIdentifier createIdentifier(const std::shared_ptr<ASTNode> &parent,
                                      const std::shared_ptr<SymbolTable> &scope,
                                      const ReturnVariable &returnVariable);

};


