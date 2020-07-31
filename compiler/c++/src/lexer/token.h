//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_TOKEN_H
#define ARKOICOMPILER_TOKEN_H

#include <iostream>
#include <utility>
#include <cstring>
#include <vector>
#include <string>
#include <memory>

enum TokenType {
    TOKEN_WHITESPACE,
    TOKEN_COMMENT,
    TOKEN_KEYWORD,
    TOKEN_TYPE,
    TOKEN_OPERATOR,
    TOKEN_SYMBOL,
    TOKEN_STRING,
    TOKEN_NUMBER,
    TOKEN_IDENTIFIER,
    TOKEN_INVALID
};

class Token {

public:
    unsigned int lineNumber, startChar;
    std::string content;
    TokenType type;

public:
    friend std::ostream &operator<<(std::ostream &out,
                                    const std::shared_ptr<Token> &token);

    friend bool operator==(const std::shared_ptr<Token> &token,
                           const std::string &toCheck);

    friend bool operator!=(const std::shared_ptr<Token> &token,
                           const std::string &toCheck);

    friend bool operator==(const std::shared_ptr<Token> &token, TokenType toCheck);

    friend bool operator!=(const std::shared_ptr<Token> &token, TokenType toCheck);

};

#endif //ARKOICOMPILER_TOKEN_H
