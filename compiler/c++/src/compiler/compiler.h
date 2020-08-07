//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_COMPILER_H
#define ARKOICOMPILER_COMPILER_H

#include <sys/stat.h>
#include <utility>
#include <fstream>
#include <memory>
#include <vector>
#include <set>
#include "options.h"

class RootNode;
class Parser;

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
