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

import java.util.Objects;
import java.util.Optional;

public class EndOfFileToken extends AbstractToken
{
    
    protected EndOfFileToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.END_OF_FILE, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<EndOfFileToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
        
        this.setTokenContent("EOF");
        this.setStart(0);
        this.setEnd(this.getLexicalAnalyzer().getArkoiClass().getContent().length);
        return Optional.of(this);
    }
    
    
    public static EndOfFileTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new EndOfFileTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static EndOfFileTokenBuilder builder() {
        return new EndOfFileTokenBuilder();
    }
    
    
    public static class EndOfFileTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public EndOfFileTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public EndOfFileTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public EndOfFileTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public EndOfFileTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public EndOfFileTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public EndOfFileTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public EndOfFileToken build() {
            final EndOfFileToken endOfFileToken = new EndOfFileToken(this.lexicalAnalyzer, this.crashOnAccess);
            if (this.tokenContent != null)
                endOfFileToken.setTokenContent(this.tokenContent);
            endOfFileToken.setStart(this.start);
            endOfFileToken.setEnd(this.end);
            return endOfFileToken;
        }
        
    }
    
}