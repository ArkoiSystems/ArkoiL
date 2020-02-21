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

import java.util.Arrays;

public class StringToken extends AbstractToken
{
    
    public StringToken() {
        this.setTokenType(TokenType.STRING_LITERAL);
    }
    
    
    @Override
    public StringToken lex(final LexicalAnalyzer lexicalAnalyzer) {
        if(lexicalAnalyzer.currentChar() != '"') {
            lexicalAnalyzer.errorHandler().addError(new LexicalError(lexicalAnalyzer.getArkoiClass(), lexicalAnalyzer.getPosition(), "Couldn't lex the string because it doesn't start with an \"."));
            return null;
        } else lexicalAnalyzer.next();
        
        this.setStart(lexicalAnalyzer.getPosition() - 1);
        while (lexicalAnalyzer.getPosition() < lexicalAnalyzer.getArkoiClass().getContent().length) {
            final char currentChar = lexicalAnalyzer.currentChar();
        
            if (currentChar == '"') {
                this.setEnd(lexicalAnalyzer.getPosition());
                break;
            } else if (currentChar == 0x0A || currentChar == 0x0D)
                break;
            lexicalAnalyzer.next();
        }
    
        if (lexicalAnalyzer.currentChar() != '"') {
            lexicalAnalyzer.errorHandler().addError(new LexicalError(lexicalAnalyzer.getArkoiClass(), this.getStart(), lexicalAnalyzer.getPosition(), "The defined string doesn't end with another double quote:"));
            return null;
        }
    
        this.setTokenContent(new String(Arrays.copyOfRange(lexicalAnalyzer.getArkoiClass().getContent(), this.getStart() + 1, this.getEnd())).intern());
        lexicalAnalyzer.next();
        return this;
    }
    
}
