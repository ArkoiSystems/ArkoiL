//
// Created by timo on 8/3/20.
//

#include "typeresolver.h"

#include "../parser/symboltable.h"
#include "../parser/astnodes.h"
#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "../utils/utils.h"

void TypeResolver::visit(const SharedASTNode &node) {
    if (node->getKind() == ASTNode::TYPE) {
        TypeResolver::visit(std::static_pointer_cast<TypeNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        TypeResolver::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        TypeResolver::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        TypeResolver::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (auto identifierNode = std::dynamic_pointer_cast<IdentifierNode>(node)) {
        auto firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        if (firstIdentifier->getKind() == ASTNode::FUNCTION_CALL)
            return TypeResolver::visit(std::static_pointer_cast<FunctionCallNode>(node));
        return TypeResolver::visit(firstIdentifier);
    } else if (node->getKind() == ASTNode::NUMBER) {
        TypeResolver::visit(std::static_pointer_cast<NumberNode>(node));
    } else if (node->getKind() == ASTNode::PARAMETER) {
        TypeResolver::visit(std::static_pointer_cast<ParameterNode>(node));
    } else if (node->getKind() == ASTNode::BLOCK) {
        TypeResolver::visit(std::static_pointer_cast<BlockNode>(node));
    } else if (node->getKind() == ASTNode::STRING) {
        TypeResolver::visit(std::static_pointer_cast<StringNode>(node));
    } else if (node->getKind() == ASTNode::ROOT) {
        TypeResolver::visit(std::static_pointer_cast<RootNode>(node));
    } else if (node->getKind() == ASTNode::VARIABLE) {
        TypeResolver::visit(std::static_pointer_cast<VariableNode>(node));
    } else if (node->getKind() == ASTNode::RETURN) {
        TypeResolver::visit(std::static_pointer_cast<ReturnNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        TypeResolver::visit(std::static_pointer_cast<FunctionArgumentNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        TypeResolver::visit(std::static_pointer_cast<StructArgumentNode>(node));
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        TypeResolver::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        TypeResolver::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        TypeResolver::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        TypeResolver::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->getKind() != ASTNode::IMPORT) {
        THROW_NODE_ERROR(node, "TypeResolver: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }
}

void TypeResolver::visit(const SharedRootNode &rootNode) {
    for (const auto &node : rootNode->getNodes())
        TypeResolver::visit(node);
}

void TypeResolver::visit(const SharedFunctionNode &functionNode) {
    if (functionNode->isTypeResolved())
        return;
    functionNode->setTypeResolved(true);

    if (functionNode->getScopeResolution())
        TypeResolver::visit(functionNode->getScopeResolution());

    TypeResolver::visit(functionNode->getType());

    for (const auto &parameter : functionNode->getParameters())
        TypeResolver::visit(parameter);

    if (!functionNode->isNative())
        TypeResolver::visit(functionNode->getBlock());
}

void TypeResolver::visit(const SharedBlockNode &blockNode) {
    auto functionNode = blockNode->findNodeOfParents<FunctionNode>();
    blockNode->setType(functionNode->getType());

    for (const auto &node : blockNode->getNodes())
        TypeResolver::visit(node);
}

void TypeResolver::visit(const SharedVariableNode &variableNode) {
    if (variableNode->isTypeResolved())
        return;

    variableNode->setLocal(variableNode->findNodeOfParents<BlockNode>() != nullptr);

    if (variableNode->getType() != nullptr)
        TypeResolver::visit(variableNode->getType());

    if (variableNode->getExpression() != nullptr) {
        TypeResolver::visit(variableNode->getExpression());

        if (variableNode->getType() == nullptr)
            variableNode->setType(variableNode->getExpression()->getType());
    }

    variableNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedBinaryNode &binaryNode) {
    if (binaryNode->isTypeResolved())
        return;

    TypeResolver::visit(binaryNode->getLHS());
    TypeResolver::visit(binaryNode->getRHS());

    if (binaryNode->getOperatorKind() == BinaryNode::BIT_CAST) {
        binaryNode->setType(std::static_pointer_cast<TypeNode>(binaryNode->getRHS()));
    } else {
        binaryNode->setType(binaryNode->getLHS()->getType());
    }

    binaryNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedUnaryNode &unaryNode) {
    if (unaryNode->isTypeResolved())
        return;

    TypeResolver::visit(unaryNode->getExpression());
    unaryNode->setType(unaryNode->getExpression()->getType());

    unaryNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedParenthesizedNode &parenthesizedNode) {
    if (parenthesizedNode->isTypeResolved())
        return;

    TypeResolver::visit(parenthesizedNode->getExpression());
    parenthesizedNode->setType(parenthesizedNode->getExpression()->getType());

    parenthesizedNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedNumberNode &numberNode) {
    if (numberNode->isTypeResolved())
        return;

    numberNode->setType(std::make_shared<TypeNode>());
    numberNode->getType()->setStartToken(numberNode->getStartToken());
    numberNode->getType()->setEndToken(numberNode->getEndToken());
    numberNode->getType()->setScope(numberNode->getScope());
    numberNode->getType()->setFloating(
            numberNode->getNumber()->getContent().find('.') != std::string::npos);
    numberNode->getType()->setBits(32);

    if (!numberNode->getType()->isFloating())
        numberNode->getType()->setSigned(true);

    numberNode->getType()->setTypeToken(std::make_shared<Token>());
    std::string typeContent = (numberNode->getType()->isFloating() ? "f" : "i");
    typeContent += "32";
    numberNode->getType()->getTypeToken()->setContent(typeContent);

    numberNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedStringNode &stringNode) {
    if (stringNode->isTypeResolved())
        return;

    stringNode->setType(std::make_shared<TypeNode>());
    stringNode->getType()->setStartToken(stringNode->getStartToken());
    stringNode->getType()->setEndToken(stringNode->getEndToken());
    stringNode->getType()->setScope(stringNode->getScope());
    stringNode->getType()->setSigned(false);
    stringNode->getType()->setPointerLevel(1);
    stringNode->getType()->setBits(8);

    stringNode->getType()->setTypeToken(std::make_shared<Token>());
    stringNode->getType()->getTypeToken()->setContent("u8*");

    stringNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedIdentifierNode &identifierNode) {
    if (identifierNode->isTypeResolved())
        return;

    Symbols foundNodes;
    auto functionNode = identifierNode->findNodeOfParents<FunctionNode>();
    if (identifierNode->getParent()->getKind() == ASTNode::STRUCT_CREATE) {
        auto scopeCheck = [](const SharedASTNode &node) {
            return node->getKind() == ASTNode::STRUCT;
        };

        identifierNode->findNodeOfParents<RootNode>()->searchWithImports(
                foundNodes, identifierNode->getIdentifier()->getContent(), scopeCheck);
    } else if (identifierNode->getKind() == ASTNode::FUNCTION_CALL) {
        auto functionCall = std::static_pointer_cast<FunctionCallNode>(identifierNode);
        auto scopeResolution = (functionNode && functionNode->getScopeResolution()) ?
                               functionNode->getScopeResolution() : nullptr;

        auto scopeCheck = [identifierNode, scopeResolution, functionCall](
                const SharedASTNode &node) {
            if (node->getKind() != ASTNode::FUNCTION)
                return false;

            auto function = std::static_pointer_cast<FunctionNode>(node);
            if (scopeResolution
                && (!function->getScopeResolution() ||
                    function->getScopeResolution()->getTypeToken()->getContent() !=
                    scopeResolution->getTypeToken()->getContent()))
                return false;
            if (function->getScopeResolution() && !scopeResolution) {
                if (functionCall->getLastIdentifier() == nullptr ||
                    !functionCall->getLastIdentifier()->getType()->getTargetStruct())
                    return false;

                if (function->getScopeResolution()->getTypeToken()->getContent() !=
                    functionCall->getLastIdentifier()->getType()->getTargetStruct()->getName()->getContent())
                    return false;
            }

            if (!function->isVariadic() &&
                (function->getParameters().size() != functionCall->getArguments().size()))
                return false;

            auto sortedArguments(functionCall->getArguments());
            if (!functionCall->getSortedArguments(function, sortedArguments))
                return false;

            for (auto index = 0; index < functionCall->getArguments().size(); index++) {
                if (index >= function->getParameters().size())
                    break;

                auto argument = sortedArguments.at(index);
                if (argument->isTypeWhitelisted())
                    continue;

                TypeResolver::visit(argument);
                auto parameter = function->getParameters().at(index);
                TypeResolver::visit(parameter);

                if (*parameter->getType() != *argument->getType())
                    return false;
            }

            return true;
        };

        if (scopeResolution) {
            identifierNode->findNodeOfParents<RootNode>()->searchWithImports(
                    foundNodes, identifierNode->getIdentifier()->getContent(), scopeCheck);
            // TODO: Check if this works (settings a variable to something different when
            //  accessed by a lambda function)
            scopeResolution = nullptr;
        }

        if (foundNodes.empty()) {
            identifierNode->findNodeOfParents<RootNode>()->searchWithImports(
                    foundNodes, identifierNode->getIdentifier()->getContent(), scopeCheck);
        }
    } else {
        auto scopeCheck = [](const SharedASTNode &node) {
            return node->getKind() == ASTNode::VARIABLE || node->getKind() == ASTNode::PARAMETER ||
                   node->getKind() == ASTNode::FUNCTION_ARGUMENT ||
                   node->getKind() == ASTNode::STRUCT_ARGUMENT;
        };

        identifierNode->getScope()->all(foundNodes, identifierNode->getIdentifier()->getContent(),
                                        scopeCheck);

        if (functionNode && functionNode->getScopeResolution() && foundNodes.empty()) {
            auto targetStruct = functionNode->getScopeResolution()->getTargetStruct();
            targetStruct->getScope()->scope(foundNodes,
                                            identifierNode->getIdentifier()->getContent(),
                                            scopeCheck);
        }

        if (foundNodes.empty()) {
            identifierNode->findNodeOfParents<RootNode>()->searchWithImports(
                    foundNodes, identifierNode->getIdentifier()->getContent(), scopeCheck);
        }
    }

    if (foundNodes.empty()) {
        THROW_NODE_ERROR(identifierNode, "Couldn't find the identifier \"{}\".",
                         identifierNode->getIdentifier()->getContent())
        return;
    }

    auto targetNode = foundNodes[0];
    TypeResolver::visit(targetNode);

    // TODO: See if this is necessary
    auto typedNode = std::static_pointer_cast<TypedNode>(targetNode);
    if ((typedNode != nullptr && typedNode->getKind() == ASTNode::FUNCTION_ARGUMENT) &&
        (typedNode->getParent() != nullptr && typedNode->getParent()->getKind() == ASTNode::STRUCT_CREATE)) {
        auto structCreate = std::static_pointer_cast<StructCreateNode>(typedNode->getParent());
        targetNode = nullptr;

        auto structNode = std::static_pointer_cast<StructNode>(structCreate->getTargetNode());
        for (auto const &variable : structNode->getVariables()) {
            if (variable->getName()->getContent() == identifierNode->getIdentifier()->getContent())
                targetNode = variable;
        }
    }

    if (targetNode == nullptr) {
        THROW_NODE_ERROR(identifierNode, "Couldn't find the target node for the identifier \"{}\".",
                         identifierNode->getIdentifier()->getContent())
        return;
    }

    if (typedNode->getType() == nullptr) {
        THROW_NODE_ERROR(typedNode, "The found identifier has no type.")
        return;
    }

    typedNode->setAccessed(true);

    identifierNode->setTargetNode(targetNode);
    identifierNode->setType(SharedTypeNode(typedNode->getType()->clone(
            identifierNode, identifierNode->getScope())));
    TypeResolver::visit(identifierNode->getType());
    identifierNode->setTypeResolved(true);

    if (identifierNode->isDereference() && identifierNode->getType()->getPointerLevel() <= 0) {
        THROW_NODE_ERROR(identifierNode, "Can't dereference a non-pointer type.")
        return;
    }

    if (identifierNode->isPointer())
        identifierNode->getType()->setPointerLevel(identifierNode->getType()->getPointerLevel() + 1);
    else if (identifierNode->isDereference())
        identifierNode->getType()->setPointerLevel(identifierNode->getType()->getPointerLevel() - 1);

    if (identifierNode->getNextIdentifier() != nullptr &&
        identifierNode->getType()->getTargetStruct() == nullptr) {
        THROW_NODE_ERROR(identifierNode, "The identifier has no struct, so there can't be a following "
                                         "identifier.")
        return;
    }

    if (identifierNode->getNextIdentifier() != nullptr) {
        identifierNode->getNextIdentifier()->setScope(
                identifierNode->getType()->getTargetStruct()->getScope());
        TypeResolver::visit(identifierNode->getNextIdentifier());
    }
}

void TypeResolver::visit(const SharedParameterNode &parameterNode) {
    if (parameterNode->isTypeResolved())
        return;

    TypeResolver::visit(parameterNode->getType());

    parameterNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedFunctionArgumentNode &functionArgumentNode) {
    if (functionArgumentNode->isTypeResolved())
        return;

    auto parentCall = std::static_pointer_cast<FunctionCallNode>(functionArgumentNode->getParent());
    if (functionArgumentNode->getExpression()->getKind() == ASTNode::STRUCT_CREATE) {
        auto structCreate = std::static_pointer_cast<StructCreateNode>(
                functionArgumentNode->getExpression());
        if (structCreate->isUnnamed()) {
            if (parentCall->getTargetNode() != nullptr) {
                auto parentFunction = std::static_pointer_cast<FunctionNode>(
                        parentCall->getTargetNode());

                auto sortedArguments(parentCall->getArguments());
                parentCall->getSortedArguments(parentFunction, sortedArguments);

                auto argumentIndex = Utils::indexOf(sortedArguments, functionArgumentNode).second;
                if (argumentIndex == -1) {
                    THROW_NODE_ERROR(structCreate, "Couldn't find the argument index for the "
                                                   "function call.")
                    exit(EXIT_FAILURE);
                }

                functionArgumentNode->setType(parentFunction->getParameters()[argumentIndex]->getType());
            } else functionArgumentNode->setTypeWhitelisted(true);
        }
    }

    if (functionArgumentNode->isTypeWhitelisted())
        return;

    TypeResolver::visit(functionArgumentNode->getExpression());
    if (functionArgumentNode->getType() == nullptr)
        functionArgumentNode->setType(functionArgumentNode->getExpression()->getType());

    functionArgumentNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedStructArgumentNode &structArgumentNode) {
    if (structArgumentNode->isTypeResolved())
        return;

    auto parentCreate = std::static_pointer_cast<StructCreateNode>(structArgumentNode->getParent());
    auto parentStruct = std::static_pointer_cast<StructNode>(parentCreate->getTargetNode());

    if (structArgumentNode->getType() == nullptr &&
        structArgumentNode->getExpression() != nullptr &&
        structArgumentNode->getExpression()->getKind() == ASTNode::STRUCT_CREATE) {
        auto childCreate = std::static_pointer_cast<StructCreateNode>(
                structArgumentNode->getExpression());
        if (childCreate->isUnnamed()) {
            for (auto const &variable : parentStruct->getVariables()) {
                if (variable->getName()->getContent() !=
                    structArgumentNode->getName()->getContent())
                    continue;

                structArgumentNode->setType(variable->getType());
                structArgumentNode->setTargetNode(variable);
                break;
            }

            if (structArgumentNode->getType() == nullptr) {
                THROW_NODE_ERROR(childCreate, "Couldn't find the variable type for the argument.")
                exit(EXIT_FAILURE);
            }
        }
    }

    if (structArgumentNode->getExpression() != nullptr) {
        TypeResolver::visit(structArgumentNode->getExpression());
        if (structArgumentNode->getType() == nullptr)
            structArgumentNode->setType(structArgumentNode->getExpression()->getType());
    }

    structArgumentNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedFunctionCallNode &functionCallNode) {
    if (functionCallNode->isTypeResolved())
        return;

    for (const auto &argument : functionCallNode->getArguments())
        TypeResolver::visit(argument);
    TypeResolver::visit(std::static_pointer_cast<IdentifierNode>(functionCallNode));

    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    if (functionNode == nullptr) {
        THROW_NODE_ERROR(functionCallNode, "Couldn't find the function for this call.")
        exit(EXIT_FAILURE);
    }

    functionCallNode->setType(SharedTypeNode(functionNode->getType()->clone(
            functionCallNode, functionCallNode->getScope())));
    TypeResolver::visit(functionCallNode->getType());

    for (const auto &argument : functionCallNode->getArguments()) {
        if (!argument->isTypeWhitelisted())
            continue;

        argument->setTypeWhitelisted(false);
        TypeResolver::visit(argument);
    }

    functionCallNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedStructCreateNode &structCreateNode) {
    if (structCreateNode->isTypeResolved())
        return;

    SharedStructNode targetNode;
    SharedTypeNode typeNode;

    if (structCreateNode->isUnnamed()) {
        auto typedNode = std::dynamic_pointer_cast<TypedNode>(structCreateNode->getParent());

        if (typedNode == nullptr) {
            THROW_NODE_ERROR(structCreateNode, "Can't create an unnamed struct, because there is "
                                               "no type defined by a function/argument or variable.")
            exit(EXIT_FAILURE);
        }

        if (typedNode->getType() == nullptr || typedNode->getType()->getTargetStruct() == nullptr) {
            THROW_NODE_ERROR(typedNode, "Can't create an unnamed struct because the type is not "
                                        "resolved yet.")
            exit(EXIT_FAILURE);
        }

        targetNode = typedNode->getType()->getTargetStruct();
        typeNode = typedNode->getType();
    } else {
        TypeResolver::visit(structCreateNode->getIdentifier());

        targetNode = structCreateNode->getIdentifier()->getType()->getTargetStruct();
        typeNode = structCreateNode->getIdentifier()->getType();
    }

    structCreateNode->setTargetNode(targetNode);
    structCreateNode->setType(typeNode);

    for (const auto &argument : structCreateNode->getArguments()) {
        if (argument->getName()->getContent() == "_")
            continue;

        SharedVariableNode foundVariable;
        for (const auto &variable : targetNode->getVariables()) {
            if (variable->getName()->getContent() == "_")
                continue;
            if (variable->getName()->getContent() != argument->getName()->getContent())
                continue;

            foundVariable = variable;
            break;
        }

        if (foundVariable == nullptr) {
            THROW_NODE_ERROR(argument, "Couldn't find a variable for the argument node.")
            exit(EXIT_FAILURE);
        }
    }

    for (auto index = 0; index < targetNode->getVariables().size(); index++) {
        auto variable = targetNode->getVariables().at(index);

        SharedStructArgumentNode foundArgument;
        if (variable->getName()->getContent() != "_") {
            for (const auto &argument : structCreateNode->getArguments()) {
                if (argument->getName()->getContent() == "_")
                    continue;
                if (variable->getName()->getContent() != argument->getName()->getContent())
                    continue;

                foundArgument = argument;
                break;
            }
        }

        if (foundArgument && variable->getName()->getContent() != "_") {
            structCreateNode->removeArgument(foundArgument);
            structCreateNode->insertArgument(index, foundArgument);

            foundArgument->setType(variable->getType());
            continue;
        }

        auto argument = std::make_shared<StructArgumentNode>();
        argument->setStartToken(variable->getStartToken());
        argument->setParent(structCreateNode);
        argument->setScope(structCreateNode->getScope());
        argument->setName(variable->getName());
        argument->setDontCopy(true);

        if (variable->getExpression() != nullptr)
            argument->setExpression(SharedOperableNode(variable->getExpression()->clone(
                    argument, argument->getScope())));

        argument->setEndToken(variable->getEndToken());
        argument->getScope()->insert(argument->getName()->getContent(), argument);
        argument->setType(variable->getType());

        structCreateNode->insertArgument(index, argument);
    }

    for (const auto &argument : structCreateNode->getArguments())
        TypeResolver::visit(argument);

    structCreateNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedAssignmentNode &assignmentNode) {
    if (assignmentNode->isTypeResolved())
        return;

    TypeResolver::visit(assignmentNode->getStartIdentifier());

    assignmentNode->setTargetNode(assignmentNode->getEndIdentifier()->getTargetNode());
    assignmentNode->setType(assignmentNode->getEndIdentifier()->getType());

    TypeResolver::visit(assignmentNode->getExpression());

    assignmentNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedReturnNode &returnNode) {
    if (returnNode->isTypeResolved())
        return;

    auto function = returnNode->findNodeOfParents<FunctionNode>();
    if (function == nullptr) {
        THROW_NODE_ERROR(returnNode, "Return node is not inside of a function.")
        exit(EXIT_FAILURE);
    }

    returnNode->setType(function->getType());

    if (returnNode->getExpression() != nullptr)
        TypeResolver::visit(returnNode->getExpression());

    returnNode->setTypeResolved(true);
}

void TypeResolver::visit(const SharedStructNode &structNode) {
    if (structNode->isTypeResolved())
        return;

    structNode->setTypeResolved(true);

    structNode->setType(std::make_shared<TypeNode>());
    structNode->getType()->setStartToken(structNode->getStartToken());
    structNode->getType()->setEndToken(structNode->getEndToken());
    structNode->getType()->setScope(structNode->getScope());
    structNode->getType()->setTargetStruct(structNode);

    structNode->getType()->setTypeToken(std::make_shared<Token>(*structNode->getName()));

    for (const auto &variable : structNode->getVariables())
        TypeResolver::visit(variable);
}

void TypeResolver::visit(const SharedTypeNode &typeNode) {
    if (typeNode->getTypeToken() == Token::TYPE &&
        (std::strncmp(typeNode->getTypeToken()->getContent().c_str(), "i", 1) == 0 ||
         std::strncmp(typeNode->getTypeToken()->getContent().c_str(), "u", 1) == 0)) {
        auto bits = std::stoi(typeNode->getTypeToken()->getContent().substr(1));
        auto isSigned = std::strncmp(typeNode->getTypeToken()->getContent().c_str(), "i", 1) == 0;
        typeNode->setSigned(isSigned);
        typeNode->setFloating(false);
        typeNode->setBits(bits);
    } else if (typeNode->getTypeToken() == "bool") {
        typeNode->setFloating(false);
        typeNode->setSigned(true);
        typeNode->setBits(1);
    } else if (typeNode->getTypeToken() == "float") {
        typeNode->setFloating(true);
        typeNode->setSigned(false);
        typeNode->setBits(32);
    } else if (typeNode->getTypeToken() == "double") {
        typeNode->setFloating(true);
        typeNode->setSigned(false);
        typeNode->setBits(64);
    } else if (typeNode->getTypeToken() == "void") {
        typeNode->setFloating(false);
        typeNode->setSigned(false);
        typeNode->setBits(0);
    } else if (typeNode->getTypeToken() == Token::IDENTIFIER) {
        auto scopeCheck = [](const SharedASTNode &node) {
            return node->getKind() == ASTNode::STRUCT;
        };

        auto rootNode = typeNode->findNodeOfParents<RootNode>();
        Symbols foundNodes;
        rootNode->searchWithImports(foundNodes, typeNode->getTypeToken()->getContent(), scopeCheck);

        if (foundNodes.empty()) {
            THROW_NODE_ERROR(typeNode, "Couldn't find the struct for the searched identifier.")
            exit(EXIT_FAILURE);
        }

        typeNode->setTargetStruct(std::static_pointer_cast<StructNode>(foundNodes[0]));
        TypeResolver::visit(typeNode->getTargetStruct());
    }
}