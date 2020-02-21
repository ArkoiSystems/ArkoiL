/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import lombok.NonNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the {@link ErrorHandler} for the {@link SyntaxAnalyzer} which
 * needs to provide an {@link ErrorHandler}. It will just stores the {@link
 * AbstractError}s until it needs to print them.
 */
public class SyntaxErrorHandler extends ErrorHandler
{
    
    /**
     * The {@link AbstractError} list which is used to store the thrown errors.
     */
    private final List<AbstractError> abstractErrors = new ArrayList<>();
    
    
    /**
     * Adds the given {@link AbstractError} to the {@link SyntaxErrorHandler#abstractErrors}
     * list for later usage (see {@link SyntaxErrorHandler#printStackTrace(PrintStream)}).
     *
     * @param abstractError
     *         the given {@link AbstractError} which should get added to the {@link
     *         SyntaxErrorHandler#abstractErrors} list.
     */
    @Override
    public void addError(@NonNull final AbstractError abstractError) {
        this.abstractErrors.add(abstractError);
    }
    
    
    @Override
    public void printStackTrace(@NonNull final PrintStream printStream) {
        for (final AbstractError abstractError : this.abstractErrors)
            printStream.println(abstractError.toString());
    }
    
}
