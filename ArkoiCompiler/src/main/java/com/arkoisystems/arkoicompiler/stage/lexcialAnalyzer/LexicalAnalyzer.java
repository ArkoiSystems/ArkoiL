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
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
    @NonNull
    private final ArkoiClass arkoiClass;
    
    
    /**
     * The {@link LexicalErrorHandler} is used for errors which are happening when lexing
     * the input content.
     */
    @Getter
    @NotNull
    private final LexicalErrorHandler errorHandler = new LexicalErrorHandler();
    
    
    /**
     * This {@link AbstractToken}[] is used to access the parsed {@link AbstractToken}s
     * like in the {@link SyntaxAnalyzer} for methods like {@link
     * SyntaxAnalyzer#matchesNextToken(SymbolType)}.
     */
    @Getter
    @NotNull
    private AbstractToken[] tokens = new AbstractToken[0];
    
    
    /**
     * The current position in the {@link ArkoiClass#getContent()} array. This position is
     * used to peek and get tokens (e.g. {@link #next()} or {@link #peekChar(int)}).
     */
    @Getter
    @Setter
    private int position;
    
    
    /**
     * This {@link Runnable} defines an error routine, when a token couldn't be parsed.
     */
    @Getter
    @NotNull
    private final Runnable errorRoutine = () -> {
        this.failed();
        this.next();
    };
    
    
    /**
     * Constructs a new {@link LexicalAnalyzer} with the given {@link ArkoiClass} to get
     * the content or even the {@link ArkoiCompiler}.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} in which the {@link LexicalAnalyzer} gets
     *         constructed.
     */
    public LexicalAnalyzer(@NotNull final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will run this stage.
     * The lexer will go through every position in the {@link ArkoiClass#getContent()}
     * array. It will skip newlines (and every other "whitespace" character e.g. 0x0c,
     * 0x0a and 0x0d). Comments are getting parsed but they don't get added to the tokens
     * list because they are irrelevant for the later process and are just used for the
     * developers.
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
                WhitespaceToken
                        .builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse(tokens::add, this.errorRoutine);
            } else if (currentChar == '#') {
                CommentToken
                        .builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                            if (abstractToken instanceof BadToken)
                                errorRoutine.run();
                            tokens.add(abstractToken);
                        }, this.errorRoutine);
            } else if (currentChar == '"') {
                StringToken
                        .builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                            if (abstractToken instanceof BadToken)
                                errorRoutine.run();
                            tokens.add(abstractToken);
                        }, this.errorRoutine);
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
                final Optional<? extends AbstractToken> numberToken = NumberToken
                        .builder(this)
                        .build()
                        .parseToken();
                if (numberToken.isPresent() && !(numberToken.get() instanceof BadToken))
                    tokens.add(numberToken.get());
                else {
                    final Optional<? extends AbstractToken> symbolToken = SymbolToken
                            .builder(this)
                            .build()
                            .parseToken();
                    if(symbolToken.isEmpty() || symbolToken.get() instanceof BadToken)
                        this.errorRoutine.run();
                    symbolToken.ifPresent(tokens::add);
                }
            } else if (Character.isJavaIdentifierStart(currentChar)) {
                IdentifierToken
                        .builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                            if (abstractToken instanceof BadToken)
                                errorRoutine.run();
                            tokens.add(abstractToken);
                        }, this.errorRoutine);
            } else {
                switch (currentChar) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '%':
                    case '!':
                    case '=': {
                        OperatorToken
                                .builder(this)
                                .build()
                                .parseToken()
                                .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                                    if (abstractToken instanceof BadToken)
                                        errorRoutine.run();
                                    tokens.add(abstractToken);
                                }, this.errorRoutine);
                        continue;
                    }
    
                    case '@':
                    case '^':
                    case '|':
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
                    case '>': {
                        SymbolToken
                                .builder(this)
                                .build()
                                .parseToken()
                                .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                                    if (abstractToken instanceof BadToken)
                                        errorRoutine.run();
                                    tokens.add(abstractToken);
                                }, this.errorRoutine);
                        continue;
                    }
    
                    default:
                        BadToken
                                .builder()
                                .start(this.getPosition())
                                .end(this.getPosition() + 1)
                                .build()
                                .parseToken()
                                .ifPresent(tokens::add);
                        this.getErrorHandler().addError(new ArkoiError(
                                this.getArkoiClass(),
                                this.position,
                                "The defined character is unknown for the lexical analyzer:"
                        ));
                        this.failed();
                        this.next();
                        break;
                }
            }
        }
    
        EndOfFileToken
                .builder(this)
                .build()
                .parseToken()
                .ifPresent(tokens::add);
        this.tokens = tokens.toArray(new AbstractToken[] { });
        return !this.isFailed();
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will return the
     * created {@link LexicalErrorHandler}.
     *
     * @return the {@link LexicalErrorHandler} which got created in the constructor.
     */
    @NotNull
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
    @NotNull
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
     * position from the {@link ArkoiClass#getContent()} array.
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
     *         the offset which is used added to the current position {@link #position}.
     *         Keep in mind, that it won't change the position and just returns the peeked
     *         token.
     *
     * @return the peeked token if it didn't went out of bounds. If it did, it will return
     *         the last possible char in the {@link ArkoiClass#getContent()} array.
     */
    public char peekChar(final int offset) {
        if (this.position + offset >= this.getArkoiClass().getContent().length)
            return this.getArkoiClass().getContent()[this.getArkoiClass().getContent().length - 1];
        return this.getArkoiClass().getContent()[this.position + offset];
    }
    
    
    /**
     * Returns the current char and checks if the position went out of bounds. If the
     * current {@link #position} went out of bounds, it will simply return the last
     * possible char of the {@link ArkoiClass#getContent()} array.
     *
     * @return the current token if it didn't went out of bounds. If it did, it will
     *         return the last possible char in the {@link ArkoiClass#getContent()}
     *         array.
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
