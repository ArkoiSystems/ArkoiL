//
// Created by timo on 8/3/20.
//

#ifndef ARKOICOMPILER_TYPERESOLVER_H
#define ARKOICOMPILER_TYPERESOLVER_H

#include <memory>
#include <utility>
#include "astnodes.h"

class TypeResolver {

public:
    void visitRoot(const std::shared_ptr<RootNode> &rootNode);

    void visitFunction(const std::shared_ptr<FunctionNode> &functionNode);

    void visitBlock(const std::shared_ptr<BlockNode> &blockNode);

    void visitVariable(const std::shared_ptr<VariableNode> &variableNode);

    void visitBinary(const std::shared_ptr<BinaryNode> &binaryNode);

    void visitUnary(const std::shared_ptr<UnaryNode> &unaryNode);

    void visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    void visitNumber(const std::shared_ptr<NumberNode> &numberNode);

    void visitString(const std::shared_ptr<StringNode> &stringNode);

    void visitIdentifier(const std::shared_ptr<IdentifierNode> &identifierNode);

    void visitParameter(const std::shared_ptr<ParameterNode> &parameterNode);

    void visitArgument(const std::shared_ptr<ArgumentNode> &argumentNode);

    void visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    void visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode);

    void visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode);

    void visitReturn(const std::shared_ptr<ReturnNode> &returnNode);

    void visitStruct(const std::shared_ptr<StructNode> &structNode);

    void visitOperable(const std::shared_ptr<OperableNode> &operableNode,
                       const std::shared_ptr<TypeNode> &targetType = nullptr);

    void visitType(const std::shared_ptr<TypeNode> &typeNode,
                   const std::shared_ptr<ASTNode> &parent);

};


#endif //ARKOICOMPILER_TYPERESOLVER_H
