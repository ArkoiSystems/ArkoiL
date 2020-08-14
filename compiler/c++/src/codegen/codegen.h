//
// Created by timo on 8/13/20.
//

#ifndef ARKOICOMPILER_CODEGEN_H
#define ARKOICOMPILER_CODEGEN_H

#include <llvm-c-10/llvm-c/Core.h>
#include <llvm-c-10/llvm-c/Analysis.h>
#include <memory>
#include <unordered_map>
#include <vector>

class ASTNode;

class RootNode;

class FunctionNode;

class TypeNode;

class StructNode;

class ParameterNode;

class BlockNode;

class ReturnNode;

class OperableNode;

class AssignmentNode;

class IdentifierNode;

class NumberNode;

class StringNode;

class BinaryNode;

class UnaryNode;

class ParenthesizedNode;

class FunctionCallNode;

class StructCreateNode;

class ArgumentNode;

class CodeGen {

private:
    std::unordered_map<std::shared_ptr<BlockNode>,
            std::tuple<LLVMBasicBlockRef, LLVMValueRef, LLVMBasicBlockRef>> blocks;
    std::unordered_map<std::shared_ptr<FunctionNode>, LLVMValueRef> functions;
    std::unordered_map<std::shared_ptr<StructNode>, LLVMTypeRef> structs;

    LLVMBasicBlockRef currentBlock;
    LLVMBuilderRef builder;
    LLVMContextRef context;
    LLVMModuleRef module;

public:
    CodeGen() : blocks({}), functions({}), structs({}), currentBlock(),
                builder(), module(), context() {}

public:
    void visitNode(const std::shared_ptr<ASTNode> &node);

    void visitRoot(const std::shared_ptr<RootNode> &rootNode);

    LLVMValueRef visitFunction(const std::shared_ptr<FunctionNode> &functionNode);

    LLVMTypeRef visitType(const std::shared_ptr<TypeNode> &typeNode);

    LLVMTypeRef visitStruct(const std::shared_ptr<StructNode> &structNode);

    LLVMValueRef visitParameter(const std::shared_ptr<ParameterNode> &parameterNode);

    LLVMBasicBlockRef visitBlock(const std::shared_ptr<BlockNode> &blockNode);

    void visitReturn(const std::shared_ptr<ReturnNode> &returnNode);

    LLVMValueRef visitOperable(const std::shared_ptr<OperableNode> &operableNode);

    LLVMValueRef visitAssignment(const std::shared_ptr<AssignmentNode> &assignmentNode);

    LLVMValueRef visitIdentifier(const std::shared_ptr<IdentifierNode> &identifierNode);

    LLVMValueRef visitNumber(const std::shared_ptr<NumberNode> &numberNode);

    LLVMValueRef visitString(const std::shared_ptr<StringNode> &stringNode);

    LLVMValueRef visitBinary(const std::shared_ptr<BinaryNode> &binaryNode);

    LLVMValueRef visitUnary(const std::shared_ptr<UnaryNode> &unaryNode);

    LLVMValueRef visitParenthesized(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    LLVMValueRef visitFunctionCall(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    LLVMValueRef visitStructCreate(const std::shared_ptr<StructCreateNode> &structCreateNode);

    LLVMValueRef visitArgument(const std::shared_ptr<ArgumentNode>& argumentNode);

    void setPositionAtEnd(const LLVMBasicBlockRef &basicBlock);

    LLVMValueRef makeIntToFP(const std::shared_ptr<TypeNode> &typeNode,
                             const LLVMTypeRef &target,
                             const LLVMValueRef &value);

    LLVMValueRef makeAdd(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs);

    LLVMValueRef makeMul(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs);

    LLVMValueRef makeDiv(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                         const LLVMValueRef &lhs);

    LLVMValueRef makeSub(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs);

    LLVMValueRef makeRem(bool isSigned, const LLVMValueRef &rhs, const LLVMValueRef &lhs);

    LLVMValueRef makeLT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                        const LLVMValueRef &lhs);

    LLVMValueRef makeGT(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                        const LLVMValueRef &lhs);

    LLVMValueRef makeLE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                        const LLVMValueRef &lhs);

    LLVMValueRef makeGE(bool floatingPoint, bool isSigned, const LLVMValueRef &rhs,
                        const LLVMValueRef &lhs);

    LLVMValueRef makeEQ(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs);

    LLVMValueRef makeNE(bool floatingPoint, const LLVMValueRef &rhs, const LLVMValueRef &lhs);

};

#endif //ARKOICOMPILER_CODEGEN_H
