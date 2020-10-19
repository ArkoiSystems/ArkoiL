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

// TODO: Currently just working for functions.
Inliner::Inliner() {}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<ASTNode> &node) {
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
        // TODO: CHECK IF THIS WORKS LIKE INTENDED

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
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
                lastIdentifier->setNextIdentifier(createIdentifier(oldIdentifier, oldIdentifier->getScope(),
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

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<RootNode> &rootNode) {
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

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    if (!functionNode->isNative())
        Inliner::visit(functionNode->getBlock());
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<BlockNode> &blockNode) {
    auto copiedNodes(blockNode->getNodes());
    for (const auto &node : copiedNodes)
        Inliner::visit(node);
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<VariableNode> &variableNode) {
    if (variableNode->getExpression() == nullptr)
        return nullptr;

    auto returnVariable = Inliner::visit(variableNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    variableNode->setExpression(createIdentifier(variableNode, variableNode->getScope(), returnVariable));
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
    auto returnVariable = Inliner::visit(binaryNode->getLHS());
    if (returnVariable != nullptr)
        binaryNode->setLHS(createIdentifier(binaryNode, binaryNode->getScope(), returnVariable));

    returnVariable = Inliner::visit(binaryNode->getRHS());
    if (returnVariable != nullptr)
        binaryNode->setRHS(createIdentifier(binaryNode, binaryNode->getScope(), returnVariable));
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    auto returnVariable = Inliner::visit(unaryNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    unaryNode->setExpression(createIdentifier(unaryNode, unaryNode->getScope(), returnVariable));
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    auto returnVariable = Inliner::visit(parenthesizedNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    parenthesizedNode->setExpression(createIdentifier(parenthesizedNode, parenthesizedNode->getScope(),
                                                      returnVariable));
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<IdentifierNode> &identifierNode) {
    if (identifierNode->getKind() == ASTNode::FUNCTION_CALL)
        return Inliner::visit(std::static_pointer_cast<FunctionCallNode>(identifierNode));
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode) {
    Inliner::visit(functionArgumentNode->getExpression());
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode) {
    if (structArgumentNode->getExpression() != nullptr)
        Inliner::visit(structArgumentNode->getExpression());
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    for (const auto &argument : functionCallNode->getArguments())
        Inliner::visit(argument);

    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    if (!functionNode->hasAnnotation("inlined"))
        return nullptr;

    auto callFunction = functionCallNode->findNodeOfParents<FunctionNode>();
    if (callFunction == nullptr) {
        THROW_NODE_ERROR(functionCallNode, "The calling of an inlined function is currently just valid "
                                           "inside a function block.")
        abort();
    }

    std::shared_ptr<ASTNode> nodeBeforeBlock = functionCallNode;
    while (nodeBeforeBlock->getParent() != nullptr
           && nodeBeforeBlock->getParent()->getKind() != ASTNode::BLOCK) {
        nodeBeforeBlock = functionCallNode->getParent();
    }

    auto nodeIndex = Utils::indexOf(callFunction->getBlock()->getNodes(), nodeBeforeBlock).second;
    return Inliner::generate(functionNode, functionCallNode, callFunction->getBlock(), nodeIndex);
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    for (const auto &argument : structCreateNode->getArguments())
        Inliner::visit(argument);
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    auto returnVariable = Inliner::visit(assignmentNode->getStartIdentifier());
    if (returnVariable != nullptr) {
        auto oldIdentifier = assignmentNode->getStartIdentifier();
        assignmentNode->setStartIdentifier(createIdentifier(oldIdentifier, oldIdentifier->getScope(),
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
            lastIdentifier->setNextIdentifier(createIdentifier(oldIdentifier, oldIdentifier->getScope(),
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

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    if (returnNode->getExpression() == nullptr)
        return nullptr;

    auto returnVariable = Inliner::visit(returnNode->getExpression());
    if (returnVariable == nullptr)
        return nullptr;

    returnNode->setExpression(createIdentifier(returnNode, returnNode->getScope(), returnVariable));
    return nullptr;
}

Inliner::ReturnVariable Inliner::visit(const std::shared_ptr<StructNode> &structNode) {
    for (const auto &variable : structNode->getVariables())
        Inliner::visit(variable);
    return nullptr;
}

Inliner::ReturnVariable Inliner::generate(const std::shared_ptr<FunctionNode> &targetFunction,
                                          const std::shared_ptr<IdentifierNode> &identifierNode,
                                          const std::shared_ptr<BlockNode> &insertBlock, int insertIndex) {
    auto variableNode = std::make_shared<VariableNode>();
    variableNode->setStartToken(identifierNode->getStartToken());
    variableNode->setEndToken(identifierNode->getEndToken());
    variableNode->setParent(insertBlock);
    variableNode->setScope(insertBlock->getScope());
    variableNode->setConstant(false);

    variableNode->setName(std::make_shared<Token>());
    variableNode->getName()->setContent(std::to_string(identifierNode->getIdentifier()->getLineNumber()) +
                                        std::to_string(identifierNode->getIdentifier()->getStartChar()) +
                                        "#" + identifierNode->getIdentifier()->getContent());
    variableNode->getName()->setLineNumber(identifierNode->getIdentifier()->getLineNumber());
    variableNode->getName()->setStartChar(identifierNode->getIdentifier()->getStartChar());
    variableNode->getName()->setEndChar(identifierNode->getIdentifier()->getEndChar());
    variableNode->getName()->setType(Token::IDENTIFIER);
    variableNode->getScope()->insert(variableNode->getName()->getContent(), variableNode);

    variableNode->setLocal(true);
    variableNode->setType(std::shared_ptr<TypeNode>(identifierNode->getType()->clone(
            variableNode, variableNode->getScope())));
    insertBlock->insertNode(variableNode, insertIndex);

    return variableNode;
}

Inliner::ReturnIdentifier Inliner::createIdentifier(const std::shared_ptr<ASTNode> &parent,
                                                    const std::shared_ptr<SymbolTable> &scope,
                                                    const Inliner::ReturnVariable &returnVariable) {
    auto identifierNode = std::make_shared<IdentifierNode>();
    identifierNode->setStartToken(returnVariable->getStartToken());
    identifierNode->setEndToken(returnVariable->getEndToken());
    identifierNode->setScope(parent->getScope());
    identifierNode->setParent(parent);
    identifierNode->setIdentifier(std::make_shared<Token>(*returnVariable->getName()));
    return identifierNode;
}
