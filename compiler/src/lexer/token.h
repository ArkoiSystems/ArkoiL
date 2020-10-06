//
// Created by timo on 7/30/20.
//

#pragma once

#include <iostream>
#include <string>
#include <memory>

class Token {

public:
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

private:
    unsigned int m_LineNumber, m_StartChar, m_EndChar;
    std::string m_Content;
    TokenType m_Type;

public:
    Token();

    Token(const Token &);

    Token &operator=(const Token &) = delete;

public:
    void addContent(const std::string &content);

public:
    [[nodiscard]]
    unsigned int getLineNumber() const;

    void setLineNumber(unsigned int lineNumber);

    [[nodiscard]]
    unsigned int getStartChar() const;

    void setStartChar(unsigned int startChar);

    [[nodiscard]]
    unsigned int getEndChar() const;

    void setEndChar(unsigned int endChar);

    [[nodiscard]]
    const std::string &getContent() const;

    void setContent(const std::string &content);

    [[nodiscard]]
    TokenType getType() const;

    void setType(TokenType type);

public:
    friend std::ostream &operator<<(std::ostream &out, const std::shared_ptr<Token> &token);

    friend bool operator==(const std::shared_ptr<Token> &token, const std::string &toCheck);

    friend bool operator!=(const std::shared_ptr<Token> &token, const std::string &toCheck);

    friend bool operator==(const std::shared_ptr<Token> &token, TokenType toCheck);

    friend bool operator!=(const std::shared_ptr<Token> &token, TokenType toCheck);

};