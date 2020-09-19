//
// Created by timo on 7/30/20.
//

#include "token.h"

Token::Token()
        : m_Type(Token::INVALID), m_LineNumber(0),
          m_StartChar(0), m_EndChar(0),
          m_Content({}) {}

void Token::addContent(const std::string &content) {
    m_Content += content;
}

unsigned int Token::getLineNumber() const {
    return m_LineNumber;
}

void Token::setLineNumber(unsigned int lineNumber) {
    m_LineNumber = lineNumber;
}

unsigned int Token::getStartChar() const {
    return m_StartChar;
}

void Token::setStartChar(unsigned int startChar) {
    m_StartChar = startChar;
}

unsigned int Token::getEndChar() const {
    return m_EndChar;
}

void Token::setEndChar(unsigned int endChar) {
    m_EndChar = endChar;
}

const std::string &Token::getContent() const {
    return m_Content;
}

void Token::setContent(const std::string &content) {
    m_Content = content;
}

Token::TokenType Token::getType() const {
    return m_Type;
}

void Token::setType(Token::TokenType type) {
    m_Type = type;
}

std::ostream &operator<<(std::ostream &out, const std::shared_ptr<Token> &token) {
    if (token == nullptr) {
        out << "null";
        return out;
    }

    out << "(" << token->getType() << " with \"" << token->getContent() << "\" on line "
        << (token->getLineNumber() + 1) << ")";
    return out;
}

bool operator==(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    if(token == nullptr)
        return false;
    return token->getContent() == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, const std::string &toCheck) {
    if(token == nullptr)
        return false;
    return !(token == toCheck);
}

bool operator==(const std::shared_ptr<Token> &token, Token::TokenType toCheck) {
    if(token == nullptr)
        return false;
    return token->getType() == toCheck;
}

bool operator!=(const std::shared_ptr<Token> &token, Token::TokenType toCheck) {
    if(token == nullptr)
        return false;
    return !(token == toCheck);
}