//
// Created by timo on 7/30/20.
//

#include "parser.h"

std::shared_ptr<RootNode> Parser::parseRoot() {
    auto rootNode = std::make_shared<RootNode>();
    if (tokens.empty())
        return rootNode;

    rootNode->type = ROOT;
    rootNode->startLine = currentToken()->lineNumber;

    while (position < tokens.size()) {
        auto current = currentToken();

        if (current == std::pair(KEYWORD, "import"))
            rootNode->nodes.push_back(parseImport());
        else if(current == std::pair(KEYWORD, "fun"))
            rootNode->nodes.push_back(parseFunction());

        position++;
    }

    rootNode->endLine = currentToken()->lineNumber;

    return rootNode;
}

std::shared_ptr<ImportNode> Parser::parseImport() {
    auto importNode = std::make_shared<ImportNode>();
    importNode->type = IMPORT;
    importNode->startLine = currentToken()->lineNumber;

    if (currentToken() != std::pair(KEYWORD, "import")) {
        std::cerr << "Import expected 'import' but got " << *currentToken()
                  << " instead." << std::endl;
        return importNode;
    }

    if (nextToken() != STRING) {
        std::cerr << "Import expected <string> but got " << *currentToken()
                  << " instead." << std::endl;
        return importNode;
    }

    importNode->path = currentToken();
    importNode->endLine = currentToken()->lineNumber;

    return importNode;
}

std::shared_ptr<FunctionNode> Parser::parseFunction() {
    auto functionNode = std::make_shared<FunctionNode>();
    functionNode->type = FUNCTION;
    functionNode->startLine = currentToken()->lineNumber;

    if (currentToken() != std::pair(KEYWORD, "fun")) {
        std::cerr << "Function expected 'function' but got " << *currentToken()
                  << " instead." << std::endl;
        return functionNode;
    }

    if (nextToken() != IDENTIFIER) {
        std::cerr << "Function expected <identifier> but got " << *currentToken()
                  << " instead." << std::endl;
        return functionNode;
    }
    functionNode->name = currentToken();

    if (nextToken() != std::pair(SYMBOL, "(")) {
        std::cerr << "Function expected '(' but got " << *currentToken()
                  << " instead." << std::endl;
        return functionNode;
    }

    if (nextToken() != std::pair(SYMBOL, ")")) {
        // TODO: Parse parameters.
    }

    if (currentToken() != std::pair(SYMBOL, ")")) {
        std::cerr << "Function expected ':' but got " << *currentToken() << " instead." << std::endl;
        return functionNode;
    }

    functionNode->endLine = currentToken()->lineNumber;

    return functionNode;
}

std::shared_ptr<Token> Parser::peekToken(int offset, bool advance) {
    auto lastPosition = position;
    auto token = nextToken(advance);
    for (auto index = 1; index < offset; index++)
        token = nextToken(advance);
    position = lastPosition;
    return token;
}

std::shared_ptr<Token> Parser::nextToken(bool advance) {
    position++;

    while (position < tokens.size() && advance) {
        if (currentToken() != WHITESPACE && currentToken() != COMMENT)
            break;
        this->position++;
    }

    return currentToken();
}

std::shared_ptr<Token> Parser::currentToken() {
    position %= tokens.size();
    return tokens[position];
}