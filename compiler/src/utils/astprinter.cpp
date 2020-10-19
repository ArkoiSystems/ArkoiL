//
// Created by timo on 10/17/20.
//

#include "astprinter.h"

#include "../parser/astnodes.h"
#include "../compiler/error.h"
#include "../lexer/token.h"
#include "../utils/utils.h"

void ASTPrinter::visit(const std::shared_ptr<ASTNode> &node,
                       std::ostream &output, int indents) {
    if (node->getKind() == ASTNode::ROOT) {
        ASTPrinter::visit(std::static_pointer_cast<RootNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::IMPORT) {
        ASTPrinter::visit(std::static_pointer_cast<ImportNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::FUNCTION) {
        ASTPrinter::visit(std::static_pointer_cast<FunctionNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::PARAMETER) {
        ASTPrinter::visit(std::static_pointer_cast<ParameterNode>(node), output);
    } else if (node->getKind() == ASTNode::TYPE) {
        ASTPrinter::visit(std::static_pointer_cast<TypeNode>(node), output);
    } else if (node->getKind() == ASTNode::BLOCK) {
        ASTPrinter::visit(std::static_pointer_cast<BlockNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::VARIABLE) {
        ASTPrinter::visit(std::static_pointer_cast<VariableNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::BINARY) {
        ASTPrinter::visit(std::static_pointer_cast<BinaryNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::UNARY) {
        ASTPrinter::visit(std::static_pointer_cast<UnaryNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::PARENTHESIZED) {
        ASTPrinter::visit(std::static_pointer_cast<ParenthesizedNode>(node), output);
    } else if (node->getKind() == ASTNode::STRING) {
        ASTPrinter::visit(std::static_pointer_cast<StringNode>(node), output);
    } else if (node->getKind() == ASTNode::NUMBER) {
        ASTPrinter::visit(std::static_pointer_cast<NumberNode>(node), output);
    } else if (auto identifierNode = std::dynamic_pointer_cast<IdentifierNode>(node)) {
        ASTPrinter::visit(identifierNode, output, indents);
    } else if (node->getKind() == ASTNode::FUNCTION_ARGUMENT) {
        ASTPrinter::visit(std::static_pointer_cast<FunctionArgumentNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::STRUCT_ARGUMENT) {
        ASTPrinter::visit(std::static_pointer_cast<StructArgumentNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::STRUCT_CREATE) {
        ASTPrinter::visit(std::static_pointer_cast<StructCreateNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::ASSIGNMENT) {
        ASTPrinter::visit(std::static_pointer_cast<AssignmentNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::RETURN) {
        ASTPrinter::visit(std::static_pointer_cast<ReturnNode>(node), output, indents);
    } else if (node->getKind() == ASTNode::STRUCT) {
        ASTPrinter::visit(std::static_pointer_cast<StructNode>(node), output, indents);
    } else {
        THROW_NODE_ERROR(node, "ASTPrinter: Unsupported node: " + node->getKindAsString())
        exit(EXIT_FAILURE);
    }
}

void ASTPrinter::visit(const std::shared_ptr<RootNode> &rootNode,
                       std::ostream &output, int indents) {
    for (auto index = 0; index < rootNode->getNodes().size(); index++) {
        auto node = rootNode->getNodes()[index];
        ASTPrinter::visit(node, output, indents);

        if (index != rootNode->getNodes().size() - 1)
            output << std::endl;
    }
}

void ASTPrinter::visit(const std::shared_ptr<ImportNode> &importNode,
                       std::ostream &output, int indents) {
    output << std::string(indents, '\t')
           << "import \"" << importNode->getPath()->getContent() << "\"" << std::endl;
}

void ASTPrinter::visit(const std::shared_ptr<FunctionNode> &functionNode,
                       std::ostream &output, int indents) {
    output << std::string(indents, '\t')
           << "fun " << functionNode->getName()->getContent() << "(";

    for (auto index = 0; index < functionNode->getParameters().size(); index++) {
        auto parameter = functionNode->getParameters()[index];
        visit(parameter, output);

        if (index != functionNode->getParameters().size() - 1)
            output << ", ";
    }

    output << "): ";

    visit(functionNode->getType(), output);

    if (functionNode->getBlock() != nullptr) {
        output << (functionNode->getBlock()->isInlined() ? " = " : " {");
        if (!functionNode->getBlock()->isInlined())
            output << std::endl;

        visit(functionNode->getBlock(), output, indents + 1);

        output << std::string(indents, '\t')
               << (functionNode->getBlock()->isInlined() ? "" : "}");
    }

    output << std::endl;
}

void ASTPrinter::visit(const std::shared_ptr<ParameterNode> &parameterNode,
                       std::ostream &output) {
    output << parameterNode->getName()->getContent() << ": ";
    visit(parameterNode->getType(), output);
}

void ASTPrinter::visit(const std::shared_ptr<TypeNode> &typeNode,
                       std::ostream &output) {
    output << typeNode->getTypeToken()->getContent() << std::string(typeNode->getPointerLevel(), '*');
}

void ASTPrinter::visit(const std::shared_ptr<BlockNode> &blockNode,
                       std::ostream &output, int indents) {
    for (auto const &node : blockNode->getNodes()) {
        visit(node, output, indents);
        output << std::endl;
    }
}

void ASTPrinter::visit(const std::shared_ptr<VariableNode> &variableNode,
                       std::ostream &output, int indents) {
    output << std::string(indents, '\t')
           << (variableNode->isConstant() ? "const " : "var ") << variableNode->getName()->getContent();

    if (variableNode->getType() != nullptr) {
        output << ": ";
        visit(variableNode->getType(), output);
    }

    if (variableNode->getExpression() != nullptr) {
        output << " = ";
        visit(variableNode->getExpression(), output, indents);
    }
}

void ASTPrinter::visit(const std::shared_ptr<BinaryNode> &binaryNode,
                       std::ostream &output, int indents) {
    visit(binaryNode->getLHS(), output, indents);

    switch (binaryNode->getOperatorKind()) {
        case BinaryNode::ADDITION:
            output << " + ";
            break;
        case BinaryNode::SUBTRACTION:
            output << " - ";
            break;
        case BinaryNode::MULTIPLICATION:
            output << "const std::shared_ptr<> & ";
            break;
        case BinaryNode::DIVISION:
            output << " / ";
            break;
        case BinaryNode::REMAINING:
            output << " % ";
            break;
        case BinaryNode::LESS_THAN:
            output << " < ";
            break;
        case BinaryNode::GREATER_THAN:
            output << " > ";
            break;
        case BinaryNode::LESS_EQUAL_THAN:
            output << " <= ";
            break;
        case BinaryNode::GREATER_EQUAL_THAN:
            output << " >= ";
            break;
        case BinaryNode::EQUAL:
            output << " == ";
            break;
        case BinaryNode::NOT_EQUAL:
            output << " != ";
            break;
        case BinaryNode::BIT_CAST:
            output << " bitcast ";
            break;

        default:
            std::cerr << "Not implemented!" << std::endl;
            abort();
    }

    visit(binaryNode->getRHS(), output, indents);
}

void ASTPrinter::visit(const std::shared_ptr<UnaryNode> &unaryNode,
                       std::ostream &output, int indents) {
    switch (unaryNode->getOperatorKind()) {
        case UnaryNode::NEGATE:
            output << "!";
            break;

        default:
            std::cerr << "Not implemented!" << std::endl;
            abort();
    }

    visit(unaryNode->getExpression(), output, indents);
}

void ASTPrinter::visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode,
                       std::ostream &output, int indents) {
    output << "(";
    visit(parenthesizedNode->getExpression(), output, indents);
    output << ")";
}

void ASTPrinter::visit(const std::shared_ptr<NumberNode> &numberNode,
                       std::ostream &output) {
    output << numberNode->getNumber()->getContent();
}

void ASTPrinter::visit(const std::shared_ptr<StringNode> &stringNode,
                       std::ostream &output) {
    output << "\"" << stringNode->getString()->getContent() << "\"";
}

void ASTPrinter::visit(const std::shared_ptr<IdentifierNode> &identifierNode,
                       std::ostream &output, int indents) {
    auto firstIdentifier = identifierNode;
    while (firstIdentifier->getLastIdentifier() != nullptr)
        firstIdentifier = firstIdentifier->getLastIdentifier();

    auto isParentBlock = firstIdentifier->getParent()->getKind() == ASTNode::BLOCK;

    output << (isParentBlock ? std::string(indents, '\t') : "")
           << (firstIdentifier->isPointer() ? "&" : "") << (firstIdentifier->isDereference() ? "@" : "")
           << firstIdentifier->getIdentifier()->getContent();
    if (firstIdentifier->getKind() == ASTNode::FUNCTION_CALL)
        visit(std::reinterpret_pointer_cast<FunctionCallNode>(firstIdentifier), output);

    auto nextIdentifier = firstIdentifier->getNextIdentifier();
    while (nextIdentifier != nullptr) {
        output << "."
               << (nextIdentifier->isPointer() ? "&" : "") << (nextIdentifier->isDereference() ? "@" : "")
               << nextIdentifier->getIdentifier()->getContent();
        if (nextIdentifier->getKind() == ASTNode::FUNCTION_CALL)
            visit(std::reinterpret_pointer_cast<FunctionCallNode>(nextIdentifier), output);

        nextIdentifier = nextIdentifier->getNextIdentifier();
    }
}

void ASTPrinter::visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode,
                       std::ostream &output, int indents) {
    if (functionArgumentNode->getName() != nullptr)
        output << functionArgumentNode->getName()->getContent() << ": ";

    visit(functionArgumentNode->getExpression(), output, indents);
}

void ASTPrinter::visit(const std::shared_ptr<FunctionCallNode> &functionCallNode,
                       std::ostream &output) {
    output << "(";

    for (auto index = 0; index < functionCallNode->getArguments().size(); index++) {
        auto argument = functionCallNode->getArguments()[index];
        visit(argument, output, index);

        if (index != functionCallNode->getArguments().size() - 1)
            output << ", ";
    }

    output << ")";
}

void ASTPrinter::visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode,
                       std::ostream &output, int indents) {
    output << std::string(indents, '\t')
           << structArgumentNode->getName()->getContent() << ": ";

    if (structArgumentNode->getExpression() != nullptr)
        visit(structArgumentNode->getExpression(), output, indents);
}

void ASTPrinter::visit(const std::shared_ptr<StructCreateNode> &structCreateNode,
                       std::ostream &output, int indents) {
    auto blockNode = structCreateNode->findNodeOfParents<BlockNode>();
    bool isParentBlock = structCreateNode->getParent()->getKind() == ASTNode::BLOCK;
    bool isInlinedNode = blockNode != nullptr && blockNode->isInlined()
                         && !isParentBlock && (structCreateNode->getParent()->getKind() == ASTNode::RETURN);

    output << ((isParentBlock && !isInlinedNode) ? std::string(indents, '\t') : "")
           << structCreateNode->getType()->getTargetStruct()->getName()->getContent() << " {" << std::endl;

    for (auto const argument : structCreateNode->getArguments()) {
        if (argument->getExpression() == nullptr)
            continue;

        visit(argument, output, (!isInlinedNode ? indents + 1 : indents));
        output << std::endl;
    }

    output << (!isInlinedNode ? std::string(indents, '\t') : std::string(indents - 1, '\t'))
           << "}";
}

void ASTPrinter::visit(const std::shared_ptr<AssignmentNode> &assignmentNode,
                       std::ostream &output, int indents) {
    auto firstIdentifier = assignmentNode->getStartIdentifier();
    auto isParentBlock = assignmentNode->getParent()->getKind() == ASTNode::BLOCK;

    output << (isParentBlock ? std::string(indents, '\t') : "")
           << (firstIdentifier->isPointer() ? "&" : "") << (firstIdentifier->isDereference() ? "@" : "")
           << firstIdentifier->getIdentifier()->getContent();
    if (firstIdentifier->getKind() == ASTNode::FUNCTION_CALL)
        visit(std::reinterpret_pointer_cast<FunctionCallNode>(firstIdentifier), output);

    auto nextIdentifier = firstIdentifier->getNextIdentifier();
    while (nextIdentifier != nullptr) {
        output << "."
               << (nextIdentifier->isPointer() ? "&" : "") << (nextIdentifier->isDereference() ? "@" : "")
               << nextIdentifier->getIdentifier()->getContent();
        if (nextIdentifier->getKind() == ASTNode::FUNCTION_CALL)
            visit(std::reinterpret_pointer_cast<FunctionCallNode>(nextIdentifier), output);

        nextIdentifier = nextIdentifier->getNextIdentifier();
    }

    output << " = ";
    visit(assignmentNode->getExpression(), output, indents);
}

void ASTPrinter::visit(const std::shared_ptr<ReturnNode> &returnNode,
                       std::ostream &output, int indents) {
    bool isInlinedNode = returnNode->getParent()->getKind() == ASTNode::BLOCK;
    bool isParentBlock = isInlinedNode;
    if (isInlinedNode) {
        auto blockNode = std::static_pointer_cast<BlockNode>(returnNode->getParent());
        isInlinedNode = blockNode->isInlined();
    }

    if (isParentBlock && !isInlinedNode)
        output << std::string(indents, '\t')
               << "return ";

    if (returnNode->getExpression() != nullptr)
        visit(returnNode->getExpression(), output, indents);
}

void ASTPrinter::visit(const std::shared_ptr<StructNode> &structNode,
                       std::ostream &output, int indents) {
    output << std::string(indents, '\t')
           << "struct " << structNode->getName()->getContent() << " {" << std::endl;

    for (const auto variable : structNode->getVariables()) {
        visit(variable, output, indents + 1);
        output << std::endl;
    }

    output << std::string(indents, '\t')
           << "}" << std::endl;
}