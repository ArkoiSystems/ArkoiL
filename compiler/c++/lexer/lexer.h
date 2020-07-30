//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_LEXER_H
#define ARKOICOMPILER_LEXER_H

#include <iostream>
#include <utility>
#include <vector>
#include <string>
#include <memory>
#include "token.h"

class Lexer {

private:
    unsigned int position, currentLine, lineChar;
    std::string sourceCode;
    bool failed;

public:
    explicit Lexer(std::string sourceCode) : sourceCode(std::move(sourceCode)) {
        currentLine = 0;
        position = 0;
        lineChar = 0;
        failed = false;
    }

public:
    std::vector<std::shared_ptr<Token>> process();

    std::shared_ptr<Token> nextToken();

};

#endif //ARKOICOMPILER_LEXER_H
