/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils;

/**
 * This enum contains for every AST one entry to differentiate these from each other. Also
 * they are grouped in "statements", "expressions", "definitions", "operable" and
 * "others".
 */
public enum ASTType
{
    
    RETURN_STATEMENT,
    
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    ASSIGNMENT_EXPRESSION,
    EQUALITY_EXPRESSION,
    LOGICAL_EXPRESSION,
    POSTFIX_EXPRESSION,
    CAST_EXPRESSION,
    PREFIX_EXPRESSION,
    RELATIONAL_EXPRESSION,
    EXPRESSION,
    
    IMPORT_DEFINITION,
    PARAMETER_DEFINITION,
    ARGUMENT_DEFINITION,
    VARIABLE_DEFINITION,
    FUNCTION_DEFINITION,
    
    FUNCTION_CALL_PART,
    IDENTIFIER_CALL_OPERABLE,
    COLLECTION_OPERABLE,
    NUMBER_OPERABLE,
    STRING_OPERABLE,
    
    OPERABLE,
    STATEMENT,
    
    ANNOTATION,
    BLOCK,
    ROOT,
    TYPE
    
}
