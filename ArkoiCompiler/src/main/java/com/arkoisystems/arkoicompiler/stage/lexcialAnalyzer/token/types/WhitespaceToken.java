/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;

import java.util.Arrays;
import java.util.Optional;

public class WhitespaceToken extends AbstractToken
{
    
    protected WhitespaceToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.WHITESPACE);
    }
    
    
    @Override
    public Optional<WhitespaceToken> parseToken() {
        this.setStart(this.getLexicalAnalyzer().getPosition());
        this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        this.getLexicalAnalyzer().next();
        return Optional.of(this);
    }
    
    
    public static WhitespaceTokenBuilder builder(final LexicalAnalyzer lexicalAnalyzer) {
        return new WhitespaceTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static WhitespaceTokenBuilder builder() {
        return new WhitespaceTokenBuilder();
    }
    
    
    public static class WhitespaceTokenBuilder {
        
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        private String tokenContent;
        
        
        private int start, end;
        
        
        public WhitespaceTokenBuilder(final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public WhitespaceTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public WhitespaceTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public WhitespaceTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public WhitespaceTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public WhitespaceToken build() {
            final WhitespaceToken whitespaceToken = new WhitespaceToken(this.lexicalAnalyzer);
            whitespaceToken.setTokenContent(this.tokenContent);
            whitespaceToken.setStart(this.start);
            whitespaceToken.setEnd(this.end);
            return whitespaceToken;
        }
        
    }
    
}