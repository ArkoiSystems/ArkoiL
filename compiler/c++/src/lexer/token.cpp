//
// Created by timo on 7/30/20.
//

#include "token.h"

Token::Token() : lineNumber(0), startChar(0), endChar(0), content({}), type(TOKEN_INVALID) {
    type = Token::TOKEN_INVALID;
    lineNumber = 0;
    startChar = 0;
    endChar = 0;
    content = {};
}

std::ostream &operator<<(std::ostream &out, const std::shared_ptr<Token> &token) {
    if(token == nullptr) {
        out << "null";
        return out;
    }

    out << "(" << token->type << " with \"" << token->content << "\" on line "
        << (token->lineNumber + 1) << ")";
    return out;
}

bool operator==(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    if(token == nullptr)
        return false;
    return token->content == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    if(token == nullptr)
        return false;
    return !(token == toCheck);
}

bool operator==(const std::shared_ptr<Token> &token, Token::TokenType toCheck) {
    if(token == nullptr)
        return false;
    return token->type == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, Token::TokenType toCheck) {
    if(token == nullptr)
        return false;
    return !(token == toCheck);
}