/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.exceptions.CrashOnAccessException;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public abstract class AbstractToken
{
    
    /**
     * The {@link LexicalAnalyzer} is is used to call methods like {@link
     * LexicalAnalyzer#next(int)} or {@link LexicalAnalyzer#peekChar(int)}.
     */
    @Getter
    @Nullable
    private final LexicalAnalyzer lexicalAnalyzer;
    
    
    /**
     * Defines a flag if the class should throw an error, if it get accessed.
     */
    @Getter
    private final boolean crashOnAccess;
    
    
    /**
     * The {@link TokenType} is used to differentiate {@link AbstractToken}s. Also it
     * helps when debugging with the {@link #toString()} method.
     */
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private TokenType tokenType;
    
    
    /**
     * The token content which got lexed. Used for later code generation or casting if
     * it's a {@link NumberToken}.
     */
    @Setter
    @NotNull
    private String tokenContent;
    
    
    /**
     * The starting and ending position of this {@link AbstractToken}. This will help for
     * later debugging or syntax highlighting because we now the exact location.
     */
    @Setter(AccessLevel.PROTECTED)
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
    public AbstractToken(@Nullable final LexicalAnalyzer lexicalAnalyzer, @NotNull final TokenType tokenType, final boolean crashOnAccess) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.crashOnAccess = crashOnAccess;
        this.tokenType = tokenType;
    }
    
    
    @NotNull
    public abstract Optional<? extends AbstractToken> parseToken();
    
    
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
    public void addError(@NotNull final ArkoiClass arkoiClass, final int position, @NotNull final String message, final Object... arguments) {
        Objects.requireNonNull(this.getLexicalAnalyzer()).getErrorHandler().addError(new ArkoiError(
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
    public void addError(@NotNull final ArkoiClass arkoiClass, final int start, final int end, @NotNull final String message, final Object... arguments) {
        Objects.requireNonNull(this.getLexicalAnalyzer()).getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                start,
                end,
                message,
                arguments
        ));
    }
    
    
    public String getTokenContent() {
        if (this.crashOnAccess)
            throw new CrashOnAccessException(this.getClass().getSimpleName() + ": " + this.tokenContent + ", " + this.tokenType + "," + this.start + ", " + this.end);
        return this.tokenContent;
    }
    
    
    public TokenType getTokenType() {
        if (this.crashOnAccess)
            throw new CrashOnAccessException(this.getClass().getSimpleName() + ": " + this.tokenContent + ", " + this.tokenType + "," + this.start + ", " + this.end);
        return this.tokenType;
    }
    
    
    public int getStart() {
        if (this.crashOnAccess)
            throw new CrashOnAccessException(this.getClass().getSimpleName() + ": " + this.tokenContent + ", " + this.tokenType + "," + this.start + ", " + this.end);
        return this.start;
    }
    
    
    public int getEnd() {
        if (this.crashOnAccess)
            throw new CrashOnAccessException(this.getClass().getSimpleName() + ": " + this.tokenContent + ", " + this.tokenType + "," + this.start + ", " + this.end);
        return this.end;
    }
    
}
