//
// Created by timo on 10/15/20.
//

#pragma once

#include <memory>

#include "../parser/allnodes.h"

class Inliner {

public:
    Inliner() = delete;

    Inliner(const Inliner &other) = delete;

    Inliner &operator=(const Inliner &) = delete;

public:
    static SharedVariableNode visit(const SharedASTNode &node);

    static SharedVariableNode visit(const SharedRootNode &rootNode);

    static SharedVariableNode visit(const SharedFunctionNode &functionNode);

    static SharedVariableNode visit(const SharedBlockNode &blockNode);

    static SharedVariableNode visit(const SharedVariableNode &variableNode);

    static SharedVariableNode visit(const SharedBinaryNode &binaryNode);

    static SharedVariableNode visit(const SharedUnaryNode &unaryNode);

    static SharedVariableNode visit(const SharedParenthesizedNode &parenthesizedNode);

    static SharedVariableNode visit(const SharedIdentifierNode &identifierNode);

    static SharedVariableNode visit(const SharedFunctionArgumentNode &functionArgumentNode);

    static SharedVariableNode visit(const SharedStructArgumentNode &structArgumentNode);

    static SharedVariableNode visit(const SharedFunctionCallNode &functionCallNode);

    static SharedVariableNode visit(const SharedStructCreateNode &structCreateNode);

    static SharedVariableNode visit(const SharedAssignmentNode &assignmentNode);

    static SharedVariableNode visit(const SharedReturnNode &returnNode);

    static SharedVariableNode visit(const SharedStructNode &structNode);

    static SharedASTNode
    generate(const SharedASTNode &node, int insertIndex, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedParameterNode
    generate(const SharedParameterNode &parameterNode, int insertIndex, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedVariableNode
    generate(const SharedVariableNode &variableNode, int insertIndex, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedBinaryNode
    generate(const SharedBinaryNode &binaryNode, int insertIndex, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedUnaryNode
    generate(const SharedUnaryNode &unaryNode, int insertIndex, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedParenthesizedNode
    generate(const SharedParenthesizedNode &parenthesizedNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedStructCreateNode
    generate(const SharedStructCreateNode &structCreateNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedStructArgumentNode
    generate(const SharedStructArgumentNode &structArgumentNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedFunctionArgumentNode
    generate(const SharedFunctionArgumentNode &functionArgumentNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedFunctionCallNode
    generate(const SharedFunctionCallNode &functionCallNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedAssignmentNode
    generate(const SharedAssignmentNode &assignmentNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedIdentifierNode
    generate(const SharedIdentifierNode &identifierNode, int insertIndex,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedReturnNode
    generate(const SharedReturnNode &returnNode, int insertIndex, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedStringNode
    generate(const SharedStringNode &stringNode, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedNumberNode
    generate(const SharedNumberNode &numberNode, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedTypeNode
    generate(const SharedTypeNode &typeNode, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedVariableNode
    inlineFunctionCall(const SharedFunctionNode &targetFunction,
                       const SharedFunctionCallNode &functionCallNode,
                       const SharedBlockNode &insertBlock, int insertIndex);

    static SharedIdentifierNode
    createIdentifier(const SharedASTNode &parent, const SharedSymbolTable &scope,
                     const SharedVariableNode &returnVariable);

};


