/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.api.error.ICompilerError;
import com.arkoisystems.arkoicompiler.api.error.IErrorHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashSet;

public class SyntaxErrorHandler implements IErrorHandler
{
    
    @Getter
    @NotNull
    private final HashSet<ICompilerError> compilerErrors = new HashSet<>();
    
    
    @Override
    public void addError(@NotNull final ICompilerError compilerError) {
        this.compilerErrors.add(compilerError);
    }
    
    
    @Override
    public void printStackTrace(@NotNull final PrintStream printStream, boolean testing) {
        for (final ICompilerError compilerError : this.compilerErrors)
            printStream.println(testing ? compilerError.toString().substring(compilerError.toString().indexOf(' ') + 1) : compilerError.toString());
    }
    
}
