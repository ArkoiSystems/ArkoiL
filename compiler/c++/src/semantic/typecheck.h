//
// Created by timo on 8/10/20.
//

#ifndef ARKOICOMPILER_TYPECHECK_H
#define ARKOICOMPILER_TYPECHECK_H

#include <memory>

class ASTNode;

class RootNode;

class StructNode;

class VariableNode;

class FunctionNode;

class BlockNode;

class FunctionCallNode;

class ArgumentNode;

class ReturnNode;

class AssignmentNode;

class StructCreateNode;

class BinaryNode;

class UnaryNode;

class ParenthesizedNode;

class TypeCheck {

public:
    TypeCheck() = delete;

public:
    static void visit(const std::shared_ptr<ASTNode> &node);

    static void visit(const std::shared_ptr<RootNode> &rootNode);

    static void visit(const std::shared_ptr<StructNode> &structNode);

    static void visit(const std::shared_ptr<VariableNode> &variableNode);

    static void visit(const std::shared_ptr<FunctionNode> &functionNode);

    static void visit(const std::shared_ptr<BlockNode> &blockNode);

    static void visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visit(const std::shared_ptr<ArgumentNode> &argumentNode);

    static void visit(const std::shared_ptr<ReturnNode> &returnNode);

    static void visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visit(const std::shared_ptr<StructCreateNode>& structCreateNode);

    static void visit(const std::shared_ptr<BinaryNode>& binaryNode);

    static void visit(const std::shared_ptr<UnaryNode>& unaryNode);

    static void visit(const std::shared_ptr<ParenthesizedNode>& parenthesizedNode);

};

#endif //ARKOICOMPILER_TYPECHECK_H
