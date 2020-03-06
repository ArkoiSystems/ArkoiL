/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import lombok.NonNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link ErrorHandler} for the {@link SyntaxAnalyzer} which
 * needs to provide an {@link ErrorHandler}. It will just stores the {@link ArkoiError}s
 * until it needs to print them.
 */
public class SyntaxErrorHandler extends ErrorHandler
{
    
    /**
     * The {@link ArkoiError} list which is used to store the thrown errors.
     */
    private final List<ArkoiError> abstractErrors = new ArrayList<>();
    
    
    /**
     * Adds the given {@link ArkoiError} to the {@link SyntaxErrorHandler#abstractErrors}
     * list for later usage (see {@link SyntaxErrorHandler#printStackTrace(PrintStream)}).
     *
     * @param arkoiError
     *         the given {@link ArkoiError} which is used added to the {@link
     *         SyntaxErrorHandler#abstractErrors} list.
     */
    public void addError(@NonNull final ArkoiError arkoiError) {
        this.abstractErrors.add(arkoiError);
    }
    
    
    /**
     * Prints all {@link ArkoiError} which occurred during the analysing of the {@link
     * SyntaxAnalyzer}.
     *
     * @param printStream
     *         the {@link PrintStream} which is used to print all {@link ArkoiError}s.
     */
    @Override
    public void printStackTrace(@NonNull final PrintStream printStream) {
        for (final ArkoiError abstractError : this.abstractErrors)
            printStream.println(abstractError.toString());
    }
    
}
