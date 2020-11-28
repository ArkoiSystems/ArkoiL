#include <iostream>

#include "../include/utils/optionparser.h"
#include "../include/compiler/compiler.h"
#include "../include/compiler/options.h"

int main(int argc, char *argv[]) {
    CompilerOptions compilerOptions{};
    OptionParser args("The official Arkoi Language Compiler.");

    args.addArgument({"-e", "--entry"},
                     &compilerOptions.m_SourceFile,
                     "Entry file for the compiler.");
    args.addArgument({"-I", "--include"},
                     OptionParser::OptionValue(),
                     "Adds the directory to the search paths.",
                     [&compilerOptions](const std::string &value) {
                         compilerOptions.m_SearchPaths.push_back(value);
                     });
    args.addArgument({"-vlir", "--verbose-llvm-ir"},
                     &compilerOptions.mb_VerboseLLVM_IR,
                     "Enables debugging in the console for the LLVM IR.");
    args.addArgument({"-vmv", "--verbose-module-verify"},
                     &compilerOptions.mb_VerboseModuleVerify,
                     "Enables debugging in the console for the module verify.");
    args.addArgument({"-var", "--verbose-arkoi-representation"},
                     &compilerOptions.mb_VerboseArkoiRepresentation,
                     "Enables debugging in the console for the arkoi representation.");
    args.addArgument({"-h", "--help"},
                     OptionParser::OptionValue(),
                     "Prints this list in the console.",
                     [&args](const std::string &value) {
                         args.printHelp();
                     });

    try {
        args.parse(argc, argv);
    } catch (const std::runtime_error &error) {
        std::cout << error.what() << std::endl;
        return EXIT_FAILURE;
    }

    if (compilerOptions.m_SourceFile.empty()) {
        std::cout << "You need to declare an entry file for the compiler. "
                     "Use --help to see a list of options." << std::endl;
        return EXIT_FAILURE;
    }

    compilerOptions.m_SearchPaths.push_back(
            compilerOptions.m_SourceFile.substr(0, compilerOptions.m_SourceFile.rfind('/')));
    compilerOptions.m_SearchPaths.emplace_back("../../stdlib");
    compilerOptions.m_SearchPaths.emplace_back("");

    return Compiler::compile(compilerOptions);
}