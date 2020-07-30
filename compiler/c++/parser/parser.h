//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_PARSER_H
#define ARKOICOMPILER_PARSER_H

#include <utility>
#include <vector>
#include "../lexer/lexer.h"
#include "astnode.h"

class Parser {

private:
    std::vector<std::shared_ptr<Token>> tokens;
    int position;

public:
    explicit Parser(std::vector<std::shared_ptr<Token>> tokens) : tokens(
            std::move(tokens)) {
        position = 0;
    }

public:
    std::shared_ptr<RootNode> parseRoot();

private:
    std::shared_ptr<ImportNode> parseImport();

    std::shared_ptr<FunctionNode> parseFunction();

    std::shared_ptr<Token> peekToken(int offset, bool advance = true);

    std::shared_ptr<Token> nextToken(bool advance = true);

    std::shared_ptr<Token> currentToken();

};

#endif //ARKOICOMPILER_PARSER_H
