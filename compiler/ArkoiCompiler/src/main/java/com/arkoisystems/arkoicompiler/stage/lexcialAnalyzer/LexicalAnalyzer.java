/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an implementation of the {@link AbstractStage}. It will lex a list of
 * {@link AbstractToken}s with the help of the content from the {@link ArkoiClass}.
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
     * This {@link AbstractToken}[] is used to access the parsed {@link AbstractToken}s
     * like in the {@link SyntaxAnalyzer} for methods like {@link
     * SyntaxAnalyzer#matchesNextToken(SymbolType)}.
     */
    @Getter
    private AbstractToken[] tokens;
    
    
    /**
     * The current position in the {@link ArkoiClass#content} array. This position is used
     * to peek and get tokens (e.g. {@link #next()} or {@link #peekChar(int)}).
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
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will run this stage.
     * The lexer will go through every position in the {@link ArkoiClass#content} array. It will
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
        while (this.position < this.getArkoiClass().getContent().length) {
            final char currentChar = this.currentChar();
            if (Character.isWhitespace(currentChar)) {
                final WhitespaceToken whitespaceToken = new WhitespaceToken(this).parseToken();
                if (whitespaceToken != null) {
                    tokens.add(whitespaceToken);
                    continue;
                }
                continue;
            } else if (currentChar == '#') {
                final CommentToken commentToken = new CommentToken(this).parseToken();
                if (commentToken != null)
                    continue;
            } else if (currentChar == '"') {
                final StringToken stringToken = new StringToken(this).parseToken();
                if (stringToken != null) {
                    tokens.add(stringToken);
                    continue;
                }
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
                final NumberToken numberToken = new NumberToken(this).parseToken();
                if (numberToken != null) {
                    tokens.add(numberToken);
                    continue;
                }
        
                final SymbolToken symbolToken = new SymbolToken(this).parseToken();
                if (symbolToken != null) {
                    tokens.add(symbolToken);
                    continue;
                }
            } else if (Character.isJavaIdentifierStart(currentChar)) {
                final IdentifierToken identifierToken = new IdentifierToken(this).parseToken();
                if (identifierToken != null) {
                    tokens.add(identifierToken);
                    continue;
                }
            } else {
                switch (currentChar) {
                    case '@':
                    case '^':
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
                        final SymbolToken symbolToken = new SymbolToken(this).parseToken();
                        if (symbolToken != null) {
                            tokens.add(symbolToken);
                            continue;
                        }
                        break;
                    }
                    default:
                        this.getErrorHandler().addError(new ArkoiError(
                                this.getArkoiClass(),
                                this.position,
                                "The defined character is unknown for the lexical analyzer:"
                        ));
                        break;
                }
            }
    
            this.setFailedStage(true);
            this.next();
        }
    
        tokens.add(new EndOfFileToken(this));
        this.tokens = tokens.toArray(new AbstractToken[] { });
        return !this.isFailedStage();
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will return the
     * created {@link LexicalErrorHandler}.
     *
     * @return the {@link LexicalErrorHandler} which got created in the constructor.
     */
    @Override
    public LexicalErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    
    /**
     * Returns an array full of {@link TokenType}s which you can use for testing out the
     * {@link LexicalAnalyzer}. Also you can set if {@link TokenType#WHITESPACE} should be
     * inside the array or not.
     *
     * @param whitespaces
     *         the flag if the {@link TokenType#WHITESPACE} should be inside the array or
     *         not.
     *
     * @return an array full of {@link TokenType}s.
     */
    public TokenType[] getTokenTypes(final boolean whitespaces) {
        final List<TokenType> tokenTypes = new ArrayList<>();
        for (final AbstractToken abstractToken : this.getTokens()) {
            if (!whitespaces && abstractToken.getTokenType() == TokenType.WHITESPACE)
                continue;
            tokenTypes.add(abstractToken.getTokenType());
        }
        return tokenTypes.toArray(new TokenType[] { });
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
        
        if (this.position >= this.getArkoiClass().getContent().length)
            this.position = this.getArkoiClass().getContent().length;
    }
    
    
    /**
     * This method will go to the next position and checks if it went out ouf bounds. If
     * it did, it will set the current position {@link #position} to the last possible
     * position from the {@link #content} array.
     */
    public void next() {
        this.position++;
        
        if (this.position >= this.getArkoiClass().getContent().length)
            this.position = this.getArkoiClass().getContent().length;
    }
    
    
    /**
     * Differently to the {@link #next(int)} method this method will just peek the next
     * token with the specified offset and so it won't change the current {@link
     * #position}.
     *
     * @param offset
     *         the offset which is used added to the current position {@link
     *         #position}. Keep in mind, that it won't change the position and just
     *         returns the peeked token.
     *
     * @return the peeked token if it didn't went out of bounds. If it did, it will return
     *         the last possible char in the {@link ArkoiClass#content} array.
     */
    public char peekChar(final int offset) {
        if (this.position + offset >= this.getArkoiClass().getContent().length)
            return this.getArkoiClass().getContent()[this.getArkoiClass().getContent().length - 1];
        return this.getArkoiClass().getContent()[this.position + offset];
    }
    
    
    /**
     * Returns the current char and checks if the position went out of bounds. If the
     * current {@link #position} went out of bounds, it will simply return the last
     * possible char of the {@link ArkoiClass#content} array.
     *
     * @return the current token if it didn't went out of bounds. If it did, it will
     *         return the last possible char in the {@link ArkoiClass#content} array.
     */
    public char currentChar() {
        if (this.position >= this.getArkoiClass().getContent().length)
            return this.getArkoiClass().getContent()[this.getArkoiClass().getContent().length - 1];
        return this.getArkoiClass().getContent()[this.position];
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
