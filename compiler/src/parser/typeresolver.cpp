//
// Created by timo on 8/3/20.
//

#include "typeresolver.h"

#include "../parser/symboltable.h"
#include "../compiler/error.h"
#include "../lexer/lexer.h"
#include "../lexer/token.h"
#include "astnodes.h"
#include "../utils.h"

void TypeResolver::visit(const std::shared_ptr<ASTNode> &node) {
    if (node->getKind() == ASTNode::TYPE) {
        TypeResolver::visit(std::static_pointer_cast<TypeNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT) {
        TypeResolver::visit(std::static_pointer_cast<StructNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION) {
        TypeResolver::visit(std::static_pointer_cast<FunctionNode>(node));
    } else if (node->getKind() == ASTNode::FUNCTION_CALL) {
        TypeResolver::visit(std::static_pointer_cast<FunctionCallNode>(node));
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        TypeResolver::visit(std::static_pointer_cast<StructCreateNode>(node));
    } else if (node->getKind() == ASTNode::IDENTIFIER) {
        auto identifierNode = std::static_pointer_cast<IdentifierNode>(node);

        std::shared_ptr<IdentifierNode> firstIdentifier = identifierNode;
        while (firstIdentifier->getLastIdentifier() != nullptr)
            firstIdentifier = firstIdentifier->getLastIdentifier();

        TypeResolver::visit(firstIdentifier);
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
    } else if (node->getKind() == ASTNode::ARGUMENT) {
        TypeResolver::visit(std::static_pointer_cast<ArgumentNode>(node));
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        TypeResolver::visit(std::static_pointer_cast<AssignmentNode>(node));
    } else if (node->getKind() == ASTNode::BINARY) {
        TypeResolver::visit(std::static_pointer_cast<BinaryNode>(node));
    } else if (node->getKind() == ASTNode::UNARY) {
        TypeResolver::visit(std::static_pointer_cast<UnaryNode>(node));
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        TypeResolver::visit(std::static_pointer_cast<ParenthesizedNode>(node));
    } else if (node->getKind() != ASTNode::IMPORT) {
        std::cout << "TypeResolver: Unsupported node. " << node->getKind() << std::endl;
        exit(EXIT_FAILURE);
    }
}

void TypeResolver::visit(const std::shared_ptr<RootNode> &rootNode) {
    for (const auto &node : rootNode->getNodes())
        TypeResolver::visit(node);
}

void TypeResolver::visit(const std::shared_ptr<FunctionNode> &functionNode) {
    if (functionNode->isTypeResolved())
        return;
    functionNode->setTypeResolved(true);

    TypeResolver::visit(functionNode->getType());

    for (const auto &parameter : functionNode->getParameters())
        TypeResolver::visit(parameter);

    if (!functionNode->isNative())
        TypeResolver::visit(functionNode->getBlock());
}

void TypeResolver::visit(const std::shared_ptr<BlockNode> &blockNode) {
    for (const auto &node : blockNode->getNodes())
        TypeResolver::visit(node);
}

void TypeResolver::visit(const std::shared_ptr<VariableNode> &variableNode) {
    if (variableNode->isTypeResolved())
        return;

    variableNode->setLocal(variableNode->getParentNode<BlockNode>() != nullptr);

    if (variableNode->getType() != nullptr)
        TypeResolver::visit(variableNode->getType());

    if (variableNode->getExpression() != nullptr) {
        TypeResolver::visit(variableNode->getExpression());

        if (variableNode->getType() == nullptr)
            variableNode->setType(variableNode->getExpression()->getType());
    }

    variableNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<BinaryNode> &binaryNode) {
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

void TypeResolver::visit(const std::shared_ptr<UnaryNode> &unaryNode) {
    if (unaryNode->isTypeResolved())
        return;

    TypeResolver::visit(unaryNode->getExpression());
    unaryNode->setType(unaryNode->getExpression()->getType());

    unaryNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode) {
    if (parenthesizedNode->isTypeResolved())
        return;

    TypeResolver::visit(parenthesizedNode->getExpression());
    parenthesizedNode->setType(parenthesizedNode->getExpression()->getType());

    parenthesizedNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<NumberNode> &numberNode) {
    if (numberNode->isTypeResolved())
        return;

    numberNode->setType(std::make_shared<TypeNode>());
    numberNode->getType()->setFloating(numberNode->getNumber()->getContent().find('.') != std::string::npos);
    numberNode->getType()->setBits(32);

    if (!numberNode->getType()->isFloating())
        numberNode->getType()->setSigned(true);

    numberNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<StringNode> &stringNode) {
    if (stringNode->isTypeResolved())
        return;

    stringNode->setType(std::make_shared<TypeNode>());
    stringNode->getType()->setSigned(false);
    stringNode->getType()->setPointerLevel(1);
    stringNode->getType()->setBits(8);

    stringNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<IdentifierNode> &identifierNode) {
    if (identifierNode->isTypeResolved())
        return;

    std::shared_ptr<std::vector<std::shared_ptr<ASTNode>>> nodes;
    if (identifierNode->getParentNode()->getKind() == ASTNode::STRUCT_CREATE) {
        auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
            return node->getKind() == ASTNode::STRUCT;
        };
        nodes = identifierNode->getParentNode<RootNode>()->searchWithImports(
                identifierNode->getIdentifier()->getContent(), scopeCheck);
    } else if (identifierNode->getKind() == ASTNode::FUNCTION_CALL) {
        auto scopeCheck = [identifierNode](const std::shared_ptr<ASTNode> &node) {
            if (node->getKind() != ASTNode::FUNCTION)
                return false;

            auto functionCall = std::static_pointer_cast<FunctionCallNode>(identifierNode);
            auto function = std::static_pointer_cast<FunctionNode>(node);

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
        nodes = identifierNode->getParentNode<RootNode>()->searchWithImports(
                identifierNode->getIdentifier()->getContent(), scopeCheck);
    } else {
        auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
            return node->getKind() == ASTNode::VARIABLE || node->getKind() == ASTNode::PARAMETER ||
                   node->getKind() == ASTNode::ARGUMENT;
        };
        nodes = identifierNode->getScope()->all(identifierNode->getIdentifier()->getContent(), scopeCheck);

        if (nodes == nullptr) {
            nodes = identifierNode->getParentNode<RootNode>()->searchWithImports(
                    identifierNode->getIdentifier()->getContent(), scopeCheck);
        }
    }

    if (nodes == nullptr || nodes->empty()) {
        THROW_NODE_ERROR(identifierNode, "Couldn't find the identifier \"{}\".",
                         identifierNode->getIdentifier()->getContent())
        return;
    }

    auto targetNode = nodes->at(0);
    TypeResolver::visit(targetNode);

    auto typedNode = std::static_pointer_cast<TypedNode>(targetNode);
    if ((typedNode != nullptr && typedNode->getKind() == ASTNode::ARGUMENT) &&
        (typedNode->getParentNode() != nullptr &&
         typedNode->getParentNode()->getKind() == ASTNode::STRUCT_CREATE)) {
        auto structCreate = std::static_pointer_cast<StructCreateNode>(typedNode->getParentNode());
        targetNode = nullptr;

        auto structNode = std::static_pointer_cast<StructNode>(structCreate->getTargetNode());
        for (auto const &variable : structNode->getVariables()) {
            if (variable->getName()->getContent() == identifierNode->getIdentifier()->getContent())
                targetNode = variable;
        }
    }

    if (targetNode == nullptr) {
        THROW_NODE_ERROR(identifierNode, "Couldn't find the identifier \"{}\".",
                         identifierNode->getIdentifier()->getContent())
        return;
    }

    if (typedNode->getType() == nullptr) {
        THROW_NODE_ERROR(typedNode, "The found identifier has no type.")
        return;
    }

    identifierNode->setTargetNode(targetNode);
    identifierNode->setType(std::make_shared<TypeNode>(*typedNode->getType()));
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

void TypeResolver::visit(const std::shared_ptr<ParameterNode> &parameterNode) {
    if (parameterNode->isTypeResolved())
        return;

    TypeResolver::visit(parameterNode->getType());

    parameterNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<ArgumentNode> &argumentNode) {
    if (argumentNode->isTypeResolved())
        return;

    if (argumentNode->getExpression()->getKind() == ASTNode::STRUCT_CREATE) {
        auto structCreateNode = std::static_pointer_cast<StructCreateNode>(argumentNode->getExpression());

        if (structCreateNode->isUnnamed()) {
            if (argumentNode->getParent()->getKind() == ASTNode::STRUCT_CREATE) {
                auto parentCreate = std::static_pointer_cast<StructCreateNode>(argumentNode->getParent());
                auto parentStruct = std::static_pointer_cast<StructNode>(parentCreate->getTargetNode());

                for (auto const &variable : parentStruct->getVariables()) {
                    if (variable->getName()->getContent() != argumentNode->getName()->getContent())
                        continue;

                    argumentNode->setType(variable->getType());
                    break;
                }

                // TODO: See if this needs to throw an error
            } else if (argumentNode->getParentNode()->getKind() == ASTNode::FUNCTION_CALL) {
                auto parentCall = std::static_pointer_cast<FunctionCallNode>(argumentNode->getParent());
                if (parentCall->getTargetNode() != nullptr) {
                    auto parentFunction = std::static_pointer_cast<FunctionNode>(parentCall->getTargetNode());

                    auto sortedArguments(parentCall->getArguments());
                    parentCall->getSortedArguments(parentFunction, sortedArguments);

                    auto argumentIndex = Utils::indexOf(sortedArguments, argumentNode).second;
                    if(argumentIndex == -1) {
                        THROW_NODE_ERROR(structCreateNode, "Couldn't find the argument index for the "
                                                           "function call.")
                        exit(EXIT_FAILURE);
                    }

                    argumentNode->setType(parentFunction->getParameters()[argumentIndex]->getType());
                } else argumentNode->setTypeWhitelisted(true);
            } else {
                THROW_NODE_ERROR(structCreateNode, "Can't set the type for the argument because the parent "
                                                   "is not implemented yet.")
                exit(EXIT_FAILURE);
            }
        }
    }

    if (!argumentNode->isTypeWhitelisted()) {
        TypeResolver::visit(argumentNode->getExpression());
        argumentNode->setType(argumentNode->getExpression()->getType());

        argumentNode->setTypeResolved(true);
    }
}

void TypeResolver::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode) {
    if (functionCallNode->isTypeResolved())
        return;

    for (const auto &argument : functionCallNode->getArguments())
        TypeResolver::visit(argument);

    std::shared_ptr<IdentifierNode> firstIdentifier = functionCallNode;
    while (firstIdentifier->getLastIdentifier() != nullptr)
        firstIdentifier = firstIdentifier->getLastIdentifier();
    TypeResolver::visit(firstIdentifier);

    auto functionNode = std::static_pointer_cast<FunctionNode>(functionCallNode->getTargetNode());
    if(functionNode == nullptr) {
        // TODO: Throw error here.
        exit(EXIT_FAILURE);
    }

    for (const auto &argument : functionCallNode->getArguments()) {
        if (!argument->isTypeWhitelisted())
            continue;

        argument->setTypeWhitelisted(false);
        TypeResolver::visit(argument);
    }

    if (functionNode->hasAnnotation("inlined"))
        functionNode->setInlinedFunctionCall(functionCallNode);

    functionCallNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<StructCreateNode> &structCreateNode) {
    if (structCreateNode->isTypeResolved())
        return;

    std::shared_ptr<ASTNode> targetNode;
    std::shared_ptr<TypeNode> typeNode;

    if (structCreateNode->isUnnamed()) {
        auto typedNode = std::dynamic_pointer_cast<TypedNode>(structCreateNode->getParent());
        if (typedNode == nullptr) {
            THROW_NODE_ERROR(structCreateNode, "Can't create an unnamed struct, because there is no type "
                                               "defined by a function/argument or variable.")
            exit(EXIT_FAILURE);
        }

        if (typedNode->getType() == nullptr || typedNode->getType()->getTargetStruct() == nullptr) {
            THROW_NODE_ERROR(typedNode, "Can't create an unnamed struct because the type is not resolved "
                                        "yet.")
            exit(EXIT_FAILURE);
        }

        targetNode = typedNode->getType()->getTargetStruct();
        typeNode = typedNode->getType();
    } else {
        TypeResolver::visit(structCreateNode->getStartIdentifier());

        targetNode = structCreateNode->getEndIdentifier()->getTargetNode();
        typeNode = structCreateNode->getEndIdentifier()->getType();
    }

    structCreateNode->setTargetNode(targetNode);
    structCreateNode->setType(typeNode);

    if (structCreateNode->getTargetNode() != nullptr) {
        auto middleScope = std::make_shared<SymbolTable>(*structCreateNode->getTargetNode()->getScope());
        middleScope->setParent(structCreateNode->getScope());

        for (const auto &argument : structCreateNode->getArguments())
            argument->getScope()->setParent(middleScope);

        for (const auto &argument : structCreateNode->getArguments())
            TypeResolver::visit(argument);
    }

    structCreateNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<AssignmentNode> &assignmentNode) {
    if (assignmentNode->isTypeResolved())
        return;

    TypeResolver::visit(assignmentNode->getStartIdentifier());

    assignmentNode->setTargetNode(assignmentNode->getEndIdentifier()->getTargetNode());
    assignmentNode->setType(assignmentNode->getEndIdentifier()->getType());

    TypeResolver::visit(assignmentNode->getExpression());

    assignmentNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<ReturnNode> &returnNode) {
    if (returnNode->isTypeResolved())
        return;

    auto function = returnNode->getParentNode<FunctionNode>();
    if (function == nullptr) {
        std::cout << "Return node is not inside function." << std::endl;
        exit(EXIT_FAILURE);
    }

    returnNode->setType(function->getType());

    if (returnNode->getExpression() != nullptr)
        TypeResolver::visit(returnNode->getExpression());

    returnNode->setTypeResolved(true);
}

void TypeResolver::visit(const std::shared_ptr<StructNode> &structNode) {
    if (structNode->isTypeResolved())
        return;

    structNode->setTypeResolved(true);

    structNode->setType(std::make_shared<TypeNode>());
    structNode->getType()->setTargetStruct(structNode);

    for (const auto &variable : structNode->getVariables())
        TypeResolver::visit(variable);
}

void TypeResolver::visit(const std::shared_ptr<TypeNode> &typeNode) {
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
        auto scopeCheck = [](const std::shared_ptr<ASTNode> &node) {
            return node->getKind() == ASTNode::STRUCT;
        };

        auto rootNode = typeNode->getParentNode<RootNode>();
        auto foundNodes = rootNode->searchWithImports(typeNode->getTypeToken()->getContent(), scopeCheck);

        if (foundNodes->empty()) {
            THROW_NODE_ERROR(typeNode,
                             "Couldn't find the struct for the searched identifier.")
            exit(EXIT_FAILURE);
        }

        typeNode->setTargetStruct(std::static_pointer_cast<StructNode>(foundNodes->at(0)));
        TypeResolver::visit(typeNode->getTargetStruct());
    }
}