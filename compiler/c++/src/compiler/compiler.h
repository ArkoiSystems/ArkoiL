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

class CompilerOptions;

class RootNode;

class Parser;

class Compiler {

public:
    Compiler() = delete;

public:
    static int compile(const CompilerOptions &compilerOptions);

    static int loadImports(const CompilerOptions &root,
                           std::set<std::string> &loaded,
                           std::vector<std::shared_ptr<RootNode>> &roots);

    static std::shared_ptr<Parser> loadFile(const std::string &sourcePath);

};

#endif //ARKOICOMPILER_COMPILER_H
