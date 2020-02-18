/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.CharError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;

import java.util.Arrays;
import java.util.regex.Matcher;

public class StringToken extends AbstractToken
{
    
    public StringToken() {
        this.setTokenType(TokenType.STRING_LITERAL);
    }
    
    @Override
    public StringToken parse(final LexicalAnalyzer lexicalAnalyzer) {
        if(lexicalAnalyzer.currentChar() != '"') {
            lexicalAnalyzer.errorHandler().addError(new CharError(lexicalAnalyzer.currentChar(), lexicalAnalyzer.getPosition(), "Couldn't lex the string because it doesn't start with an \"."));
            return null;
        } else lexicalAnalyzer.next();
        
        this.setStart(lexicalAnalyzer.getPosition() - 1);
        while(lexicalAnalyzer.getPosition() < lexicalAnalyzer.getContent().length) {
            final char currentChar = lexicalAnalyzer.currentChar();
            lexicalAnalyzer.next();
            
            if(currentChar == '"')
                break;
        }
        this.setEnd(lexicalAnalyzer.getPosition());
        
        this.setTokenContent(new String(Arrays.copyOfRange(lexicalAnalyzer.getContent(), this.getStart() + 1, this.getEnd() - 1)).intern());
        return this;
    }
    
}
