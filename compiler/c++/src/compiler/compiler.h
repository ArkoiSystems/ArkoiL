//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_COMPILER_H
#define ARKOICOMPILER_COMPILER_H

#include <sys/stat.h>
#include <utility>
#include <fstream>
#include "../parser/parser.h"
#include "../lexer/lexer.h"
#include "options.h"
#include "utils.h"

class Compiler {

private:
    CompilerOptions compilerOptions;

public:
    explicit Compiler(CompilerOptions compilerOptions) : compilerOptions(
            std::move(compilerOptions)) {}

public:
    int compile();

};

#endif //ARKOICOMPILER_COMPILER_H
