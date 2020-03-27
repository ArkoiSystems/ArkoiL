/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class OperatorToken extends AbstractToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private OperatorType operatorType;
    
    
    protected OperatorToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.OPERATOR, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
        
        if (this.getLexicalAnalyzer().currentChar() == '=') {
            this.setOperatorType(OperatorType.EQUALS);
            this.setStart(this.getLexicalAnalyzer().getPosition());
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '+') {
            this.setStart(this.getLexicalAnalyzer().getPosition());
            
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.PLUS_EQUALS);
            } else if (this.getLexicalAnalyzer().peekChar(1) == '+') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.PLUS_PLUS);
            } else this.setOperatorType(OperatorType.PLUS);
            
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '-') {
            this.setStart(this.getLexicalAnalyzer().getPosition());
            
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.MINUS_EQUALS);
            } else if (this.getLexicalAnalyzer().peekChar(1) == '-') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.MINUS_MINUS);
            } else this.setOperatorType(OperatorType.MINUS);
            
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '*') {
            this.setStart(this.getLexicalAnalyzer().getPosition());
            
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.ASTERISK_EQUALS);
            } else if (this.getLexicalAnalyzer().peekChar(1) == '*') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.ASTERISK_ASTERISK);
            } else this.setOperatorType(OperatorType.ASTERISK);
            
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '/') {
            this.setStart(this.getLexicalAnalyzer().getPosition());
            
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.DIV_EQUALS);
            } else this.setOperatorType(OperatorType.DIV);
            
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '%') {
            this.setStart(this.getLexicalAnalyzer().getPosition());
            
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.PERCENT_EQUALS);
            } else this.setOperatorType(OperatorType.PERCENT);
            
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        } else {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex this operator because the character is unknown."
            );
            return BadToken
                    .builder()
                    .start(this.getLexicalAnalyzer().getPosition())
                    .end(this.getLexicalAnalyzer().getPosition() + 1)
                    .build()
                    .parseToken();
        }
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        this.getLexicalAnalyzer().next();
        return Optional.of(this);
    }
    
    
    public static OperatorTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new OperatorTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static OperatorTokenBuilder builder() {
        return new OperatorTokenBuilder();
    }
    
    
    public static class OperatorTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private OperatorType operatorType;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public OperatorTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public OperatorTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public OperatorTokenBuilder type(final OperatorType operatorType) {
            this.operatorType = operatorType;
            return this;
        }
        
        
        public OperatorTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public OperatorTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public OperatorTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public OperatorTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public OperatorToken build() {
            final OperatorToken operatorToken = new OperatorToken(this.lexicalAnalyzer, this.crashOnAccess);
            if (this.tokenContent != null)
                operatorToken.setTokenContent(this.tokenContent);
            if (this.operatorType != null)
                operatorToken.setOperatorType(this.operatorType);
            operatorToken.setStart(this.start);
            operatorToken.setEnd(this.end);
            return operatorToken;
        }
        
    }
    
}