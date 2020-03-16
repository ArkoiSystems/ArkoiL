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

public class IdentifierToken extends AbstractToken
{
    
    public IdentifierToken(final LexicalAnalyzer lexicalAnalyzer, final String content, final int start, final int end) {
        super(lexicalAnalyzer, TokenType.IDENTIFIER);
        
        this.setTokenContent(content);
        this.setStart(start);
        this.setEnd(end);
    }
    
    
    public IdentifierToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.IDENTIFIER);
    }
    
    
    @Override
    public Optional<IdentifierToken> parseToken() {
        final char currentChar = this.getLexicalAnalyzer().currentChar();
        if (!Character.isJavaIdentifierStart(currentChar)) {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the Identifier because it doesn't start with an alphabetic char."
            );
            return Optional.empty();
        } else this.getLexicalAnalyzer().next();
        
        this.setStart(this.getLexicalAnalyzer().getPosition() - 1);
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
            if (!Character.isUnicodeIdentifierPart(this.getLexicalAnalyzer().currentChar())) {
                this.setEnd(this.getLexicalAnalyzer().getPosition());
                break;
            } else this.getLexicalAnalyzer().next();
        }
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        return Optional.of(this);
    }
    
}
