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
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedASTNode &node, const std::string &prefix, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedIdentifierNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedParameterNode &parameterNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedVariableNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedVariableNode &variableNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedBinaryNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedBinaryNode &binaryNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedUnaryNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedUnaryNode &unaryNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedParenthesizedNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedParenthesizedNode &parenthesizedNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedStructCreateNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedStructCreateNode &structCreateNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedStructArgumentNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedStructArgumentNode &structArgumentNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedFunctionArgumentNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedFunctionArgumentNode &functionArgumentNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedIdentifierNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedFunctionCallNode &functionCallNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedASTNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedAssignmentNode &assignmentNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedIdentifierNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedIdentifierNode &identifierNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedAssignmentNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedReturnNode &returnNode, const std::string &prefix,
             const SharedASTNode &parent, const SharedSymbolTable &scope);

    static SharedStringNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedStringNode &stringNode, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedNumberNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedNumberNode &numberNode, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedTypeNode
    generate(const SharedFunctionCallNode &functionCaller, const SharedVariableNode &returnVariable,
             const SharedTypeNode &typeNode, const SharedASTNode &parent,
             const SharedSymbolTable &scope);

    static SharedVariableNode
    inlineFunctionCall(const SharedFunctionNode &targetFunction,
                       const SharedFunctionCallNode &functionCallNode,
                       const SharedBlockNode &insertBlock, int insertIndex);

    static SharedIdentifierNode
    createIdentifier(const SharedASTNode &parent, const SharedSymbolTable &scope,
                     const SharedVariableNode &returnVariable);

};


