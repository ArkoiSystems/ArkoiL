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
    
    LESS_THAN_SIGN('<'),
    GREATER_THAN_SIGN('>'),
    
    VERTICAL_BAR('|'),
    AMPERSAND('&'),
    EXCLAMATION_MARK('!'),
    ASTERISK('*'),
    EQUAL('='),
    PERCENT('%'),
    MINUS('-'),
    SLASH('/'),
    PLUS('+');
    
    @Getter
    private final char character;
    
    SymbolType(final char character) {
        this.character = character;
    }
    
}