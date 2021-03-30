#include <iostream>

#include <filesystem>

#include <cxxopts.hpp>

#include "../include/compiler/compiler.h"
#include "../include/compiler/options.h"

std::shared_ptr<CompilerOptions> parse(int argc, char *argv[]) {
    auto compilerOptions = std::make_shared<CompilerOptions>();

    auto baseName = std::filesystem::path(argv[0]).filename();
    cxxopts::Options options(baseName, "The offical Arkoi Language compiler");
    options.show_positional_help();

    options.add_options("general")
            ("e,entry", "Entry file for the compiler.", cxxopts::value<std::string>())
            ("I,include","Adds the directory to the search paths.",cxxopts::value<std::vector<std::string>>())
            ("h,help", "Prints this list in the console.");
    options.add_options("verbose")
            ("vlir", "Enables debugging in the console for the LLVM IR.")
            ("vmv", "Enables debugging in the console for the module verify.")
            ("var", "Enables debugging in the console for the arkoi representation.");

    try {
        auto result = options.parse(argc, argv);
        if (result.count("help"))
            throw std::runtime_error("Print help");

        if(!result.count("e")) {
            std::cout << "You need to declare an entry file for the compiler. "
                         "Use --help to see a list of options." << std::endl;
            exit(EXIT_FAILURE);
        }

        compilerOptions->m_SourceFile = result["e"].as<std::string>();
        compilerOptions->mb_VerboseLLVM_IR = result["vlir"].as<bool>();
        compilerOptions->mb_VerboseModuleVerify = result["vmv"].as<bool>();
        compilerOptions->mb_VerboseArkoiRepresentation = result["var"].as<bool>();

        if(result.count("I"))
            compilerOptions->m_SearchPaths = result["I"].as<std::vector<std::string>>();

        compilerOptions->m_SearchPaths.push_back(compilerOptions->m_SourceFile.substr(0, compilerOptions->m_SourceFile.rfind('/')));
    } catch (...) {
        std::cout << options.help() << std::endl;
        exit(EXIT_FAILURE);
    }

    return compilerOptions;
}

int main(int argc, char *argv[]) {
    auto compilerOptions = parse(argc, argv);
    return Compiler::compile(compilerOptions);
}