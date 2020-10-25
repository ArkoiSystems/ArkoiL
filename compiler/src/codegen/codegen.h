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

#include "../parser/allnodes.h"

class CodeGen {

    typedef std::tuple<llvm::BasicBlock *, llvm::Value *, llvm::BasicBlock *> BlockDetails;

    typedef std::variant<llvm::Value *,
            llvm::Type *,
            llvm::StructType *,
            llvm::BasicBlock *,
            llvm::Function *,
            std::nullptr_t,
            BlockDetails> NodeTypes;

    typedef std::unordered_map<SharedASTNode, NodeTypes> Nodes;

    typedef std::vector<Nodes> ScopedNodes;

private:
    ScopedNodes m_ScopedNodes;

    llvm::BasicBlock *m_CurrentBlock;

    llvm::LLVMContext m_Context;

    llvm::IRBuilder<> m_Builder;

    std::shared_ptr<llvm::Module> m_Module;

    std::string m_ModuleName;

public:
    explicit CodeGen(std::string moduleName);

    CodeGen(const CodeGen &) = delete;

    CodeGen &operator=(const CodeGen &) = delete;

public:
    void *visit(const SharedASTNode &node);

    void visit(const SharedRootNode &rootNode);

    llvm::Value *visit(const SharedFunctionNode &functionNode);

    llvm::Type *visit(const SharedTypeNode &typeNode);

    llvm::Type *visit(const SharedStructNode &structNode);

    llvm::Value *visit(const SharedParameterNode &parameterNode);

    llvm::BasicBlock *visit(const SharedBlockNode &blockNode);

    llvm::Value *visit(const SharedReturnNode &returnNode);

    llvm::Value *visit(const SharedAssignmentNode &assignmentNode);

    llvm::Value *visit(const SharedIdentifierNode &identifierNode);

    llvm::Value *visit(const SharedNumberNode &numberNode);

    llvm::Value *visit(const SharedStringNode &stringNode);

    llvm::Value *visit(const SharedBinaryNode &binaryNode);

    llvm::Value *visit(const SharedUnaryNode &unaryNode);

    llvm::Value *visit(const SharedParenthesizedNode &parenthesizedNode);

    llvm::Value *visit(const SharedFunctionCallNode &functionCallNode);

    llvm::Value *visit(const SharedFunctionArgumentNode &functionArgumentNode);

    llvm::Value *visit(const SharedStructCreateNode &structCreateNode);

    llvm::Value *visit(const SharedStructArgumentNode &structArgumentNode,
                       llvm::Value *structVariable,
                       int argumentIndex);

    llvm::Value *visit(const SharedVariableNode &variableNode);

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

    void setPositionAtEnd(llvm::BasicBlock *basicBlock);

    std::string dumpModule();

public:
    static bool shouldGenerateGEP(const SharedStructNode &structNode, int variableIndex);

    static SharedStructCreateNode createStructCreate(const SharedStructNode &targetNode,
                                                     const SharedASTNode &parentNode);

    static std::string dumpValue(llvm::Value *value);

public:
    [[nodiscard]]
    std::shared_ptr<llvm::Module> getModule() const;

};
