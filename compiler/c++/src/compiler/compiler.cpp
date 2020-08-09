//
// Created by timo on 7/29/20.
//

#include "compiler.h"
#include "../../deps/dbg-macro/dbg.h"
#include "../parser/typeresolver.h"
#include "../parser/parser.h"
#include "options.h"
#include "utils.h"
#include "error.h"
#include "../lexer/lexer.h"
#include "../parser/astnodes.h"

int Compiler::compile() {
    std::vector<std::shared_ptr<RootNode>> roots;
    for (const auto &sourcePath : compilerOptions.sourceFiles) {
        auto parser = Compiler::loadFile(sourcePath);
        if (parser == nullptr)
            continue;
        roots.push_back(parser->parseRoot());
    }

    std::set<std::string> loaded;
    while (true) {
        auto lastSize = loaded.size();
        loadImports(loaded, roots);
        if (lastSize == loaded.size())
            break;
    }

    for (const auto &rootNode : roots)
        TypeResolver::visitRoot(rootNode);

    return 0;
}

// TODO: Make an efficient function.
void Compiler::loadImports(std::set<std::string> &loaded,
                           std::vector<std::shared_ptr<RootNode>> &roots) {
    for (const auto &rootNode : roots) {
        for (const auto &node : rootNode->nodes) {
            if (node->kind != AST_IMPORT)
                continue;

            auto importNode = std::dynamic_pointer_cast<ImportNode>(node);
            std::shared_ptr<RootNode> importRoot;

            for (const auto &searchPath : compilerOptions.searchPaths) {
                // TODO: Make this secure (relative, absolute path etc)
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
                        if (strcmp(loadedRoot->sourcePath.c_str(), fullPath.c_str()) == 0) {
                            importRoot = loadedRoot;
                            break;
                        }
                    }
                    break;
                }

                auto parser = Compiler::loadFile(fullPath);
                loaded.insert(fullPath);

                importRoot = parser->parseRoot();
                roots.push_back(importRoot);
                break;
            }

            if (!importRoot)
                THROW_NODE_ERROR(importNode, "Couldn't find the file with this path.")
            importNode->target = importRoot;
        }
    }
}

std::shared_ptr<Parser> Compiler::loadFile(const std::string &sourcePath) {
    struct stat path_stat{};
    stat(sourcePath.c_str(), &path_stat);
    if (!S_ISREG(path_stat.st_mode)) {
        std::cout << "The given source path is not a file: " << sourcePath
                  << std::endl;
        return nullptr;
    }

    std::ifstream sourceFile;
    sourceFile.open(sourcePath);
    if (!sourceFile.is_open())
        return nullptr;

    auto contents = std::string{std::istreambuf_iterator<char>(sourceFile),
                                std::istreambuf_iterator<char>()};
    sourceFile.close();

    Lexer lexer{sourcePath, contents};
    auto tokens = lexer.process();

    return std::make_shared<Parser>(sourcePath, contents, tokens);
}