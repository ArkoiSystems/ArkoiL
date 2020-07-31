//
// Created by timo on 7/30/20.
//

#include <cstring>
#include "token.h"

std::ostream &operator<<(std::ostream &out, const std::shared_ptr<Token> &token) {
    out << "(" << token->type << " with \"" << token->content << "\" on line "
        << token->lineNumber << ")";
    return out;
}

bool operator==(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    return std::strcmp(token->content.c_str(), toCheck.c_str()) == 0;
}

bool operator!=(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    return std::strcmp(token->content.c_str(), toCheck.c_str()) != 0;
}

bool operator==(const std::shared_ptr<Token> &token, TokenType toCheck) {
    return token->type == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, TokenType toCheck) {
    return !(token == toCheck);
}
