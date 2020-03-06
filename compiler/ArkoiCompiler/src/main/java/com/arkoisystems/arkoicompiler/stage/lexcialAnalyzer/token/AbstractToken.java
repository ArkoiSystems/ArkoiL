/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.WhitespaceToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractToken
{
    
    /**
     * The {@link LexicalAnalyzer} is is used to call methods like {@link
     * LexicalAnalyzer#next(int)} or {@link LexicalAnalyzer#peekChar(int)}.
     */
    @Getter
    private final LexicalAnalyzer lexicalAnalyzer;
    
    
    /**
     * The {@link TokenType} is used to differentiate {@link AbstractToken}s. Also it
     * helps when debugging with the {@link #toString()} method.
     */
    @Getter
    @Setter
    private TokenType tokenType;
    
    
    /**
     * The token content which got lexed. Used for later code generation or casting if
     * it's a {@link NumberToken}.
     */
    @Getter
    @Setter
    private String tokenContent;
    
    
    /**
     * The starting and ending position of this {@link AbstractToken}. This will help for
     * later debugging or syntax highlighting because we now the exact location.
     */
    @Getter
    @Setter
    private int start, end;
    
    
    /**
     * Constructs a new {@link AbstractToken} with the given parameters. The {@link
     * LexicalAnalyzer} is used to call methods like {@link LexicalAnalyzer#next(int)} or
     * {@link LexicalAnalyzer#peekChar(int)}.
     *
     * @param lexicalAnalyzer
     *         the {@link LexicalAnalyzer} which is used to call methods like {@link
     *         LexicalAnalyzer#next(int)} or {@link LexicalAnalyzer#peekChar(int)}.
     * @param tokenType
     *         the {@link TokenType} which is used for later debugging etc.
     */
    public AbstractToken(final LexicalAnalyzer lexicalAnalyzer, final TokenType tokenType) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.tokenType = tokenType;
    }
    
    
    /**
     * Parses a new {@link AbstractToken} with the given {@link LexicalAnalyzer} which is
     * used to call methods like {@link LexicalAnalyzer#next(int)} or {@link
     * LexicalAnalyzer#peekChar(int)}.
     *
     * @return {@code null} if an error occurred or itself/some other token if it parsed
     *         successfully.
     */
    public abstract AbstractToken parseToken();
    
    
    /**
     * Creates and adds a new {@link ArkoiError} with the given parameters to the stack
     * trace which is used for better debugging.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} which is used to get important information about the
     *         source error.
     * @param position
     *         the starting and ending position where the error occurred in the source
     *         code.
     * @param message
     *         the error message which should get printed out.
     * @param arguments
     *         the arguments list for the error message from the {@link SyntaxErrorType}.
     */
    public void addError(final ArkoiClass arkoiClass, final int position, final String message, final Object... arguments) {
        this.getLexicalAnalyzer().getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                position,
                message,
                arguments
        ));
    }
    
    
    /**
     * Creates and adds a new {@link ArkoiError} with the given parameters to the stack
     * trace which is used for better debugging.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} which is used to get important information about the
     *         source error.
     * @param start
     *         the starting position where the error occurred in the source code.
     * @param end
     *         the ending position where the error occurred in the source code.
     * @param message
     *         the error message which should get printed out.
     * @param arguments
     *         the arguments list for the error message from the {@link SyntaxErrorType}.
     */
    public void addError(final ArkoiClass arkoiClass, final int start, final int end, final String message, final Object... arguments) {
        this.getLexicalAnalyzer().getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                start,
                end,
                message,
                arguments
        ));
    }
    
    
    @Override
    public String toString() {
        return this.getTokenType() + " -> " + this.getTokenContent();
    }
    
}
