/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TypeKeywordType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TypeKeywordToken extends AbstractToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private TypeKeywordType keywordType;
    
    
    protected TypeKeywordToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.TYPE_KEYWORD, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        switch (this.getTokenContent()) {
            case "char":
                this.setKeywordType(TypeKeywordType.CHAR);
                return Optional.of(this);
            case "boolean":
                this.setKeywordType(TypeKeywordType.BOOLEAN);
                return Optional.of(this);
            case "byte":
                this.setKeywordType(TypeKeywordType.BYTE);
                return Optional.of(this);
            case "int":
                this.setKeywordType(TypeKeywordType.INT);
                return Optional.of(this);
            case "long":
                this.setKeywordType(TypeKeywordType.LONG);
                return Optional.of(this);
            case "short":
                this.setKeywordType(TypeKeywordType.SHORT);
                return Optional.of(this);
            case "string":
                this.setKeywordType(TypeKeywordType.STRING);
                return Optional.of(this);
            default:
                return Optional.empty();
        }
    }
    
    
    public static TypeKeywordTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new TypeKeywordTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static TypeKeywordTokenBuilder builder() {
        return new TypeKeywordTokenBuilder();
    }
    
    
    public static class TypeKeywordTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private TypeKeywordType keywordType;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public TypeKeywordTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public TypeKeywordTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public TypeKeywordTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder type(final TypeKeywordType keywordType) {
            this.keywordType = keywordType;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public TypeKeywordToken build() {
            final TypeKeywordToken keywordToken = new TypeKeywordToken(this.lexicalAnalyzer, this.crashOnAccess);
            if (this.tokenContent != null)
                keywordToken.setTokenContent(this.tokenContent);
            if (this.keywordType != null)
                keywordToken.setKeywordType(this.keywordType);
            keywordToken.setStart(this.start);
            keywordToken.setEnd(this.end);
            return keywordToken;
        }
        
    }
    
}