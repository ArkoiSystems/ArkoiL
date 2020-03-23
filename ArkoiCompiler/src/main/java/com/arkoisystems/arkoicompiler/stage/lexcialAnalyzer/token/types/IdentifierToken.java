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

public class IdentifierToken extends AbstractToken
{
    
    protected IdentifierToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.IDENTIFIER);
    }
    
    
    @Override
    public Optional<IdentifierToken> parseToken() {
        final char currentChar = this.getLexicalAnalyzer().currentChar();
        if (!Character.isJavaIdentifierStart(currentChar)) {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the Identifier because it doesn't start with an alphabetic char."
            );
            return Optional.empty();
        } else this.getLexicalAnalyzer().next();
        
        this.setStart(this.getLexicalAnalyzer().getPosition() - 1);
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
            if (!Character.isUnicodeIdentifierPart(this.getLexicalAnalyzer().currentChar())) {
                this.setEnd(this.getLexicalAnalyzer().getPosition());
                break;
            } else this.getLexicalAnalyzer().next();
        }
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        return Optional.of(this);
    }
    
    
    public static IdentifierTokenBuilder builder(final LexicalAnalyzer lexicalAnalyzer) {
        return new IdentifierTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static IdentifierTokenBuilder builder() {
        return new IdentifierTokenBuilder();
    }
    
    
    public static class IdentifierTokenBuilder {
        
        private final LexicalAnalyzer lexicalAnalyzer;
    
        
        private String tokenContent;
        
        
        private int start, end;
        
        
        public IdentifierTokenBuilder(final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
    
        public IdentifierTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public IdentifierTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public IdentifierTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public IdentifierTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public IdentifierToken build() {
            final IdentifierToken identifierToken = new IdentifierToken(this.lexicalAnalyzer);
            identifierToken.setTokenContent(this.tokenContent);
            identifierToken.setStart(this.start);
            identifierToken.setEnd(this.end);
            return identifierToken;
        }
    
    }
    
}
