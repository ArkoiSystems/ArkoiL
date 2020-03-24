/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SymbolToken extends AbstractToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private SymbolType symbolType;
    
    
    protected SymbolToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, final boolean crashOnAccess) {
        super(lexicalAnalyzer, TokenType.SYMBOL, crashOnAccess);
    }
    
    
    @NotNull
    @Override
    public Optional<SymbolToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
        
        final char currentChar = this.getLexicalAnalyzer().currentChar();
        this.setTokenContent(String.valueOf(currentChar));
        
        SymbolType symbolType = null;
        for (final SymbolType type : SymbolType.values())
            if (type.getCharacter() == currentChar) {
                symbolType = type;
                break;
            }
        
        if (symbolType == null) {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex this symbol because it isn't supported."
            );
            return Optional.empty();
        } else {
            this.symbolType = symbolType;
            this.setStart(this.getLexicalAnalyzer().getPosition());
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
            this.getLexicalAnalyzer().next();
        }
        return Optional.of(this);
    }
    
    
    public static SymbolTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new SymbolTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static SymbolTokenBuilder builder() {
        return new SymbolTokenBuilder();
    }
    
    
    public static class SymbolTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        private boolean crashOnAccess;
        
        
        @Nullable
        private SymbolType symbolType;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public SymbolTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public SymbolTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public SymbolTokenBuilder type(@NotNull final SymbolType symbolType) {
            this.symbolType = symbolType;
            return this;
        }
        
        
        public SymbolTokenBuilder content(@NotNull final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public SymbolTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public SymbolTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public SymbolTokenBuilder crash() {
            this.crashOnAccess = true;
            return this;
        }
        
        
        public SymbolToken build() {
            final SymbolToken symbolToken = new SymbolToken(this.lexicalAnalyzer, this.crashOnAccess);
            if (this.tokenContent != null)
                symbolToken.setTokenContent(this.tokenContent);
            if (this.symbolType != null)
                symbolToken.setSymbolType(this.symbolType);
            symbolToken.setStart(this.start);
            symbolToken.setEnd(this.end);
            return symbolToken;
        }
        
    }
    
}
