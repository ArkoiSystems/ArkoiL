//
// Created by timo on 7/29/20.
//

#include "compiler.h"

#include <sys/stat.h>
#include <unistd.h>

#include <fstream>
#include <chrono>

#include <llvm/IR/Verifier.h>

#include "../semantic/typeresolver.h"
#include "../semantic/scopecheck.h"
#include "../semantic/typecheck.h"
#include "../parser/astnodes.h"
#include "../codegen/codegen.h"
#include "../semantic/inliner.h"
#include "../parser/parser.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "options.h"
#include "error.h"

int Compiler::compile(const CompilerOptions &compilerOptions) {
    auto start = std::chrono::high_resolution_clock::now();

    std::vector<std::shared_ptr<RootNode>> roots;

    std::shared_ptr<RootNode> sourceRoot;
    if (auto parser = Compiler::loadFile(compilerOptions.m_SourceFile)) {
        sourceRoot = parser->parseRoot();
        roots.push_back(sourceRoot);
    } else return EXIT_FAILURE;

    std::set<std::string> loaded;
    while (true) {
        auto lastSize = loaded.size();
        auto error = Compiler::loadImports(compilerOptions, loaded, roots);
        if (error != EXIT_SUCCESS)
            return error;
        if (lastSize == loaded.size())
            break;
    }

    for (const auto &rootNode : roots)
        TypeResolver::visit(rootNode);

    for (const auto &rootNode : roots) {
        Inliner inliner;
        inliner.visit(rootNode);
    }

    for (const auto &rootNode : roots) {
        TypeCheck::visit(rootNode);
        ScopeCheck::visit(rootNode);
    }

    CodeGen codeGen;
    codeGen.visit(sourceRoot);
    auto module = codeGen.getModule();

    if (compilerOptions.mb_VerboseLLVM_IR) {
        std::cout << "[" << module->getModuleIdentifier() << "] Printing the bitcode:" << std::endl;
        std::cout << codeGen.dumpModule() << std::endl;
    }

    if (compilerOptions.mb_VerboseModule_Verify)
        std::cout << std::endl << "[" << module->getModuleIdentifier() << "] Verifying the module:"
                  << std::endl;

    std::string errors;
    llvm::raw_string_ostream output(errors);
    if (llvm::verifyModule(*module, &output, nullptr)) {
        if (compilerOptions.mb_VerboseModule_Verify) {
            std::cout << "There was a problem during the verification of the module: "
                      << std::endl << " " << errors << std::endl;
            exit(EXIT_FAILURE);
        } else {
            std::cout << std::endl << "There was a problem during the verification of the module."
                      << std::endl << "If you want to have more information about it use the \"-vmv\" option."
                      << std::endl;
            exit(EXIT_FAILURE);
        }
    }

    if (compilerOptions.mb_VerboseModule_Verify)
        std::cout << "Verified the module and found no errors." << std::endl;

    auto finish = std::chrono::high_resolution_clock::now();
    auto elapsed = std::chrono::duration_cast<std::chrono::microseconds>(finish - start);
    std::cout << std::endl << "Elapsed time: " << elapsed.count() << "μs\n";

    return EXIT_SUCCESS;
}

int Compiler::loadImports(const CompilerOptions &compilerOptions, std::set<std::string> &loaded,
                          std::vector<std::shared_ptr<RootNode>> &roots) {
    for (const auto &rootNode : roots) {
        for (const auto &node : rootNode->getNodes()) {
            if (node->getKind() != ASTNode::IMPORT)
                continue;

            auto importNode = std::dynamic_pointer_cast<ImportNode>(node);
            std::shared_ptr<RootNode> importRoot;

            for (const auto &searchPath : compilerOptions.m_SearchPaths) {
                auto fullPath = searchPath + "/" + importNode->getPath()->getContent() + ".ark";

                struct stat path_stat{};
                stat(fullPath.c_str(), &path_stat);
                if (!S_ISREG(path_stat.st_mode) || access(fullPath.c_str(), F_OK) == -1)
                    continue;

                std::string realPath = realpath(fullPath.c_str(), nullptr);
                fullPath = realPath;

                if (loaded.find(fullPath) != loaded.end()) {
                    for (const auto &loadedRoot : roots) {
                        if (loadedRoot->getSourcePath() == fullPath) {
                            importRoot = loadedRoot;
                            break;
                        }
                    }
                    break;
                }

                if (auto parser = Compiler::loadFile(fullPath)) {
                    loaded.insert(fullPath);

                    importRoot = parser->parseRoot();
                    roots.push_back(importRoot);
                    break;
                }
            }

            if (!importRoot) {
                THROW_NODE_ERROR(importNode, "Couldn't find the file with this path.")
                return EXIT_FAILURE;
            }

            importNode->setTarget(importRoot);
        }
    }

    return EXIT_SUCCESS;
}

std::shared_ptr<Parser> Compiler::loadFile(const std::string &sourcePath) {
    struct stat path_stat{};
    stat(sourcePath.c_str(), &path_stat);
    if (!S_ISREG(path_stat.st_mode)) {
        std::cout << "The given source path is not a file: " << sourcePath << std::endl;
        return nullptr;
    }

    std::ifstream sourceFile;
    sourceFile.open(sourcePath);
    if (!sourceFile.is_open()) {
        std::cout << "Couldn't open this file: " << sourcePath << std::endl;
        return nullptr;
    }

    auto contents = std::string{std::istreambuf_iterator<char>(sourceFile),
                                std::istreambuf_iterator<char>()};
    sourceFile.close();

    Lexer lexer{sourcePath, contents};
    auto tokens = lexer.getTokens();

    return std::make_shared<Parser>(sourcePath, contents, tokens);
}