//
// Created by timo on 10/17/20.
//

#pragma once

#include <sstream>
#include <memory>

#include "../parser/allnodes.h"

class ASTPrinter {

public:
    ASTPrinter() = delete;

    ASTPrinter(const ASTPrinter &other) = delete;

    ASTPrinter &operator=(const ASTPrinter &) = delete;

public:
    static void visit(const std::shared_ptr<ASTNode> &node,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<RootNode> &rootNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<ImportNode> &importNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<FunctionNode> &functionNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<ParameterNode> &parameterNode,
                      std::ostream &output);

    static void visit(const std::shared_ptr<TypeNode> &typeNode,
                      std::ostream &output);

    static void visit(const std::shared_ptr<BlockNode> &blockNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<VariableNode> &variableNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<BinaryNode> &binaryNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<UnaryNode> &unaryNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<ParenthesizedNode> &parenthesizedNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<NumberNode> &numberNode,
                      std::ostream &output);

    static void visit(const std::shared_ptr<StringNode> &stringNode,
                      std::ostream &output);

    static void visit(const std::shared_ptr<IdentifierNode> &identifierNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<FunctionArgumentNode> &functionArgumentNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<FunctionCallNode> &functionCallNode,
                      std::ostream &output);

    static void visit(const std::shared_ptr<StructArgumentNode> &structArgumentNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<StructCreateNode> &structCreateNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<AssignmentNode> &assignmentNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<ReturnNode> &returnNode,
                      std::ostream &output, int indents = 0);

    static void visit(const std::shared_ptr<StructNode> &structNode,
                      std::ostream &output, int indents = 0);

};


