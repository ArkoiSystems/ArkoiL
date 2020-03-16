/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils;

public enum TokenType
{
    
    WHITESPACE,
    COMMENT,
    SYMBOL,
    
    STRING_LITERAL,
    NUMBER_LITERAL,
    
    // TODO: Add BOOLEAN_LITERAL and CHAR_LITERAL
    IDENTIFIER,
    END_OF_FILE
    
}
