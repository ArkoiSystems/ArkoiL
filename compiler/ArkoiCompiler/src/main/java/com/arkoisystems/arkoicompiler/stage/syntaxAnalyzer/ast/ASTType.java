/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast;

import lombok.Getter;

@Getter
public enum ASTType
{
    
    THIS_STATEMENT,
    RETURN_STATEMENT,
    
    BINARY_EXPRESSION,
    PARENTHESIZED_EXPRESSION,
    ASSIGNMENT_EXPRESSION,
    EQUALITY_EXPRESSION,
    LOGICAL_EXPRESSION,
    POSTFIX_EXPRESSION,
    PREFIX_EXPRESSION,
    RELATIONAL_EXPRESSION,
    BASIC_EXPRESSION,
    
    IMPORT_DEFINITION,
    ARGUMENT_DEFINITION,
    VARIABLE_DEFINITION,
    FUNCTION_DEFINITION,
    
    IDENTIFIER_INVOKE_OPERABLE,
    IDENTIFIER_CALL_OPERABLE,
    FUNCTION_INVOKE_OPERABLE,
    COLLECTION_OPERABLE,
    NUMBER_OPERABLE,
    STRING_OPERABLE,
    
    ANNOTATION,
    BLOCK,
    ROOT,
    TYPE
    
}
