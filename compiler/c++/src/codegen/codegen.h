//
// Created by timo on 8/13/20.
//

#ifndef ARKOICOMPILER_CODEGEN_H
#define ARKOICOMPILER_CODEGEN_H

#include <unordered_map>
#include <memory>
#include <vector>

#include <llvm-c-10/llvm-c/Core.h>

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

class VariableNode;

class TypedNode;

class CodeGen {

private:
    std::unordered_map<std::shared_ptr<ASTNode>,
            std::vector<std::shared_ptr<OperableNode>>> originalExpressions;
    std::unordered_map<std::shared_ptr<ASTNode>,
            std::vector<std::shared_ptr<OperableNode>>> lastExpressions;
    std::unordered_map<std::shared_ptr<BlockNode>,
            std::tuple<LLVMBasicBlockRef, LLVMValueRef, LLVMBasicBlockRef>> blocks;
    std::unordered_map<std::shared_ptr<ParameterNode>, LLVMValueRef> parameters;
    std::unordered_map<std::shared_ptr<FunctionNode>, LLVMValueRef> functions;
    std::unordered_map<std::shared_ptr<VariableNode>, LLVMValueRef> variables;
    std::unordered_map<std::shared_ptr<StructNode>, LLVMTypeRef> structs;

    LLVMBasicBlockRef currentBlock;
    LLVMBuilderRef builder;
    LLVMContextRef context;
    LLVMModuleRef module;

public:
    CodeGen();

public:
    void visit(const std::shared_ptr<ASTNode> &node);

    void visit(const std::shared_ptr<RootNode> &rootNode);

    LLVMValueRef visit(const std::shared_ptr<FunctionNode> &functionNode);

    LLVMTypeRef visit(const std::shared_ptr<TypeNode> &typeNode);

    LLVMTypeRef visit(const std::shared_ptr<StructNode> &structNode);

    LLVMValueRef visit(const std::shared_ptr<ParameterNode> &parameterNode);

    LLVMBasicBlockRef visit(const std::shared_ptr<BlockNode> &blockNode);

    LLVMValueRef visit(const std::shared_ptr<ReturnNode> &returnNode);

    LLVMValueRef visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    LLVMValueRef visit(const std::shared_ptr<IdentifierNode> &identifierNode);

    LLVMValueRef visit(const std::shared_ptr<NumberNode> &numberNode);

    LLVMValueRef visit(const std::shared_ptr<StringNode> &stringNode);

    LLVMValueRef visit(const std::shared_ptr<BinaryNode> &binaryNode);

    LLVMValueRef visit(const std::shared_ptr<UnaryNode> &unaryNode);

    LLVMValueRef visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    LLVMValueRef visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    LLVMValueRef visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    LLVMValueRef visit(const std::shared_ptr<ArgumentNode> &argumentNode);

    LLVMValueRef visit(const std::shared_ptr<VariableNode> &variableNode);

    LLVMValueRef visit(const std::shared_ptr<TypedNode> &typedNode);

    void setPositionAtEnd(const LLVMBasicBlockRef &basicBlock);

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
