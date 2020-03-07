/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler;

import java.io.PrintStream;

/**
 * This class contains some important methods which are needed for a {@link ErrorHandler}.
 * Such a Handler is capable to print the stack trace ({@link ErrorHandler#printStackTrace(PrintStream,
 * boolean)}) or to add errors ({@link ErrorHandler#addError(ArkoiError)}).
 */
public abstract class ErrorHandler
{
    
    /**
     * Used to add {@link ArkoiError}s to the Handler for later usage.
     *
     * @param arkoiError
     *         the {@link ArkoiError} which is used added to the Handler.
     */
    public abstract void addError(final ArkoiError arkoiError);
    
    
    /**
     * Used to print a stack trace with the given {@link PrintStream}.
     *
     * @param printStream
     *         the {@link PrintStream} which is used used for this method.
     * @param testing
     *         the flag if the output should be converted to a testable stack trace
     *         (removing the date etc).
     */
    public abstract void printStackTrace(final PrintStream printStream, final boolean testing);
    
}
