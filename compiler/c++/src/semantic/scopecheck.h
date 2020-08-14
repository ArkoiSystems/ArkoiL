//
// Created by timo on 8/10/20.
//

#ifndef ARKOICOMPILER_SCOPECHECK_H
#define ARKOICOMPILER_SCOPECHECK_H

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

class ReturnNode;

class StructNode;

class ScopeCheck {

public:
    ScopeCheck() = delete;

public:
    static void visitNode(const std::shared_ptr<ASTNode> &node);

    static void visitRoot(const std::shared_ptr<RootNode> &rootNode);

    static void visitImport(const std::shared_ptr<ImportNode> &importNode);

    static void visitFunction(const std::shared_ptr<FunctionNode> &functionNode);

    static void visitBlock(const std::shared_ptr<BlockNode> &blockNode);

    static void visitParameter(const std::shared_ptr<ParameterNode> &parameterNode);

    static void visitVariable(const std::shared_ptr<VariableNode> &variableNode);

    static void visitBinary(const std::shared_ptr<BinaryNode> &binaryNode);

    static void visitUnary(const std::shared_ptr<UnaryNode> &unaryNode);

    static void visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    static void visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode);

    static void visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode);

    static void visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visitReturn(const std::shared_ptr<ReturnNode> &returnNode);

    static void visitStruct(const std::shared_ptr<StructNode> &structNode);

};

#endif //ARKOICOMPILER_SCOPECHECK_H
