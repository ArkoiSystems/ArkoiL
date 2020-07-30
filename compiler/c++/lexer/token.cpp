//
// Created by timo on 7/30/20.
//

#include "token.h"

std::ostream &operator<<(std::ostream &out, Token &token) {
    out << "(" << token.type << " with \"" << token.content << "\" on line "
        << token.lineNumber << "/" << (token.charStart - token.lineChar) << ")";
    return out;
}

bool operator==(const std::shared_ptr<Token> &token,
                const std::pair<TokenType, std::string> &toCheck) {
    return token->type == toCheck.first && token->content == toCheck.second;
}

bool operator!=(const std::shared_ptr<Token> &token,
                const std::pair<TokenType, std::string> &toCheck) {
    return token->type != toCheck.first && token->content != toCheck.second;
}

bool operator==(const std::shared_ptr<Token> &token, TokenType toCheck) {
    return token->type == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, TokenType toCheck) {
    return token->type != toCheck;
}
