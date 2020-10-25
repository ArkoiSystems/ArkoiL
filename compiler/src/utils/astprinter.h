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
    static void visit(const SharedASTNode &node,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedRootNode &rootNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedImportNode &importNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedFunctionNode &functionNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedParameterNode &parameterNode,
                      std::ostream &output);

    static void visit(const SharedTypeNode &typeNode,
                      std::ostream &output);

    static void visit(const SharedBlockNode &blockNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedVariableNode &variableNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedBinaryNode &binaryNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedUnaryNode &unaryNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedParenthesizedNode &parenthesizedNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedNumberNode &numberNode,
                      std::ostream &output);

    static void visit(const SharedStringNode &stringNode,
                      std::ostream &output);

    static void visit(const SharedIdentifierNode &identifierNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedFunctionArgumentNode &functionArgumentNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedFunctionCallNode &functionCallNode,
                      std::ostream &output);

    static void visit(const SharedStructArgumentNode &structArgumentNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedStructCreateNode &structCreateNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedAssignmentNode &assignmentNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedReturnNode &returnNode,
                      std::ostream &output, int indents = 0);

    static void visit(const SharedStructNode &structNode,
                      std::ostream &output, int indents = 0);

};


