package com.arkoisystems.arkoicompiler.phases.lexer.token.enums;

import lombok.Getter;

public enum SymbolType
{
    
    AT_SIGN("@"),
    CARET("^"),
    
    COLON(":"),
    
    OPENING_BRACE("{"),
    CLOSING_BRACE("}"),
    
    OPENING_PARENTHESIS("("),
    CLOSING_PARENTHESIS(")"),
    
    OPENING_BRACKET("["),
    CLOSING_BRACKET("]"),
    
    COMMA(","),
    PERIOD("."),
    
    OPENING_ARROW("<"),
    CLOSING_ARROW(">");
    
    @Getter
    private final String name;
    
    SymbolType(final String name) {
        this.name = name;
    }
    
}