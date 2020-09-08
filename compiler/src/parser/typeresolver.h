//
// Created by timo on 8/3/20.
//

#pragma once

#include <memory>

class RootNode;

class FunctionNode;

class BlockNode;

class VariableNode;

class BinaryNode;

class UnaryNode;

class ParenthesizedNode;

class NumberNode;

class StringNode;

class IdentifierNode;

class ParameterNode;

class ArgumentNode;

class FunctionCallNode;

class StructCreateNode;

class AssignmentNode;

class ReturnNode;

class StructNode;

class OperableNode;

class TypeNode;

class ASTNode;

class TypeResolver {

public:
    TypeResolver() = delete;

    TypeResolver(const TypeResolver &other) = delete;

    TypeResolver &operator=(const TypeResolver &) = delete;

public:
    static void visit(const std::shared_ptr<ASTNode> &node);

    static void visit(const std::shared_ptr<RootNode> &rootNode);

    static void visit(const std::shared_ptr<FunctionNode> &functionNode);

    static void visit(const std::shared_ptr<BlockNode> &blockNode);

    static void visit(const std::shared_ptr<VariableNode> &variableNode);

    static void visit(const std::shared_ptr<BinaryNode> &binaryNode);

    static void visit(const std::shared_ptr<UnaryNode> &unaryNode);

    static void visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    static void visit(const std::shared_ptr<NumberNode> &numberNode);

    static void visit(const std::shared_ptr<StringNode> &stringNode);

    static void visit(const std::shared_ptr<IdentifierNode> &identifierNode);

    static void visit(const std::shared_ptr<ParameterNode> &parameterNode);

    static void visit(const std::shared_ptr<ArgumentNode> &argumentNode);

    static void visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    static void visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visit(const std::shared_ptr<ReturnNode> &returnNode);

    static void visit(const std::shared_ptr<StructNode> &structNode);

    static void visit(const std::shared_ptr<TypeNode> &typeNode);

};