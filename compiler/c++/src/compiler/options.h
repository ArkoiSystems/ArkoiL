//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_OPTIONS_H
#define ARKOICOMPILER_OPTIONS_H

#include <vector>

struct CompilerOptions {
    std::vector<std::string> searchPaths;
    std::string sourceFile;
};

#endif //ARKOICOMPILER_OPTIONS_H
