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

class Token;

class Lexer {

private:
    unsigned int position, currentLine;
    std::string sourceCode, sourcePath;

public:
    explicit Lexer(std::string sourcePath, std::string sourceCode) :
            sourcePath(std::move(sourcePath)),
            sourceCode(std::move(sourceCode)),
            currentLine(0),
            position(0) {}

public:
    std::vector<std::shared_ptr<Token>> process();

    std::shared_ptr<Token> nextToken();

};

#endif //ARKOICOMPILER_LEXER_H