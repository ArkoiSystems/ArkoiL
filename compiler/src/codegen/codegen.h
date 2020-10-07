//
// Created by timo on 8/13/20.
//

#pragma once

#include <unordered_map>
#include <variant>
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

class FunctionArgumentNode;

class StructCreateNode;

class StructArgumentNode;

class VariableNode;

class TypedNode;

class CodeGen {

    typedef std::tuple<llvm::BasicBlock *, llvm::Value *, llvm::BasicBlock *> BlockDetails;

    typedef std::variant<llvm::Value *,
            llvm::Type *,
            llvm::StructType *,
            llvm::BasicBlock *,
            std::nullptr_t,
            BlockDetails> NodeTypes;

    typedef std::unordered_map<std::shared_ptr<ASTNode>, NodeTypes> Nodes;

    typedef std::vector<Nodes> ScopedNodes;

private:
    ScopedNodes m_ScopedNodes;

    llvm::BasicBlock *m_CurrentBlock;

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

    llvm::Value *visit(const std::shared_ptr<StringNode> &stringNode);

    llvm::Value *visit(const std::shared_ptr<BinaryNode> &binaryNode);

    llvm::Value *visit(const std::shared_ptr<UnaryNode> &unaryNode);

    llvm::Value *visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode);

    llvm::Value *visit(const std::shared_ptr<FunctionCallNode> &functionCallNode);

    llvm::Value *visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode);

    llvm::Value *visit(const std::shared_ptr<StructCreateNode> &structCreateNode);

    llvm::Value *visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode,
                       llvm::Value *structVariable,
                       int argumentIndex);

    llvm::Value *visit(const std::shared_ptr<VariableNode> &variableNode);

    llvm::Value *visit(const std::shared_ptr<TypedNode> &typedNode);

    void setPositionAtEnd(llvm::BasicBlock *basicBlock);

    llvm::Value *makeAdd(bool isFloating, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeMul(bool isFloating, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeDiv(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeSub(bool isFloating, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeRem(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeLT(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeGT(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeLE(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeGE(bool isFloating, bool isSigned, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeEQ(bool isFloating, llvm::Value *rhs, llvm::Value *lhs);

    llvm::Value *makeNE(bool isFloating, llvm::Value *rhs, llvm::Value *lhs);

    std::string dumpModule();

public:
    std::shared_ptr<llvm::Module> getModule() const;

};
