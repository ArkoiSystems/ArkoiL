/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api.error;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public interface IErrorHandler
{
    
    void addError(@NotNull final ICompilerError compilerError);
    
    
    void printStackTrace(@NotNull final PrintStream printStream, final boolean testing);
    
}
