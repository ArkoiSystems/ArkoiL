//
// Created by timo on 7/29/20.
//

#include "lexer.h"

#include <iostream>
#include <utility>

#include <fmt/core.h>

#include "../compiler/error.h"
#include "../utils.h"
#include "token.h"

Lexer::Lexer(std::string sourcePath, std::string sourceCode)
        : m_SourceCode(std::move(sourceCode)), m_SourcePath(std::move(sourcePath)),
          m_CurrentLine(0), m_Position(0) {}

std::vector<std::shared_ptr<Token>> Lexer::getTokens() {
    std::vector<std::shared_ptr<Token>> tokens;
    while (true) {
        auto token = nextToken();
        if (token == nullptr)
            break;
        tokens.push_back(token);
    }

    return tokens;
}

std::shared_ptr<Token> Lexer::nextToken() {
    if (m_Position > m_SourceCode.size() - 1)
        return nullptr;

    auto token = std::make_shared<Token>();
    token->setLineNumber(m_CurrentLine);
    token->setStartChar(m_Position);

    auto currentChar = m_SourceCode[m_Position];
    if (currentChar == '#') {
        Lexer::parseComment(token);
    } else if (std::isalpha(currentChar)) {
        Lexer::parseIdentifier(token);
    } else if (std::isdigit(currentChar) ||
               (currentChar == '.' && std::isdigit(m_SourceCode[m_Position + 1]))) {
        Lexer::parseNumber(token);
    } else if (currentChar == '"') {
        Lexer::parseString(token);
    } else {
        Lexer::parseRemaining(token);
    }

    token->setEndChar(++m_Position);
    return token;
}

void Lexer::parseComment(const std::shared_ptr<Token> &token) {
    token->setType(Token::COMMENT);

    while (m_Position < m_SourceCode.size() - 1) {
        auto commentChar = m_SourceCode[m_Position];
        if (commentChar == '\n') {
            m_Position--;
            break;
        }

        token->addContent(std::string(1, commentChar));
        m_Position++;
    }
}

void Lexer::parseIdentifier(const std::shared_ptr<Token> &token) {
    token->setType(Token::COMMENT);

    while (m_Position < m_SourceCode.size() - 1) {
        auto identifierChar = m_SourceCode[m_Position];
        if (!std::isalnum(identifierChar) && identifierChar != '_') {
            m_Position--;
            break;
        }

        token->addContent(std::string(1, identifierChar));
        m_Position++;
    }

    switch (Utils::hash(token->getContent().c_str())) {
        case Utils::hash("this"):
        case Utils::hash("var"):
        case Utils::hash("return"):
        case Utils::hash("struct"):
        case Utils::hash("fun"):
        case Utils::hash("as"):
        case Utils::hash("import"):
        case Utils::hash("if"):
        case Utils::hash("const"):
        case Utils::hash("else"):
        case Utils::hash("bitcast"):
            token->setType(Token::KEYWORD);
            break;
        case Utils::hash("bool"):
        case Utils::hash("float"):
        case Utils::hash("double"):
        case Utils::hash("void"):
            token->setType(Token::TYPE);
            break;
        default:
            token->setType(Token::IDENTIFIER);
            break;
    }

    if ((std::strncmp(token->getContent().c_str(), "i", 1) == 0 ||
         std::strncmp(token->getContent().c_str(), "u", 1) == 0) &&
        std::isdigit(token->getContent()[1])) {
        token->setType(Token::TYPE);
    }
}

void Lexer::parseNumber(const std::shared_ptr<Token> &token) {
    token->setType(Token::NUMBER);

    if (m_SourceCode[m_Position + 1] == 'x') {
        token->addContent(std::string(1, m_SourceCode[m_Position]));
        token->addContent(std::string(1, m_SourceCode[m_Position++]));

        for (auto index = 0; index < 6; index++) {
            auto hexChar = m_SourceCode[m_Position];
            if (!std::isalnum(hexChar)) {
                m_Position--;
                break;
            }

            token->addContent(std::string(1, hexChar));
            m_Position++;
        }
    } else {
        while (m_Position < m_SourceCode.size() - 1) {
            auto numberChar = m_SourceCode[m_Position];
            if (numberChar != '_' && !std::isdigit(numberChar))
                break;

            token->addContent(std::string(1, numberChar));
            m_Position++;
        }

        if (m_SourceCode[m_Position] == '.')
            token->addContent(std::string(1, m_SourceCode[m_Position++]));

        while (m_Position < m_SourceCode.size() - 1) {
            auto numberChar = m_SourceCode[m_Position];
            if (numberChar != '_' && !std::isdigit(numberChar)) {
                m_Position--;
                break;
            }

            token->addContent(std::string(1, numberChar));
            m_Position++;
        }
    }
}

void Lexer::parseString(const std::shared_ptr<Token> &token) {
    token->setType(Token::STRING);

    m_Position++;
    while (m_Position < m_SourceCode.size() - 1) {
        auto identifierChar = m_SourceCode[m_Position];
        if (identifierChar == '\n')
            break;

        if (identifierChar == '"')
            break;

        token->addContent(std::string(1, identifierChar));
        m_Position++;
    }

    if (m_SourceCode[m_Position] != '"') {
        THROW_LEXER_ERROR(token->getStartChar(), m_Position, "Strings must be terminated correctly.")

        if (m_SourceCode[m_Position] == '\n')
            m_Position--;
    }
}

void Lexer::parseRemaining(const std::shared_ptr<Token> &token) {
    token->addContent(std::string(1, m_SourceCode[m_Position]));

    switch (m_SourceCode[m_Position]) {
        case '=':
        case '!':
        case '+':
        case '-':
        case '*':
        case '/':
        case '%':
        case '<':
        case '>':
            token->setType(Token::OPERATOR);

            if (m_SourceCode[m_Position + 1] == '=' ||
                (m_SourceCode[m_Position] == '+' && m_SourceCode[m_Position + 1] == '+') ||
                (m_SourceCode[m_Position] == '-' && m_SourceCode[m_Position + 1] == '-')) {
                token->addContent(std::string(1, m_SourceCode[m_Position++]));
            }
            break;

        case '_':
        case '@':
        case '^':
        case ':':
        case '{':
        case '}':
        case '(':
        case ')':
        case '[':
        case ']':
        case '.':
        case '?':
        case '&':
        case ',':
            token->setType(Token::SYMBOL);
            break;

        case ' ':
            token->setType(Token::WHITESPACE);
            break;
        case '\n':
            token->setType(Token::WHITESPACE);
            token->setContent("\\n");
            m_CurrentLine++;
            break;
        case '\f':
            token->setType(Token::WHITESPACE);
            token->setContent("\\f");
            break;
        case '\r':
            token->setType(Token::WHITESPACE);
            token->setContent("\\r");
            break;
        case '\t':
            token->setType(Token::WHITESPACE);
            token->setContent("\\t");
            break;

        default:
            token->setType(Token::INVALID);
            break;
    }
}
