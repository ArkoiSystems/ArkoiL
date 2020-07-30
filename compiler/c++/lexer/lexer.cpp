//
// Created by timo on 7/29/20.
//

#include "lexer.h"
#include "../compiler/utils.h"

std::vector<std::shared_ptr<Token>> Lexer::process() {
    std::vector<std::shared_ptr<Token>> tokens;
    while (!failed) {
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
    token->charStart = position;
    token->lineChar = lineChar;

    auto currentChar = sourceCode[position];
    if (currentChar == '\n' || currentChar == ' ' || currentChar == '\r' ||
        currentChar == '\f' || currentChar == '\t') {
        token->type = WHITESPACE;

        switch (currentChar) {
            case '\n':
                token->content = "\\n";
                currentLine++;
                lineChar = position;
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
        token->type = COMMENT;

        while (position < sourceCode.size()) {
            auto commentChar = sourceCode[position];
            if (commentChar == '\n') {
                position--;
                break;
            }

            token->content += std::string(1, commentChar);
            position++;
        }
    } else if (std::isalpha(currentChar)) {
        token->type = COMMENT;

        while (position < sourceCode.size()) {
            auto identifierChar = sourceCode[position];
            if (!std::isalnum(identifierChar)) {
                position--;
                break;
            }

            token->content += std::string(1, identifierChar);
            position++;
        }

        switch (str2int(token->content.c_str())) {
            case str2int("this"):
            case str2int("var"):
            case str2int("return"):
            case str2int("struct"):
            case str2int("fun"):
            case str2int("as"):
            case str2int("import"):
            case str2int("if"):
            case str2int("const"):
            case str2int("else"):
                token->type = KEYWORD;
                break;
            case str2int("bool"):
            case str2int("float"):
            case str2int("double"):
            case str2int("void"):
                token->type = TYPE;
                break;
            default:
                token->type = IDENTIFIER;
                break;
        }

        if ((token->content.rfind('i', 0) == 0 ||
             token->content.rfind('u', 0) == 0) &&
            std::isdigit(sourceCode[position + 1])) {
            token->type = TYPE;
        }
    } else if (std::isdigit(currentChar) ||
               (currentChar == '.' && std::isdigit(sourceCode[position + 1]))) {
        token->type = NUMBER;

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
        token->type = STRING;

        position++;
        while (position < sourceCode.size()) {
            auto identifierChar = sourceCode[position];
            if (identifierChar == '\n') {
                position--;
                break;
            }

            if (identifierChar == '"')
                break;

            token->content += std::string(1, identifierChar);
            position++;
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
                token->type = OPERATOR;

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
                token->type = SYMBOL;
                break;
            default:
                token->type = INVALID;
                failed = true;
                break;

        }
    }

    position++;
    return token;
}