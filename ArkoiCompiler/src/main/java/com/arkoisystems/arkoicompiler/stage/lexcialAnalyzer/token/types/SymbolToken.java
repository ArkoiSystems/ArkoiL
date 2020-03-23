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

import java.util.Optional;

public class SymbolToken extends AbstractToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private SymbolType symbolType;
    
    
    protected SymbolToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.SYMBOL);
    }
    
    
    @Override
    public Optional<SymbolToken> parseToken() {
        final char currentChar = this.getLexicalAnalyzer().currentChar();
        this.setTokenContent(String.valueOf(currentChar));
        
        for (final SymbolType symbolType : SymbolType.values())
            if (symbolType.getCharacter() == currentChar) {
                this.setSymbolType(symbolType);
                break;
            }
        
        if (this.getSymbolType() == null) {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex this symbol because it isn't supported."
            );
            return Optional.empty();
        } else {
            this.setStart(this.getLexicalAnalyzer().getPosition());
            this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
            this.getLexicalAnalyzer().next();
        }
        return Optional.of(this);
    }
    
    
    public static SymbolTokenBuilder builder(final LexicalAnalyzer lexicalAnalyzer) {
        return new SymbolTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static SymbolTokenBuilder builder() {
        return new SymbolTokenBuilder();
    }
    
    
    public static class SymbolTokenBuilder {
        
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        private SymbolType symbolType;
        
        
        private String tokenContent;
        
        
        private int start, end;
        
        
        public SymbolTokenBuilder(final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public SymbolTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public SymbolTokenBuilder type(final SymbolType symbolType) {
            this.symbolType = symbolType;
            return this;
        }
        
        public SymbolTokenBuilder content(final String tokenContent) {
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
        
        
        public SymbolToken build() {
            final SymbolToken symbolToken = new SymbolToken(this.lexicalAnalyzer);
            symbolToken.setTokenContent(this.tokenContent);
            symbolToken.setSymbolType(this.symbolType);
            symbolToken.setStart(this.start);
            symbolToken.setEnd(this.end);
            return symbolToken;
        }
        
    }
    
}
