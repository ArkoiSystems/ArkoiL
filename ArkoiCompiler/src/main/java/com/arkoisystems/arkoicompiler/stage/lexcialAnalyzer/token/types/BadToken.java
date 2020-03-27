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

import java.util.Optional;

public class BadToken extends AbstractToken
{
    
    protected BadToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.BAD, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<BadToken> parseToken() {
        return Optional.of(this);
    }
    
    
    public static BadTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new BadTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static BadTokenBuilder builder() {
        return new BadTokenBuilder();
    }
    
    
    public static class BadTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public BadTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public BadTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public BadTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public BadTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public BadTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public BadTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public BadToken build() {
            final BadToken commentToken = new BadToken(this.lexicalAnalyzer, this.crashOnAccess);
            if (this.tokenContent != null)
                commentToken.setTokenContent(this.tokenContent);
            commentToken.setStart(this.start);
            commentToken.setEnd(this.end);
            return commentToken;
        }
        
    }
    
}