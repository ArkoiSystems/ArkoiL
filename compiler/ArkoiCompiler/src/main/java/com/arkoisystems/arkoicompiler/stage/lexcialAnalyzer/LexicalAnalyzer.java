/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.CharError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an implementation of the {@link AbstractStage}. It will lex a list of
 * {@link AbstractToken}'s with the help of the content from the {@link ArkoiClass}.
 */
public class LexicalAnalyzer extends AbstractStage
{
    
    /**
     * This defines the {@link ArkoiClass} in which the {@link LexicalAnalyzer} got
     * created.
     */
    @Getter
    private final ArkoiClass arkoiClass;
    
    
    /**
     * The {@link LexicalErrorHandler} is used for errors which are happening when lexing
     * the input content.
     */
    @Getter
    private final LexicalErrorHandler errorHandler;
    
    
    /**
     * The input content of the {@link ArkoiClass} which got translated to an {@code
     * char[]} for better access (peeking, next, current etc).
     */
    @Getter
    private final char[] content;
    
    
    /**
     * This {@link AbstractToken}[] is used to access the lexed {@link AbstractToken}'s
     * like in the {@link SyntaxAnalyzer} for methods like {@link
     * SyntaxAnalyzer#matchesNextToken(SymbolToken.SymbolType)}.
     */
    @Getter
    private AbstractToken[] tokens;
    
    
    /**
     * The current position in the {@link LexicalAnalyzer#content} array. This position is
     * used to peek and get tokens (e.g. {@link #next()} or {@link #peekChar(int)}).
     */
    @Getter
    private int position;
    
    
    /**
     * Constructs a new {@link LexicalAnalyzer} with the given {@link ArkoiClass} to get
     * the content or even the {@link ArkoiCompiler}.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} in which the {@link LexicalAnalyzer} gets
     *         constructed.
     */
    public LexicalAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new LexicalErrorHandler();
        this.content = arkoiClass.getContent().toCharArray();
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will run this stage.
     * The lexer will go through every position in the {@link #content} array. It will
     * skip newlines (and every other "whitespace" character e.g. 0x0c, 0x0a and 0x0d).
     * Comments are getting lexed but they don't get added to the tokens list because they
     * are irrelevant for the later process and are just used for the developers.
     *
     * @return {@code false} if an error occurred or {@code true} if everything worked
     *         correctly.
     */
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
                    final CommentToken commentToken = new CommentToken().lex(this);
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
                    final SymbolToken symbolToken = new SymbolToken().lex(this);
                    if (symbolToken == null)
                        return false;
                    tokens.add(symbolToken);
                    break;
                }
                case '"': {
                    final StringToken stringToken = new StringToken().lex(this);
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
                    final NumberToken numberToken = new NumberToken().lex(this);
                    if (numberToken == null) {
                        final SymbolToken symbolToken = new SymbolToken().lex(this);
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
                    final IdentifierToken identifierToken = new IdentifierToken().lex(this);
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
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will return the
     * created {@link LexicalErrorHandler}.
     *
     * @return the {@link LexicalErrorHandler} which got created in the constructor.
     */
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    
    /**
     * This method will skip the next specified positions from the current location
     * ({@link LexicalAnalyzer#position}. This method is helpful if you don't want to copy
     * & paste {@link #next()} x times until you got your result.
     *
     * @param positions
     *         the positions which are getting added to our current position {@link
     *         #position}.
     */
    public void next(final int positions) {
        this.position += positions;
        
        if (this.position >= this.content.length)
            this.position = this.content.length;
    }
    
    
    /**
     * This method will go to the next position and checks if it went out ouf bounds. If
     * it did, it will set the current position {@link #position} to the last possible
     * position from the {@link #content} array.
     */
    public void next() {
        this.position++;
        
        if (this.position >= this.content.length)
            this.position = this.content.length;
    }
    
    
    /**
     * Differently to the {@link #next(int)} method this method will just peek the next
     * token with the specified offset and so it won't change the current {@link
     * #position}.
     *
     * @param offset
     *         the offset which should get added to the current position {@link
     *         #position}. Keep in mind, that it won't change the position and just
     *         returns the peeked token.
     *
     * @return the peeked token if it didn't went out of bounds. If it did, it will return
     *         the last possible char in the {@link #content} array.
     */
    public char peekChar(final int offset) {
        if (this.position + offset >= this.content.length)
            return this.content[this.content.length - 1];
        return this.content[this.position + offset];
    }
    
    
    /**
     * Returns the current char and checks if the position went out of bounds. If the
     * current {@link #position} went out of bounds, it will simply return the last
     * possible char of the {@link #content} array.
     *
     * @return the current token if it didn't went out of bounds. If it did, it will
     *         return the last possible char in the {@link #content} array.
     */
    public char currentChar() {
        if (this.position >= this.content.length)
            return this.content[this.content.length - 1];
        return this.content[this.position];
    }
    
    
    /**
     * Returns to the last positions (subtracts one from the current {@link #position}).
     * Also it will check if it went out of bounds (below 0) and will reset itself to 0 if
     * it did.
     */
    public void undo() {
        this.position--;
        if (this.position < 0)
            this.position = 0;
    }
    
}
