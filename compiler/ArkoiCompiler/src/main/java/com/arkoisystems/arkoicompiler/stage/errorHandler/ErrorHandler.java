/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler;

import java.io.PrintStream;
import java.text.SimpleDateFormat;

/**
 * This class contains some important methods which are needed for a {@link ErrorHandler}.
 * Such a Handler is capable to print the stack trace ({@link ErrorHandler#printStackTrace(PrintStream)})
 * or to add errors ({@link ErrorHandler#addError(AbstractError)}).
 */
public abstract class ErrorHandler
{
    
    
    /**
     * Defines a {@link SimpleDateFormat} for later debugging to see the time in which
     * this error happened.
     */
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    
    
    /**
     * Used to add {@link AbstractError}s to the Handler for later usage.
     *
     * @param abstractError
     *         the {@link AbstractError} which should get added to the Handler.
     */
    public abstract void addError(final AbstractError abstractError);
    
    /**
     * Used to print a stack trace with the given {@link PrintStream}.
     *
     * @param printStream
     *         the {@link PrintStream} which should get used for this method.
     */
    public abstract void printStackTrace(final PrintStream printStream);
    
}
