//
// Created by timo on 7/30/20.
//

#ifndef ARKOICOMPILER_ASTNODES_H
#define ARKOICOMPILER_ASTNODES_H

#include <vector>
#include "../lexer/token.h"

enum ASTKind {
    AST_NONE,
    AST_ROOT,
    AST_IMPORT,
    AST_FUNCTION,
    AST_PARAMETER,
    AST_TYPE,
    AST_BLOCK,
    AST_VARIABLE,
    AST_BINARY,
    AST_UNARY,
    AST_PARENTHESIZED,
    AST_NUMBER,
    AST_STRING,
    AST_IDENTIFIER,
    AST_ARGUMENT,
    AST_FUNCTION_CALL,
    AST_STRUCT_CREATE,
    AST_ASSIGNMENT,
    AST_RETURN,
    AST_STRUCT,
};

struct ASTNode {

    unsigned int startLine, endLine;
    ASTKind kind;

    ASTNode() {
        startLine = 0;
        endLine = 0;
        kind = AST_NONE;
    }

    virtual ~ASTNode() = default;

};

struct RootNode: public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;

    RootNode() {
        kind = AST_ROOT;
    }

};

struct ImportNode: public ASTNode {

    std::shared_ptr<Token> path;

    ImportNode() {
        kind = AST_IMPORT;
    }

};

struct TypeNode: public ASTNode {

    unsigned int pointerLevel;

    TypeNode() {
        kind = AST_TYPE;
        pointerLevel = 0;
    }

};

struct ParameterNode: public ASTNode {

    std::shared_ptr<Token> name;
    std::shared_ptr<TypeNode> type;

    ParameterNode() {
        kind = AST_PARAMETER;
    }

};

struct BlockNode: public ASTNode {

    std::vector<std::shared_ptr<ASTNode>> nodes;
    bool inlined;

    BlockNode() {
        kind = AST_BLOCK;
        inlined = false;
    }

};

struct FunctionNode: public ASTNode {

    std::shared_ptr<Token> name;
    std::vector<std::shared_ptr<ParameterNode>> parameters;
    std::shared_ptr<TypeNode> type;
    std::shared_ptr<BlockNode> block;

    FunctionNode() {
        kind = AST_FUNCTION;
    }

};

struct OperableNode: public ASTNode { };

struct VariableNode: public ASTNode {

    std::shared_ptr<Token> name;
    bool constant;
    std::shared_ptr<TypeNode> type;
    std::shared_ptr<OperableNode> expression;

    VariableNode() {
        kind = AST_VARIABLE;
    }

};

enum BinaryKind {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    REMAINING,

    LESS_THAN,
    GREATER_THAN,
    LESS_EQUAL_THAN,
    GREATER_EQUAL_THAN,
    EQUAL,
    NOT_EQUAL,
};

struct BinaryNode: public OperableNode {

    std::shared_ptr<OperableNode> rhs;
    BinaryKind operatorKind;
    std::shared_ptr<OperableNode> lhs;

    BinaryNode() {
        kind = AST_BINARY;
    }

};

enum UnaryKind {
    NEGATE
};

struct UnaryNode: public OperableNode {

    std::shared_ptr<OperableNode> operable;
    UnaryKind operatorKind;

    UnaryNode() {
        kind = AST_UNARY;
    }

};

struct ParenthesizedNode: public OperableNode {

    std::shared_ptr<OperableNode> expression;

    ParenthesizedNode() {
        kind = AST_PARENTHESIZED;
    }

};

struct NumberNode: public OperableNode {

    std::shared_ptr<Token> number;

    NumberNode() {
        kind = AST_NUMBER;
    }

};

struct StringNode : public OperableNode {

    std::shared_ptr<Token> string;

    StringNode() {
        kind = AST_STRING;
    }

};

struct ArgumentNode : public ASTNode {

    std::shared_ptr<Token> name;
    std::shared_ptr<OperableNode> expression;

    ArgumentNode() {
        kind = AST_ARGUMENT;
    }

};

struct IdentifierNode : public OperableNode {

    bool pointer, dereference;
    std::shared_ptr<Token> identifier;
    std::shared_ptr<IdentifierNode> nextIdentifier;

    IdentifierNode() {
        kind = AST_IDENTIFIER;
        dereference = false;
        pointer = false;
    }

};

struct FunctionCallNode : public IdentifierNode {

    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    FunctionCallNode() {
        kind = AST_FUNCTION_CALL;
    }

};

struct StructCreateNode : public IdentifierNode {

    std::vector<std::shared_ptr<ArgumentNode>> arguments;

    StructCreateNode() {
        kind = AST_STRUCT_CREATE;
    }

};

struct AssignmentNode : public IdentifierNode {

    std::shared_ptr<OperableNode> expression;

    AssignmentNode() {
        kind = AST_ASSIGNMENT;
    }

};

struct ReturnNode : public ASTNode {

    std::shared_ptr<OperableNode> expression;

    ReturnNode() {
        kind = AST_RETURN;
    }

};

struct StructNode : public ASTNode {

    std::shared_ptr<Token> name;
    std::vector<std::shared_ptr<VariableNode>> variables;

    StructNode() {
        kind = AST_STRUCT;
    }

};

#endif //ARKOICOMPILER_ASTNODES_H
