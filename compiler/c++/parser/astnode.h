//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_ASTNODE_H
#define ARKOICOMPILER_ASTNODE_H

#include <vector>
#include "../lexer/token.h"

enum ASTType {
    ROOT,
    IMPORT,
    FUNCTION,
};

struct ASTNode {
    unsigned int startLine, endLine;
    ASTType type;

    virtual ~ASTNode() = default;
};

struct RootNode: public ASTNode {
    std::vector<std::shared_ptr<ASTNode>> nodes;
};

struct ImportNode: public ASTNode {
    std::shared_ptr<Token> path;
};

struct FunctionNode: public ASTNode {
    std::shared_ptr<Token> name;
};

#endif //ARKOICOMPILER_ASTNODE_H
