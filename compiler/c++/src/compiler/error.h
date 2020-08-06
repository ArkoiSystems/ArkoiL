//
// Created by timo on 7/31/20.
//

#ifndef ARKOICOMPILER_ERROR_H
#define ARKOICOMPILER_ERROR_H

#include <utility>
#include <string>
#include "../parser/parser.h"
#include "../lexer/token.h"

#define THROW_TOKEN_ERROR(...)  \
        std::cout << Error(sourcePath,  \
                           sourceCode,  \
                           currentToken()->lineNumber,  \
                           currentToken()->lineNumber,  \
                           currentToken()->startChar,  \
                           currentToken()->endChar,  \
                           fmt::format(__VA_ARGS__));

#define THROW_NODE_ERROR(sourcePath, sourceCode, node, ...) \
        std::cout << Error(sourcePath,  \
                           sourceCode,  \
                           node->startToken->lineNumber,  \
                           node->endToken->lineNumber,  \
                           node->startToken->startChar,  \
                           node->endToken->endChar,  \
                           fmt::format(__VA_ARGS__));

class Error {

private:
    std::string sourcePath, sourceCode, causeMessage;
    unsigned int startLine, endLine, startChar, endChar;

public:
    Error(std::string sourcePath, std::string sourceCode, unsigned int startLine,
          unsigned int endLine, unsigned int startChar, unsigned int endChar,
          std::string causeMessage) :
            sourcePath(std::move(sourcePath)),
            sourceCode(std::move(sourceCode)),
            startLine(startLine),
            endLine(endLine),
            startChar(startChar),
            endChar(endChar),
            causeMessage(std::move(causeMessage)) {}

public:
    friend std::ostream &operator<<(std::ostream &out, const Error &error);

};

#endif //ARKOICOMPILER_ERROR_H
