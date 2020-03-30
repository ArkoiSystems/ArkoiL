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

public class StringToken extends AbstractToken
{
    
    protected StringToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.STRING_LITERAL, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
    
        if (this.getLexicalAnalyzer().currentChar() != '"') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the string because it doesn't start with an \"."
            );
            return BadToken
                    .builder()
                    .start(this.getLexicalAnalyzer().getPosition())
                    .end(this.getLexicalAnalyzer().getPosition() + 1)
                    .build()
                    .parseToken();
        }
    
        this.setStart(this.getLexicalAnalyzer().getPosition());
        this.getLexicalAnalyzer().next();
        
        char lastChar = ' ';
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
            final char currentChar = this.getLexicalAnalyzer().currentChar();
            if (lastChar != '\\' && currentChar == '"')
                break;
            else if (currentChar == 0x0A || currentChar == 0x0D)
                break;
            lastChar = currentChar;
            this.getLexicalAnalyzer().next();
        }
        this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        
        if (this.getLexicalAnalyzer().currentChar() != '"') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getStart(),
                    this.getLexicalAnalyzer().getPosition(),
                    "The defined string doesn't end with another double quote:"
            );
            return BadToken
                    .builder()
                    .start(this.getStart())
                    .end(this.getEnd())
                    .build()
                    .parseToken();
        }
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart() + 1, this.getEnd() - 1)).intern());
        this.getLexicalAnalyzer().next();
        return Optional.of(this);
    }
    
    
    public static StringTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new StringTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static StringTokenBuilder builder() {
        return new StringTokenBuilder();
    }
    
    
    public static class StringTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public StringTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public StringTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public StringTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public StringTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public StringTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public StringTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public StringToken build() {
            final StringToken stringToken = new StringToken(this.lexicalAnalyzer, this.crashOnAccess);
            if (this.tokenContent != null)
                stringToken.setTokenContent(this.tokenContent);
            stringToken.setStart(this.start);
            stringToken.setEnd(this.end);
            return stringToken;
        }
        
    }
    
}
