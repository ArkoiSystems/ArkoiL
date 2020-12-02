//
// Created by timo on 7/29/20.
//

#pragma once

#include <vector>
#include <string>
#include <memory>

class Token;

class Lexer {

#define THROW_LEXER_ERROR(startChar, endChar, ...) \
        std::cout << Error(Error::NOTE,            \
                           m_SourcePath,  \
                           m_SourceCode,  \
                           m_CurrentLine, \
                           m_CurrentLine, \
                           startChar,  \
                           endChar,  \
                           fmt::format(__VA_ARGS__));

private:
    unsigned int m_Position, m_CurrentLine;
    std::string m_SourceCode, m_SourcePath;

public:
    Lexer(std::string sourcePath, std::string sourceCode);

    Lexer(const Lexer &) = delete;

    Lexer &operator=(const Lexer &) = delete;

public:
    std::vector<std::shared_ptr<Token>> getTokens();

private:
    std::shared_ptr<Token> nextToken();

    void parseComment(const std::shared_ptr<Token> &token);

    void parseIdentifier(const std::shared_ptr<Token> &token);

    void parseNumber(const std::shared_ptr<Token> &token);

    void parseString(const std::shared_ptr<Token> &token);

    void parseMultiLineString(const std::shared_ptr<Token> &token);

    void parseRemaining(const std::shared_ptr<Token> &token);

};