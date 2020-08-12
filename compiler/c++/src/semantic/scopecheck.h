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

class ScopeCheck {

public:
    ScopeCheck() = delete;

public:
    static void visitNode(const std::shared_ptr<ASTNode> &node);

    static void visitRoot(const std::shared_ptr<RootNode> &rootNode);

    static void visitImport(const std::shared_ptr<ImportNode>& importNode);

    static void visitFunction(const std::shared_ptr<FunctionNode>& functionNode);

    static void visitBlock(const std::shared_ptr<BlockNode>& blockNode);

};

#endif //ARKOICOMPILER_SCOPECHECK_H
