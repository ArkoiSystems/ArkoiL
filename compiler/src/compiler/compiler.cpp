//
// Created by timo on 7/29/20.
//

#include "../../include/compiler/compiler.h"

#include <filesystem>
#include <fstream>
#include <chrono>

#include <llvm/IR/Verifier.h>
#include <fmt/core.h>

#include "../../include/semantic/typeresolver.h"
#include "../../include/semantic/scopecheck.h"
#include "../../include/semantic/typecheck.h"
#include "../../include/utils/astprinter.h"
#include "../../include/compiler/options.h"
#include "../../include/semantic/inliner.h"
#include "../../include/parser/astnodes.h"
#include "../../include/codegen/codegen.h"
#include "../../include/compiler/error.h"
#include "../../include/parser/parser.h"
#include "../../include/lexer/lexer.h"
#include "../../include/lexer/token.h"

int Compiler::compile(const std::shared_ptr<CompilerOptions> &compilerOptions) {
    auto start = std::chrono::high_resolution_clock::now();

    std::vector<SharedRootNode> roots;

    SharedRootNode sourceRoot;
    if (auto parser = Compiler::loadFile(compilerOptions->m_SourceFile)) {
        sourceRoot = parser->parseRoot();
        roots.emplace_back(sourceRoot);
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

    for (const auto &rootNode : roots)
        Inliner::visit(rootNode);

    for (const auto &rootNode : roots) {
        TypeCheck::visit(rootNode);
        ScopeCheck::visit(rootNode);
    }

    auto moduleName = sourceRoot->getSourcePath();
    moduleName = moduleName.substr(moduleName.rfind('/') + 1, moduleName.length());

    if (compilerOptions->mb_VerboseArkoiRepresentation) {
        std::cout << "[" << moduleName << "] Printing the representation:" << std::endl;

        for (const auto &rootNode : roots) {
            std::cout << " " << rootNode->getSourcePath() << std::endl;
            ASTPrinter::visit(rootNode, std::cout, 1);
        }
    }

    CodeGen codeGen(moduleName);
    codeGen.visit(sourceRoot);

    if (compilerOptions->mb_VerboseLLVM_IR) {
        std::cout << "[" << moduleName << "] Printing the bitcode:" << std::endl;
        std::cout << codeGen.dumpModule() << std::endl;
    }

    if (compilerOptions->mb_VerboseModuleVerify)
        std::cout << std::endl << "[" << moduleName << "] Verifying the module:" << std::endl;

    auto module = codeGen.getModule();
    std::string errors;
    llvm::raw_string_ostream output(errors);

    if (llvm::verifyModule(*module, &output, nullptr)) {
        if (compilerOptions->mb_VerboseModuleVerify) {
            std::cout << "There was a problem during the verification of the module: "
                      << std::endl << " " << errors << std::endl;
            exit(EXIT_FAILURE);
        } else {
            std::cout << std::endl
                      << "There was a problem during the verification of the module."
                         "If you want to have more information about it use the \"-vmv\" option."
                      << std::endl;
            exit(EXIT_FAILURE);
        }
    }

    if (compilerOptions->mb_VerboseModuleVerify)
        std::cout << "Verified the module and found no errors." << std::endl;

    auto finish = std::chrono::high_resolution_clock::now();
    auto elapsed = std::chrono::duration_cast<std::chrono::microseconds>(finish - start);
    std::cout << std::endl << "Elapsed time: " << elapsed.count() << "μs\n";

    return EXIT_SUCCESS;
}

int Compiler::loadImports(const std::shared_ptr<CompilerOptions> &compilerOptions,
                          std::set<std::string> &loaded,
                          std::vector<SharedRootNode> &roots) {
    for (const auto &rootNode : roots) {
        for (const auto &node : rootNode->getNodes()) {
            if (node->getKind() != ASTNode::IMPORT)
                continue;

            auto importNode = std::dynamic_pointer_cast<ImportNode>(node);
            SharedRootNode importRoot;

            for (const auto &searchPath : compilerOptions->m_SearchPaths) {
                auto fullPath = std::filesystem::absolute(
                        searchPath + "/" + importNode->getPath()->getContent() + ".ark");

                if (!std::filesystem::exists(fullPath) || std::filesystem::is_directory(fullPath))
                    continue;

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
                throwNode(Error::ERROR, importNode, "Couldn't find the file with this path.");
                return EXIT_FAILURE;
            }

            importNode->setTarget(importRoot);
        }
    }

    return EXIT_SUCCESS;
}

std::shared_ptr<Parser> Compiler::loadFile(const std::string &sourcePath) {
    if (!std::filesystem::exists(sourcePath) || !std::filesystem::is_regular_file(sourcePath)) {
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

template<class... Args>
void Compiler::throwNode(unsigned int errorType, const SharedASTNode &node, Args... args) {
    std::cout << Error((Error::ErrorType) errorType,
                       node->findNodeOfParents<RootNode>()->getSourcePath(),
                       node->findNodeOfParents<RootNode>()->getSourceCode(),
                       node->getStartToken()->getLineNumber(),
                       node->getEndToken()->getLineNumber(),
                       node->getStartToken()->getStartChar(),
                       node->getEndToken()->getEndChar(),
                       fmt::format(args...));
}