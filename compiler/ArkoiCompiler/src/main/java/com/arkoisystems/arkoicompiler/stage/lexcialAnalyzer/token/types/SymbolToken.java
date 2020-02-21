/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.LexicalError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.Getter;
import lombok.Setter;

public class SymbolToken extends AbstractToken
{
    
    @Getter
    @Setter
    private SymbolType symbolType;
    
    
    public SymbolToken() {
        this.setTokenType(TokenType.SYMBOL);
    }
    
    
    @Override
    public SymbolToken lex(final LexicalAnalyzer lexicalAnalyzer) {
        final char currentChar = lexicalAnalyzer.currentChar();
        for (final SymbolType symbolType : SymbolType.values())
            if (symbolType.getCharacter() == currentChar) {
                this.setSymbolType(symbolType);
                break;
            }
        
        if (this.getSymbolType() == null) {
            lexicalAnalyzer.errorHandler().addError(new LexicalError(lexicalAnalyzer.getArkoiClass(), lexicalAnalyzer.getPosition(), "Couldn't lex this symbol because it isn't supported."));
            return null;
        } else {
            this.setStart(lexicalAnalyzer.getPosition());
            this.setEnd(lexicalAnalyzer.getPosition());
            
            lexicalAnalyzer.next();
        }
        return this;
    }
    
    
    public enum SymbolType
    {
        
        AT_SIGN('@'),
        
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
    
}
