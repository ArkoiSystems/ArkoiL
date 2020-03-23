/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is used if you want to create an AST. With the {@link
 * AbstractSyntaxAST#parseAST(AbstractSyntaxAST)} method you can check the syntax and
 * initialize all variables which are needed for later usage. Also you can print out this
 * class as a JSON based {@link String} with the {@link AbstractSyntaxAST#toString()}
 * method.
 */
public abstract class AbstractSyntaxAST
{
    
    /**
     * The {@link SyntaxAnalyzer} which is used to check the syntax with methods like
     * {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     * SyntaxAnalyzer#nextToken()}.
     */
    @Getter
    private final SyntaxAnalyzer syntaxAnalyzer;
    
    
    /**
     * The {@link ASTType} is used to differentiate it from other {@link
     * AbstractSyntaxAST}s. Also it is useful for debugging if you use the output of the
     * {@link AbstractSyntaxAST#toString()} method.
     */
    @Getter
    private final ASTType astType;
    
    
    /**
     * The start and end index of the AST as char positions from the input source declared
     * in {@link ArkoiClass} which you can get through the {@link SyntaxAnalyzer}.
     */
    @Getter
    @Setter
    private int start, end;
    
    
    /**
     * Defines if the {@link AbstractSyntaxAST} failed to parse the {@link
     * AbstractSyntaxAST} or not.
     */
    @Getter
    private boolean failed;
    
    /**
     * Just constructs a new {@link AbstractSyntaxAST} with the defined {@link ASTType}
     * and {@link SyntaxAnalyzer} as help for checking the syntax or debugging.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         * SyntaxAnalyzer#nextToken()}.
     * @param astType
     *         the {@link ASTType} which is used used to identify this specific {@link
     *         AbstractSyntaxAST}.
     */
    public AbstractSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer, final ASTType astType) {
        this.syntaxAnalyzer = syntaxAnalyzer;
        this.astType = astType;
    }
    
    
    /**
     * This method will be overwritten by the classes which extends {@link
     * AbstractSyntaxAST}. It will return a {@link AbstractSyntaxAST} if everything worked
     * correctly or {@code null} if an error occurred. The parent {@link
     * AbstractSyntaxAST} is used to check if the {@link AbstractSyntaxAST} can be created
     * inside it and the {@link SyntaxAnalyzer} is just used to check the syntax of
     * current/next and peeked {@link AbstractToken}.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which is used used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     *
     * @return {@code null} if an error occurred or the parsed {@link AbstractSyntaxAST}
     *         if everything worked correctly.
     */
    public abstract Optional<? extends AbstractSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST);
    
    
    /**
     * This method will be overwritten by the classes which extends {@link
     * AbstractSyntaxAST}. It will print with help of the {@link PrintStream} and {@code
     * indents} a tree which is used for debugging. To print all {@link RootSyntaxAST}s
     * you just need to call this method {@link ArkoiCompiler#printSyntaxTree(PrintStream)}.
     *
     * @param printStream
     *         the {@link PrintStream} which is used used for the output.
     * @param indents
     *         the {@code indents} which will make the AST look like a Tree.
     */
    public abstract void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents);
    
    
    protected void skipToNextValidToken() {
        int openBraces = 0;
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().currentToken().getTokenType() == TokenType.END_OF_FILE)
                break;
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) != null)
                openBraces++;
            else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null) {
                openBraces--;
                if (openBraces <= 0) {
                    this.getSyntaxAnalyzer().nextToken();
                    break;
                }
            }
            this.getSyntaxAnalyzer().nextToken();
        }
        this.failed();
    }
    
    
    /**
     * Creates and adds a new {@link ArkoiError} with the given parameters to the stack
     * trace which is used for better debugging.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} which is used to get important information about the
     *         source error.
     * @param abstractSyntaxASTs
     *         the {@link AbstractSyntaxAST}[] which is used to get all ASTs which are
     *         involved in the error.
     * @param message
     *         the error message which should get printed out.
     * @param arguments
     *         the arguments list for the error message from the {@link SyntaxErrorType}.
     */
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractSyntaxAST[] abstractSyntaxASTs, @NonNull final String message, final Object... arguments) {
        this.getSyntaxAnalyzer().getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                abstractSyntaxASTs,
                message,
                arguments
        ));
        this.failed();
    }
    
    
    /**
     * Creates and adds a new {@link ArkoiError} with the given parameters to the stack
     * trace which is used for better debugging.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} which is used to get important information about the
     *         source error.
     * @param abstractSyntaxAST
     *         the {@link AbstractSyntaxAST} which is used to get the source line etc.
     * @param message
     *         the error message which should get printed out.
     * @param arguments
     *         the arguments list for the error message from the {@link SyntaxErrorType}.
     */
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractSyntaxAST abstractSyntaxAST, @NonNull final String message, final Object... arguments) {
        this.getSyntaxAnalyzer().getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                abstractSyntaxAST,
                message,
                arguments
        ));
        this.failed();
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
    public void addError(@NonNull final ArkoiClass arkoiClass, final int start, final int end, @NonNull final String message, final Object... arguments) {
        this.getSyntaxAnalyzer().getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                start,
                end,
                message,
                arguments
        ));
        this.failed();
    }
    
    
    /**
     * Creates and adds a new {@link ArkoiError} with the given parameters to the stack
     * trace which is used for better debugging.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} which is used to get important information about the
     *         source error.
     * @param abstractToken
     *         the {@link AbstractToken} which is used to get the source line etc.
     * @param message
     *         the error message which should get printed out.
     * @param arguments
     *         the arguments list for the error message from the {@link SyntaxErrorType}.
     */
    public void addError(@NonNull final ArkoiClass arkoiClass, @NonNull final AbstractToken abstractToken, @NonNull final String message, final Object... arguments) {
        this.getSyntaxAnalyzer().getErrorHandler().addError(new ArkoiError(
                arkoiClass,
                abstractToken,
                message,
                arguments
        ));
        this.failed();
    }
    
    
    public void failed() {
        this.failed = true;
    }
    
    
    /**
     * Generates the SyntaxTree with help of the {@link #printSyntaxAST(PrintStream,
     * String)} method and returns it.
     *
     * @return a SyntaxTree with help of the {@link #printSyntaxAST(PrintStream, String)}
     *         method.
     */
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        this.printSyntaxAST(printStream, "");
        return byteArrayOutputStream.toString();
    }
    
    
    /**
     * Returns a unique hash for this {@link AbstractSyntaxAST}. This hash is used when
     * comparing for errors etc.
     *
     * @return a unique hash for this {@link AbstractSyntaxAST}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getAstType(), this.getStart(), this.getEnd());
    }
    
}
