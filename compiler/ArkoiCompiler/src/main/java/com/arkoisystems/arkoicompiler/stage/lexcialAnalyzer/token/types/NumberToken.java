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
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class NumberToken extends AbstractToken
{
    
    public NumberToken() {
        this.setTokenType(TokenType.NUMBER_LITERAL);
    }
    
    
    @Override
    public NumberToken lex(final LexicalAnalyzer lexicalAnalyzer) {
        if (!Character.isDigit(lexicalAnalyzer.currentChar()) && lexicalAnalyzer.currentChar() != '.') {
            lexicalAnalyzer.errorHandler().addError(new CharError(lexicalAnalyzer.currentChar(), lexicalAnalyzer.getPosition(), "Couldn't lex the number because it doesn't start with a digit or dot."));
            return null;
        } else this.setStart(lexicalAnalyzer.getPosition());
        
        if (lexicalAnalyzer.currentChar() == '0' && lexicalAnalyzer.peekChar(1) == 'x') {
            lexicalAnalyzer.next(2);
            for (int i = 0; i < 8; i++) {
                final int currentChar = lexicalAnalyzer.currentChar();
                switch (currentChar) {
                    case 'a':
                    case 'A':
                    case 'b':
                    case 'B':
                    case 'c':
                    case 'C':
                    case 'd':
                    case 'D':
                    case 'e':
                    case 'E':
                    case 'f':
                    case 'F':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        continue;
                    default:
                        break;
                }
            }
        } else {
            while (lexicalAnalyzer.getPosition() < lexicalAnalyzer.getContent().length) {
                final int currentChar = lexicalAnalyzer.currentChar();
                if (!Character.isDigit(currentChar))
                    break;
                lexicalAnalyzer.next();
            }
            
            if (lexicalAnalyzer.currentChar() == '.') {
                lexicalAnalyzer.next();
                
                while (lexicalAnalyzer.getPosition() < lexicalAnalyzer.getContent().length) {
                    final int currentChar = lexicalAnalyzer.currentChar();
                    if (!Character.isDigit(currentChar))
                        break;
                    lexicalAnalyzer.next();
                }
            }
        }
        this.setEnd(lexicalAnalyzer.getPosition());
        
        this.setTokenContent(new String(Arrays.copyOfRange(lexicalAnalyzer.getContent(), this.getStart(), this.getEnd())).intern());
        if (this.getTokenContent().equals(".")) {
            lexicalAnalyzer.undo();
            return null;
        } else return this;
    }
    
}