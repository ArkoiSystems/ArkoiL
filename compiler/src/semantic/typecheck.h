//
// Created by timo on 8/10/20.
//

#pragma once

#include <memory>

class ASTNode;

class RootNode;

class StructNode;

class VariableNode;

class FunctionNode;

class BlockNode;

class FunctionCallNode;

class FunctionArgumentNode;

class ReturnNode;

class AssignmentNode;

class StructCreateNode;

class StructArgumentNode;

class BinaryNode;

class UnaryNode;

class ParenthesizedNode;

class TypeCheck {

public:
    TypeCheck() = delete;

    TypeCheck(const TypeCheck &other) = delete;

    TypeCheck &operator=(const TypeCheck &) = delete;

public:
    static void visit(const std::shared_ptr<ASTNode> &node);

    static void visit(const std::shared_ptr<RootNode> &rootNode);

    static void visit(const std::shared_ptr<StructNode> &structNode);

    static void visit(const std::shared_ptr<VariableNode> &variableNode);

    static void visit(const std::shared_ptr<FunctionNode> &functionNode);

    static void visit(const std::shared_ptr<BlockNode> &blockNode);

    static void visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode);

    static void visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode);

    static void visit(const std::shared_ptr<ReturnNode> &returnNode);

    static void visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    static void visit(const std::shared_ptr<BinaryNode> &binaryNode);

    static void visit(const std::shared_ptr<UnaryNode> &unaryNode);

    static void visit(const std::shared_ptr<ParenthesizedNode>& parenthesizedNode);

};