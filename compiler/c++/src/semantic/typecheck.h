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

class TypeCheck {

public:
    TypeCheck() = delete;

public:
    static void visitNode(const std::shared_ptr<ASTNode> &node);

    static void visitRoot(const std::shared_ptr<RootNode> &rootNode);

    static void visitStruct(const std::shared_ptr<StructNode> &structNode);

    static void visitVariable(const std::shared_ptr<VariableNode> &variableNode);

    static void visitFunction(const std::shared_ptr<FunctionNode> &functionNode);

    static void visitBlock(const std::shared_ptr<BlockNode> &blockNode);

    static void visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode);

    static void visitReturn(const std::shared_ptr<ReturnNode> &returnNode);

    static void visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visitStructCreate(const std::shared_ptr<StructCreateNode>& structCreateNode);

};

#endif //ARKOICOMPILER_TYPECHECK_H
