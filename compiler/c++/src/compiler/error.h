//
// Created by timo on 7/31/20.
//

#ifndef ARKOICOMPILER_ERROR_H
#define ARKOICOMPILER_ERROR_H

#include <string>
#include <utility>
#include "../parser/parser.h"
#include "../lexer/token.h"

class Error {

private:
    std::string sourcePath, sourceCode, causeMessage;
    unsigned int startLine;

public:
    Error(std::string sourcePath,
          std::string sourceCode,
          unsigned int startLine,
          std::string causeMessage) :
            sourcePath(std::move(sourcePath)),
            sourceCode(std::move(sourceCode)),
            startLine(startLine),
            causeMessage(causeMessage) { }

public:
    friend std::ostream &operator<<(std::ostream &out, const Error &error);

};

#endif //ARKOICOMPILER_ERROR_H
