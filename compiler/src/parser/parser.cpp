//
// Created by timo on 7/30/20.
//

#include "../../include/parser/parser.h"

#include <utility>

#include <fmt/core.h>

#include "../../include/parser/symboltable.h"
#include "../../include/parser/astnodes.h"
#include "../../include/compiler/error.h"
#include "../../include/lexer/lexer.h"
#include "../../include/lexer/token.h"
#include "../../include/utils/utils.h"

Parser::Parser(std::string sourcePath, std::string sourceCode,
               std::vector<std::shared_ptr<Token>> tokens)
        : m_SourceCode(std::move(sourceCode)), m_SourcePath(std::move(sourcePath)),
          m_Tokens(std::move(tokens)), m_Position(0) {}

SharedRootNode Parser::parseRoot() {
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
        } else if (currentToken() != Token::WHITESPACE && currentToken() != Token::COMMENT) {
            throwParser(Error::ERROR, "Root expected <import>, <function>, <variable> or "
                                      "<structure> but got '{}' instead.",
                        currentToken()->getContent());

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

SharedImportNode Parser::parseImport(const SharedASTNode &parent, bool resetFirstFail) {
    auto importNode = std::make_shared<ImportNode>();
    importNode->setStartToken(currentToken());
    importNode->setParent(parent);
    importNode->setScope(parent->getScope());

    if (currentToken() != "import") {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Import expected 'import' but got '{}' instead.",
                    currentToken()->getContent());
        return importNode;
    }

    if (nextToken() != Token::STRING) {
        throwParser(Error::ERROR, "Import expected <string> but got '{}' instead.",
                    currentToken()->getContent());
        return importNode;
    }
    importNode->setPath(currentToken());

    importNode->setEndToken(currentToken());
    return importNode;
}

SharedFunctionNode Parser::parseFunction(const std::set<std::string> &annotations,
                                         const SharedASTNode &parent, bool resetFirstFail) {
    auto functionNode = std::make_shared<FunctionNode>();
    functionNode->setStartToken(currentToken());
    functionNode->setParent(parent);
    functionNode->setScope(parent->getScope());

    functionNode->setAnnotations(annotations);

    if (currentToken() != "fun") {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Function expected 'fun' but got '{}' instead.",
                    currentToken()->getContent());
        return functionNode;
    }

    auto isIntrinsic = false;
    if (nextToken() == "llvm") {
        if (nextToken() != ".") {
            throwParser(Error::ERROR, "Function expected '.' but got '{}' instead.",
                        currentToken()->getContent());
            return functionNode;
        }

        functionNode->setNative(true);
        isIntrinsic = true;
        nextToken();
    }

    if (currentToken() != Token::IDENTIFIER) {
        throwParser(Error::ERROR, "Function expected <identifier> but got '{}' instead.",
                    currentToken()->getContent());
        return functionNode;
    }

    if ((peekToken(1) == ":" && peekToken(2) == ":") && !isIntrinsic) {
        functionNode->setScopeResolution(parseType(functionNode));
        nextToken(3);

        if (currentToken() != Token::IDENTIFIER) {
            throwParser(Error::ERROR, "Function expected <identifier> but got '{}' instead.",
                        currentToken()->getContent());
            return functionNode;
        }
    }

    functionNode->setName(currentToken());
    if (isIntrinsic)
        functionNode->getName()->setContent("llvm." + functionNode->getName()->getContent());

    functionNode->getScope()->insert(functionNode->getName()->getContent(), functionNode);

    if (nextToken() != "(") {
        throwParser(Error::ERROR, "Function expected '(' but got '{}' instead.",
                    currentToken()->getContent());
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
                chainedParameters.emplace_back(currentToken());
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
                    parameterScope->insert(chainedParameter->getName()->getContent(),
                                           chainedParameter);
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
        throwParser(Error::ERROR, "Function expected ')' but got '{}' instead.",
                    currentToken()->getContent());
        return functionNode;
    }

    if (nextToken() != ":") {
        throwParser(Error::ERROR, "Function expected ':' but got '{}' instead.",
                    currentToken()->getContent());
        return functionNode;
    }

    nextToken();
    functionNode->setType(parseType(functionNode));

    if (peekToken(1) == "{" || peekToken(1) == "=") {
        if (isIntrinsic) {
            throwParser(Error::WARN, "Intrinsic functions can't have a body.",
                        currentToken()->getContent());
            return functionNode;
        }

        nextToken();
        functionNode->setBlock(parseBlock(functionNode, parameterScope));
    } else functionNode->setNative(true);

    functionNode->setEndToken(currentToken());
    return functionNode;
}

SharedParameterNode Parser::parseParameter(const SharedASTNode &parent, bool resetFirstFail) {
    auto parameterNode = std::make_shared<ParameterNode>();
    parameterNode->setStartToken(currentToken());
    parameterNode->setParent(parent);
    parameterNode->setScope(parent->getScope());

    if (currentToken() != Token::IDENTIFIER) {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Parameter expected <identifier> but got '{}' instead.",
                    currentToken()->getContent());
        return parameterNode;
    }
    parameterNode->setName(currentToken());
    parameterNode->getScope()->insert(parameterNode->getName()->getContent(), parameterNode);

    if (nextToken() != ":") {
        throwParser(Error::ERROR, "Parameter expected ':' but got '{}' instead.",
                    currentToken()->getContent());
        return parameterNode;
    }

    nextToken();
    parameterNode->setType(parseType(parameterNode));

    parameterNode->setEndToken(currentToken());
    return parameterNode;
}

SharedTypeNode Parser::parseType(const SharedASTNode &parent, bool resetFirstFail) {
    auto typeNode = std::make_shared<TypeNode>();
    typeNode->setStartToken(currentToken());
    typeNode->setParent(parent);
    typeNode->setScope(parent->getScope());

    if (currentToken() != Token::TYPE && currentToken() != Token::IDENTIFIER) {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Type expected <kind> or <identifier> but got '{}' instead.",
                    currentToken()->getContent());
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

SharedBlockNode Parser::parseBlock(const SharedASTNode &parent, const SharedSymbolTable &scope,
                                   bool resetFirstFail) {
    auto blockNode = std::make_shared<BlockNode>();
    blockNode->setStartToken(currentToken());
    blockNode->setParent(parent);
    blockNode->setScope(scope);

    blockNode->setInlined(currentToken() == "=");

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

                if (identifier->getKind() == ASTNode::STRUCT_CREATE)
                    throwNode(Error::WARN, identifier, "Creating a struct without binding it to "
                                                       "a variable is unnecessary.");
            } else if (currentToken() == "return")
                blockNode->addNode(parseReturn(blockNode));
            else if (currentToken() != Token::WHITESPACE &&
                     currentToken() != Token::COMMENT) {
                throwParser(Error::ERROR, "Block expected <variable>, <identifier> or <return> but"
                                          " got '{}' instead.",
                            currentToken()->getContent());

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
            throwParser(Error::ERROR, "Block expected '}}' but got '{}' instead.",
                        currentToken()->getContent());
            return blockNode;
        }
    } else if (currentToken() == "=") {
        nextToken();

        auto returnNode = std::make_shared<ReturnNode>();
        returnNode->setStartToken(blockNode->getStartToken());
        returnNode->setParent(blockNode);
        returnNode->setScope(blockNode->getScope());
        returnNode->setExpression(parseRelational(returnNode));
        returnNode->setEndToken(currentToken());
        blockNode->addNode(returnNode);
    } else {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Block expected '{{' or '=' but got '{}' instead.",
                    currentToken()->getContent());
        return blockNode;
    }

    blockNode->setEndToken(currentToken());
    return blockNode;
}

SharedVariableNode Parser::parseVariable(const SharedASTNode &parent,
                                         const SharedSymbolTable &scope, bool resetFirstFail) {
    auto variableNode = std::make_shared<VariableNode>();
    variableNode->setStartToken(currentToken());
    variableNode->setParent(parent);
    variableNode->setScope(scope);

    if (currentToken() != "var" && currentToken() != "const") {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Variable expected 'var' or 'const' but got '{}' instead.",
                    currentToken()->getContent());
        return variableNode;
    }
    variableNode->setConstant(currentToken() == "const");

    if (nextToken() != Token::IDENTIFIER && currentToken() != "_") {
        throwParser(Error::ERROR, "Variable expected <identifier> but got '{}' instead.",
                    currentToken()->getContent());
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

SharedOperableNode Parser::parseRelational(const SharedASTNode &parent, bool resetFirstFail) {
    auto lhs = parseAdditive(parent, resetFirstFail);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (utils::hash(peekToken(1)->getContent().c_str())) {
            case utils::hash(">"):
                operatorKind = BinaryNode::GREATER_THAN;
                break;
            case utils::hash("<"):
                operatorKind = BinaryNode::LESS_THAN;
                break;
            case utils::hash(">="):
                operatorKind = BinaryNode::GREATER_EQUAL_THAN;
                break;
            case utils::hash("<="):
                operatorKind = BinaryNode::LESS_EQUAL_THAN;
                break;
            case utils::hash("=="):
                operatorKind = BinaryNode::EQUAL;
                break;
            case utils::hash("!="):
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

SharedOperableNode Parser::parseAdditive(const SharedASTNode &parent, bool resetFirstFail) {
    auto lhs = parseMultiplicative(parent, resetFirstFail);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (utils::hash(peekToken(1)->getContent().c_str())) {
            case utils::hash("+"):
                operatorKind = BinaryNode::ADDITION;
                break;
            case utils::hash("-"):
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

SharedOperableNode Parser::parseMultiplicative(const SharedASTNode &parent, bool resetFirstFail) {
    auto lhs = parseCast(parent, resetFirstFail);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (utils::hash(peekToken(1)->getContent().c_str())) {
            case utils::hash("*"):
                operatorKind = BinaryNode::MULTIPLICATION;
                break;
            case utils::hash("/"):
                operatorKind = BinaryNode::DIVISION;
                break;
            case utils::hash("%"):
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

SharedOperableNode Parser::parseCast(const SharedASTNode &parent, bool resetFirstFail) {
    auto lhs = parseOperable(parent, resetFirstFail);
    while (true) {
        BinaryNode::BinaryKind operatorKind;
        switch (utils::hash(peekToken(1)->getContent().c_str())) {
            case utils::hash("bitcast"):
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

SharedOperableNode Parser::parseOperable(const SharedASTNode &parent, bool resetFirstFail) {
    if (currentToken() == "{") {
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
            throwParser(Error::ERROR, "Parenthesized expected ')' but got '{}' instead.",
                        currentToken()->getContent());
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
        if (resetFirstFail)
            return nullptr;

        auto operable = std::make_shared<OperableNode>();
        throwParser(Error::ERROR, "Operable expected <string>, <number>, <unary> or "
                                  "<parenthesized> but got '{}' instead.",
                    currentToken()->getContent());
        return operable;
    }
}

SharedOperableNode Parser::parseIdentifier(const SharedASTNode &parent, bool resetFirstFail) {
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
        if (resetFirstFail && !identifierNode->isPointer() && !identifierNode->isDereference())
            return nullptr;

        throwParser(Error::ERROR, "Identifier expected <identifier> but got '{}' instead.",
                    currentToken()->getContent());
        return identifierNode;
    }
    identifierNode->setIdentifier(currentToken());

    if (identifierNode->getIdentifier()->getContent() == "llvm" && peekToken(1) == ".") {
        nextToken(1);

        if (nextToken() != Token::IDENTIFIER) {
            throwParser(Error::ERROR, "Identifier expected <identifier> but got '{}' instead.",
                        currentToken()->getContent());
            return identifierNode;
        }

        identifierNode->getIdentifier()->setContent("llvm." + currentToken()->getContent());
    }

    identifierNode->setEndToken(currentToken());

    if (peekToken(1) == "{") {
        nextToken(2);

        if (identifierNode->getKind() == ASTNode::FUNCTION_CALL) {
            throwParser(Error::WARN, "Can't do a struct creation with a function call.");
            return identifierNode;
        }

        if (identifierNode->getNextIdentifier()) {
            throwNode(Error::WARN, identifierNode, "Struct creation nodes can't have child nodes.");
            return identifierNode;
        }

        auto structCreate = std::make_shared<StructCreateNode>();
        structCreate->setStartToken(identifierNode->getStartToken());
        structCreate->setScope(std::make_shared<SymbolTable>(parent->getScope()));
        structCreate->setParent(parent);

        identifierNode->setParent(structCreate);
        structCreate->setIdentifier(identifierNode);

        identifierNode->setScope(structCreate->getScope());
        identifierNode->setParent(structCreate);

        if (currentToken() != "}")
            parseStructArguments(structCreate, structCreate);
        structCreate->setEndToken(currentToken());

        if (currentToken() != "}") {
            throwParser(Error::ERROR, "Struct create expected '}}' but got '{}' instead.",
                        currentToken()->getContent());
            return structCreate;
        }

        return structCreate;
    }

    if (peekToken(1) == "(") {
        nextToken(2);

        auto functionCall = std::make_shared<FunctionCallNode>();

        functionCall->setStartToken(identifierNode->getStartToken());
        functionCall->setParent(identifierNode->getParent());
        functionCall->setScope(std::make_shared<SymbolTable>(identifierNode->getScope()));

        functionCall->setNextIdentifier(identifierNode->getNextIdentifier());
        functionCall->setLastIdentifier(identifierNode->getLastIdentifier());
        functionCall->setDereference(identifierNode->isDereference());
        functionCall->setIdentifier(identifierNode->getIdentifier());
        functionCall->setPointer(identifierNode->isPointer());

        identifierNode = functionCall;

        if (currentToken() != ")" || currentToken() == Token::IDENTIFIER)
            parseFunctionArguments(functionCall, functionCall);

        if (currentToken() != ")") {
            throwParser(Error::ERROR, "Function call expected ')' but got '{}' instead.",
                        currentToken()->getContent());
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
            throwParser(Error::ERROR, "Identifier expected <identifier> but got '{}' instead.",
                        currentToken()->getContent());
            return chainedIdentifier;
        }
        chainedIdentifier->setIdentifier(currentToken());

        if (peekToken(1) == "(") {
            nextToken(2);

            auto functionCall = std::make_shared<FunctionCallNode>();

            functionCall->setStartToken(chainedIdentifier->getStartToken());
            functionCall->setParent(chainedIdentifier->getParent());
            functionCall->setScope(std::make_shared<SymbolTable>(chainedIdentifier->getScope()));

            functionCall->setNextIdentifier(chainedIdentifier->getNextIdentifier());
            functionCall->setLastIdentifier(chainedIdentifier->getLastIdentifier());
            functionCall->setDereference(chainedIdentifier->isDereference());
            functionCall->setIdentifier(chainedIdentifier->getIdentifier());
            functionCall->setPointer(chainedIdentifier->isPointer());

            chainedIdentifier = functionCall;

            if (currentToken() != ")" || currentToken() == Token::IDENTIFIER)
                parseFunctionArguments(functionCall, functionCall);

            if (currentToken() != ")") {
                throwParser(Error::ERROR, "Function call expected ')' but got '{}' instead.",
                            currentToken()->getContent());
                return functionCall;
            }
        }

        chainedIdentifier->setEndToken(currentToken());

        lastIdentifier->setNextIdentifier(chainedIdentifier);
        chainedIdentifier->setLastIdentifier(lastIdentifier);
        lastIdentifier = chainedIdentifier;
        identifierNode = chainedIdentifier;
    }

    if (peekToken(1) == "=") {
        nextToken(2);

        if (identifierNode->getKind() == ASTNode::FUNCTION_CALL) {
            throwParser(Error::WARN, "Can't do an assignment with a function call.");
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

SharedReturnNode Parser::parseReturn(const SharedASTNode &parent, bool resetFirstFail) {
    auto returnNode = std::make_shared<ReturnNode>();
    returnNode->setStartToken(currentToken());
    returnNode->setParent(parent);
    returnNode->setScope(parent->getScope());

    if (currentToken() != "return") {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Return expected 'return' but got '{}' instead.",
                    currentToken()->getContent());
        return returnNode;
    }

    nextToken();
    returnNode->setExpression(parseRelational(returnNode, true));
    if (!returnNode->getExpression())
        undoToken();

    returnNode->setEndToken(currentToken());
    return returnNode;
}

SharedStructNode Parser::parseStruct(const SharedASTNode &parent, bool resetFirstFail) {
    auto structNode = std::make_shared<StructNode>();
    structNode->setStartToken(currentToken());
    structNode->setParent(parent);
    structNode->setScope(std::make_shared<SymbolTable>(parent->getScope()));

    if (currentToken() != "struct") {
        if (resetFirstFail)
            return nullptr;

        throwParser(Error::ERROR, "Struct expected 'struct' but got '{}' instead.",
                    currentToken()->getContent());
        return structNode;
    }

    if (nextToken() != Token::IDENTIFIER) {
        throwParser(Error::ERROR, "Struct expected <identifier> but got '{}' instead.",
                    currentToken()->getContent());
        return structNode;
    }
    structNode->setName(currentToken());
    parent->getScope()->insert(structNode->getName()->getContent(), structNode);

    if (nextToken() != "{") {
        throwParser(Error::ERROR, "Struct expected '{{' but got '{}' instead.",
                    currentToken()->getContent());
        return structNode;
    }

    nextToken();
    while (m_Position < m_Tokens.size()) {
        if (currentToken() == "}")
            break;

        if (currentToken() == "var" || currentToken() == "const")
            structNode->addVariable(parseVariable(structNode, structNode->getScope()));
        else if (currentToken() != Token::WHITESPACE && currentToken() != Token::COMMENT) {
            throwParser(Error::ERROR, "Struct expected <variable> but got '{}' instead.",
                        currentToken()->getContent());

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

std::set<std::string> Parser::parseAnnotations(const SharedASTNode &parent, bool resetFirstFail) {
    std::set<std::string> annotations;
    if (currentToken() != "[") {
        if (resetFirstFail)
            return annotations;

        throwParser(Error::ERROR, "Annotations expected '[' but got '{}' instead.",
                    currentToken()->getContent());
        return annotations;
    }

    nextToken();
    while (m_Position < m_Tokens.size()) {
        if (currentToken() == "]")
            break;

        if (currentToken() != Token::IDENTIFIER) {
            throwParser(Error::ERROR, "Annotations expected <identifier> but got '{}' instead.",
                        currentToken()->getContent());
            return annotations;
        }

        annotations.insert(currentToken()->getContent());

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }

    if (currentToken() != "]") {
        throwParser(Error::ERROR, "Annotations expected ']' but got '{}' instead.",
                    currentToken()->getContent());
        return annotations;
    }

    return annotations;
}

void Parser::parseFunctionArguments(const SharedFunctionCallNode &functionCallNode,
                                    const SharedASTNode &parent, bool resetFirstFail) {
    auto mustBeNamed = false;
    while (m_Position < m_Tokens.size()) {
        auto argument = std::make_shared<FunctionArgumentNode>();
        argument->setStartToken(currentToken());
        argument->setParent(parent);
        argument->setScope(parent->getScope());

        if (mustBeNamed || (currentToken() == Token::IDENTIFIER && peekToken(1) == ":")) {
            if (currentToken() != Token::IDENTIFIER) {
                if (resetFirstFail)
                    return;

                throwParser(Error::ERROR, "Arguments expected <identifier> but got '{}' instead.",
                            currentToken()->getContent());
                break;
            }
            argument->setName(currentToken());

            if (nextToken() != ":") {
                throwParser(Error::ERROR, "Arguments expected ':' but got '{}' instead.",
                            currentToken()->getContent());
                break;
            }

            mustBeNamed = true;
            nextToken();
        }

        argument->setExpression(parseRelational(argument, resetFirstFail));
        argument->setEndToken(currentToken());
        if (argument->getName())
            argument->getScope()->insert(argument->getName()->getContent(), argument);

        functionCallNode->addArgument(argument);
        resetFirstFail = false;

        if (nextToken() != ",")
            break;
        nextToken(1, true, false);
    }
}

void Parser::parseStructArguments(const SharedStructCreateNode &structCreateNode,
                                  const SharedASTNode &parent, bool resetFirstFail) {
    while (m_Position < m_Tokens.size()) {
        auto argument = std::make_shared<StructArgumentNode>();
        argument->setStartToken(currentToken());
        argument->setParent(parent);
        argument->setScope(parent->getScope());

        if (currentToken() != Token::IDENTIFIER) {
            if (resetFirstFail)
                return;

            throwParser(Error::ERROR, "Arguments expected <identifier> but got '{}' instead.",
                        currentToken()->getContent());
            break;
        }
        argument->setName(currentToken());

        if (nextToken() != ":") {
            throwParser(Error::ERROR, "Arguments expected ':' but got '{}' instead.",
                        currentToken()->getContent());
            break;
        }

        nextToken();
        argument->setExpression(parseRelational(argument, resetFirstFail));
        argument->setEndToken(currentToken());
        argument->getScope()->insert(argument->getName()->getContent(), argument);

        structCreateNode->addArgument(argument);
        resetFirstFail = false;

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

template<class... Args>
void Parser::throwParser(unsigned int errorType, Args... args) {
    std::cout << Error((Error::ErrorType) errorType, m_SourcePath, m_SourceCode,
                       currentToken()->getLineNumber(), currentToken()->getLineNumber(),
                       currentToken()->getStartChar(), currentToken()->getEndChar(),
                       fmt::format(args...));
}

template<class... Args>
void Parser::throwNode(unsigned int errorType, const SharedASTNode &node, Args...args) {
    std::cout << Error((Error::ErrorType) errorType,
                       node->findNodeOfParents<RootNode>()->getSourcePath(),
                       node->findNodeOfParents<RootNode>()->getSourceCode(),
                       node->getStartToken()->getLineNumber(),
                       node->getEndToken()->getLineNumber(),
                       node->getStartToken()->getStartChar(),
                       node->getEndToken()->getEndChar(),
                       fmt::format(args...));
}