/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.api.error.ICompilerError;
import com.arkoisystems.arkoicompiler.api.error.IErrorHandler;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashSet;


public class LexicalErrorHandler implements IErrorHandler
{
    
    @Getter
    @NonNull
    private final HashSet<ICompilerError> compilerErrors = new HashSet<>();
    
    
    public void addError(@NotNull final ICompilerError compilerError) {
        this.compilerErrors.add(compilerError);
    }
    
    
    @Override
    public void printStackTrace(@NotNull final PrintStream printStream, boolean testing) {
        for (final ICompilerError arkoiError : this.compilerErrors)
            printStream.println(testing ? arkoiError.toString().substring(arkoiError.toString().indexOf(' ') + 1) : arkoiError.toString());
    }
    
}
