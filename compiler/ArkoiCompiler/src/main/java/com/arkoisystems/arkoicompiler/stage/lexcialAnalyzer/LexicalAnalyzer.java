/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.CharError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LexicalAnalyzer extends AbstractStage
{
    
    private final ArkoiClass arkoiClass;
    
    @Expose
    private final LexicalErrorHandler errorHandler;
    
    private final char[] content;
    
    @Expose
    private AbstractToken[] tokens;
    
    private int position;
    
    public LexicalAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new LexicalErrorHandler();
        this.content = arkoiClass.getContent().toCharArray();
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        final List<AbstractToken> tokens = new ArrayList<>();
        while (this.position < this.content.length) {
            final char currentChar = this.currentChar();
            switch (currentChar) {
                case 0x0c:
                case 0x0a:
                case 0x20:
                case 0x0d:
                case 0x09:
                case 0x0b:
                    this.next();
                    break;
                case '#': {
                    final CommentToken commentToken = new CommentToken().parse(this);
                    if (commentToken == null)
                        return false;
                    break;
                }
                case '@':
                case ':':
                case ';':
                case '{':
                case '}':
                case '(':
                case ')':
                case '[':
                case ']':
                case ',':
                case '<':
                case '>':
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                case '!':
                case '=':
                case '&': {
                    final SymbolToken symbolToken = new SymbolToken().parse(this);
                    if (symbolToken == null)
                        return false;
                    tokens.add(symbolToken);
                    break;
                }
                case '"': {
                    final StringToken stringToken = new StringToken().parse(this);
                    if (stringToken == null)
                        return false;
                    tokens.add(stringToken);
                    break;
                }
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
                case '.': {
                    final NumberToken numberToken = new NumberToken().parse(this);
                    if (numberToken == null) {
                        final SymbolToken symbolToken = new SymbolToken().parse(this);
                        if (symbolToken == null)
                            return false;
                        tokens.add(symbolToken);
                        break;
                    } else tokens.add(numberToken);
                    break;
                }
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
                case 'g':
                case 'G':
                case 'h':
                case 'H':
                case 'i':
                case 'I':
                case 'j':
                case 'J':
                case 'k':
                case 'K':
                case 'l':
                case 'L':
                case 'm':
                case 'M':
                case 'n':
                case 'N':
                case 'o':
                case 'O':
                case 'p':
                case 'P':
                case 'q':
                case 'Q':
                case 'r':
                case 'R':
                case 's':
                case 'S':
                case 't':
                case 'T':
                case 'u':
                case 'U':
                case 'v':
                case 'V':
                case 'w':
                case 'W':
                case 'x':
                case 'X': {
                    final IdentifierToken identifierToken = new IdentifierToken().parse(this);
                    if (identifierToken == null)
                        return false;
                    tokens.add(identifierToken);
                    break;
                }
                default:
                    this.getErrorHandler().addError(new CharError(currentChar, this.position, "Couldn't lex this file because it contains an unknown character."));
                    return false;
            }
        }
        tokens.add(new EndOfFileToken());
        this.tokens = tokens.toArray(new AbstractToken[] { });
        return true;
    }
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    public void next(final int positions) {
        this.position += positions;
        
        if (this.position >= this.content.length)
            this.position = this.content.length;
    }
    
    public void next() {
        this.position++;
        
        if (this.position >= this.content.length)
            this.position = this.content.length;
    }
    
    public char peekChar(final int offset) {
        if (this.position + offset >= this.content.length)
            return this.content[this.content.length - 1];
        return this.content[this.position + offset];
    }
    
    public char currentChar() {
        if (this.position >= this.content.length)
            return this.content[this.content.length - 1];
        return this.content[this.position];
    }
    
    public void undo() {
        this.position--;
        if (this.position < 0)
            this.position = 0;
    }
    
}
