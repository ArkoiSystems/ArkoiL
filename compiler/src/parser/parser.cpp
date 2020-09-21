//
// Created by timo on 7/30/20.
//

#include "parser.h"

#include <utility>

#include "../parser/symboltable.h"
#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "../utils.h"
#include "astnodes.h"

Parser::Parser(std::string sourcePath, std::string sourceCode,
               std::vector<std::shared_ptr<Token>> tokens)
        : m_SourceCode(std::move(sourceCode)), m_SourcePath(std::move(sourcePath)),
          m_Tokens(std::move(tokens)), m_Position(0) {}

std::shared_ptr<RootNode> Parser::parseRoot() {
    auto rootNode = std::make_shared<RootNode>();
    if (m_Tokens.empty())
        return rootNode;

    rootNode->setStartToken(currentToken());
    rootNode->setSourcePath(m_SourcePath);
    rootNode->setSourceCode(m_SourceCode);
    rootNode->setScope(std::make_shared<SymbolTable>(nullptr));

    while (m_Position < m_Tokens.size()) {
        if (currentToken() == "import")
            rootNode->addNode(parseImport(rootNode));
        else if (currentToken() == "var" || currentToken() == "const")
            rootNode->addNode(parseVariable(rootNode, rootNode->getScope()));
        else if (currentToken() == "struct")
            rootNode->addNode(parseStruct(rootNode));
        else if (currentToken() == "[" || currentToken() == "fun") {
            std::set<std::string> annotations;
            if (currentToken() == "[") {
                annotations = parseAnnotations(rootNode);
                nextToken();
            }

            if (currentToken() == "fun")
                rootNode->addNode(parseFunction(annotations, rootNode));
        } else if (currentToken() != Token::WHITESPACE &&
                   currentToken() != Token::COMMENT) {
            THROW_TOKEN_ERROR("Root expected <import>, <function>, <variable> or <structure> but got '{}' "
                              "instead.",
                              currentToken()->getContent())
            rootNode->setFailed(true);

            while (m_Position < m_Tokens.size()) {
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

    rootNode->setEndToken(currentToken());
    return rootNode;
}

std::shared_ptr<ImportNode> Parser::parseImport(const std::shared_ptr<ASTNode> &parent) {
    auto importNode = std::make_shared<ImportNode>();
    importNode->setStartToken(currentToken());
    importNode->setParent(parent);
    importNode->setScope(parent->getScope());

    if (currentToken() != "import") {
        THROW_TOKEN_ERROR("Import expected 'import' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return importNode;
    }

    if (nextToken() != Token::STRING) {
        THROW_TOKEN_ERROR("Import expected <string> but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return importNode;
    }
    importNode->setPath(currentToken());

    importNode->setEndToken(currentToken());
    return importNode;
}

std::shared_ptr<FunctionNode> Parser::parseFunction(const std::set<std::string> &annotations,
                                                    const std::shared_ptr<ASTNode> &parent) {
    auto functionNode = std::make_shared<FunctionNode>();
    functionNode->setStartToken(currentToken());
    functionNode->setParent(parent);
    functionNode->setScope(parent->getScope());

    functionNode->setAnnotations(annotations);

    if (currentToken() != "fun") {
        THROW_TOKEN_ERROR("Function expected 'fun' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return functionNode;
    }

    auto isIntrinsic = false;
    if (nextToken() == "llvm") {
        if (nextToken() != ".") {
            THROW_TOKEN_ERROR("Function expected '.' but got '{}' instead.", currentToken()->getContent())
            parent->setFailed(true);
            return functionNode;
        }

        functionNode->setNative(true);
        isIntrinsic = true;
        nextToken();
    }

    if (currentToken() != Token::IDENTIFIER) {
        THROW_TOKEN_ERROR("Function expected <identifier> but got '{}' instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return functionNode;
    }

    functionNode->setName(currentToken());
    if (isIntrinsic)
        functionNode->getName()->setContent("llvm." + functionNode->getName()->getContent());

    functionNode->getScope()->insert(functionNode->getName()->getContent(), functionNode);

    if (nextToken() != "(") {
        THROW_TOKEN_ERROR("Function expected '(' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return functionNode;
    }

    auto parameterScope = std::make_shared<SymbolTable>(functionNode->getScope());
    functionNode->setScope(parameterScope);
    if (nextToken() != ")") {
        std::vector<std::shared_ptr<Token>> chainedParameters;
        while (m_Position < m_Tokens.size()) {
            if (currentToken() != Token::IDENTIFIER)
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
                    chainedParameter->setStartToken(parameter->getStartToken());
                    chainedParameter->setEndToken(parameter->getEndToken());
                    chainedParameter->setType(parameter->getType());
                    chainedParameter->setScope(parameterScope);
                    chainedParameter->setParent(functionNode);
                    chainedParameter->setName(name);
                    parameterScope->insert(chainedParameter->getName()->getContent(), chainedParameter);
                    functionNode->addParameter(chainedParameter);
                }
            }

            functionNode->addParameter(parameter);

            if (nextToken() != ",")
                break;
            nextToken(1, true, false);
        }
    }

    if (currentToken() == "." && peekToken(1) == "." && peekToken(2) == ".") {
        functionNode->setVariadic(true);
        nextToken(3);
    }

    if (currentToken() != ")") {
        THROW_TOKEN_ERROR("Function expected ')' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return functionNode;
    }

    if (nextToken() != ":") {
        THROW_TOKEN_ERROR("Function expected ':' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return functionNode;
    }

    nextToken();
    functionNode->setType(parseType(functionNode));

    if (peekToken(1) == "{" || peekToken(1) == "=") {
        if (isIntrinsic) {
            THROW_TOKEN_ERROR("Intrinsic functions can't have a body.", currentToken()->getContent())
            parent->setFailed(true);
            return functionNode;
        }

        nextToken();
        functionNode->setBlock(parseBlock(functionNode, parameterScope));
    } else functionNode->setNative(true);

    functionNode->setEndToken(currentToken());
    return functionNode;
}

std::shared_ptr<ParameterNode> Parser::parseParameter(const std::shared_ptr<ASTNode> &parent) {
    auto parameterNode = std::make_shared<ParameterNode>();
    parameterNode->setStartToken(currentToken());
    parameterNode->setParent(parent);
    parameterNode->setScope(parent->getScope());

    if (currentToken() != Token::IDENTIFIER) {
        THROW_TOKEN_ERROR("Parameter expected <identifier> but got '{}' instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return parameterNode;
    }
    parameterNode->setName(currentToken());
    parameterNode->getScope()->insert(parameterNode->getName()->getContent(), parameterNode);

    if (nextToken() != ":") {
        THROW_TOKEN_ERROR("Parameter expected ':' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return parameterNode;
    }

    nextToken();
    parameterNode->setType(parseType(parameterNode));

    parameterNode->setEndToken(currentToken());
    return parameterNode;
}

std::shared_ptr<TypeNode> Parser::parseType(const std::shared_ptr<ASTNode> &parent) {
    auto typeNode = std::make_shared<TypeNode>();
    typeNode->setStartToken(currentToken());
    typeNode->setParent(parent);
    typeNode->setScope(parent->getScope());

    if (currentToken() != Token::TYPE && currentToken() != Token::IDENTIFIER) {
        THROW_TOKEN_ERROR("Type expected <kind> or <identifier> but got '{}' instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return typeNode;
    }
    typeNode->setTypeToken(currentToken());

    while (m_Position < m_Tokens.size()) {
        if (peekToken(1) != "*")
            break;
        nextToken();
        typeNode->setPointerLevel(typeNode->getPointerLevel() + 1);
    }

    typeNode->setEndToken(currentToken());
    return typeNode;
}

std::shared_ptr<BlockNode> Parser::parseBlock(const std::shared_ptr<ASTNode> &parent,
                                              const std::shared_ptr<SymbolTable> &scope) {
    auto blockNode = std::make_shared<BlockNode>();
    blockNode->setStartToken(currentToken());
    blockNode->setParent(parent);
    blockNode->setScope(scope);

    if (currentToken() == "{") {
        nextToken();

        while (m_Position < m_Tokens.size()) {
            if (currentToken() == "}")
                break;

            if (currentToken() == "var" || currentToken() == "const")
                blockNode->addNode(parseVariable(blockNode, blockNode->getScope()));
            else if (currentToken() == Token::IDENTIFIER ||
                     (currentToken() == "&" || currentToken() == "@")) {
                auto identifier = parseIdentifier(blockNode);
                blockNode->addNode(identifier);

                if (identifier->getKind() == ASTNode::STRUCT_CREATE) {
                    THROW_NODE_ERROR(identifier, "Creating a struct without binding it to a variable is "
                                                 "unnecessary.")
                    parent->setFailed(true);
                }
            } else if (currentToken() == "return")
                blockNode->addNode(parseReturn(blockNode));
            else if (currentToken() != Token::WHITESPACE &&
                     currentToken() != Token::COMMENT) {
                THROW_TOKEN_ERROR("Block expected <variable>, <identifier> or <return> but got '{}' instead.",
                                  currentToken()->getContent())
                parent->setFailed(true);

                while (m_Position < m_Tokens.size()) {
                    if (currentToken() == "var" || currentToken() == "const" ||
                        currentToken() == Token::IDENTIFIER ||
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
            THROW_TOKEN_ERROR("Block expected '}}' but got '{}' instead.", currentToken()->getContent())
            parent->setFailed(true);
            return blockNode;
        }
    } else if (currentToken() == "=") {
        nextToken();
        blockNode->addNode(parseRelational(blockNode));
    } else {
        THROW_TOKEN_ERROR("Block expected '{{' or '=' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return blockNode;
    }

    blockNode->setEndToken(currentToken());
    return blockNode;
}

std::shared_ptr<VariableNode> Parser::parseVariable(const std::shared_ptr<ASTNode> &parent,
                                                    const std::shared_ptr<SymbolTable> &scope) {
    auto variableNode = std::make_shared<VariableNode>();
    variableNode->setStartToken(currentToken());
    variableNode->setParent(parent);
    variableNode->setScope(scope);

    if (currentToken() != "var" && currentToken() != "const") {
        THROW_TOKEN_ERROR("Variable expected 'var' or 'const' but got '{}' instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return variableNode;
    }
    variableNode->setConstant(currentToken() == "const");

    if (nextToken() != Token::IDENTIFIER && currentToken() != "_") {
        THROW_TOKEN_ERROR("Variable expected <identifier> but got '{}' instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return variableNode;
    }
    variableNode->setName(currentToken());
    variableNode->getScope()->insert(variableNode->getName()->getContent(), variableNode);

    if (peekToken(1) == ":") {
        nextToken(2);
        variableNode->setType(parseType(variableNode));
    }

    if (peekToken(1) == "=") {
        nextToken(2);
        variableNode->setExpression(parseRelational(variableNode));
    }

    variableNode->setEndToken(currentToken());
    return variableNode;
}

std::shared_ptr<OperableNode> Parser::parseRelational(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseAdditive(parent);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->getContent().c_str())) {
            case Utils::hash(">"):
                operatorKind = BinaryNode::GREATER_THAN;
                break;
            case Utils::hash("<"):
                operatorKind = BinaryNode::LESS_THAN;
                break;
            case Utils::hash(">="):
                operatorKind = BinaryNode::GREATER_EQUAL_THAN;
                break;
            case Utils::hash("<="):
                operatorKind = BinaryNode::LESS_EQUAL_THAN;
                break;
            case Utils::hash("=="):
                operatorKind = BinaryNode::EQUAL;
                break;
            case Utils::hash("!="):
                operatorKind = BinaryNode::NOT_EQUAL;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->setStartToken(lhs->getStartToken());
        newLhs->setScope(lhs->getScope());
        newLhs->setParent(lhs);
        newLhs->setLHS(lhs);
        newLhs->setOperatorKind(operatorKind);
        newLhs->setRHS(parseRelational(lhs));
        newLhs->setEndToken(newLhs->getRHS()->getEndToken());
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseAdditive(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseMultiplicative(parent);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->getContent().c_str())) {
            case Utils::hash("+"):
                operatorKind = BinaryNode::ADDITION;
                break;
            case Utils::hash("-"):
                operatorKind = BinaryNode::SUBTRACTION;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->setStartToken(lhs->getStartToken());
        newLhs->setScope(lhs->getScope());
        newLhs->setParent(lhs);
        newLhs->setLHS(lhs);
        newLhs->setOperatorKind(operatorKind);
        newLhs->setRHS(parseAdditive(lhs));
        newLhs->setEndToken(newLhs->getRHS()->getEndToken());
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseMultiplicative(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseCast(parent);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->getContent().c_str())) {
            case Utils::hash("*"):
                operatorKind = BinaryNode::MULTIPLICATION;
                break;
            case Utils::hash("/"):
                operatorKind = BinaryNode::DIVISION;
                break;
            case Utils::hash("%"):
                operatorKind = BinaryNode::REMAINING;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->setStartToken(lhs->getStartToken());
        newLhs->setScope(lhs->getScope());
        newLhs->setParent(lhs);
        newLhs->setLHS(lhs);
        newLhs->setOperatorKind(operatorKind);
        newLhs->setRHS(parseMultiplicative(lhs));
        newLhs->setEndToken(newLhs->getRHS()->getEndToken());
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseCast(const std::shared_ptr<ASTNode> &parent) {
    auto lhs = parseOperable(parent);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (Utils::hash(peekToken(1)->getContent().c_str())) {
            case Utils::hash("bitcast"):
                operatorKind = BinaryNode::BIT_CAST;
                break;
            default:
                return lhs;
        }

        nextToken(2);

        auto newLhs = std::make_shared<BinaryNode>();
        newLhs->setStartToken(lhs->getStartToken());
        newLhs->setScope(lhs->getScope());
        newLhs->setParent(lhs);
        newLhs->setLHS(lhs);
        newLhs->setOperatorKind(operatorKind);
        newLhs->setRHS(parseType(lhs));
        newLhs->setEndToken(newLhs->getRHS()->getEndToken());
        lhs = newLhs;
    }
}

std::shared_ptr<OperableNode> Parser::parseOperable(const std::shared_ptr<ASTNode> &parent) {
    if(currentToken() == "{") {
        auto structCreate = std::make_shared<StructCreateNode>();
        structCreate->setStartToken(currentToken());
        structCreate->setScope(std::make_shared<SymbolTable>(parent->getScope()));
        structCreate->setParent(parent);

        structCreate->setUnnamed(true);

        nextToken();

        if (currentToken() != "}")
            parseStructArguments(structCreate, structCreate);
        structCreate->setEndToken(currentToken());
        return structCreate;
    } else if(currentToken() == "-") {
        auto operable = std::make_shared<UnaryNode>();
        operable->setStartToken(currentToken());
        operable->setParent(parent);
        operable->setScope(parent->getScope());

        nextToken();
        operable->setExpression(parseOperable(operable));

        operable->setOperatorKind(UnaryNode::NEGATE);
        operable->setEndToken(operable->getExpression()->getEndToken());
        return operable;
    } else if(currentToken() == "(") {
        auto operable = std::make_shared<ParenthesizedNode>();
        operable->setStartToken(currentToken());
        operable->setParent(parent);
        operable->setScope(parent->getScope());

        nextToken();
        operable->setExpression(parseRelational(operable));

        operable->setEndToken(operable->getExpression()->getEndToken());

        if (nextToken() != ")") {
            THROW_TOKEN_ERROR("Parenthesized expected ')' but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            return operable;
        }

        return operable;
    } else if (currentToken() == Token::NUMBER) {
        auto operable = std::make_shared<NumberNode>();
        operable->setStartToken(currentToken());
        operable->setParent(parent);
        operable->setScope(parent->getScope());
        operable->setNumber(currentToken());
        operable->setEndToken(currentToken());
        return operable;
    } else if (currentToken() == Token::STRING) {
        auto operable = std::make_shared<StringNode>();
        operable->setStartToken(currentToken());
        operable->setParent(parent);
        operable->setScope(parent->getScope());
        operable->setString(currentToken());
        operable->setEndToken(currentToken());
        return operable;
    } else if (currentToken() == Token::IDENTIFIER ||
               (currentToken() == "&" || currentToken() == "@")) {
        return parseIdentifier(parent);
    } else {
        auto operable = std::make_shared<OperableNode>();
        THROW_TOKEN_ERROR("Operable expected <string>, <number>, <unary> or <parenthesized> but got '{}' "
                          "instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return operable;
    }
}

std::shared_ptr<OperableNode> Parser::parseIdentifier(const std::shared_ptr<ASTNode> &parent) {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->setStartToken(currentToken());
    identifierNode->setParent(parent);
    identifierNode->setScope(parent->getScope());

    if (currentToken() == "&") {
        identifierNode->setPointer(true);
        nextToken();
    } else if (currentToken() == "@") {
        identifierNode->setDereference(true);
        nextToken();
    }

    if (currentToken() != Token::IDENTIFIER) {
        THROW_TOKEN_ERROR("Identifier expected <identifier> but got '{}' instead.",
                          currentToken()->getContent())
        parent->setFailed(true);
        return identifierNode;
    }
    identifierNode->setIdentifier(currentToken());

    if (identifierNode->getIdentifier()->getContent() == "llvm" && peekToken(1) == ".") {
        nextToken(1);

        if (nextToken() != Token::IDENTIFIER) {
            THROW_TOKEN_ERROR("Identifier expected <identifier> but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            return identifierNode;
        }

        identifierNode->getIdentifier()->setContent("llvm." + currentToken()->getContent());
    }

    if (peekToken(1) == "(") {
        nextToken(2);

        auto functionCall = std::make_shared<FunctionCallNode>(*identifierNode);
        functionCall->setScope(std::make_shared<SymbolTable>(functionCall->getScope()));
        identifierNode = functionCall;

        if (currentToken() != ")" || currentToken() == Token::IDENTIFIER)
            parseCallArguments(functionCall, functionCall);

        if (currentToken() != ")") {
            THROW_TOKEN_ERROR("Function call expected ')' but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            return functionCall;
        }
    }

    identifierNode->setEndToken(currentToken());

    auto startIdentifier = identifierNode;
    auto lastIdentifier = identifierNode;
    while (peekToken(1) == ".") {
        nextToken(2);

        auto chainedIdentifier = std::make_shared<IdentifierNode>();
        chainedIdentifier->setStartToken(currentToken());
        chainedIdentifier->setParent(lastIdentifier);
        chainedIdentifier->setScope(parent->getScope());

        if (currentToken() == "&") {
            chainedIdentifier->setPointer(true);
            nextToken();
        } else if (currentToken() == "@") {
            chainedIdentifier->setDereference(true);
            nextToken();
        }

        if (currentToken() != Token::IDENTIFIER) {
            THROW_TOKEN_ERROR("Identifier expected <identifier> but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            return chainedIdentifier;
        }
        chainedIdentifier->setIdentifier(currentToken());

        if (peekToken(1) == "(") {
            nextToken(2);

            auto functionCall = std::make_shared<FunctionCallNode>(*chainedIdentifier);
            functionCall->setScope(std::make_shared<SymbolTable>(functionCall->getScope()));
            chainedIdentifier = functionCall;

            if (currentToken() != ")" || currentToken() == Token::IDENTIFIER)
                parseCallArguments(functionCall, functionCall);

            if (currentToken() != ")") {
                THROW_TOKEN_ERROR("Function call expected ')' but got '{}' instead.",
                                  currentToken()->getContent())
                parent->setFailed(true);
                return functionCall;
            }
        }

        chainedIdentifier->setEndToken(currentToken());

        lastIdentifier->setNextIdentifier(chainedIdentifier);
        chainedIdentifier->setLastIdentifier(lastIdentifier);
        lastIdentifier = chainedIdentifier;
        identifierNode = chainedIdentifier;
    }

    if (peekToken(1) == "{") {
        nextToken(2);

        if (identifierNode->getKind() == ASTNode::FUNCTION_CALL) {
            THROW_TOKEN_ERROR("Can't do a struct creation with a function call.")
            parent->setFailed(true);
            return identifierNode;
        }

        if (identifierNode->getNextIdentifier() != nullptr) {
            THROW_NODE_ERROR(identifierNode, "Struct creation nodes can't have child nodes.")
            parent->setFailed(true);
            return identifierNode;
        }

        auto structCreate = std::make_shared<StructCreateNode>();
        structCreate->setStartToken(identifierNode->getStartToken());
        structCreate->setScope(std::make_shared<SymbolTable>(parent->getScope()));
        structCreate->setParent(parent);

        startIdentifier->setParent(structCreate);
        structCreate->setStartIdentifier(startIdentifier);
        structCreate->setEndIdentifier(identifierNode);

        identifierNode->setScope(structCreate->getScope());
        identifierNode->setParent(structCreate);

        if (currentToken() != "}")
            parseStructArguments(structCreate, structCreate);
        structCreate->setEndToken(currentToken());

        if (currentToken() != "}") {
            THROW_TOKEN_ERROR("Struct create expected '}}' but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            return structCreate;
        }

        return structCreate;
    } else if (peekToken(1) == "=") {
        nextToken(2);

        if (identifierNode->getKind() == ASTNode::FUNCTION_CALL) {
            THROW_TOKEN_ERROR("Can't do a assignment with a function call.")
            parent->setFailed(true);
            return identifierNode;
        }

        auto assignment = std::make_shared<AssignmentNode>();
        assignment->setStartToken(identifierNode->getStartToken());
        assignment->setScope(parent->getScope());
        assignment->setParent(parent);

        startIdentifier->setParent(assignment);
        assignment->setStartIdentifier(startIdentifier);
        assignment->setEndIdentifier(identifierNode);

        identifierNode->setScope(assignment->getScope());
        identifierNode->setParent(assignment);

        assignment->setExpression(parseRelational(assignment));
        assignment->setEndToken(currentToken());

        return assignment;
    }

    return identifierNode;
}

std::shared_ptr<ReturnNode> Parser::parseReturn(const std::shared_ptr<ASTNode> &parent) {
    auto returnNode = std::make_shared<ReturnNode>();
    returnNode->setStartToken(currentToken());
    returnNode->setParent(parent);
    returnNode->setScope(parent->getScope());

    if (currentToken() != "return") {
        THROW_TOKEN_ERROR("Return expected 'return' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return returnNode;
    }

    nextToken();
    returnNode->setExpression(parseRelational(returnNode));

    // TODO: Issue 5
    if (returnNode->isFailed()) {
        returnNode->setExpression(nullptr);
        returnNode->setFailed(false);
        undoToken();
    }

    returnNode->setEndToken(currentToken());
    return returnNode;
}

std::shared_ptr<StructNode> Parser::parseStruct(const std::shared_ptr<ASTNode> &parent) {
    auto structNode = std::make_shared<StructNode>();
    structNode->setStartToken(currentToken());
    structNode->setParent(parent);
    structNode->setScope(std::make_shared<SymbolTable>(parent->getScope()));

    if (currentToken() != "struct") {
        THROW_TOKEN_ERROR("Struct expected 'struct' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return structNode;
    }

    if (nextToken() != Token::IDENTIFIER) {
        THROW_TOKEN_ERROR("Struct expected <identifier> but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return structNode;
    }
    structNode->setName(currentToken());
    parent->getScope()->insert(structNode->getName()->getContent(), structNode);

    if (nextToken() != "{") {
        THROW_TOKEN_ERROR("Struct expected '{{' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return structNode;
    }

    nextToken();
    while (m_Position < m_Tokens.size()) {
        if (currentToken() == "}")
            break;

        if (currentToken() == "var" || currentToken() == "const")
            structNode->addVariable(parseVariable(structNode, structNode->getScope()));
        else if (currentToken() != Token::WHITESPACE && currentToken() != Token::COMMENT) {
            THROW_TOKEN_ERROR("Struct expected <variable> but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);

            while (m_Position < m_Tokens.size()) {
                if (currentToken() == "var" || currentToken() == "const" ||
                    currentToken() == "}")
                    break;
                nextToken(1, true, false);
            }

            continue;
        }

        nextToken(1, true, false);
    }

    structNode->setEndToken(currentToken());
    return structNode;
}

std::set<std::string> Parser::parseAnnotations(const std::shared_ptr<ASTNode> &parent) {
    std::set<std::string> annotations;
    if (currentToken() != "[") {
        THROW_TOKEN_ERROR("Annotations expected '[' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return annotations;
    }

    nextToken();
    while (m_Position < m_Tokens.size()) {
        if (currentToken() == "]")
            break;

        if (currentToken() != Token::IDENTIFIER) {
            THROW_TOKEN_ERROR("Annotations expected <identifier> but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            return annotations;
        }

        annotations.insert(currentToken()->getContent());

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }

    if (currentToken() != "]") {
        THROW_TOKEN_ERROR("Annotations expected ']' but got '{}' instead.", currentToken()->getContent())
        parent->setFailed(true);
        return annotations;
    }

    return annotations;
}

void Parser::parseCallArguments(std::shared_ptr<FunctionCallNode> &functionCallNode,
                                const std::shared_ptr<ASTNode> &parent) {
    auto mustBeNamed = false;
    while (m_Position < m_Tokens.size()) {
        auto argument = std::make_shared<ArgumentNode>();
        argument->setStartToken(currentToken());
        argument->setParent(parent);
        argument->setScope(parent->getScope());

        if (mustBeNamed || (currentToken() == Token::IDENTIFIER && peekToken(1) == ":")) {
            if (currentToken() != Token::IDENTIFIER) {
                THROW_TOKEN_ERROR("Arguments expected <identifier> but got '{}' instead.",
                                  currentToken()->getContent())
                parent->setFailed(true);
                break;
            }
            argument->setName(currentToken());

            if (nextToken() != ":") {
                THROW_TOKEN_ERROR("Arguments expected ':' but got '{}' instead.",
                                  currentToken()->getContent())
                parent->setFailed(true);
                break;
            }

            mustBeNamed = true;
            nextToken();
        }

        argument->setExpression(parseRelational(argument));
        argument->setEndToken(currentToken());
        if (argument->getName() != nullptr)
            argument->getScope()->insert(argument->getName()->getContent(), argument);

        functionCallNode->addArgument(argument);

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }
}

void Parser::parseStructArguments(std::shared_ptr<StructCreateNode> &structCreateNode,
                                  const std::shared_ptr<ASTNode> &parent) {
    while (m_Position < m_Tokens.size()) {
        auto argument = std::make_shared<ArgumentNode>();
        argument->setStartToken(currentToken());
        argument->setParent(parent);
        argument->setScope(parent->getScope());

        if (currentToken() != Token::IDENTIFIER) {
            THROW_TOKEN_ERROR("Arguments expected <identifier> but got '{}' instead.",
                              currentToken()->getContent())
            parent->setFailed(true);
            break;
        }
        argument->setName(currentToken());

        if (nextToken() != ":") {
            THROW_TOKEN_ERROR("Arguments expected ':' but got '{}' instead.", currentToken()->getContent())
            parent->setFailed(true);
            break;
        }

        nextToken();
        argument->setExpression(parseRelational(argument));
        argument->setEndToken(currentToken());
        argument->getScope()->insert(argument->getName()->getContent(), argument);

        structCreateNode->addArgument(argument);

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }
}

std::shared_ptr<Token> Parser::peekToken(int offset, bool advance, bool safety) {
    auto lastPosition = m_Position;
    auto token = nextToken(1, advance, safety);
    for (auto index = 1; index < offset; index++)
        token = nextToken(1, advance, safety);
    m_Position = lastPosition;
    return token;
}

std::shared_ptr<Token> Parser::nextToken(int times, bool advance, bool safety) {
    std::shared_ptr<Token> token;

    for (int index = 0; index < times; index++) {
        m_Position++;

        while (m_Position < m_Tokens.size() && advance) {
            if (currentToken(safety) != Token::WHITESPACE &&
                currentToken(safety) != Token::COMMENT)
                break;
            this->m_Position++;
        }

        token = currentToken(safety);
    }

    return token;
}

std::shared_ptr<Token> Parser::undoToken(int times, bool advance, bool safety) {
    std::shared_ptr<Token> token;

    for (int index = 0; index < times; index++) {
        m_Position--;

        while (m_Position > 0 && advance) {
            if (currentToken(safety) != Token::WHITESPACE &&
                currentToken(safety) != Token::COMMENT)
                break;
            this->m_Position--;
        }

        token = currentToken(safety);
    }

    return token;
}

std::shared_ptr<Token> Parser::currentToken(bool safety) {
    if (safety)
        m_Position = m_Position >= m_Tokens.size() ? m_Tokens.size() - 1 : m_Position;
    else if (m_Position >= m_Tokens.size())
        return nullptr;
    return m_Tokens[m_Position];
}