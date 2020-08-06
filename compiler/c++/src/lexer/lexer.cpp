//
// Created by timo on 7/29/20.
//

#include "lexer.h"
#include "../compiler/error.h"
#include "../../deps/dbg-macro/dbg.h"

std::vector<std::shared_ptr<Token>> Lexer::process() {
    std::vector<std::shared_ptr<Token>> tokens;
    while (!isFailed) {
        auto token = nextToken();
        if (token == nullptr)
            break;
        tokens.push_back(token);
    }

    return tokens;
}

std::shared_ptr<Token> Lexer::nextToken() {
    if (position > sourceCode.size())
        return nullptr;

    auto token = std::make_shared<Token>();
    token->lineNumber = currentLine;
    token->startChar = position;

    auto currentChar = sourceCode[position];
    if (currentChar == '\n' || currentChar == ' ' || currentChar == '\r' ||
        currentChar == '\f' || currentChar == '\t' || currentChar == '\0') {
        token->type = TOKEN_WHITESPACE;

        switch (currentChar) {
            case '\n':
                token->content = "\\n";
                currentLine++;
                break;
            case '\f':
                token->content = "\\f";
                break;
            case '\r':
                token->content = "\\r";
                break;
            case '\t':
                token->content = "\\t";
                break;
            default:
                token->content = std::string(1, currentChar);
                break;
        }
    } else if (currentChar == '#') {
        token->type = TOKEN_COMMENT;

        while (position < sourceCode.size()) {
            auto commentChar = sourceCode[position];
            if (commentChar == '\n') {
                position--;
                break;
            }

            token->content += std::string(1, commentChar);
            position++;
        }
    } else if (std::isalpha(currentChar) || currentChar == '_') {
        token->type = TOKEN_COMMENT;

        while (position < sourceCode.size()) {
            auto identifierChar = sourceCode[position];
            if (!std::isalnum(identifierChar) && identifierChar != '_') {
                position--;
                break;
            }

            token->content += std::string(1, identifierChar);
            position++;
        }

        switch (hash(token->content.c_str())) {
            case hash("this"):
            case hash("var"):
            case hash("return"):
            case hash("struct"):
            case hash("fun"):
            case hash("as"):
            case hash("import"):
            case hash("if"):
            case hash("const"):
            case hash("else"):
                token->type = TOKEN_KEYWORD;
                break;
            case hash("bool"):
            case hash("float"):
            case hash("double"):
            case hash("void"):
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
    } else if (std::isdigit(currentChar) ||
               (currentChar == '.' && std::isdigit(sourceCode[position + 1]))) {
        token->type = TOKEN_NUMBER;

        if (sourceCode[position + 1] == 'x') {
            token->content += std::string(1, currentChar);
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
            while (position < sourceCode.size()) {
                auto numberChar = sourceCode[position];
                if (numberChar != '_' && !std::isdigit(numberChar))
                    break;

                token->content += std::string(1, numberChar);
                position++;
            }

            if (sourceCode[position] == '.')
                token->content += std::string(1, sourceCode[position++]);

            while (position < sourceCode.size()) {
                auto numberChar = sourceCode[position];
                if (numberChar != '_' && !std::isdigit(numberChar)) {
                    position--;
                    break;
                }

                token->content += std::string(1, numberChar);
                position++;
            }
        }
    } else if (currentChar == '"') {
        token->type = TOKEN_STRING;

        position++;
        while (position < sourceCode.size()) {
            auto identifierChar = sourceCode[position];
            if (identifierChar == '\n')
                break;

            if (identifierChar == '"')
                break;

            token->content += std::string(1, identifierChar);
            position++;
        }

        if (sourceCode[position] != '"') {
            std::cout << Error(sourcePath, sourceCode, currentLine, currentLine,
                               token->startChar, position,
                               fmt::format("Strings must be terminated correctly."))
                      << std::endl;

            if (sourceCode[position] == '\n')
                position--;
        }
    } else {
        token->content += std::string(1, currentChar);

        switch (currentChar) {
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
                    (currentChar == '+' && sourceCode[position + 1] == '+') ||
                    (currentChar == '-' && sourceCode[position + 1] == '-')) {
                    token->content += std::string(1,
                                                  sourceCode[position++]);
                }
                break;
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
            default:
                token->type = TOKEN_INVALID;
                isFailed = true;
                break;
        }
    }

    token->endChar = ++position;
    return token;
}