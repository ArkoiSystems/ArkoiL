/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@Setter
public class NumberToken extends AbstractToken
{
    
    public NumberToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.NUMBER_LITERAL);
    }
    
    
    @Override
    public Optional<NumberToken> parseToken() {
        if (!Character.isDigit(this.getLexicalAnalyzer().currentChar()) && this.getLexicalAnalyzer().currentChar() != '.') {
            this.addError(
                    this.getLexicalAnalyzer().getArkoiClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the number because it doesn't start with a digit or dot."
            );
            return Optional.empty();
        } else this.setStart(this.getLexicalAnalyzer().getPosition());
        
        if (this.getLexicalAnalyzer().currentChar() == '0' && this.getLexicalAnalyzer().peekChar(1) == 'x') {
            this.getLexicalAnalyzer().next(2);
            for (int i = 0; i < 8; i++) {
                final int currentChar = this.getLexicalAnalyzer().currentChar();
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
            while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
                final int currentChar = this.getLexicalAnalyzer().currentChar();
                if (!Character.isDigit(currentChar))
                    break;
                this.getLexicalAnalyzer().next();
            }
            
            if (this.getLexicalAnalyzer().currentChar() == '.') {
                this.getLexicalAnalyzer().next();
                
                while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getArkoiClass().getContent().length) {
                    final int currentChar = this.getLexicalAnalyzer().currentChar();
                    if (!Character.isDigit(currentChar))
                        break;
                    this.getLexicalAnalyzer().next();
                }
            }
        }
        this.setEnd(this.getLexicalAnalyzer().getPosition());
        
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getArkoiClass().getContent(), this.getStart(), this.getEnd())).intern());
        if (this.getTokenContent().equals(".")) {
            this.getLexicalAnalyzer().undo();
            return Optional.empty();
        }
        return Optional.of(this);
    }
    
}