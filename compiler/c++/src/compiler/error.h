//
// Created by timo on 7/31/20.
//

#ifndef ARKOICOMPILER_ERROR_H
#define ARKOICOMPILER_ERROR_H

#include <fmt/core.h>
#include <string>

#define THROW_TOKEN_ERROR(...) \
        std::cout << Error(sourcePath,  \
                           sourceCode,  \
                           currentToken()->lineNumber,  \
                           currentToken()->lineNumber,  \
                           currentToken()->startChar,  \
                           currentToken()->endChar,  \
                           fmt::format(__VA_ARGS__));

#define THROW_NODE_ERROR(node, ...) \
        std::cout << Error(node->getParent<RootNode>()->sourcePath,  \
                           node->getParent<RootNode>()->sourceCode,  \
                           node->startToken->lineNumber,  \
                           node->endToken->lineNumber,  \
                           node->startToken->startChar,  \
                           node->endToken->endChar,  \
                           fmt::format(__VA_ARGS__));

#define THROW_LEXER_ERROR(startChar, endChar, ...) \
        std::cout << Error(sourcePath,  \
                           sourceCode,  \
                           currentLine, \
                           currentLine, \
                           startChar,  \
                           endChar,  \
                           fmt::format(__VA_ARGS__));

class Error {

private:
    unsigned int startLine, endLine, startChar, endChar;
    std::string sourcePath, sourceCode, causeMessage;

public:
    Error(const std::string &sourcePath, const std::string &sourceCode, unsigned int startLine,
          unsigned int endLine, unsigned int startChar, unsigned int endChar,
          const std::string &causeMessage);

public:
    friend std::ostream &operator<<(std::ostream &out, const Error &error);

};

#endif //ARKOICOMPILER_ERROR_H
