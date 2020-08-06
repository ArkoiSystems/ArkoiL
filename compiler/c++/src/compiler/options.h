//
// Created by timo on 7/29/20.
//

#ifndef ARKOICOMPILER_OPTIONS_H
#define ARKOICOMPILER_OPTIONS_H

#include <iostream>
#include <vector>

enum CommandType {
    EXE,
    RUN
};

struct CompilerOptions {
    CommandType commandType;

    std::vector<std::string> runArguments;
    std::vector<std::string> sourceFiles;
    std::vector<std::string> searchPaths;

    bool emitLLVMIR;
};

#endif //ARKOICOMPILER_OPTIONS_H
