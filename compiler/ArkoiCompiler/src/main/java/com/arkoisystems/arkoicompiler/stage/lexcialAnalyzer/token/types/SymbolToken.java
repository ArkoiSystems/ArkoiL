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
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public class SymbolToken extends AbstractToken
{
    
    @Getter
    @Setter
    private SymbolType symbolType;
    
    
    public SymbolToken(final LexicalAnalyzer lexicalAnalyzer, final SymbolType symbolType) {
        super(lexicalAnalyzer, TokenType.SYMBOL);
        
        this.symbolType = symbolType;
        
        this.setStart(-1);
        this.setEnd(-1);
    }
    
    
    public SymbolToken(final LexicalAnalyzer lexicalAnalyzer) {
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
    
}
