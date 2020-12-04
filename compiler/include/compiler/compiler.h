//
// Created by timo on 7/29/20.
//

#pragma once

#include <memory>
#include <vector>
#include <set>

#include "../parser/allnodes.h"

class CompilerOptions;

class Parser;

class Compiler {

public:
    Compiler() = delete;

    Compiler(const Compiler &) = delete;

    Compiler &operator=(const Compiler &) = delete;

public:
    static int compile(const CompilerOptions &compilerOptions);

    static int loadImports(const CompilerOptions &root,
                           std::set<std::string> &loaded,
                           std::vector<SharedRootNode> &roots);

    static std::shared_ptr<Parser> loadFile(const std::string &sourcePath);

private:
    template<class... Args>
    static void throwNode(unsigned int errorType, const SharedASTNode &node, Args...args);

};
