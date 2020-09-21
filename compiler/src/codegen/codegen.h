//
// Created by timo on 8/13/20.
//

#pragma once

#include <unordered_map>
#include <memory>
#include <vector>

#include <llvm/Support/raw_ostream.h>
#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
#include <llvm/IR/Type.h>

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

    typedef std::unordered_map<std::shared_ptr<BlockNode>,
            std::tuple<llvm::BasicBlock*, llvm::Value*, llvm::BasicBlock*>> Blocks;

    typedef std::unordered_map<std::shared_ptr<ParameterNode>, llvm::Value*> Parameters;

    typedef std::unordered_map<std::shared_ptr<FunctionNode>, llvm::Value*> Functions;

    typedef std::unordered_map<std::shared_ptr<VariableNode>, llvm::Value*> Variables;

    typedef std::unordered_map<std::shared_ptr<StructNode>, llvm::StructType*> Structs;

private:
    Blocks m_Blocks;

    Parameters m_Parameters;
    Functions m_Functions;
    Variables m_Variables;
    Structs m_Structs;

    llvm::BasicBlock* m_CurrentBlock;

    llvm::LLVMContext m_Context;

    llvm::IRBuilder<> m_Builder;

    std::shared_ptr<llvm::Module> m_Module;

public:
    CodeGen();

    CodeGen(const CodeGen &) = delete;

    CodeGen &operator=(const CodeGen &) = delete;

public:
    void visit(const std::shared_ptr<ASTNode> &node);

    void visit(const std::shared_ptr<RootNode> &rootNode);

    llvm::Value* visit(const std::shared_ptr<FunctionNode> &functionNode);

    llvm::Type* visit(const std::shared_ptr<TypeNode> &typeNode);

    llvm::Type* visit(const std::shared_ptr<StructNode> &structNode);

    llvm::Value* visit(const std::shared_ptr<ParameterNode> &parameterNode);

    llvm::BasicBlock* visit(const std::shared_ptr<BlockNode> &blockNode);

    llvm::Value* visit(const std::shared_ptr<ReturnNode> &returnNode);

    llvm::Value* visit(const std::shared_ptr<AssignmentNode> &assignmentNode);

    llvm::Value* visit(const std::shared_ptr<IdentifierNode> &identifierNode);

    llvm::Value* visit(const std::shared_ptr<NumberNode> &numberNode);

    llvm::Value* visit(const std::shared_ptr<StringNode> &stringNode);

    llvm::Value* visit(const std::shared_ptr<BinaryNode> &binaryNode);

    llvm::Value *visit(const std::shared_ptr<UnaryNode> &unaryNode);

    llvm::Value *visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    llvm::Value *visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    llvm::Value *visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    llvm::Value *visit(const std::shared_ptr<ArgumentNode> &argumentNode);

    llvm::Value *visit(const std::shared_ptr<VariableNode> &variableNode);

    llvm::Value *visit(const std::shared_ptr<TypedNode> &typedNode);

    void setPositionAtEnd(llvm::BasicBlock *basicBlock);

    llvm::Value *makeAdd(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeMul(bool floatingPoint, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeDiv(bool floatingPoint, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value* makeSub(bool floatingPoint, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeRem(bool isSigned, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeLT(bool floatingPoint, bool isSigned, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeGT(bool floatingPoint, bool isSigned, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeLE(bool floatingPoint, bool isSigned, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeGE(bool floatingPoint, bool isSigned, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeEQ(bool floatingPoint, llvm::Value* rhs, llvm::Value* lhs);

    llvm::Value* makeNE(bool floatingPoint, llvm::Value* rhs, llvm::Value* lhs);

public:
    std::shared_ptr<llvm::Module> getModule() const;

};
