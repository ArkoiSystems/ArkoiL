package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils;

import lombok.Getter;

public enum SymbolType
{
    
    AT_SIGN('@'),
    CARET('^'),
    
    COLON(':'),
    SEMICOLON(';'),
    
    OPENING_BRACE('{'),
    CLOSING_BRACE('}'),
    
    OPENING_PARENTHESIS('('),
    CLOSING_PARENTHESIS(')'),
    
    OPENING_BRACKET('['),
    CLOSING_BRACKET(']'),
    
    COMMA(','),
    PERIOD('.'),
    
    OPENING_ARROW('<'),
    CLOSING_ARROW('>');
    
    @Getter
    private final char character;
    
    SymbolType(final char character) {
        this.character = character;
    }
    
}