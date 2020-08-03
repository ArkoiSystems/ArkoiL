//
// Created by timo on 7/29/20.
//

#include "compiler.h"
#include "../parser/typeresolver.h"
#include "../../deps/dbg-macro/dbg.h"

int Compiler::compile() {
    std::vector<std::pair<std::shared_ptr<Parser>, std::shared_ptr<RootNode>>> pairs;
    for (const auto &sourcePath : compilerOptions.sourceFiles) {
        struct stat path_stat{};
        stat(sourcePath.c_str(), &path_stat);
        if (!S_ISREG(path_stat.st_mode)) {
            std::cout << "The given source path is not a file: " << sourcePath
                      << std::endl;
            return -1;
        }

        std::ifstream sourceFile;
        sourceFile.open(sourcePath);
        if (!sourceFile.is_open())
            return -1;
        defer(sourceFile.close());

        auto contents = std::string{std::istreambuf_iterator<char>(sourceFile),
                                    std::istreambuf_iterator<char>()};

        auto lexer = Lexer{sourcePath, contents};
        auto tokens = lexer.process();

        auto parser = std::make_shared<Parser>(sourcePath, contents, tokens);
        pairs.emplace_back(parser, parser->parseRoot());
    }

    for (const auto &pair : pairs) {
        std::vector<std::shared_ptr<RootNode>> imports;
        for (const auto &node : pair.second->nodes) {
            if (node->kind != AST_IMPORT)
                continue;

            auto importNode = std::dynamic_pointer_cast<ImportNode>(node);
            for (const auto &importPair : pairs) {
                if (strcmp(importNode->path->content.c_str(),
                            importPair.first->sourcePath.c_str()) != 0)
                    continue;

                imports.push_back(importPair.second);
                break;
            }
        }
    }

    return 0;
}
