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
    SharedImportNode parseImport(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedFunctionNode parseFunction(const std::set<std::string> &annotations,
                                     const SharedASTNode &parent, bool resetFirstFail = false);

    SharedParameterNode parseParameter(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedTypeNode parseType(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedBlockNode parseBlock(const SharedASTNode &parent, const SharedSymbolTable &scope,
                               bool resetFirstFail = false);

    SharedVariableNode parseVariable(const SharedASTNode &parent, const SharedSymbolTable &scope,
                                     bool resetFirstFail = false);

    SharedOperableNode parseRelational(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedOperableNode parseAdditive(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedOperableNode parseMultiplicative(const SharedASTNode &parent,
                                           bool resetFirstFail = false);

    SharedOperableNode parseCast(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedOperableNode parseOperable(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedOperableNode parseIdentifier(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedReturnNode parseReturn(const SharedASTNode &parent, bool resetFirstFail = false);

    SharedStructNode parseStruct(const SharedASTNode &parent, bool resetFirstFail = false);

    std::set<std::string> parseAnnotations(const SharedASTNode &parent,
                                           bool resetFirstFail = false);

    void parseFunctionArguments(const SharedFunctionCallNode &functionCallNode,
                                const SharedASTNode &parent, bool resetFirstFail = false);

    void parseStructArguments(const SharedStructCreateNode &structCreateNode,
                              const SharedASTNode &parent, bool resetFirstFail = false);

    std::shared_ptr<Token> peekToken(int offset, bool advance = true, bool safety = true);

    std::shared_ptr<Token> nextToken(int times = 1, bool advance = true, bool safety = true);

    std::shared_ptr<Token> undoToken(int times = 1, bool advance = true, bool safety = true);

    std::shared_ptr<Token> currentToken(bool safety = true);

    template<class... Args>
    void throwParser(unsigned int errorType, Args...args);

    template<class... Args>
    void throwNode(unsigned int errorType, const SharedASTNode& node, Args...args);

};