/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils;

public enum ASTType
{
    
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
    
    IMPORT,
    PARAMETER,
    RETURN,
    ARGUMENT,
    VARIABLE,
    FUNCTION,
    
    FUNCTION_CALL_PART,
    IDENTIFIER_CALL,
    COLLECTION,
    NUMBER,
    STRING,
    
    OPERABLE,
    STATEMENT,
    
    PARAMETER_LIST,
    ARGUMENT_LIST,
    
    ANNOTATION,
    BLOCK,
    ROOT,
    TYPE
    
}
