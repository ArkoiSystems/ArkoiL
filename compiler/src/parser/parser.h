//
// Created by timo on 7/30/20.
//

#pragma once

#include <utility>
#include <vector>
#include <memory>
#include <set>

#include "allnodes.h"

class SymbolTable;

class Token;

class Parser {

private:
    std::vector<std::shared_ptr<Token>> m_Tokens;
    std::string m_SourceCode, m_SourcePath;
    unsigned int m_Position;

public:
    Parser(std::string sourcePath, std::string sourceCode,
           std::vector<std::shared_ptr<Token>> tokens);

    Parser(const Parser &other) = delete;

    Parser &operator=(const Parser &) = delete;

public:
    SharedRootNode parseRoot();

private:
    SharedImportNode parseImport(const SharedASTNode &parent);

    SharedFunctionNode parseFunction(const std::set<std::string> &annotations,
                                     const SharedASTNode &parent);

    SharedParameterNode parseParameter(const SharedASTNode &parent);

    SharedTypeNode parseType(const SharedASTNode &parent);

    SharedBlockNode parseBlock(const SharedASTNode &parent,
                               const SharedSymbolTable &scope);

    SharedVariableNode parseVariable(const SharedASTNode &parent,
                                     const SharedSymbolTable &scope);

    SharedOperableNode parseRelational(const SharedASTNode &parent);

    SharedOperableNode parseAdditive(const SharedASTNode &parent);

    SharedOperableNode parseMultiplicative(const SharedASTNode &parent);

    SharedOperableNode parseCast(const SharedASTNode &parent);

    SharedOperableNode parseOperable(const SharedASTNode &parent);

    SharedOperableNode parseIdentifier(const SharedASTNode &parent);

    SharedReturnNode parseReturn(const SharedASTNode &parent);

    SharedStructNode parseStruct(const SharedASTNode &parent);

    std::set<std::string> parseAnnotations(const SharedASTNode &parent);

    void parseFunctionArguments(SharedFunctionCallNode &functionCallNode,
                                const SharedASTNode &parent);

    void parseStructArguments(SharedStructCreateNode &structCreateNode,
                              const SharedASTNode &parent);

    std::shared_ptr<Token> peekToken(int offset, bool advance = true, bool safety = true);

    std::shared_ptr<Token> nextToken(int times = 1, bool advance = true, bool safety = true);

    std::shared_ptr<Token> undoToken(int times = 1, bool advance = true, bool safety = true);

    std::shared_ptr<Token> currentToken(bool safety = true);

};