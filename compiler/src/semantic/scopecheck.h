//
// Created by timo on 8/10/20.
//

#pragma once

#include <memory>

class ASTNode;

class RootNode;

class ImportNode;

class FunctionNode;

class BlockNode;

class ParameterNode;

class VariableNode;

class BinaryNode;

class UnaryNode;

class ParenthesizedNode;

class StructCreateNode;

class ArgumentNode;

class FunctionCallNode;

class AssignmentNode;

class IdentifierNode;

class ReturnNode;

class StructNode;

class ScopeCheck {

public:
    ScopeCheck() = delete;

    ScopeCheck(const ScopeCheck &other) = delete;

    ScopeCheck &operator=(const ScopeCheck &) = delete;

public:
    static void visit(const std::shared_ptr<ASTNode> &node);

    static void visit(const std::shared_ptr<RootNode> &rootNode);

    static void visit(const std::shared_ptr<ImportNode> &importNode);

    static void visit(const std::shared_ptr<FunctionNode> &functionNode);

    static void visit(const std::shared_ptr<BlockNode> &blockNode);

    static void visit(const std::shared_ptr<ParameterNode> &parameterNode);

    static void visit(const std::shared_ptr<VariableNode> &variableNode);

    static void visit(const std::shared_ptr<IdentifierNode> &identifierNode);

    static void visit(const std::shared_ptr<BinaryNode> &binaryNode);

    static void visit(const std::shared_ptr<UnaryNode> &unaryNode);

    static void visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    static void visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    static void visit(const std::shared_ptr<ArgumentNode> &argumentNode);

    static void visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visit(const std::shared_ptr<ReturnNode> &returnNode);

    static void visit(const std::shared_ptr<StructNode> &structNode);

};