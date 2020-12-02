//
// Created by timo on 7/31/20.
//

#pragma once

#include <string>

#include <fmt/core.h>

#define THROW_NODE_ERROR(node, ...) \
        std::cout << Error(Error::NOTE,         \
                           node->findNodeOfParents<RootNode>()->getSourcePath(),  \
                           node->findNodeOfParents<RootNode>()->getSourceCode(),  \
                           node->getStartToken()->getLineNumber(),  \
                           node->getEndToken()->getLineNumber(),  \
                           node->getStartToken()->getStartChar(),  \
                           node->getEndToken()->getEndChar(),  \
                           fmt::format(__VA_ARGS__));

class Error {

public:
    enum ErrorType {
        WARN,
        ERROR,
        NOTE
    };

private:
    unsigned int m_StartLine, m_EndLine, m_StartChar, m_EndChar;
    std::string m_SourcePath, m_SourceCode, m_CauseMessage;
    ErrorType m_ErrorType;

public:
    Error(ErrorType errorType, std::string sourcePath, std::string sourceCode,
          unsigned int startLine, unsigned int endLine, unsigned int startChar,
          unsigned int endChar, std::string causeMessage);

    Error(const Error &) = delete;

    Error &operator=(const Error &) = delete;

public:
    friend std::ostream &operator<<(std::ostream &out, const Error &error);

};
