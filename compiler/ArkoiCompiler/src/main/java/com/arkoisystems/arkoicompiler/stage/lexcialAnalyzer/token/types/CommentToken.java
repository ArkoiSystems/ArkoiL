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

public class CommentToken extends AbstractToken
{
    
    public CommentToken() {
        this.setTokenType(TokenType.COMMENT);
    }
    
    
    @Override
    public CommentToken lex(final LexicalAnalyzer lexicalAnalyzer) {
        if (lexicalAnalyzer.currentChar() != '#') {
            lexicalAnalyzer.getErrorHandler().addError(new LexicalError(lexicalAnalyzer.getArkoiClass(), lexicalAnalyzer.getPosition(), "Couldn't lex this comment because it doesn't start with an \"#\"."));
            return null;
        }
    
        this.setStart(lexicalAnalyzer.getPosition());
        while (lexicalAnalyzer.getPosition() < lexicalAnalyzer.getArkoiClass().getContent().length) {
            final char currentChar = lexicalAnalyzer.currentChar();
            lexicalAnalyzer.next();
        
            if(currentChar == 0x0a)
                break;
        }
        this.setEnd(lexicalAnalyzer.getPosition());
        
        this.setTokenContent(new String(Arrays.copyOfRange(lexicalAnalyzer.getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        return this;
    }
    
}