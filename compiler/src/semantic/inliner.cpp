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

    auto copiedNodes(rootNode->getNodes());
    for (const auto &node : copiedNodes) {
        if (node->getKind() != ASTNode::FUNCTION)
            continue;

        auto functionNode = std::static_pointer_cast<FunctionNode>(node);
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
        assignmentNode->setStartIdentifier(
                createIdentifier(oldIdentifier, oldIdentifier->getScope(),
                                 returnVariable));
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
Inliner::generate(const SharedASTNode &node, int insertIndex, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    if (node->getKind() == ASTNode::PARAMETER) {
        return Inliner::generate(std::static_pointer_cast<ParameterNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::VARIABLE) {
        return Inliner::generate(std::static_pointer_cast<VariableNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::BINARY) {
        return Inliner::generate(std::static_pointer_cast<BinaryNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::UNARY) {
        return Inliner::generate(std::static_pointer_cast<UnaryNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        return Inliner::generate(std::static_pointer_cast<ParenthesizedNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        return Inliner::generate(std::static_pointer_cast<StructCreateNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        return Inliner::generate(std::static_pointer_cast<StructArgumentNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        return Inliner::generate(std::static_pointer_cast<FunctionArgumentNode>(node), insertIndex,
                                 parent, scope);
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        return Inliner::generate(std::static_pointer_cast<AssignmentNode>(node), insertIndex,
                                 parent, scope);
    } else if (auto identifierNode = std::dynamic_pointer_cast<IdentifierNode>(node)) {
        auto firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        return Inliner::generate(firstIdentifier, insertIndex, parent, scope);
    } else if (node->getKind() == ASTNode::RETURN) {
        return Inliner::generate(std::static_pointer_cast<ReturnNode>(node), insertIndex, parent,
                                 scope);
    } else if (node->getKind() == ASTNode::STRING) {
        return Inliner::generate(std::static_pointer_cast<StringNode>(node), parent, scope);
    } else if (node->getKind() == ASTNode::NUMBER) {
        return Inliner::generate(std::static_pointer_cast<NumberNode>(node), parent, scope);
    } else if (node->getKind() == ASTNode::TYPE) {
        return Inliner::generate(std::static_pointer_cast<TypeNode>(node), parent, scope);
    } else {
        THROW_NODE_ERROR(node, "Inliner: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }
}

SharedParameterNode
Inliner::generate(const SharedParameterNode &parameterNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    return nullptr;
}

SharedVariableNode
Inliner::generate(const SharedVariableNode &variableNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<VariableNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto uniqueName = std::to_string(insertIndex) + "#" + variableNode->getName()->getContent();
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
            variableNode->getExpression(), insertIndex, generatedNode, generatedNode->getScope()));
    generatedNode->setExpression(expression);

    TypeResolver::visit(generatedNode);

    return generatedNode;
}

SharedBinaryNode
Inliner::generate(const SharedBinaryNode &binaryNode, int insertIndex, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<BinaryNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto lhs = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            binaryNode->getLHS(), insertIndex, generatedNode, generatedNode->getScope()));
    generatedNode->setLHS(lhs);

    generatedNode->setOperatorKind(binaryNode->getOperatorKind());

    auto rhs = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            binaryNode->getRHS(), insertIndex, generatedNode, generatedNode->getScope()));
    generatedNode->setRHS(rhs);

    return generatedNode;
}

SharedUnaryNode
Inliner::generate(const SharedUnaryNode &unaryNode, int insertIndex, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<UnaryNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    generatedNode->setOperatorKind(unaryNode->getOperatorKind());

    auto expression = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            unaryNode->getExpression(), insertIndex, generatedNode, generatedNode->getScope()));
    generatedNode->setExpression(expression);

    return generatedNode;
}

SharedParenthesizedNode
Inliner::generate(const SharedParenthesizedNode &parenthesizedNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<ParenthesizedNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    auto expression = std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            parenthesizedNode->getExpression(), insertIndex, generatedNode,
            generatedNode->getScope()));
    generatedNode->setExpression(expression);

    return generatedNode;
}

SharedStructCreateNode
Inliner::generate(const SharedStructCreateNode &structCreateNode, int insertIndex,
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

        auto argument = Inliner::generate(foundArgument, insertIndex, generatedNode,
                                          generatedNode->getScope());
        generatedNode->insertArgument(std::min(generatedNode->getArguments().size(), index),
                                      argument);
    }

    return generatedNode;
}

SharedStructArgumentNode
Inliner::generate(const SharedStructArgumentNode &structArgumentNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<StructArgumentNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    generatedNode->setName(structArgumentNode->getName());
    generatedNode->setDontCopy(true);
    generatedNode->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            structArgumentNode->getExpression(), insertIndex, parent, scope)));

    generatedNode->getScope()->insert(generatedNode->getName()->getContent(), generatedNode);

    return generatedNode;
}

SharedFunctionArgumentNode
Inliner::generate(const SharedFunctionArgumentNode &functionArgumentNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    auto generatedNode = std::make_shared<FunctionArgumentNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    if (functionArgumentNode->getName() != nullptr)
        generatedNode->setName(std::make_shared<Token>(*functionArgumentNode->getName()));
    generatedNode->setExpression(std::dynamic_pointer_cast<OperableNode>(Inliner::generate(
            functionArgumentNode->getExpression(), insertIndex, parent, scope)));

    return generatedNode;
}

SharedFunctionCallNode
Inliner::generate(const SharedFunctionCallNode &functionCallNode, int insertIndex,
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
        generatedNode->addArgument(Inliner::generate(argument, insertIndex, parent, scope));

    if (functionCallNode->getNextIdentifier() != nullptr)
        generatedNode->setNextIdentifier(Inliner::generate(functionCallNode->getNextIdentifier(),
                                                           insertIndex, parent, scope));

    return generatedNode;
}

SharedAssignmentNode
Inliner::generate(const SharedAssignmentNode &assignmentNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    return nullptr;
}

SharedIdentifierNode
Inliner::generate(const SharedIdentifierNode &identifierNode, int insertIndex,
                  const SharedASTNode &parent, const SharedSymbolTable &scope) {
    if (identifierNode->getKind() == ASTNode::FUNCTION_CALL)
        return Inliner::generate(std::static_pointer_cast<FunctionCallNode>(identifierNode),
                                 insertIndex, parent, scope);

    auto generatedNode = std::make_shared<IdentifierNode>();
    generatedNode->setStartToken(parent->getStartToken());
    generatedNode->setEndToken(parent->getEndToken());
    generatedNode->setParent(parent);
    generatedNode->setScope(scope);

    std::string identifierName;
    if (identifierNode->getTargetNode()->getKind() == ASTNode::VARIABLE) {
        auto variableNode = std::static_pointer_cast<VariableNode>(identifierNode->getTargetNode());
        identifierName = std::to_string(insertIndex) + "#"
                         + identifierNode->getIdentifier()->getContent();
    } else identifierName = identifierNode->getIdentifier()->getContent();

    generatedNode->setIdentifier(std::make_shared<Token>(*identifierNode->getIdentifier()));
    generatedNode->getIdentifier()->setContent(identifierName);

    return generatedNode;
}

SharedReturnNode
Inliner::generate(const SharedReturnNode &returnNode, int insertIndex, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    return nullptr;
}

SharedStringNode
Inliner::generate(const SharedStringNode &stringNode, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    return SharedStringNode(stringNode->clone(parent, scope));
}

SharedNumberNode
Inliner::generate(const SharedNumberNode &numberNode, const SharedASTNode &parent,
                  const SharedSymbolTable &scope) {
    return SharedNumberNode(numberNode->clone(parent, scope));
}

SharedTypeNode Inliner::generate(const SharedTypeNode &typeNode, const SharedASTNode &parent,
                                 const SharedSymbolTable &scope) {
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
    SharedVariableNode variableNode = nullptr;
    if (!functionCallNode->getType()->isVoid()) {
        variableNode = std::make_shared<VariableNode>();
        variableNode->setStartToken(functionCallNode->getStartToken());
        variableNode->setEndToken(functionCallNode->getEndToken());
        variableNode->setParent(insertBlock);
        variableNode->setScope(insertBlock->getScope());
        variableNode->setConstant(false);

        // CREATE A BETTER UNIQUE NAME
        auto uniqueName = std::to_string(insertIndex) + "#return";
        variableNode->setName(std::make_shared<Token>());
        variableNode->getName()->setContent(uniqueName);
        variableNode->getName()->setLineNumber(functionCallNode->getIdentifier()->getLineNumber());
        variableNode->getName()->setStartChar(functionCallNode->getIdentifier()->getStartChar());
        variableNode->getName()->setEndChar(functionCallNode->getIdentifier()->getEndChar());
        variableNode->getName()->setType(Token::IDENTIFIER);
        variableNode->getScope()->insert(variableNode->getName()->getContent(), variableNode);

        variableNode->setLocal(true);
        variableNode->setType(SharedTypeNode(functionCallNode->getType()->clone(
                variableNode, variableNode->getScope())));

        TypeResolver::visit(variableNode);
    }

    auto copiedNodes(targetFunction->getBlock()->getNodes());
    std::vector<SharedASTNode> generatedNodes;
    if (variableNode != nullptr)
        generatedNodes.push_back(variableNode);

    for (const auto &node : copiedNodes) {
        auto generatedNode = Inliner::generate(node, insertIndex, insertBlock,
                                               insertBlock->getScope());
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

    return variableNode;
}

SharedIdentifierNode
Inliner::createIdentifier(const SharedASTNode &parent,const SharedSymbolTable &scope,
                          const SharedVariableNode &returnVariable) {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->setStartToken(returnVariable->getStartToken());
    identifierNode->setEndToken(returnVariable->getEndToken());
    identifierNode->setScope(parent->getScope());
    identifierNode->setParent(parent);
    identifierNode->setIdentifier(std::make_shared<Token>(*returnVariable->getName()));
    return identifierNode;
}