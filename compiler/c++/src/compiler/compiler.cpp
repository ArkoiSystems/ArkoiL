//
// Created by timo on 7/29/20.
//

#include "compiler.h"
#include "../parser/typeresolver.h"
#include "../semantic/scopecheck.h"
#include "../semantic/typecheck.h"
#include "../parser/astnodes.h"
#include "../codegen/codegen.h"
#include "../parser/parser.h"
#include "../lexer/lexer.h"
#include "options.h"
#include "error.h"

#include "../../deps/dbg-macro/dbg.h"

int Compiler::compile(const CompilerOptions &compilerOptions) {
    std::vector<std::shared_ptr<RootNode>> roots;
    for (const auto &sourcePath : compilerOptions.sourceFiles) {
        if (auto parser = Compiler::loadFile(sourcePath)) {
            roots.push_back(parser->parseRoot());
            break;
        }

        return EXIT_FAILURE;
    }

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
        TypeCheck::visit(rootNode);
        ScopeCheck::visit(rootNode);
    }

    for (const auto &rootNode : roots)
        CodeGen().visit(rootNode);

    return EXIT_SUCCESS;
}

// TODO: Make an efficient function.
int Compiler::loadImports(const CompilerOptions &compilerOptions,
                          std::set<std::string> &loaded,
                          std::vector<std::shared_ptr<RootNode>> &roots) {
    for (const auto &rootNode : roots) {
        for (const auto &node : rootNode->nodes) {
            if (node->kind != AST_IMPORT)
                continue;

            auto importNode = std::dynamic_pointer_cast<ImportNode>(node);
            std::shared_ptr<RootNode> importRoot;

            for (const auto &searchPath : compilerOptions.searchPaths) {
                auto fullPath = searchPath + "/" + importNode->path->content + ".ark";

                struct stat path_stat{};
                stat(fullPath.c_str(), &path_stat);
                if (!S_ISREG(path_stat.st_mode) || access(fullPath.c_str(), F_OK) == -1)
                    continue;

                auto realPath = realpath(fullPath.c_str(), nullptr);
                fullPath = std::string(realPath);
                free(realPath);

                if (loaded.find(fullPath) != loaded.end()) {
                    for (const auto &loadedRoot : roots) {
                        if(loadedRoot->sourcePath == fullPath){
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

            importNode->target = importRoot;
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