/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.LexicalError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;

import java.util.Arrays;

public class IdentifierToken extends AbstractToken
{
    
    public IdentifierToken(final String content, final int start, final int end) {
        super();
        
        this.setTokenContent(content);
        this.setStart(start);
        this.setEnd(end);
    }
    
    
    public IdentifierToken() {
        this.setTokenType(TokenType.IDENTIFIER);
    }
    
    
    @Override
    public IdentifierToken lex(final LexicalAnalyzer lexicalAnalyzer) {
        final char currentChar = lexicalAnalyzer.currentChar();
        if (!Character.isJavaIdentifierStart(currentChar)) {
            lexicalAnalyzer.errorHandler().addError(new LexicalError(lexicalAnalyzer.getArkoiClass(), lexicalAnalyzer.getPosition(), "Couldn't lex the Identifier because it doesn't start with an alphabetic char."));
            return null;
        } else lexicalAnalyzer.next();
        
        this.setStart(lexicalAnalyzer.getPosition() - 1);
        while(lexicalAnalyzer.getPosition() < lexicalAnalyzer.getArkoiClass().getContent().length) {
            if(!Character.isUnicodeIdentifierPart(lexicalAnalyzer.currentChar())) {
                this.setEnd(lexicalAnalyzer.getPosition() - 1);
                break;
            } else lexicalAnalyzer.next();
        }
    
        this.setTokenContent(new String(Arrays.copyOfRange(lexicalAnalyzer.getArkoiClass().getContent(), this.getStart(), this.getEnd() + 1)).intern());
        return this;
    }
    
}
