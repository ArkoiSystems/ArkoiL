//
// Created by timo on 7/31/20.
//

#pragma once

#include <string>

class Error {

public:
    enum ErrorType : unsigned int {
        WARN = 0u,
        ERROR = 1u,
        NOTE = 2u
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
