//
// Created by timo on 8/3/20.
//

#ifndef ARKOICOMPILER_TYPERESOLVER_H
#define ARKOICOMPILER_TYPERESOLVER_H

#include <memory>
#include <utility>

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

public:
    static void visitNode(const std::shared_ptr<ASTNode> &node);

    static void visitRoot(const std::shared_ptr<RootNode> &rootNode);

    static void visitFunction(const std::shared_ptr<FunctionNode> &functionNode);

    static void visitBlock(const std::shared_ptr<BlockNode> &blockNode);

    static void visitVariable(const std::shared_ptr<VariableNode> &variableNode);

    static void visitBinary(const std::shared_ptr<BinaryNode> &binaryNode);

    static void visitUnary(const std::shared_ptr<UnaryNode> &unaryNode);

    static void visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    static void visitNumber(const std::shared_ptr<NumberNode> &numberNode);

    static void visitString(const std::shared_ptr<StringNode> &stringNode);

    static void visitIdentifier(const std::shared_ptr<IdentifierNode> &identifierNode);

    static void visitParameter(const std::shared_ptr<ParameterNode> &parameterNode);

    static void visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode);

    static void visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    static void visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode);

    static void visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode);

    static void visitReturn(const std::shared_ptr<ReturnNode> &returnNode);

    static void visitStruct(const std::shared_ptr<StructNode> &structNode);

    static void visitType(const std::shared_ptr<TypeNode> &typeNode);

};


#endif //ARKOICOMPILER_TYPERESOLVER_H
