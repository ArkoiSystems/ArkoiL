//
// Created by timo on 10/15/20.
//

#pragma once

#include <vector>

class ASTNode;

typedef std::shared_ptr<ASTNode> SharedASTNode;

class RootNode;

typedef std::shared_ptr<RootNode> SharedRootNode;

class ImportNode;

typedef std::shared_ptr<ImportNode> SharedImportNode;

class FunctionNode;

typedef std::shared_ptr<FunctionNode> SharedFunctionNode;

class BlockNode;

typedef std::shared_ptr<BlockNode> SharedBlockNode;

class ParameterNode;

typedef std::shared_ptr<ParameterNode> SharedParameterNode;

class VariableNode;

typedef std::shared_ptr<VariableNode> SharedVariableNode;

class BinaryNode;

typedef std::shared_ptr<BinaryNode> SharedBinaryNode;

class UnaryNode;

typedef std::shared_ptr<UnaryNode> SharedUnaryNode;

class ParenthesizedNode;

typedef std::shared_ptr<ParenthesizedNode> SharedParenthesizedNode;

class StructCreateNode;

typedef std::shared_ptr<StructCreateNode> SharedStructCreateNode;

class StructArgumentNode;

typedef std::shared_ptr<StructArgumentNode> SharedStructArgumentNode;

class FunctionArgumentNode;

typedef std::shared_ptr<FunctionArgumentNode> SharedFunctionArgumentNode;

class FunctionCallNode;

typedef std::shared_ptr<FunctionCallNode> SharedFunctionCallNode;

class AssignmentNode;

typedef std::shared_ptr<AssignmentNode> SharedAssignmentNode;

class IdentifierNode;

typedef std::shared_ptr<IdentifierNode> SharedIdentifierNode;

class ReturnNode;

typedef std::shared_ptr<ReturnNode> SharedReturnNode;

class StructNode;

typedef std::shared_ptr<StructNode> SharedStructNode;

class StringNode;

typedef std::shared_ptr<StringNode> SharedStringNode;

class NumberNode;

typedef std::shared_ptr<NumberNode> SharedNumberNode;

class TypeNode;

typedef std::shared_ptr<TypeNode> SharedTypeNode;

class TypedNode;

typedef std::shared_ptr<TypedNode> SharedTypedNode;

class OperableNode;

typedef std::shared_ptr<OperableNode> SharedOperableNode;

class SymbolTable;

typedef std::shared_ptr<SymbolTable> SharedSymbolTable;

typedef std::vector<SharedASTNode> Symbols;