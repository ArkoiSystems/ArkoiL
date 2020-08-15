//
// Created by timo on 7/30/20.
//

#include "token.h"

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
    return std::strcmp(token->content.c_str(), toCheck.c_str()) == 0;
}

bool operator!=(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    if(token == nullptr)
        return false;
    return !(token == toCheck);
}

bool operator==(const std::shared_ptr<Token> &token, TokenType toCheck) {
    if(token == nullptr)
        return false;
    return token->type == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, TokenType toCheck) {
    if(token == nullptr)
        return false;
    return !(token == toCheck);
}
