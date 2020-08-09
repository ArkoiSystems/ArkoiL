//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_PARSER_H
#define ARKOICOMPILER_PARSER_H

#include <utility>
#include <vector>
#include <memory>

class Token;

class ASTNode;

class RootNode;

class ImportNode;

class FunctionNode;

class ParameterNode;

class TypeNode;

class BlockNode;

class VariableNode;

class OperableNode;

class IdentifierNode;

class ReturnNode;

class StructNode;

class ArgumentNode;

class SymbolTable;

class TypedNode;

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
            tokens(std::move(tokens)),
            position(0) {}

public:
    std::shared_ptr<RootNode> parseRoot();

private:
    std::shared_ptr<ImportNode> parseImport(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<FunctionNode> parseFunction(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<ParameterNode> parseParameter(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<TypeNode> parseType(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<BlockNode> parseBlock(const std::shared_ptr<ASTNode> &parent,
                                          const std::shared_ptr<SymbolTable>& scope);

    std::shared_ptr<VariableNode> parseVariable(const std::shared_ptr<ASTNode> &parent,
                                                const std::shared_ptr<SymbolTable>& scope);

    std::shared_ptr<OperableNode> parseRelational(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<OperableNode> parseAdditive(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<OperableNode> parseMultiplicative(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<OperableNode> parseOperable(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<OperableNode> parseIdentifier(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<ReturnNode> parseReturn(const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<StructNode> parseStruct(const std::shared_ptr<ASTNode> &parent);

    void parseMixedArguments(std::vector<std::shared_ptr<ArgumentNode>> &arguments,
                             const std::shared_ptr<ASTNode> &parent);

    void parseNamedArguments(std::vector<std::shared_ptr<ArgumentNode>> &arguments,
                             const std::shared_ptr<ASTNode> &parent);

    std::shared_ptr<Token> peekToken(int offset, bool advance = true, bool safety = true);

    std::shared_ptr<Token> nextToken(int times = 1, bool advance = true, bool safety = true);

    std::shared_ptr<Token> currentToken(bool safety = true);

};

#endif //ARKOICOMPILER_PARSER_H
