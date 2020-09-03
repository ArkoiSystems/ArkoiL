//
// Created by timo on 7/29/20.
//

#include "lexer.h"
#include <fmt/core.h>
#include "../utils.h"
#include "../compiler/error.h"
#include "token.h"

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
    if (position > sourceCode.size() - 1)
        return nullptr;

    auto token = std::make_shared<Token>();
    token->lineNumber = currentLine;
    token->startChar = position;

    auto currentChar = sourceCode[position];
    if (currentChar == '#') {
        Lexer::parseComment(token);
    } else if (std::isalpha(currentChar)) {
        Lexer::parseIdentifier(token);
    } else if (std::isdigit(currentChar) ||
               (currentChar == '.' && std::isdigit(sourceCode[position + 1]))) {
        Lexer::parseNumber(token);
    } else if (currentChar == '"') {
        Lexer::parseString(token);
    } else {
        Lexer::parseRemaining(token);
    }

    token->endChar = ++position;
    return token;
}

void Lexer::parseComment(const std::shared_ptr<Token> &token) {
    token->type = TOKEN_COMMENT;

    while (position < sourceCode.size() - 1) {
        auto commentChar = sourceCode[position];
        if (commentChar == '\n') {
            position--;
            break;
        }

        token->content += std::string(1, commentChar);
        position++;
    }
}

void Lexer::parseIdentifier(const std::shared_ptr<Token> &token) {
    token->type = TOKEN_COMMENT;

    while (position < sourceCode.size() - 1) {
        auto identifierChar = sourceCode[position];
        if (!std::isalnum(identifierChar) && identifierChar != '_') {
            position--;
            break;
        }

        token->content += std::string(1, identifierChar);
        position++;
    }

    switch (Utils::hash(token->content.c_str())) {
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
            token->type = TOKEN_KEYWORD;
            break;
        case Utils::hash("bool"):
        case Utils::hash("float"):
        case Utils::hash("double"):
        case Utils::hash("void"):
            token->type = TOKEN_TYPE;
            break;
        default:
            token->type = TOKEN_IDENTIFIER;
            break;
    }

    if ((std::strncmp(token->content.c_str(), "i", 1) == 0 ||
         std::strncmp(token->content.c_str(), "u", 1) == 0) &&
        std::isdigit(token->content[1])) {
        token->type = TOKEN_TYPE;
    }
}

void Lexer::parseNumber(const std::shared_ptr<Token> &token) {
    token->type = TOKEN_NUMBER;

    if (sourceCode[position + 1] == 'x') {
        token->content += std::string(1, sourceCode[position]);
        token->content += std::string(1, sourceCode[position++]);

        for (auto index = 0; index < 6; index++) {
            auto hexChar = sourceCode[position];
            if (!std::isalnum(hexChar)) {
                position--;
                break;
            }

            token->content += std::string(1, hexChar);
            position++;
        }
    } else {
        while (position < sourceCode.size() - 1) {
            auto numberChar = sourceCode[position];
            if (numberChar != '_' && !std::isdigit(numberChar))
                break;

            token->content += std::string(1, numberChar);
            position++;
        }

        if (sourceCode[position] == '.')
            token->content += std::string(1, sourceCode[position++]);

        while (position < sourceCode.size() - 1) {
            auto numberChar = sourceCode[position];
            if (numberChar != '_' && !std::isdigit(numberChar)) {
                position--;
                break;
            }

            token->content += std::string(1, numberChar);
            position++;
        }
    }
}

void Lexer::parseString(const std::shared_ptr<Token> &token) {
    token->type = TOKEN_STRING;

    position++;
    while (position < sourceCode.size() - 1) {
        auto identifierChar = sourceCode[position];
        if (identifierChar == '\n')
            break;

        if (identifierChar == '"')
            break;

        token->content += std::string(1, identifierChar);
        position++;
    }

    if (sourceCode[position] != '"') {
        THROW_LEXER_ERROR(token->startChar, position, "Strings must be terminated correctly.")

        if (sourceCode[position] == '\n')
            position--;
    }
}

void Lexer::parseRemaining(const std::shared_ptr<Token> &token) {
    token->content += std::string(1, sourceCode[position]);

    switch (sourceCode[position]) {
        case '=':
        case '!':
        case '+':
        case '-':
        case '*':
        case '/':
        case '%':
        case '<':
        case '>':
            token->type = TOKEN_OPERATOR;

            if (sourceCode[position + 1] == '=' ||
                (sourceCode[position] == '+' && sourceCode[position + 1] == '+') ||
                (sourceCode[position] == '-' && sourceCode[position + 1] == '-')) {
                token->content += std::string(1, sourceCode[position++]);
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
            token->type = TOKEN_SYMBOL;
            break;

        case ' ':
            token->type = TOKEN_WHITESPACE;
            break;
        case '\n':
            token->type = TOKEN_WHITESPACE;
            token->content = "\\n";
            currentLine++;
            break;
        case '\f':
            token->type = TOKEN_WHITESPACE;
            token->content = "\\f";
            break;
        case '\r':
            token->type = TOKEN_WHITESPACE;
            token->content = "\\r";
            break;
        case '\t':
            token->type = TOKEN_WHITESPACE;
            token->content = "\\t";
            break;

        default:
            token->type = TOKEN_INVALID;
            break;
    }
}
