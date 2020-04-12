/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class IdentifierToken extends AbstractToken
{
    
    protected IdentifierToken(@Nullable final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.IDENTIFIER);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
    
        final char currentChar = this.getLexicalAnalyzer().currentChar();
        if (!Character.isJavaIdentifierStart(currentChar))
            return this.addError(
                    BadToken.builder(this.getLexicalAnalyzer())
                            .start(this.getLexicalAnalyzer().getPosition())
                            .end(this.getLexicalAnalyzer().getPosition() + 1)
                            .build()
                            .parseToken(),
                
                    this.getLexicalAnalyzer().getCompilerClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the Identifier because it doesn't start with an alphabetic char."
            );
        
        this.getLexicalAnalyzer().next();
    
        this.setStart(this.getLexicalAnalyzer().getPosition() - 1);
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getCompilerClass().getContent().length) {
            if (!Character.isUnicodeIdentifierPart(this.getLexicalAnalyzer().currentChar()))
                break;
            this.getLexicalAnalyzer().next();
        }
    
        this.setEnd(this.getLexicalAnalyzer().getPosition());
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getCompilerClass().getContent(), this.getStart(), this.getEnd())).intern());
        
        final Optional<? extends AbstractToken> optionalKeywordToken = KeywordToken
                .builder(this.getLexicalAnalyzer())
                .content(this.getTokenContent())
                .start(this.getStart())
                .end(this.getEnd())
                .build()
                .parseToken();
        if (optionalKeywordToken.isPresent())
            return optionalKeywordToken;
        
        final Optional<? extends AbstractToken> optionalTypeKeywordToken = TypeKeywordToken
                .builder(this.getLexicalAnalyzer())
                .content(this.getTokenContent())
                .start(this.getStart())
                .end(this.getEnd())
                .build()
                .parseToken();
        if (optionalTypeKeywordToken.isPresent())
            return optionalTypeKeywordToken;
        return Optional.of(this);
    }
    
    
    public static IdentifierTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new IdentifierTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static IdentifierTokenBuilder builder() {
        return new IdentifierTokenBuilder();
    }
    
    
    public static class IdentifierTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public IdentifierTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
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
            if (this.tokenContent != null)
                identifierToken.setTokenContent(this.tokenContent);
            identifierToken.setStart(this.start);
            identifierToken.setEnd(this.end);
            return identifierToken;
        }
        
    }
    
}
