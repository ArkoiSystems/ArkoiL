/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.api.error.ICompilerError;
import com.arkoisystems.arkoicompiler.api.error.IErrorHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SemanticErrorHandler implements IErrorHandler
{
    
    @Getter
    private final List<ICompilerError> compileErrors = new ArrayList<>();
    
    
    @Override
    public void addError(@NotNull final ICompilerError compilerError) {
        this.compileErrors.add(compilerError);
    }
    
    
    @Override
    public void printStackTrace(@NotNull final PrintStream printStream, boolean testing) {
        for (final ICompilerError arkoiError : this.getCompileErrors())
            printStream.println(testing ? arkoiError.toString().substring(arkoiError.toString().indexOf(' ') + 1) : arkoiError.toString());
    }
    
}
