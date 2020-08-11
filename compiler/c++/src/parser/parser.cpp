//
// Created by timo on 7/30/20.
//

#include "parser.h"
#include "../compiler/error.h"
#include "../compiler/utils.h"
#include "../lexer/lexer.h"
#include "astnodes.h"

std::shared_ptr<RootNode> Parser::parseRoot() {
    auto rootNode = std::make_shared<RootNode>();
    if (tokens.empty())
        return rootNode;

    rootNode->startToken = currentToken();
    rootNode->sourcePath = sourcePath;
    rootNode->sourceCode = sourceCode;
    rootNode->scope = std::make_shared<SymbolTable>(nullptr);

    while (position < tokens.size()) {
        if (currentToken() == "import")
            rootNode->nodes.push_back(parseImport(rootNode));
        else if (currentToken() == "fun")
            rootNode->nodes.push_back(parseFunction(rootNode));
        else if (currentToken() == "var" || currentToken() == "const")
            rootNode->nodes.push_back(parseVariable(rootNode, rootNode->scope));
        else if (currentToken() == "struct")
            rootNode->nodes.push_back(parseStruct(rootNode));
        else if (currentToken() != TOKEN_WHITESPACE &&
                 currentToken() != TOKEN_COMMENT) {
            THROW_TOKEN_ERROR("Root expected <import>, <function>, <variable> or <structure> but got '{}' instead.",
                              currentToken()->content)
            rootNode->isFailed = true;

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

    rootNode->endToken = currentToken();
    return rootNode;
}

std::shared_ptr<ImportNode> Parser::parseImport(const std::shared_ptr<ASTNode> &parent) {
    auto importNode = std::make_shared<ImportNode>();
    importNode->startToken = currentToken();
    importNode->parent = parent;
    importNode->scope = parent->scope;

    if (currentToken() != "import") {
        THROW_TOKEN_ERROR("Import expected 'import' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return importNode;
    }

    if (nextToken() != TOKEN_STRING) {
        THROW_TOKEN_ERROR("Import expected <string> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return importNode;
    }
    importNode->path = currentToken();

    importNode->endToken = currentToken();
    return importNode;
}

std::shared_ptr<FunctionNode> Parser::parseFunction(const std::shared_ptr<ASTNode> &parent) {
    auto functionNode = std::make_shared<FunctionNode>();
    functionNode->startToken = currentToken();
    functionNode->parent = parent;
    functionNode->scope = parent->scope;

    if (currentToken() != "fun") {
        THROW_TOKEN_ERROR("Function expected 'fun' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return functionNode;
    }

    if(peekToken(1) == "@") {
        functionNode->isBuiltin = true;
        nextToken();
    }

    if (nextToken() != TOKEN_IDENTIFIER) {
        THROW_TOKEN_ERROR("Function expected <identifier> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return functionNode;
    }
    functionNode->name = currentToken();
    functionNode->scope->insert(functionNode->name->content, functionNode);

    if (nextToken() != "(") {
        THROW_TOKEN_ERROR("Function expected '(' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return functionNode;
    }

    auto parameterScope = std::make_shared<SymbolTable>(functionNode->scope);
    functionNode->scope = parameterScope;
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

            auto parameter = parseParameter(functionNode);
            if (!chainedParameters.empty()) {
                for (const auto &name : chainedParameters) {
                    auto chainedParameter = std::make_shared<ParameterNode>();
                    chainedParameter->startToken = parameter->startToken;
                    chainedParameter->endToken = parameter->endToken;
                    chainedParameter->type = parameter->type;
                    chainedParameter->scope = parameterScope;
                    chainedParameter->parent = functionNode;
                    chainedParameter->name = name;
                    parameterScope->insert(chainedParameter->name->content, chainedParameter);
                    functionNode->parameters.push_back(chainedParameter);
                }
            }

            functionNode->parameters.push_back(parameter);

            if (nextToken() != ",")
                break;
            nextToken(1, true, false);
        }
    }

    if (currentToken() == "." && peekToken(1) == "." && peekToken(2) == ".") {
        functionNode->isVariadic = true;
        nextToken(3);
    }

    if (currentToken() != ")") {
        THROW_TOKEN_ERROR("Function expected ')' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return functionNode;
    }

    if (nextToken() != ":") {
        THROW_TOKEN_ERROR("Function expected ':' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return functionNode;
    }

    nextToken();
    functionNode->type = parseType(functionNode);

    if ((peekToken(1) == "{" || peekToken(1) == "=") && !functionNode->isBuiltin) {
        nextToken();
        functionNode->block = parseBlock(functionNode, parameterScope);
    } else if (!functionNode->isBuiltin) {
        functionNode->isNative = true;
    }

    functionNode->endToken = currentToken();
    return functionNode;
}

std::shared_ptr<ParameterNode> Parser::parseParameter(const std::shared_ptr<ASTNode> &parent) {
    auto parameterNode = std::make_shared<ParameterNode>();
    parameterNode->startToken = currentToken();
    parameterNode->parent = parent;
    parameterNode->scope = parent->scope;

    if (currentToken() != TOKEN_IDENTIFIER) {
        THROW_TOKEN_ERROR("Parameter expected <identifier> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return parameterNode;
    }
    parameterNode->name = currentToken();
    parameterNode->scope->insert(parameterNode->name->content, parameterNode);

    if (nextToken() != ":") {
        THROW_TOKEN_ERROR("Parameter expected ':' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return parameterNode;
    }

    nextToken();
    parameterNode->type = parseType(parameterNode);

    parameterNode->endToken = currentToken();
    return parameterNode;
}

std::shared_ptr<TypeNode> Parser::parseType(const std::shared_ptr<ASTNode> &parent) {
    auto typeNode = std::make_shared<TypeNode>();
    typeNode->startToken = currentToken();
    typeNode->parent = parent;
    typeNode->scope = parent->scope;

    if (currentToken() != TOKEN_TYPE && currentToken() != TOKEN_IDENTIFIER) {
        THROW_TOKEN_ERROR("Type expected <kind> or <identifier> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return typeNode;
    }
    typeNode->typeToken = currentToken();

    while (position < tokens.size()) {
        if (peekToken(1) != "*")
            break;
        nextToken();
        typeNode->pointerLevel++;
    }

    typeNode->endToken = currentToken();
    return typeNode;
}

std::shared_ptr<BlockNode> Parser::parseBlock(const std::shared_ptr<ASTNode> &parent,
                                              const std::shared_ptr<SymbolTable> &scope) {
    auto blockNode = std::make_shared<BlockNode>();
    blockNode->startToken = currentToken();
    blockNode->parent = parent;
    blockNode->scope = scope;

    if (currentToken() == "{") {
        nextToken();

        while (position < tokens.size()) {
            if (currentToken() == "}")
                break;

            if (currentToken() == "var" || currentToken() == "const")
                blockNode->nodes.push_back(parseVariable(blockNode, blockNode->scope));
            else if (currentToken() == TOKEN_IDENTIFIER ||
                     (currentToken() == "&" || currentToken() == "@")) {
                auto identifier = parseIdentifier(blockNode);
                blockNode->nodes.push_back(identifier);

                if (identifier->kind == AST_STRUCT_CREATE) {
                    THROW_NODE_ERROR(identifier,
                                     "Creating a struct without binding it to a variable is unnecessary.")
                    parent->isFailed = true;
                }
            } else if (currentToken() == "return")
                blockNode->nodes.push_back(parseReturn(blockNode));
            else if (currentToken() != TOKEN_WHITESPACE &&
                     currentToken() != TOKEN_COMMENT) {
                THROW_TOKEN_ERROR("Block expected <variable>, <identifier> or <return> but got '{}' instead.",
                                  currentToken()->content)
                parent->isFailed = true;

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
            THROW_TOKEN_ERROR("Block expected '}}' but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            return blockNode;
        }
    } else if (currentToken() == "=") {
        nextToken();
        blockNode->nodes.push_back(parseRelational(blockNode));
    } else {
        THROW_TOKEN_ERROR("Block expected '{{' or '=' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return blockNode;
    }

    blockNode->endToken = currentToken();
    return blockNode;
}

std::shared_ptr<VariableNode> Parser::parseVariable(const std::shared_ptr<ASTNode> &parent,
                                                    const std::shared_ptr<SymbolTable> &scope) {
    auto variableNode = std::make_shared<VariableNode>();
    variableNode->startToken = currentToken();
    variableNode->parent = parent;
    variableNode->scope = scope;

    if (currentToken() != "var" && currentToken() != "const") {
        THROW_TOKEN_ERROR("Variable expected 'var' or 'const' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return variableNode;
    }
    variableNode->isConstant = (currentToken() == "const");

    if (nextToken() != TOKEN_IDENTIFIER) {
        THROW_TOKEN_ERROR("Variable expected <identifier> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return variableNode;
    }
    variableNode->name = currentToken();
    variableNode->scope->insert(variableNode->name->content, variableNode);

    if (peekToken(1) == ":") {
        nextToken(2);
        variableNode->type = parseType(variableNode);
    }

    if (peekToken(1) == "=") {
        nextToken(2);
        variableNode->expression = parseRelational(variableNode);
    }

    variableNode->endToken = currentToken();
    return variableNode;
}

std::shared_ptr<OperableNode> Parser::parseRelational(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseAdditive(parent);
    while (true) {
        BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->content.c_str())) {
            case Utils::hash(">"):
                operatorKind = GREATER_THAN;
                break;
            case Utils::hash("<"):
                operatorKind = LESS_THAN;
                break;
            case Utils::hash(">="):
                operatorKind = GREATER_EQUAL_THAN;
                break;
            case Utils::hash("<="):
                operatorKind = LESS_EQUAL_THAN;
                break;
            case Utils::hash("=="):
                operatorKind = EQUAL;
                break;
            case Utils::hash("!="):
                operatorKind = NOT_EQUAL;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->startToken = lhs->startToken;
        newLhs->scope = lhs->scope;
        newLhs->parent = lhs;
        newLhs->lhs = lhs;
        newLhs->operatorKind = operatorKind;
        newLhs->rhs = parseRelational(lhs);
        newLhs->endToken = newLhs->rhs->endToken;
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseAdditive(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseMultiplicative(parent);
    while (true) {
        BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->content.c_str())) {
            case Utils::hash("+"):
                operatorKind = ADDITION;
                break;
            case Utils::hash("-"):
                operatorKind = SUBTRACTION;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->startToken = lhs->startToken;
        newLhs->scope = lhs->scope;
        newLhs->parent = lhs;
        newLhs->lhs = lhs;
        newLhs->operatorKind = operatorKind;
        newLhs->rhs = parseAdditive(lhs);
        newLhs->endToken = newLhs->rhs->endToken;
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseMultiplicative(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseOperable(parent);
    while (true) {
        BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->content.c_str())) {
            case Utils::hash("*"):
                operatorKind = MULTIPLICATION;
                break;
            case Utils::hash("/"):
                operatorKind = DIVISION;
                break;
            case Utils::hash("%"):
                operatorKind = REMAINING;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->startToken = lhs->startToken;
        newLhs->scope = lhs->scope;
        newLhs->parent = lhs;
        newLhs->lhs = lhs;
        newLhs->operatorKind = operatorKind;
        newLhs->rhs = parseMultiplicative(lhs);
        newLhs->endToken = newLhs->rhs->endToken;
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseOperable(const std::shared_ptr<ASTNode> &parent) {
    if(currentToken() == "-") {
        auto operable = std::make_shared<UnaryNode>();
        operable->startToken = currentToken();
        operable->parent = parent;
        operable->scope = parent->scope;

        nextToken();
        operable->operable = parseOperable(operable);

        operable->operatorKind = NEGATE;
        operable->endToken = operable->operable->endToken;
        return operable;
    } else if(currentToken() == "(") {
        auto operable = std::make_shared<ParenthesizedNode>();
        operable->startToken = currentToken();
        operable->parent = parent;
        operable->scope = parent->scope;

        nextToken();
        operable->expression = parseRelational(operable);

        operable->endToken = operable->expression->endToken;

        if (nextToken() != ")") {
            THROW_TOKEN_ERROR("Parenthesized expected ')' but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            return operable;
        }

        return operable;
    } else if (currentToken() == TOKEN_NUMBER) {
        auto operable = std::make_shared<NumberNode>();
        operable->startToken = currentToken();
        operable->parent = parent;
        operable->scope = parent->scope;
        operable->number = currentToken();
        operable->endToken = currentToken();
        return operable;
    } else if (currentToken() == TOKEN_STRING) {
        auto operable = std::make_shared<StringNode>();
        operable->startToken = currentToken();
        operable->parent = parent;
        operable->scope = parent->scope;
        operable->string = currentToken();
        operable->endToken = currentToken();
        return operable;
    } else if (currentToken() == TOKEN_IDENTIFIER ||
               (currentToken() == "&" || currentToken() == "@")) {
        return parseIdentifier(parent);
    } else {
        auto operable = std::make_shared<OperableNode>();
        THROW_TOKEN_ERROR("Operable expected <string>, <number>, <unary> or <parenthesized> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return operable;
    }
}

std::shared_ptr<OperableNode> Parser::parseIdentifier(const std::shared_ptr<ASTNode> &parent) {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->startToken = currentToken();
    identifierNode->parent = parent;
    identifierNode->scope = parent->scope;

    if (currentToken() == "&") {
        identifierNode->isPointer = true;
        nextToken();
    } else if (currentToken() == "@") {
        identifierNode->isDereference = true;
        nextToken();
    }

    if (currentToken() != TOKEN_IDENTIFIER) {
        THROW_TOKEN_ERROR("Identifier expected <identifier> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return identifierNode;
    }
    identifierNode->identifier = currentToken();

    if (peekToken(1) == "(") {
        nextToken(2);

        auto functionCall = std::make_shared<FunctionCallNode>(*identifierNode);
        identifierNode = functionCall;

        if (currentToken() != ")" || currentToken() == TOKEN_IDENTIFIER)
            parseMixedArguments(functionCall->arguments, functionCall);

        if (currentToken() != ")") {
            THROW_TOKEN_ERROR("Function call expected ')' but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            return functionCall;
        }
    }

    identifierNode->endToken = currentToken();

    auto lastIdentifier = identifierNode;
    while (peekToken(1) == ".") {
        nextToken(2);

        auto chainedIdentifier = std::make_shared<IdentifierNode>();
        chainedIdentifier->startToken = currentToken();
        chainedIdentifier->parent = lastIdentifier;
        chainedIdentifier->scope = parent->scope;

        if (currentToken() == "&") {
            chainedIdentifier->isPointer = true;
            nextToken();
        } else if (currentToken() == "@") {
            chainedIdentifier->isDereference = true;
            nextToken();
        }

        if (currentToken() != TOKEN_IDENTIFIER) {
            THROW_TOKEN_ERROR("Identifier expected <identifier> but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            return chainedIdentifier;
        }
        chainedIdentifier->identifier = currentToken();

        if (peekToken(1) == "(") {
            nextToken(2);

            auto functionCall = std::make_shared<FunctionCallNode>(*chainedIdentifier);
            chainedIdentifier = functionCall;

            if (currentToken() != ")" || currentToken() == TOKEN_IDENTIFIER)
                parseMixedArguments(functionCall->arguments, functionCall);

            if (currentToken() != ")") {
                THROW_TOKEN_ERROR("Function call expected ')' but got '{}' instead.",
                                  currentToken()->content)
                parent->isFailed = true;
                return functionCall;
            }
        }

        chainedIdentifier->endToken = currentToken();

        lastIdentifier->nextIdentifier = chainedIdentifier;
        chainedIdentifier->lastIdentifier = lastIdentifier;
        lastIdentifier = chainedIdentifier;
        identifierNode = chainedIdentifier;
    }

    if (peekToken(1) == "{") {
        nextToken(2);

        if (identifierNode->kind == AST_FUNCTION_CALL) {
            THROW_TOKEN_ERROR("Can't do a struct creation with a function call.")
            parent->isFailed = true;
            return identifierNode;
        }

        if (identifierNode->nextIdentifier != nullptr) {
            THROW_NODE_ERROR(identifierNode, "Struct creation nodes can't have child nodes.")
            parent->isFailed = true;
            return identifierNode;
        }

        auto structCreate = std::make_shared<StructCreateNode>();
        structCreate->startToken = identifierNode->startToken;
        structCreate->scope = parent->scope;
        structCreate->parent = parent;

        structCreate->endIdentifier = identifierNode;

        identifierNode->scope = structCreate->scope;
        identifierNode->parent = structCreate;

        parseNamedArguments(structCreate->arguments, structCreate);
        structCreate->endToken = currentToken();

        if (currentToken() != "}") {
            THROW_TOKEN_ERROR("Struct create expected '}}' but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            return structCreate;
        }

        return structCreate;
    } else if (peekToken(1) == "=") {
        nextToken(2);

        if (identifierNode->kind == AST_FUNCTION_CALL) {
            THROW_TOKEN_ERROR( "Can't do a assignment with a function call.")
            parent->isFailed = true;
            return identifierNode;
        }

        auto assignment = std::make_shared<AssignmentNode>();
        assignment->startToken = identifierNode->startToken;
        assignment->scope = parent->scope;
        assignment->parent = parent;

        assignment->endIdentifier = identifierNode;

        identifierNode->scope = assignment->scope;
        identifierNode->parent = assignment;

        assignment->expression = parseRelational(assignment);
        assignment->endToken = currentToken();

        return assignment;
    }

    return identifierNode;
}

std::shared_ptr<ReturnNode> Parser::parseReturn(const std::shared_ptr<ASTNode> &parent) {
    auto returnNode = std::make_shared<ReturnNode>();
    returnNode->startToken = currentToken();
    returnNode->parent = parent;
    returnNode->scope = parent->scope;

    if (currentToken() != "return") {
        THROW_TOKEN_ERROR("Return expected 'return' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return returnNode;
    }

    nextToken();
    returnNode->expression = parseRelational(returnNode);

    // TODO: Reset here if failed, because expressions are just optional (do it a better way..)
    if(returnNode->isFailed) {
        returnNode->expression = nullptr;
        returnNode->isFailed = false;
        undoToken();
    }

    returnNode->endToken = currentToken();
    return returnNode;
}

std::shared_ptr<StructNode> Parser::parseStruct(const std::shared_ptr<ASTNode> &parent) {
    auto structNode = std::make_shared<StructNode>();
    structNode->startToken = currentToken();
    structNode->parent = parent;
    structNode->scope = std::make_shared<SymbolTable>(parent->scope);

    if (currentToken() != "struct") {
        THROW_TOKEN_ERROR("Struct expected 'struct' but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return structNode;
    }

    if(peekToken(1) == "@") {
        structNode->isBuiltin = true;
        nextToken();
    }

    if (nextToken() != TOKEN_IDENTIFIER) {
        THROW_TOKEN_ERROR("Struct expected <identifier> but got '{}' instead.",
                          currentToken()->content)
        parent->isFailed = true;
        return structNode;
    }
    structNode->name = currentToken();
    parent->scope->insert(structNode->name->content, structNode);

    if(!structNode->isBuiltin) {
        if (nextToken() != "{") {
            THROW_TOKEN_ERROR("Struct expected '{{' but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            return structNode;
        }

        nextToken();
        while (position < tokens.size()) {
            if (currentToken() == "}")
                break;

            if (currentToken() == "var" || currentToken() == "const")
                structNode->variables.push_back(parseVariable(structNode,
                                                              structNode->scope));
            else if (currentToken() != TOKEN_WHITESPACE &&
                     currentToken() != TOKEN_COMMENT) {
                THROW_TOKEN_ERROR("Struct expected <variable> but got '{}' instead.",
                                  currentToken()->content)
                parent->isFailed = true;

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
    }

    structNode->endToken = currentToken();
    return structNode;
}

void Parser::parseMixedArguments(std::vector<std::shared_ptr<ArgumentNode>> &arguments,
                                 const std::shared_ptr<ASTNode> &parent) {
    auto mustBeNamed = false;
    while (position < tokens.size()) {
        auto argument = std::make_shared<ArgumentNode>();
        argument->startToken = currentToken();
        argument->parent = parent;
        argument->scope = parent->scope;

        if (mustBeNamed ||
            (currentToken() == TOKEN_IDENTIFIER && peekToken(1) == ":")) {
            if (currentToken() != TOKEN_IDENTIFIER) {
                THROW_TOKEN_ERROR("Arguments expected <identifier> but got '{}' instead.",
                                  currentToken()->content)
                parent->isFailed = true;
                break;
            }
            argument->name = currentToken();

            if (nextToken() != ":") {
                THROW_TOKEN_ERROR("Arguments expected ':' but got '{}' instead.",
                                  currentToken()->content)
                parent->isFailed = true;
                break;
            }

            mustBeNamed = true;
            nextToken();
        }

        argument->expression = parseRelational(argument);
        argument->endToken = currentToken();

        arguments.push_back(argument);

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }
}

void Parser::parseNamedArguments(std::vector<std::shared_ptr<ArgumentNode>> &arguments,
                                 const std::shared_ptr<ASTNode> &parent) {
    while (position < tokens.size()) {
        auto argument = std::make_shared<ArgumentNode>();
        argument->startToken = currentToken();
        argument->parent = parent;
        argument->scope = parent->scope;

        if (currentToken() != TOKEN_IDENTIFIER) {
            THROW_TOKEN_ERROR("Arguments expected <identifier> but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            break;
        }
        argument->name = currentToken();

        if (nextToken() != ":") {
            THROW_TOKEN_ERROR("Arguments expected ':' but got '{}' instead.",
                              currentToken()->content)
            parent->isFailed = true;
            break;
        }

        nextToken();
        argument->expression = parseRelational(argument);
        argument->endToken = currentToken();

        arguments.push_back(argument);

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }
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

std::shared_ptr<Token> Parser::undoToken(int times, bool advance, bool safety) {
    std::shared_ptr<Token> token;

    for (int index = 0; index < times; index++) {
        position--;

        while (position > 0 && advance) {
            if (currentToken(safety) != TOKEN_WHITESPACE &&
                currentToken(safety) != TOKEN_COMMENT)
                break;
            this->position--;
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
