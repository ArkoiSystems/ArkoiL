/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import org.jetbrains.annotations.NotNull;

public interface ISyntaxParser
{
    
    @NotNull
    ICompilerSyntaxAST parse(@NotNull final ICompilerSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer);
    
    
    boolean canParse(@NotNull final ICompilerSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer);
    
}
