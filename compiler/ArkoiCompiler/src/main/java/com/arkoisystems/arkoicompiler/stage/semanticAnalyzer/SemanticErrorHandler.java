/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.stage.errorHandler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import lombok.Getter;

import java.io.PrintStream;
import java.util.HashMap;

public class SemanticErrorHandler extends ErrorHandler
{
    
    @Getter
    private final HashMap<Integer, ArkoiError> arkoiErrors = new HashMap<>();
    
    
    @Override
    public void addError(final ArkoiError arkoiError) {
        if (!this.getArkoiErrors().containsKey(arkoiError.hashCode()))
            this.getArkoiErrors().put(arkoiError.hashCode(), arkoiError);
    }
    
    
    @Override
    public void printStackTrace(final PrintStream printStream) {
        for (final ArkoiError arkoiError : this.getArkoiErrors().values())
            printStream.println(arkoiError.toString());
    }
    
}
