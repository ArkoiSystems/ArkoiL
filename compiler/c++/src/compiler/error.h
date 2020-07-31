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
          std::shared_ptr<Token> token,
          std::string causeMessage) :
            sourcePath(std::move(sourcePath)),
            sourceCode(std::move(sourceCode)),
            causeMessage(causeMessage) {
        startLine = token->lineNumber;
    }

public:
    friend std::ostream &operator<<(std::ostream &out, const Error &error);

};

#endif //ARKOICOMPILER_ERROR_H
