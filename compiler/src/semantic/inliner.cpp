//
// Created by timo on 10/15/20.
//

#include "inliner.h"

#include "../parser/symboltable.h"
#include "../compiler/error.h"
#include "../parser/astnodes.h"
#include "../lexer/token.h"
#include "../utils/utils.h"
#include "typeresolver.h"

SharedVariableNode Inliner::visit(const SharedASTNode &node) {
    if (node->getKind() == ASTNode::ROOT) {
        return Inliner::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        return Inliner::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::BLOCK) {
        return Inliner::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        return Inliner::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        return Inliner::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        return Inliner::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        return Inliner::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (auto identifierNode = std::dynamic_pointer_cast<IdentifierNode>(node)) {
        auto firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        auto returnVariable = Inliner::visit(firstIdentifier);
        if (returnVariable != nullptr) {
            auto generatedIdentifier = createIdentifier(firstIdentifier->getParent(),
                                                        firstIdentifier->getScope(),
                                                        returnVariable);
            firstIdentifier->setKind(ASTNode::IDENTIFIER);
            firstIdentifier->setTypeResolved(false);
            firstIdentifier->setIdentifier(generatedIdentifier->getIdentifier());
            firstIdentifier->setStartToken(generatedIdentifier->getStartToken());
            firstIdentifier->setEndToken(generatedIdentifier->getEndToken());
            firstIdentifier->setAccessed(false);
        }

        auto lastIdentifier = firstIdentifier;
        auto currentIdentifier = firstIdentifier->getNextIdentifier();
        while (currentIdentifier != nullptr) {
            returnVariable = Inliner::visit(currentIdentifier);
            currentIdentifier->setTypeResolved(false);

            if (returnVariable != nullptr) {
                auto oldIdentifier = lastIdentifier->getNextIdentifier();
                lastIdentifier->setNextIdentifier(
                        createIdentifier(oldIdentifier, oldIdentifier->getScope(),
                                         returnVariable));
                currentIdentifier = lastIdentifier->getNextIdentifier();

                currentIdentifier->setNextIdentifier(oldIdentifier->getNextIdentifier());
                currentIdentifier->setLastIdentifier(oldIdentifier);
                currentIdentifier->setDereference(oldIdentifier->isDereference());
                currentIdentifier->setPointer(oldIdentifier->isPointer());
            }

            lastIdentifier = currentIdentifier;
            currentIdentifier = currentIdentifier->getNextIdentifier();
        }

        TypeResolver::visit(firstIdentifier);

        return nullptr;
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        return Inliner::visit(std::static_pointer_cast<FunctionArgumentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        return Inliner::visit(std::static_pointer_cast<StructArgumentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        return Inliner::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        return Inliner::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        return Inliner::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        return Inliner::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() != ASTNode::IMPORT && node->getKind() != ASTNode::PARAMETER
               && node->getKind() != ASTNode::TYPE && node->getKind() != ASTNode::NUMBER
               && node->getKind() != ASTNode::STRING && node->getKind() != ASTNode::OPERABLE) {
        THROW_NODE_ERROR(node, "Inliner: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }

    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedRootNode &rootNode) {
    for (const auto &node : rootNode->getNodes())
        Inliner::visit(node);

    auto rootCopiedNodes(rootNode->getNodes());
    for (const auto &nodeRoot : rootCopiedNodes) {
        if (nodeRoot->getKind() != ASTNode::FUNCTION)
            continue;

        auto functionNode = std::static_pointer_cast<FunctionNode>(nodeRoot);
        if (!functionNode->hasAnnotation("inlined"))
            continue;

        rootNode->removeNode(functionNode);
    }

    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedFunctionNode &functionNode) {
    if (!functionNode->isNative())
        Inliner::visit(functionNode->getBlock());
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedBlockNode &blockNode) {
    auto copiedNodes(blockNode->getNodes());
    for (const auto &node : copiedNodes)
        Inliner::visit(node);
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedVariableNode &variableNode) {
    if (variableNode->getExpression() == nullptr)
        return nullptr;

    auto returnVariable = Inliner::visit(variableNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    variableNode->setExpression(createIdentifier(variableNode, variableNode->getScope(),
                                                 returnVariable));
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedBinaryNode &binaryNode) {
    auto returnVariable = Inliner::visit(binaryNode->getLHS());
    if (returnVariable != nullptr)
        binaryNode->setLHS(createIdentifier(binaryNode, binaryNode->getScope(), returnVariable));

    returnVariable = Inliner::visit(binaryNode->getRHS());
    if (returnVariable != nullptr)
        binaryNode->setRHS(createIdentifier(binaryNode, binaryNode->getScope(), returnVariable));
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedUnaryNode &unaryNode) {
    auto returnVariable = Inliner::visit(unaryNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    unaryNode->setExpression(createIdentifier(unaryNode, unaryNode->getScope(), returnVariable));
    return nullptr;
}

SharedVariableNode
Inliner::visit(const SharedParenthesizedNode &parenthesizedNode) {
    auto returnVariable = Inliner::visit(parenthesizedNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    parenthesizedNode->setExpression(createIdentifier(
            parenthesizedNode, parenthesizedNode->getScope(), returnVariable));
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedIdentifierNode &identifierNode) {
    if (identifierNode->getKind() == ASTNode::FUNCTION_CALL)
        return Inliner::visit(std::static_pointer_cast<FunctionCallNode>(identifierNode));
    return nullptr;
}

SharedVariableNode
Inliner::visit(const SharedFunctionArgumentNode &functionArgumentNode) {
    Inliner::visit(functionArgumentNode->getExpression());
    return nullptr;
}

SharedVariableNode
Inliner::visit(const SharedStructArgumentNode &structArgumentNode) {
    if (structArgumentNode->getExpression() != nullptr)
        Inliner::visit(structArgumentNode->getExpression());
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedFunctionCallNode &functionCallNode) {
    for (const auto &argument : functionCallNode->getArguments())
        Inliner::visit(argument);

    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    if (!functionNode->hasAnnotation("inlined"))
        return nullptr;

    auto callFunction = functionCallNode->findNodeOfParents<FunctionNode>();
    if (callFunction == nullptr) {
        THROW_NODE_ERROR(functionCallNode,
                         "The calling of an inlined function is currently just valid "
                         "inside a function block.")
        abort();
    }

    SharedASTNode nodeBeforeBlock = functionCallNode;
    while (nodeBeforeBlock->getParent() != nullptr
           && nodeBeforeBlock->getParent()->getKind() != ASTNode::BLOCK) {
        nodeBeforeBlock = functionCallNode->getParent();
    }

    auto nodeIndex = Utils::indexOf(callFunction->getBlock()->getNodes(), nodeBeforeBlock).second;
    return Inliner::inlineFunctionCall(functionNode, functionCallNode, callFunction->getBlock(),
                                       nodeIndex);
}

SharedVariableNode Inliner::visit(const SharedStructCreateNode &structCreateNode) {
    for (const auto &argument : structCreateNode->getArguments())
        Inliner::visit(argument);
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedAssignmentNode &assignmentNode) {
    auto returnVariable = Inliner::visit(assignmentNode->getStartIdentifier());
    if (returnVariable != nullptr) {
        auto oldIdentifier = assignmentNode->getStartIdentifier();
        assignmentNode->setStartIdentifier(createIdentifier(
                oldIdentifier, oldIdentifier->getScope(), returnVariable));
        assignmentNode->getStartIdentifier()->setNextIdentifier(oldIdentifier->getNextIdentifier());
        assignmentNode->getStartIdentifier()->setDereference(oldIdentifier->isDereference());
        assignmentNode->getStartIdentifier()->setPointer(oldIdentifier->isPointer());
    }

    auto lastIdentifier = assignmentNode->getStartIdentifier();
    auto currentIdentifier = lastIdentifier->getNextIdentifier();
    while (currentIdentifier != nullptr) {
        returnVariable = Inliner::visit(currentIdentifier);
        currentIdentifier->setTypeResolved(false);

        if (returnVariable != nullptr) {
            auto oldIdentifier = lastIdentifier->getNextIdentifier();
            lastIdentifier->setNextIdentifier(
                    createIdentifier(oldIdentifier, oldIdentifier->getScope(),
                                     returnVariable));
            currentIdentifier = lastIdentifier->getNextIdentifier();

            currentIdentifier->setNextIdentifier(oldIdentifier->getNextIdentifier());
            currentIdentifier->setLastIdentifier(oldIdentifier);
            currentIdentifier->setDereference(oldIdentifier->isDereference());
            currentIdentifier->setPointer(oldIdentifier->isPointer());
        }

        lastIdentifier = currentIdentifier;
        currentIdentifier = currentIdentifier->getNextIdentifier();
    }

    TypeResolver::visit(assignmentNode->getStartIdentifier());

    returnVariable = Inliner::visit(assignmentNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    assignmentNode->setExpression(createIdentifier(assignmentNode, assignmentNode->getScope(),
                                                   returnVariable));
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedReturnNode &returnNode) {
    if (returnNode->getExpression() == nullptr)
        return nullptr;

    auto returnVariable = Inliner::visit(returnNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    returnNode->setExpression(createIdentifier(returnNode, returnNode->getScope(), returnVariable));
    return nullptr;
}

SharedVariableNode Inliner::visit(const SharedStructNode &structNode) {
    for (const auto &variable : structNode->getVariables())
        Inliner::visit(variable);
    return nullptr;
}

SharedASTNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedASTNode &node, const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    if (node->getKind() == ASTNode::PARAMETER) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<ParameterNode>(node), prefix, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::VARIABLE) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<VariableNode>(node), prefix, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::BINARY) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<BinaryNode>(node), prefix, parent, scope);
    } else if (node->getKind() == ASTNode::UNARY) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<UnaryNode>(node), prefix, parent, scope);
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<ParenthesizedNode>(node), prefix, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<StructCreateNode>(node), prefix, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<StructArgumentNode>(node), prefix, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<FunctionArgumentNode>(node), prefix,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<AssignmentNode>(node), prefix, parent,
                                 scope);
    } else if (auto identifierNode = std::dynamic_pointer_cast<IdentifierNode>(node)) {
        auto firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        return Inliner::generate(functionCaller, returnVariable, firstIdentifier, prefix, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::RETURN) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<ReturnNode>(node), prefix, parent, scope);
    } else if (node->getKind() == ASTNode::STRING) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<StringNode>(node), parent, scope);
    } else if (node->getKind() == ASTNode::NUMBER) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<NumberNode>(node), parent, scope);
    } else if (node->getKind() == ASTNode::TYPE) {
        return Inliner::generate(functionCaller, returnVariable,
                                 std::static_pointer_cast<TypeNode>(node), parent, scope);
    } else {
        THROW_NODE_ERROR(node, "Inliner: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }
}

SharedIdentifierNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedParameterNode &parameterNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<IdentifierNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setScope(parent->getScope());
    generatedNode->setParent(parent);

    auto uniqueName = prefix + "#_" + parameterNode->getName()->getContent();
    generatedNode->setIdentifier(std::make_shared<Token>());
    generatedNode->getIdentifier()->setContent(uniqueName);
    generatedNode->getIdentifier()->setLineNumber(parameterNode->getName()->getLineNumber());
    generatedNode->getIdentifier()->setStartChar(parameterNode->getName()->getStartChar());
    generatedNode->getIdentifier()->setEndChar(parameterNode->getName()->getEndChar());
    generatedNode->getIdentifier()->setType(Token::IDENTIFIER);

    return generatedNode;
}

SharedVariableNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedVariableNode &variableNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<VariableNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto uniqueName = prefix + "#" + variableNode->getName()->getContent();
    generatedNode->setName(std::make_shared<Token>());
    generatedNode->getName()->setContent(uniqueName);
    generatedNode->getName()->setLineNumber(variableNode->getName()->getLineNumber());
    generatedNode->getName()->setStartChar(variableNode->getName()->getStartChar());
    generatedNode->getName()->setEndChar(variableNode->getName()->getEndChar());
    generatedNode->getName()->setType(Token::IDENTIFIER);
    generatedNode->getScope()->insert(generatedNode->getName()->getContent(), generatedNode);

    generatedNode->setConstant(variableNode->isConstant());
    generatedNode->setLocal(variableNode->isLocal());

    generatedNode->setType(SharedTypeNode(variableNode->getType()->clone(
            generatedNode, generatedNode->getScope())));
    auto expression = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, variableNode->getExpression(), prefix, generatedNode,
            generatedNode->getScope()));
    generatedNode->setExpression(expression);

    TypeResolver::visit(generatedNode);

    return generatedNode;
}

SharedBinaryNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedBinaryNode &binaryNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<BinaryNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto lhs = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, binaryNode->getLHS(), prefix, generatedNode,
            generatedNode->getScope()));
    generatedNode->setLHS(lhs);

    generatedNode->setOperatorKind(binaryNode->getOperatorKind());

    auto rhs = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, binaryNode->getRHS(), prefix, generatedNode,
            generatedNode->getScope()));
    generatedNode->setRHS(rhs);

    return generatedNode;
}

SharedUnaryNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedUnaryNode &unaryNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<UnaryNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    generatedNode->setOperatorKind(unaryNode->getOperatorKind());

    auto expression = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, unaryNode->getExpression(), prefix, generatedNode,
            generatedNode->getScope()));
    generatedNode->setExpression(expression);

    return generatedNode;
}

SharedParenthesizedNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedParenthesizedNode &parenthesizedNode, const std::string &prefix,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<ParenthesizedNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto expression = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, parenthesizedNode->getExpression(), prefix,
            generatedNode,
            generatedNode->getScope()));
    generatedNode->setExpression(expression);

    return generatedNode;
}

SharedStructCreateNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedStructCreateNode &structCreateNode, const std::string &prefix,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<StructCreateNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(std::make_shared<SymbolTable>(scope));

    generatedNode->setUnnamed(structCreateNode->isUnnamed());

    auto targetStruct = structCreateNode->getType()->getTargetStruct();
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->setStartToken(generatedNode->getStartToken());
    identifierNode->setEndToken(generatedNode->getEndToken());
    identifierNode->setScope(parent->getScope());
    identifierNode->setParent(parent);
    identifierNode->setIdentifier(std::make_shared<Token>(*targetStruct->getName()));
    generatedNode->setIdentifier(identifierNode);

    for (auto index = 0ul; index < targetStruct->getVariables().size(); index++) {
        auto variable = targetStruct->getVariables().at(index);
        if (variable->getName()->getContent() == "_")
            continue;

        SharedStructArgumentNode foundArgument;
        for (auto const &argument : structCreateNode->getArguments()) {
            if (argument->getName()->getContent() == "_")
                continue;
            if (argument->getName()->getContent() != variable->getName()->getContent())
                continue;

            foundArgument = argument;
            break;
        }

        if (foundArgument == nullptr)
            continue;

        auto argument = Inliner::generate(functionCaller, returnVariable, foundArgument, prefix,
                                          generatedNode, generatedNode->getScope());
        generatedNode->insertArgument(std::min(generatedNode->getArguments().size(), index),
                                      argument);
    }

    return generatedNode;
}

SharedStructArgumentNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedStructArgumentNode &structArgumentNode, const std::string &prefix,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<StructArgumentNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    generatedNode->setName(structArgumentNode->getName());
    generatedNode->setDontCopy(true);
    generatedNode->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, structArgumentNode->getExpression(), prefix,
            generatedNode, generatedNode->getScope())));

    generatedNode->getScope()->insert(generatedNode->getName()->getContent(), generatedNode);

    return generatedNode;
}

SharedFunctionArgumentNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedFunctionArgumentNode &functionArgumentNode, const std::string &prefix,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<FunctionArgumentNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    if (functionArgumentNode->getName() != nullptr)
        generatedNode->setName(std::make_shared<Token>(*functionArgumentNode->getName()));
    generatedNode->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, functionArgumentNode->getExpression(), prefix,
            generatedNode, generatedNode->getScope())));

    return generatedNode;
}

SharedIdentifierNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedFunctionCallNode &functionCallNode, const std::string &prefix,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<FunctionCallNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    generatedNode->setIdentifier(std::make_shared<Token>(*functionCallNode->getIdentifier()));
    generatedNode->setDereference(functionCallNode->isDereference());
    generatedNode->setPointer(functionCallNode->isPointer());

    for (auto const &argument : functionCallNode->getArguments())
        generatedNode->addArgument(Inliner::generate(functionCaller, returnVariable, argument,
                                                     prefix, generatedNode,
                                                     generatedNode->getScope()));

    if (functionCallNode->getNextIdentifier() != nullptr) {
        generatedNode->setNextIdentifier(Inliner::generate(
                functionCaller, returnVariable, functionCallNode->getNextIdentifier(), prefix,
                generatedNode, generatedNode->getScope()));
        generatedNode->getNextIdentifier()->setLastIdentifier(generatedNode);
    }

    SharedIdentifierNode lastNode = generatedNode;
    while (lastNode->getNextIdentifier() != nullptr)
        lastNode = lastNode->getNextIdentifier();
    return lastNode;
}

SharedASTNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedAssignmentNode &assignmentNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<AssignmentNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto identifierNode = Inliner::generate(functionCaller, returnVariable,
                                            assignmentNode->getStartIdentifier(), prefix,
                                            generatedNode, generatedNode->getScope());
    generatedNode->setEndIdentifier(identifierNode);
    auto firstNode = identifierNode;
    while (firstNode->getLastIdentifier() != nullptr)
        firstNode = firstNode->getLastIdentifier();
    generatedNode->setStartIdentifier(firstNode);

    generatedNode->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, assignmentNode->getExpression(), prefix,
            generatedNode, generatedNode->getScope())));

    return generatedNode;
}

SharedIdentifierNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable,
                  const SharedIdentifierNode &identifierNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    if (auto functionCallNode = std::dynamic_pointer_cast<FunctionCallNode>(identifierNode))
        return Inliner::generate(functionCaller, returnVariable, functionCallNode, prefix, parent,
                                 scope);
    if (auto parameterNode = std::dynamic_pointer_cast<ParameterNode>(
            identifierNode->getTargetNode()))
        return Inliner::generate(functionCaller, returnVariable, parameterNode, prefix, parent,
                                 scope);

    auto generatedNode = std::make_shared<IdentifierNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    generatedNode->setDereference(identifierNode->isDereference());
    generatedNode->setPointer(identifierNode->isPointer());

    std::string identifierName;
    auto variableNode = std::dynamic_pointer_cast<VariableNode>(identifierNode->getTargetNode());
    if (variableNode != nullptr && identifierNode->getLastIdentifier() == nullptr) {
        Symbols symbols;
        if (variableNode->isLocal()) {
            functionCaller->getScope()->all(symbols, identifierNode->getIdentifier()->getContent(),
                                            nullptr);
        } else {
            auto rootNode = functionCaller->findNodeOfParents<RootNode>();
            rootNode->searchWithImports(symbols, identifierNode->getIdentifier()->getContent(),
                                        nullptr);
        }

        if (symbols.empty())
            identifierName = prefix + "#" + identifierNode->getIdentifier()->getContent();
        else identifierName = identifierNode->getIdentifier()->getContent();
    } else identifierName = identifierNode->getIdentifier()->getContent();

    generatedNode->setIdentifier(std::make_shared<Token>(*identifierNode->getIdentifier()));
    generatedNode->getIdentifier()->setContent(identifierName);

    if (identifierNode->getNextIdentifier() != nullptr) {
        generatedNode->setNextIdentifier(Inliner::generate(
                functionCaller, returnVariable, identifierNode->getNextIdentifier(), prefix,
                generatedNode, generatedNode->getScope()));
        generatedNode->getNextIdentifier()->setLastIdentifier(generatedNode);
    }

    auto lastNode = generatedNode;
    while (lastNode->getNextIdentifier() != nullptr)
        lastNode = lastNode->getNextIdentifier();
    return lastNode;
}

SharedAssignmentNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedReturnNode &returnNode,
                  const std::string &prefix, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    if (returnVariable == nullptr)
        return nullptr;

    auto generatedNode = std::make_shared<AssignmentNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto identifierNode = createIdentifier(generatedNode, generatedNode->getScope(),
                                           returnVariable);
    generatedNode->setStartIdentifier(identifierNode);
    generatedNode->setEndIdentifier(identifierNode);

    generatedNode->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionCaller, returnVariable, returnNode->getExpression(), prefix,
            generatedNode, generatedNode->getScope())));

    return generatedNode;
}

SharedStringNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedStringNode &stringNode,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    return SharedStringNode(stringNode->clone(parent, scope));
}

SharedNumberNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedNumberNode &numberNode,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    return SharedNumberNode(numberNode->clone(parent, scope));
}

SharedTypeNode
Inliner::generate(const SharedFunctionCallNode &functionCaller,
                  const SharedVariableNode &returnVariable, const SharedTypeNode &typeNode,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<TypeNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    if (typeNode->getTypeToken() != nullptr)
        generatedNode->setTypeToken(std::make_shared<Token>(*typeNode->getTypeToken()));
    generatedNode->setPointerLevel(typeNode->getPointerLevel());
    generatedNode->setFloating(typeNode->isFloating());
    generatedNode->setSigned(typeNode->isSigned());
    generatedNode->setBits(typeNode->getBits());

    return generatedNode;
}

SharedVariableNode
Inliner::inlineFunctionCall(const SharedFunctionNode &targetFunction,
                            const SharedFunctionCallNode &functionCallNode,
                            const SharedBlockNode &insertBlock,
                            int insertIndex) {
    auto prefix = std::to_string(Utils::hash(Utils::random_string(6).c_str()));
    std::vector<SharedASTNode> generatedNodes;

    SharedVariableNode returnVariable = nullptr;
    if (!functionCallNode->getType()->isVoid()) {
        returnVariable = std::make_shared<VariableNode>();
        returnVariable->setStartToken(functionCallNode->getStartToken());
        returnVariable->setEndToken(functionCallNode->getEndToken());
        returnVariable->setParent(insertBlock);
        returnVariable->setScope(insertBlock->getScope());
        returnVariable->setConstant(false);

        auto uniqueName = prefix + "#return";
        returnVariable->setName(std::make_shared<Token>());
        returnVariable->getName()->setContent(uniqueName);
        returnVariable->getName()->setLineNumber(
                functionCallNode->getIdentifier()->getLineNumber());
        returnVariable->getName()->setStartChar(functionCallNode->getIdentifier()->getStartChar());
        returnVariable->getName()->setEndChar(functionCallNode->getIdentifier()->getEndChar());
        returnVariable->getName()->setType(Token::IDENTIFIER);
        returnVariable->getScope()->insert(returnVariable->getName()->getContent(), returnVariable);

        returnVariable->setLocal(true);
        returnVariable->setType(SharedTypeNode(functionCallNode->getType()->clone(
                returnVariable, returnVariable->getScope())));

        generatedNodes.push_back(returnVariable);
    }

    for (auto index = 0; index < functionCallNode->getArguments().size(); index++) {
        const auto argument = functionCallNode->getArguments()[index];
        const auto parameter = targetFunction->getParameters()[index];

        auto argumentVariable = std::make_shared<VariableNode>();
        argumentVariable->setStartToken(argument->getStartToken());
        argumentVariable->setEndToken(argument->getEndToken());
        argumentVariable->setParent(insertBlock);
        argumentVariable->setScope(insertBlock->getScope());
        argumentVariable->setConstant(false);

        auto uniqueName = prefix + "#_" +
                          (parameter->getName()->getContent());
        argumentVariable->setName(std::make_shared<Token>(*parameter->getName()));
        argumentVariable->getName()->setContent(uniqueName);
        argumentVariable->getScope()->insert(argumentVariable->getName()->getContent(),
                                             argumentVariable);

        argumentVariable->setLocal(true);

        argumentVariable->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
                functionCallNode, nullptr, argument->getExpression(), prefix, argumentVariable,
                argumentVariable->getScope())));

        generatedNodes.push_back(argumentVariable);
    }

    auto copiedNodes(targetFunction->getBlock()->getNodes());
    for (const auto &node : copiedNodes) {
        auto generatedNode = Inliner::generate(functionCallNode, returnVariable, node, prefix,
                                               insertBlock, insertBlock->getScope());
        if (generatedNode == nullptr) {
            THROW_NODE_ERROR(node, "Inliner: Couldn't inline this node.")
            continue;
        }

        generatedNodes.push_back(generatedNode);
    }

    std::reverse(generatedNodes.begin(), generatedNodes.end());
    for (const auto &node : generatedNodes) {
        insertBlock->insertNode(node, insertIndex);

        TypeResolver::visit(node);
        Inliner::visit(node);
    }

    if (functionCallNode->getType()->isVoid()) {
        if (functionCallNode->getParent()->getKind() != ASTNode::BLOCK) {
            std::cout << "This should not have happened. "
                         "Report this bug with a reconstruction of it." << std::endl;
            abort();
        }

        insertBlock->removeNode(functionCallNode);
    }

    return returnVariable;
}

SharedIdentifierNode
Inliner::createIdentifier(const SharedASTNode &parent, const SharedSymbolTable &scope,
                          const SharedVariableNode &returnVariable) {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->setStartToken(returnVariable->getStartToken());
    identifierNode->setEndToken(returnVariable->getEndToken());
    identifierNode->setScope(parent->getScope());
    identifierNode->setParent(parent);
    identifierNode->setIdentifier(std::make_shared<Token>(*returnVariable->getName()));
    return identifierNode;
}