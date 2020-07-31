//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_ASTNODE_H
#define ARKOICOMPILER_ASTNODE_H

#include <vector>
#include "../lexer/token.h"

enum ASTKind {
    AST_NONE,
    AST_ROOT,
    AST_IMPORT,
    AST_FUNCTION,
    AST_PARAMETER,
    AST_TYPE,
    AST_BLOCK,
};

struct ASTNode {

    unsigned int startLine, endLine;
    ASTKind kind;

    ASTNode() {
        startLine = 0;
        endLine = 0;
        kind = AST_NONE;
    }

    virtual ~ASTNode() = default;

};

struct RootNode: public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;

    RootNode() {
        kind = AST_ROOT;
    }

};

struct ImportNode: public ASTNode {

    std::shared_ptr<Token> path;

    ImportNode() {
        kind = AST_IMPORT;
    }

};

struct TypeNode: public ASTNode {

    unsigned int pointerLevel;

    TypeNode() {
        kind = AST_TYPE;
        pointerLevel = 0;
    }

};

struct ParameterNode: public ASTNode {

    std::shared_ptr<Token> name;
    std::shared_ptr<TypeNode> type;

    ParameterNode() {
        kind = AST_PARAMETER;
    }

};

struct BlockNode: public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;
    bool inlined;

    BlockNode() {
        kind = AST_BLOCK;
        inlined = false;
    }

};

struct FunctionNode: public ASTNode {

    std::shared_ptr<Token> name;
    std::vector<std::shared_ptr<ParameterNode>> parameters;
    std::shared_ptr<TypeNode> type;
    std::shared_ptr<BlockNode> block;

    FunctionNode() {
        kind = AST_FUNCTION;
    }

};

#endif //ARKOICOMPILER_ASTNODE_H
