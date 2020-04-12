/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 31, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface ICompilerClass
{
    
    @NotNull
    SyntaxAnalyzer getSyntaxAnalyzer();
    
    
    @NotNull
    LexicalAnalyzer getLexicalAnalyzer();
    
    
    @NotNull
    SemanticAnalyzer getSemanticAnalyzer();
    
    
    @NotNull
    char[] getContent();
    
    
    @NotNull
    String getFilePath();
    
    
    @NotNull
    ArkoiCompiler getArkoiCompiler();
    
    
    boolean isNative();
    
}
