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

public class CommentToken extends AbstractToken
{
    
    public CommentToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.COMMENT);
    }
    
    
    @Override
    public Optional<CommentToken> parseToken() {
        if (this.getLexicalAnalyzer().currentChar() != '#') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex this comment because it doesn't start with an \"#\"."
            );
            return Optional.empty();
        }
        
        this.setStart(this.getLexicalAnalyzer().getPosition());
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
            final char currentChar = this.getLexicalAnalyzer().currentChar();
            this.getLexicalAnalyzer().next();
            
            if (currentChar == 0x0a)
                break;
        }
        this.setEnd(this.getLexicalAnalyzer().getPosition());
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        return Optional.of(this);
    }
    
}