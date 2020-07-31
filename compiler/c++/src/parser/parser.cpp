//
// Created by timo on 7/30/20.
//

#include "parser.h"

std::shared_ptr<RootNode> Parser::parseRoot() {
    auto rootNode = std::make_shared<RootNode>();
    if (tokens.empty())
        return rootNode;

    rootNode->startLine = currentToken()->lineNumber;

    while (position < tokens.size()) {
        if (currentToken() == "import")
            rootNode->nodes.push_back(parseImport());
        else if (currentToken() == "fun")
            rootNode->nodes.push_back(parseFunction());
        else if (currentToken() == "var" || currentToken() == "const")
            rootNode->nodes.push_back(parseVariable());
        else if (currentToken() == "struct")
            rootNode->nodes.push_back(parseStruct());
        else if (currentToken() != TOKEN_WHITESPACE &&
                 currentToken() != TOKEN_COMMENT) {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Root expected <import>, <function>, <variable> or <structure> but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;

            while (position < tokens.size()) {
                if (currentToken() == "import" || currentToken() == "fun" ||
                    currentToken() == "var" || currentToken() == "const" ||
                    currentToken() == "struct")
                    break;
                nextToken(1, true, false);
            }

            continue;
        }

        nextToken(1, true, false);
    }

    rootNode->endLine = currentToken()->lineNumber;
    return rootNode;
}

std::shared_ptr<ImportNode> Parser::parseImport() {
    auto importNode = std::make_shared<ImportNode>();
    importNode->startLine = currentToken()->lineNumber;

    if (currentToken() != "import") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Import expected 'import' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return importNode;
    }

    if (nextToken() != TOKEN_STRING) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Import expected <string> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return importNode;
    }
    importNode->path = currentToken();

    importNode->endLine = currentToken()->lineNumber;
    return importNode;
}

std::shared_ptr<FunctionNode> Parser::parseFunction() {
    auto functionNode = std::make_shared<FunctionNode>();
    functionNode->startLine = currentToken()->lineNumber;

    if (currentToken() != "fun") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Function expected 'fun' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return functionNode;
    }

    if (nextToken() != TOKEN_IDENTIFIER) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Function expected <identifier> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return functionNode;
    }
    functionNode->name = currentToken();

    if (nextToken() != "(") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Function expected '(' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return functionNode;
    }

    if (nextToken() != ")") {
        std::vector<std::shared_ptr<Token>> chainedParameters;
        while (position < tokens.size()) {
            if (currentToken() != TOKEN_IDENTIFIER)
                break;

            if (peekToken(1) == ",") {
                chainedParameters.push_back(currentToken());
                nextToken(2);
                continue;
            }

            auto parameter = parseParameter();
            if (!chainedParameters.empty()) {
                for (const auto &name : chainedParameters) {
                    auto chainedParameter = std::make_shared<ParameterNode>();
                    chainedParameter->startLine = parameter->startLine;
                    chainedParameter->endLine = parameter->endLine;
                    chainedParameter->type = parameter->type;
                    chainedParameter->name = name;
                    functionNode->parameters.push_back(chainedParameter);
                }
            }

            functionNode->parameters.push_back(parameter);

            if (nextToken() != ",")
                break;
            nextToken(1, true, false);
        }
    }

    if (currentToken() != ")") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Function expected ')' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return functionNode;
    }

    if (nextToken() != ":") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Function expected ':' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return functionNode;
    }

    nextToken();
    functionNode->type = parseType();

    nextToken();
    functionNode->block = parseBlock();

    functionNode->endLine = currentToken()->lineNumber;
    return functionNode;
}

std::shared_ptr<ParameterNode> Parser::parseParameter() {
    auto parameterNode = std::make_shared<ParameterNode>();
    parameterNode->startLine = currentToken()->lineNumber;

    if (currentToken() != TOKEN_IDENTIFIER) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Parameter expected <identifier> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return parameterNode;
    }
    parameterNode->name = currentToken();

    if (nextToken() != ":") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Parameter expected ':' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return parameterNode;
    }

    nextToken();
    parameterNode->type = parseType();
    return parameterNode;
}

std::shared_ptr<TypeNode> Parser::parseType() {
    auto typeNode = std::make_shared<TypeNode>();
    typeNode->startLine = currentToken()->lineNumber;

    if (currentToken() != TOKEN_TYPE && currentToken() != TOKEN_IDENTIFIER) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Type expected <kind> or <identifier> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return typeNode;
    }

    while (position < tokens.size()) {
        if (peekToken(1) != "*")
            break;
        nextToken();
        typeNode->pointerLevel++;
    }

    typeNode->endLine = currentToken()->lineNumber;
    return typeNode;
}

std::shared_ptr<BlockNode> Parser::parseBlock() {
    auto blockNode = std::make_shared<BlockNode>();
    blockNode->startLine = currentToken()->lineNumber;

    if (currentToken() == "{") {
        nextToken();

        while (position < tokens.size()) {
            if (currentToken() == "}")
                break;

            if (currentToken() == "var" || currentToken() == "const")
                blockNode->nodes.push_back(parseVariable());
            else if (currentToken() == TOKEN_IDENTIFIER ||
                     (currentToken() == "&" || currentToken() == "@"))
                blockNode->nodes.push_back(parseIdentifier());
            else if (currentToken() == "return")
                blockNode->nodes.push_back(parseReturn());
            else if (currentToken() != TOKEN_WHITESPACE &&
                     currentToken() != TOKEN_COMMENT) {
                std::cout
                        << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                                 fmt::format(
                                         "Block expected <variable>, <identifier> or <return> but got '{}' instead.",
                                         currentToken()->content
                                 )) << std::endl;

                while (position < tokens.size()) {
                    if (currentToken() == "var" || currentToken() == "const" ||
                        currentToken() == TOKEN_IDENTIFIER ||
                        currentToken() == "&" || currentToken() == "@" ||
                        currentToken() == "}" || currentToken() == "return")
                        break;
                    nextToken(1, true, false);
                }

                continue;
            }

            nextToken(1, true, false);
        }

        if (currentToken() != "}") {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Block expected '}}' but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;
            return blockNode;
        }
    } else if (currentToken() == "=") {
        nextToken();
        blockNode->nodes.push_back(parseRelational());
    } else {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Block expected '{{' or '=' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return blockNode;
    }

    blockNode->endLine = currentToken()->lineNumber;
    return blockNode;
}

std::shared_ptr<VariableNode> Parser::parseVariable() {
    auto variableNode = std::make_shared<VariableNode>();
    variableNode->startLine = currentToken()->lineNumber;

    if (currentToken() != "var" && currentToken() != "const") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Variable expected 'var' or 'const' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return variableNode;
    }
    variableNode->constant = (currentToken() == "const");

    if (nextToken() != TOKEN_IDENTIFIER) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Variable expected <identifier> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return variableNode;
    }
    variableNode->name = currentToken();

    if (peekToken(1) == ":") {
        nextToken(2);
        variableNode->type = parseType();
    }

    if (peekToken(1) == "=") {
        nextToken(2);
        variableNode->expression = parseRelational();
    }

    variableNode->endLine = currentToken()->lineNumber;
    return variableNode;
}

std::shared_ptr<OperableNode> Parser::parseRelational() {
    auto lhs = parseAdditive();
    while (true) {
        BinaryKind operatorKind;
        switch(str2int(peekToken(1)->content.c_str())) {
            case str2int(">"):
                operatorKind = GREATER_THAN;
                break;
            case str2int("<"):
                operatorKind = LESS_THAN;
                break;
            case str2int(">="):
                operatorKind = GREATER_EQUAL_THAN;
                break;
            case str2int("<="):
                operatorKind = LESS_EQUAL_THAN;
                break;
            case str2int("=="):
                operatorKind = EQUAL;
                break;
            case str2int("!="):
                operatorKind = NOT_EQUAL;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->startLine = lhs->startLine;
        newLhs->lhs = lhs;
        newLhs->operatorKind = operatorKind;
        newLhs->rhs = parseRelational();
        newLhs->endLine = newLhs->rhs->endLine;
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseAdditive() {
    auto lhs = parseMultiplicative();
    while (true) {
        BinaryKind operatorKind;
        switch(str2int(peekToken(1)->content.c_str())) {
            case str2int("+"):
                operatorKind = ADDITION;
                break;
            case str2int("-"):
                operatorKind = SUBTRACTION;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->startLine = lhs->startLine;
        newLhs->lhs = lhs;
        newLhs->operatorKind = operatorKind;
        newLhs->rhs = parseAdditive();
        newLhs->endLine = newLhs->rhs->endLine;
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseMultiplicative() {
    auto lhs = parseOperable();
    while (true) {
        BinaryKind operatorKind;
        switch(str2int(peekToken(1)->content.c_str())) {
            case str2int("*"):
                operatorKind = MULTIPLICATION;
                break;
            case str2int("/"):
                operatorKind = DIVISION;
                break;
            case str2int("%"):
                operatorKind = REMAINING;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->startLine = lhs->startLine;
        newLhs->lhs = lhs;
        newLhs->operatorKind = operatorKind;
        newLhs->rhs = parseMultiplicative();
        newLhs->endLine = newLhs->rhs->endLine;
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseOperable() {
    if(currentToken() == "-") {
        auto operable = std::make_shared<UnaryNode>();
        operable->startLine = currentToken()->lineNumber;
        nextToken();
        operable->operable = parseOperable();
        operable->operatorKind = NEGATE;
        operable->endLine = operable->operable->endLine;
        return operable;
    } else if(currentToken() == "(") {
        auto operable = std::make_shared<ParenthesizedNode>();
        operable->startLine = currentToken()->lineNumber;
        nextToken();
        operable->expression = parseRelational();
        operable->endLine = operable->expression->endLine;

        if (nextToken() != ")") {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Parenthesized expected ')' but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;
            return operable;
        }

        return operable;
    } else if (currentToken() == TOKEN_NUMBER) {
        auto operable = std::make_shared<NumberNode>();
        operable->startLine = currentToken()->lineNumber;
        operable->number = currentToken();
        operable->endLine = currentToken()->lineNumber;
        return operable;
    } else if (currentToken() == TOKEN_STRING) {
        auto operable = std::make_shared<StringNode>();
        operable->startLine = currentToken()->lineNumber;
        operable->string = currentToken();
        operable->endLine = currentToken()->lineNumber;
        return operable;
    } else if (currentToken() == TOKEN_IDENTIFIER ||
               (currentToken() == "&" || currentToken() == "@")) {
        return parseIdentifier();
    } else {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Operable expected <string>, <number>, <unary> or <parenthesized> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return std::make_shared<OperableNode>();
    }
}

std::shared_ptr<IdentifierNode> Parser::parseIdentifier() {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->startLine = currentToken()->lineNumber;

    if (currentToken() == "&") {
        identifierNode->dereference = true;
        nextToken();
    } else if (currentToken() == "@") {
        identifierNode->pointer = true;
        nextToken();
    }

    if (currentToken() != TOKEN_IDENTIFIER) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Identifier expected <identifier> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return identifierNode;
    }
    identifierNode->identifier = currentToken();

    if (peekToken(1) == "(") {
        nextToken(2);

        auto functionCall = std::make_shared<FunctionCallNode>();
        functionCall->dereference = identifierNode->dereference;
        functionCall->identifier = identifierNode->identifier;
        functionCall->startLine = identifierNode->startLine;
        functionCall->pointer = identifierNode->pointer;
        identifierNode = functionCall;

        if (currentToken() != ")" || currentToken() == TOKEN_IDENTIFIER)
            functionCall->arguments = parseMixedArguments();

        if (currentToken() != ")") {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Function call expected ')' but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;
            return functionCall;
        }
    }

    if (peekToken(1) == ".") {
        nextToken(2);

        auto nextIdentifier = parseIdentifier();
        identifierNode->nextIdentifier = nextIdentifier;
        return identifierNode;
    }

    if (peekToken(1) == "{") {
        nextToken(2);

        if (identifierNode->kind == AST_FUNCTION_CALL) {
            std::cout << Error(sourcePath, sourceCode, identifierNode->startLine,
                               "Can't do a struct creation with a function call.")
                      << std::endl;
            return identifierNode;
        }

        auto structCreate = std::make_shared<StructCreateNode>();
        structCreate->dereference = identifierNode->dereference;
        structCreate->identifier = identifierNode->identifier;
        structCreate->startLine = identifierNode->startLine;
        structCreate->pointer = identifierNode->pointer;
        identifierNode = structCreate;

        structCreate->arguments = parseNamedArguments();

        if (currentToken() != "}") {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Struct create expected '}}' but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;
            return identifierNode;
        }
    } else if (peekToken(1) == "=") {
        nextToken(2);

        if (identifierNode->kind == AST_FUNCTION_CALL) {
            std::cout << Error(sourcePath, sourceCode, identifierNode->startLine,
                               "Can't do a assignment with a function call.")
                      << std::endl;
            return identifierNode;
        }

        auto assignment = std::make_shared<AssignmentNode>();
        assignment->dereference = identifierNode->dereference;
        assignment->identifier = identifierNode->identifier;
        assignment->startLine = identifierNode->startLine;
        assignment->pointer = identifierNode->pointer;
        identifierNode = assignment;

        assignment->expression = parseRelational();
    }

    identifierNode->endLine = currentToken()->lineNumber;
    return identifierNode;
}

std::vector<std::shared_ptr<ArgumentNode>> Parser::parseMixedArguments() {
    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    auto mustBeNamed = false;
    while (position < tokens.size()) {
        auto argument = std::make_shared<ArgumentNode>();
        if (mustBeNamed ||
            (currentToken() == TOKEN_IDENTIFIER && peekToken(1) == ":")) {
            if (currentToken() != TOKEN_IDENTIFIER) {
                std::cout
                        << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                                 fmt::format(
                                         "Arguments expected <identifier> but got '{}' instead.",
                                         currentToken()->content
                                 )) << std::endl;
                break;
            }
            argument->name = currentToken();

            if (nextToken() != ":") {
                std::cout
                        << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                                 fmt::format(
                                         "Arguments expected ':' but got '{}' instead.",
                                         currentToken()->content
                                 )) << std::endl;
                break;
            }

            mustBeNamed = true;
            nextToken();
        }

        argument->expression = parseRelational();

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }

    return arguments;
}

std::vector<std::shared_ptr<ArgumentNode>> Parser::parseNamedArguments() {
    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    while (position < tokens.size()) {
        auto argument = std::make_shared<ArgumentNode>();
        if (currentToken() != TOKEN_IDENTIFIER) {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Arguments expected <identifier> but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;
            break;
        }
        argument->name = currentToken();

        if (nextToken() != ":") {
            std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                               fmt::format(
                                       "Arguments expected ':' but got '{}' instead.",
                                       currentToken()->content
                               )) << std::endl;
            break;
        }

        nextToken();
        argument->expression = parseRelational();

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }

    return arguments;
}

std::shared_ptr<ReturnNode> Parser::parseReturn() {
    auto returnNode = std::make_shared<ReturnNode>();
    returnNode->startLine = currentToken()->lineNumber;

    if (currentToken() != "return") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Return expected 'return' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return returnNode;
    }

    nextToken();
    returnNode->expression = parseRelational();

    returnNode->endLine = currentToken()->lineNumber;
    return returnNode;
}

std::shared_ptr<StructNode> Parser::parseStruct() {
    auto structNode = std::make_shared<StructNode>();
    structNode->startLine = currentToken()->lineNumber;

    if (currentToken() != "struct") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Struct expected 'struct' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return structNode;
    }

    if (nextToken() != TOKEN_IDENTIFIER) {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Struct expected <identifier> but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return structNode;
    }
    structNode->name = currentToken();

    if (nextToken() != "{") {
        std::cout << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                           fmt::format(
                                   "Struct expected '{{' but got '{}' instead.",
                                   currentToken()->content
                           )) << std::endl;
        return structNode;
    }

    nextToken();
    while (position < tokens.size()) {
        if (currentToken() == "}")
            break;

        if (currentToken() == "var" || currentToken() == "const")
            structNode->variables.push_back(parseVariable());
        else if (currentToken() != TOKEN_WHITESPACE &&
                 currentToken() != TOKEN_COMMENT) {
            std::cout
                    << Error(sourcePath, sourceCode, currentToken()->lineNumber,
                             fmt::format(
                                     "Struct expected <variable> but got '{}' instead.",
                                     currentToken()->content
                             )) << std::endl;

            while (position < tokens.size()) {
                if (currentToken() == "var" || currentToken() == "const" ||
                    currentToken() == "}")
                    break;
                nextToken(1, true, false);
            }

            continue;
        }

        nextToken(1, true, false);
    }

    structNode->endLine = currentToken()->lineNumber;
    return structNode;
}

std::shared_ptr<Token> Parser::peekToken(int offset, bool advance, bool safety) {
    auto lastPosition = position;
    auto token = nextToken(1, advance, safety);
    for (auto index = 1; index < offset; index++)
        token = nextToken(1, advance, safety);
    position = lastPosition;
    return token;
}

std::shared_ptr<Token> Parser::nextToken(int times, bool advance, bool safety) {
    std::shared_ptr<Token> token;

    for (int index = 0; index < times; index++) {
        position++;

        while (position < tokens.size() && advance) {
            if (currentToken(safety) != TOKEN_WHITESPACE &&
                currentToken(safety) != TOKEN_COMMENT)
                break;
            this->position++;
        }

        token = currentToken(safety);
    }

    return token;
}

std::shared_ptr<Token> Parser::currentToken(bool safety) {
    if (safety)
        position = position >= tokens.size() ? tokens.size() - 1 : position;
    else if (position >= tokens.size())
        return nullptr;
    return tokens[position];
}