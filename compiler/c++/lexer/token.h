//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_TOKEN_H
#define ARKOICOMPILER_TOKEN_H

#include <iostream>
#include <utility>
#include <vector>
#include <string>
#include <memory>

enum TokenType {
    WHITESPACE,
    COMMENT,
    KEYWORD,
    TYPE,
    OPERATOR,
    SYMBOL,
    STRING,
    NUMBER,
    IDENTIFIER,
    INVALID
};

class Token {

public:
    unsigned int lineNumber, lineChar, charStart;
    std::string content;
    TokenType type;

public:
    friend std::ostream &operator<<(std::ostream &out, Token &token);

    friend bool operator==(const std::shared_ptr<Token> &token,
                           const std::pair<TokenType, std::string> &toCheck);

    friend bool operator!=(const std::shared_ptr<Token> &token,
                           const std::pair<TokenType, std::string> &toCheck);

    friend bool operator==(const std::shared_ptr<Token> &token, TokenType toCheck);

    friend bool operator!=(const std::shared_ptr<Token> &token, TokenType toCheck);

};

#endif //ARKOICOMPILER_TOKEN_H
