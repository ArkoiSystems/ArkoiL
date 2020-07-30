//
// Created by timo on 7/29/20.
//

#include <fstream>
#include <sys/stat.h>
#include "compiler.h"
#include "utils.h"
#include "../lexer/lexer.h"
#include "../parser/parser.h"

int Compiler::compile() {
    std::vector<std::shared_ptr<RootNode>> roots;
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

        std::string contents((std::istreambuf_iterator<char>(sourceFile)),
                             std::istreambuf_iterator<char>());

        Lexer lexer(contents);
        auto tokens = lexer.process();

        Parser parser(tokens);
        roots.push_back(parser.parseRoot());
    }

    for (const auto &root : roots) {
        for(const auto &node : root->nodes) {
            if(node->type == IMPORT) {
                auto import = std::dynamic_pointer_cast<ImportNode>(node);

            }
        }
    }

    return 0;
}
