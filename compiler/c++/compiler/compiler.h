//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_COMPILER_H
#define ARKOICOMPILER_COMPILER_H

#include <utility>

#include "options.h"

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
