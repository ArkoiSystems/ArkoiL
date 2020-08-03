//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_PARSER_H
#define ARKOICOMPILER_PARSER_H

#include <utility>
#include <vector>
#include "../compiler/utils.h"
#include "../lexer/lexer.h"
#include "astnodes.h"
#include "../../deps/fmt-7.0.2/include/fmt/core.h"

class Parser {

public:
    std::string sourceCode, sourcePath;

private:
    std::vector<std::shared_ptr<Token>> tokens;
    unsigned int position;

public:
    explicit Parser(std::string sourcePath, std::string sourceCode,
                    std::vector<std::shared_ptr<Token>> tokens) :
            sourcePath(std::move(sourcePath)),
            sourceCode(std::move(sourceCode)),
            tokens(std::move(tokens)) {
        position = 0;
    }

public:
    std::shared_ptr<RootNode> parseRoot();

private:
    std::shared_ptr<ImportNode> parseImport();

    std::shared_ptr<FunctionNode> parseFunction();

    std::shared_ptr<ParameterNode> parseParameter();

    std::shared_ptr<TypeNode> parseType();

    std::shared_ptr<BlockNode> parseBlock();

    std::shared_ptr<VariableNode> parseVariable();

    std::shared_ptr<OperableNode> parseRelational();

    std::shared_ptr<OperableNode> parseAdditive();

    std::shared_ptr<OperableNode> parseMultiplicative();

    std::shared_ptr<OperableNode> parseOperable();

    std::shared_ptr<IdentifierNode> parseIdentifier();

    std::shared_ptr<ReturnNode> parseReturn();

    std::shared_ptr<StructNode> parseStruct();

    void parseMixedArguments(std::vector<std::shared_ptr<ArgumentNode>> &arguments);

    void parseNamedArguments(std::vector<std::shared_ptr<ArgumentNode>> &arguments);

    std::shared_ptr<Token> peekToken(int offset, bool advance = true, bool safety = true);

    std::shared_ptr<Token> nextToken(int times = 1, bool advance = true, bool safety = true);

    std::shared_ptr<Token> currentToken(bool safety = true);

};

#endif //ARKOICOMPILER_PARSER_H
