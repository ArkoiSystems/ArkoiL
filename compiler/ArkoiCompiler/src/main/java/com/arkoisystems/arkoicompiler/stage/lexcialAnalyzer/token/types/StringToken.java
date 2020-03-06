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

public class StringToken extends AbstractToken
{
    
    public StringToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.STRING_LITERAL);
    }
    
    
    @Override
    public StringToken parseToken() {
        if (this.getLexicalAnalyzer().currentChar() != '"') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the string because it doesn't start with an \"."
            );
            return null;
        } else this.getLexicalAnalyzer().next();
        
        this.setStart(this.getLexicalAnalyzer().getPosition() - 1);
        char lastChar = ' ';
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
            final char currentChar = this.getLexicalAnalyzer().currentChar();
            if (lastChar != '\\' && currentChar == '"') {
                this.setEnd(this.getLexicalAnalyzer().getPosition());
                break;
            } else if (currentChar == 0x0A || currentChar == 0x0D)
                break;
            lastChar = currentChar;
            this.getLexicalAnalyzer().next();
        }
        
        if (this.getLexicalAnalyzer().currentChar() != '"') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getStart(),
                    this.getLexicalAnalyzer().getPosition(),
                    "The defined string doesn't end with another double quote:"
            );
            return null;
        }
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart() + 1, this.getEnd())).intern());
        this.getLexicalAnalyzer().next();
        return this;
    }
    
}
