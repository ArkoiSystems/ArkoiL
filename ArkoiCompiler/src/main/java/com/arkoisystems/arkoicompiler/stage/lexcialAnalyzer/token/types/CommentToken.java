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

public class CommentToken extends AbstractToken
{
    
    protected CommentToken(@Nullable final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.COMMENT);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
        
        if (this.getLexicalAnalyzer().currentChar() != '#') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex this comment because it doesn't start with an \"#\"."
            );
            return BadToken
                    .builder(this.getLexicalAnalyzer())
                    .start(this.getLexicalAnalyzer().getPosition())
                    .end(this.getLexicalAnalyzer().getPosition() + 1)
                    .build()
                    .parseToken();
        }
        
        this.setStart(this.getLexicalAnalyzer().getPosition());
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
            final char currentChar = this.getLexicalAnalyzer().currentChar();
            this.getLexicalAnalyzer().next();
            
            if (currentChar == 0x0a)
                break;
        }
        this.setEnd(this.getLexicalAnalyzer().getPosition());
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        return Optional.of(this);
    }
    
    
    public static CommentTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new CommentTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static CommentTokenBuilder builder() {
        return new CommentTokenBuilder();
    }
    
    
    public static class CommentTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public CommentTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public CommentTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public CommentTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public CommentTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public CommentTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public CommentToken build() {
            final CommentToken commentToken = new CommentToken(this.lexicalAnalyzer);
            if (this.tokenContent != null)
                commentToken.setTokenContent(this.tokenContent);
            commentToken.setStart(this.start);
            commentToken.setEnd(this.end);
            return commentToken;
        }
        
    }
    
}