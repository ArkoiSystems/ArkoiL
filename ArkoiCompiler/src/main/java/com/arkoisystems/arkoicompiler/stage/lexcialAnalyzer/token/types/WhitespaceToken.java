/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;

import java.util.Arrays;
import java.util.Optional;

public class WhitespaceToken extends AbstractToken
{
    
    public WhitespaceToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.WHITESPACE);
    }
    
    
    @Override
    public Optional<WhitespaceToken> parseToken() {
        this.setStart(this.getLexicalAnalyzer().getPosition());
        this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        this.getLexicalAnalyzer().next();
        return Optional.of(this);
    }
    
}