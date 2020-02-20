/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import lombok.Getter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SemanticErrorHandler extends ErrorHandler
{
    
    @Getter
    private final List<AbstractError> abstractErrors = new ArrayList<>();
    
    
    @Override
    public void addError(final AbstractError abstractError) {
        this.getAbstractErrors().add(abstractError);
    }
    
    
    @Override
    public void printStackTrace(final PrintStream printStream) {
        for (final AbstractError abstractError : this.getAbstractErrors())
            printStream.println(abstractError.toString());
    }
    
}
