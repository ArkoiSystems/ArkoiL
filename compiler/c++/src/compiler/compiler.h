//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_COMPILER_H
#define ARKOICOMPILER_COMPILER_H

#include <sys/stat.h>
#include <utility>
#include <fstream>
#include <set>
#include "../parser/typeresolver.h"
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
    void loadImports(std::set<std::string> &loaded,
                     std::vector<std::shared_ptr<RootNode>> &roots);

    static std::shared_ptr<Parser> loadFile(const std::string &sourcePath);

    int compile();

};

#endif //ARKOICOMPILER_COMPILER_H
