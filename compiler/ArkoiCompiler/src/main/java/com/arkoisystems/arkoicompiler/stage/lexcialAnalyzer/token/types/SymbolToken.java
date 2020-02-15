/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;

@Getter
@Setter
public class SymbolToken extends AbstractToken
{
    
    @Expose
    private SymbolType symbolType;
    
    public SymbolToken(final String tokenContent, final int start, final int end) {
        super(TokenType.SYMBOL, tokenContent, start, end);
    }
    
    @Override
    public AbstractToken parse(final Matcher matcher) {
        switch (this.getTokenContent().charAt(0)) {
            case '@':
                this.setSymbolType(SymbolType.AT_SIGN);
                break;
            case ':':
                this.setSymbolType(SymbolType.COLON);
                break;
            case ';':
                this.setSymbolType(SymbolType.SEMICOLON);
                break;
            case '{':
                this.setSymbolType(SymbolType.OPENING_BRACE);
                break;
            case '}':
                this.setSymbolType(SymbolType.CLOSING_BRACE);
                break;
            case '(':
                this.setSymbolType(SymbolType.OPENING_PARENTHESIS);
                break;
            case ')':
                this.setSymbolType(SymbolType.CLOSING_PARENTHESIS);
                break;
            case '[':
                this.setSymbolType(SymbolType.OPENING_BRACKET);
                break;
            case ']':
                this.setSymbolType(SymbolType.CLOSING_BRACKET);
                break;
            case ',':
                this.setSymbolType(SymbolType.COMMA);
                break;
            case '.':
                this.setSymbolType(SymbolType.PERIOD);
                break;
            case '>':
                this.setSymbolType(SymbolType.GREATER_THAN_SIGN);
                break;
            case '<':
                this.setSymbolType(SymbolType.LESS_THAN_SIGN);
                break;
            case '+':
                this.setSymbolType(SymbolType.PLUS);
                break;
            case '*':
                this.setSymbolType(SymbolType.ASTERISK);
                break;
            case '/':
                this.setSymbolType(SymbolType.SLASH);
                break;
            case '-':
                this.setSymbolType(SymbolType.MINUS);
                break;
            case '%':
                this.setSymbolType(SymbolType.PERCENT);
                break;
            case '=':
                this.setSymbolType(SymbolType.EQUAL);
                break;
            case '!':
                this.setSymbolType(SymbolType.EXCLAMATION_MARK);
                break;
            case '&':
                this.setSymbolType(SymbolType.AMPERSAND);
                break;
        }
        return this;
    }
    
    public enum SymbolType
    {
        
        AT_SIGN,
    
        COLON,
        SEMICOLON,
        
        OPENING_BRACE,
        CLOSING_BRACE,
        
        OPENING_PARENTHESIS,
        CLOSING_PARENTHESIS,
        
        OPENING_BRACKET,
        CLOSING_BRACKET,
        
        COMMA,
        PERIOD,
        
        LESS_THAN_SIGN,
        GREATER_THAN_SIGN,
    
        VERTICAL_BAR,
        AMPERSAND,
        EXCLAMATION_MARK,
        ASTERISK,
        EQUAL,
        PERCENT,
        MINUS,
        SLASH,
        PLUS
        
    }
    
}
