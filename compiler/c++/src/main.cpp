#define DBG_MACRO_NO_WARNING

#include <iostream>
#include <cstring>
#include "compiler/compiler.h"
#include "compiler/options.h"

#include "../deps/dbg-macro/dbg.h"

void printUsage();

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printUsage();
        return EXIT_FAILURE;
    }

    CompilerOptions compilerOptions{};
    unsigned int index = 1;
    if (strcmp(argv[index], "build-exe") == 0 && argc >= 3) {
        index = 2;
        compilerOptions.sourceFile = argv[index++];
    } else {
        if (strcmp(argv[index], "help") == 0)
            printUsage();
        else
            std::cout << "Unknown command: " << argv[index]
                      << ". Use ark --help for more information." << std::endl;
        return EXIT_FAILURE;
    }

    for (; index < argc; index++) {
        if (strncmp(argv[index], "-I", 2) == 0) {
            auto filePath = std::string(argv[index]);
            filePath = filePath.substr(2, filePath.size());
            compilerOptions.searchPaths.push_back(filePath);
        } else {
            std::cout << "Unknown option: " << argv[index]
                      << ". Use ark help for more information." << std::endl;
            return EXIT_FAILURE;
        }
    }

    compilerOptions.searchPaths.push_back(
            compilerOptions.sourceFile.substr(0,compilerOptions.sourceFile.rfind('/')));
    compilerOptions.searchPaths.emplace_back("../../../natives");
    compilerOptions.searchPaths.emplace_back("");

    return Compiler::compile(compilerOptions);
}

void printUsage() {
    std::cout << "Usage: ark [command] [options]\n"
                 "\n"
                 "Commands:\n"
                 "   build-exe [source]             create executable from a source file.\n"
                 "   help                           prints this list in the console.\n"
                 "\n"
                 "Options:\n"
                 "   -I                             add directory to include search path.\n"
              << std::endl;
}