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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KeywordToken extends AbstractToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private KeywordType keywordType;
    
    
    protected KeywordToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.KEYWORD, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        switch (this.getTokenContent()) {
            case "this":
                this.setKeywordType(KeywordType.THIS);
                return Optional.of(this);
            case "var":
                this.setKeywordType(KeywordType.VAR);
                return Optional.of(this);
            case "return":
                this.setKeywordType(KeywordType.RETURN);
                return Optional.of(this);
            case "fun":
                this.setKeywordType(KeywordType.FUN);
                return Optional.of(this);
            case "as":
                this.setKeywordType(KeywordType.AS);
                return Optional.of(this);
            case "import":
                this.setKeywordType(KeywordType.IMPORT);
                return Optional.of(this);
            default:
                return Optional.empty();
        }
    }
    
    
    public static KeywordTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new KeywordTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static KeywordTokenBuilder builder() {
        return new KeywordTokenBuilder();
    }
    
    
    public static class KeywordTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private KeywordType keywordType;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public KeywordTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public KeywordTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public KeywordTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public KeywordTokenBuilder type(final KeywordType keywordType) {
            this.keywordType = keywordType;
            return this;
        }
        
        
        public KeywordTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public KeywordTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public KeywordTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public KeywordToken build() {
            final KeywordToken keywordToken = new KeywordToken(this.lexicalAnalyzer, this.crashOnAccess);
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