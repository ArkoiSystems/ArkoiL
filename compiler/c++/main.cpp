#include <iostream>
#include <cstring>
#include "compiler/options.h"
#include "compiler/compiler.h"

void printUsage();

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printUsage();
        return EXIT_FAILURE;
    }

    CompilerOptions compilerOptions{};
    unsigned int index = 1;
    if (strcmp(argv[index], "build-exe") == 0 && argc >= 3) {
        compilerOptions.commandType = EXE;

        index = 2;
        for (; index < argc; index++) {
            auto sourceFile = argv[index];
            if (strncmp("-", sourceFile, 1) == 0)
                break;
            compilerOptions.sourceFiles.emplace_back(sourceFile);
        }
    } else if (strcmp(argv[index], "run") == 0 && argc >= 3) {
        compilerOptions.commandType = RUN;

        index = 2;
        for (; index < argc; index++) {
            auto sourceFile = argv[index];
            if (strncmp("-", sourceFile, 1) == 0)
                break;
            compilerOptions.sourceFiles.emplace_back(sourceFile);
        }
    } else {
        if (strcmp(argv[index], "help") == 0)
            printUsage();
        else
            std::cout << "Unknown command: " << argv[index]
                      << ". Use ark --help for more information." << std::endl;
        return EXIT_FAILURE;
    }

    for (; index < argc; index++) {
        if (strcmp(argv[index], "-emit-llvm-ir") == 0)
            compilerOptions.emitLLVMIR = true;
        else if (compilerOptions.commandType == RUN)
            compilerOptions.runArguments.emplace_back(argv[index]);
        else {
            std::cout << "Unknown option: " << argv[index]
                      << ". Use ark --help for more information." << std::endl;
            return EXIT_FAILURE;
        }
    }

    if (compilerOptions.sourceFiles.empty()) {
        std::cout << "You need at least one specified source file." << std::endl;
        return EXIT_FAILURE;
    }

    auto compiler = Compiler(compilerOptions);
    return compiler.compile();
}

void printUsage() {
    std::cout << "Usage: ark [command] [options]\n"
                 "\n"
                 "Commands:\n"
                 "   run [sources] [args]           create executable from source and run immediately.\n"
                 "   build-exe [sources]            create executable from source or object files.\n"
                 "   help                           prints this list in the console.\n"
                 "\n"
                 "Options:\n"
                 "   -emit-llvm-ir                  gives out a .ll file with LLVM-IR."
              << std::endl;
}