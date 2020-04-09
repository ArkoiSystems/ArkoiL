/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 06, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public interface ICompilerSyntaxAST extends IFailed
{
    
    @NotNull
    MarkerFactory<? extends ICompilerSyntaxAST, AbstractToken, AbstractToken> getMarkerFactory();
    
    
    @Nullable
    SyntaxAnalyzer getSyntaxAnalyzer();
    
    
    @Nullable
    AbstractToken getStartToken();
    
    
    @Nullable
    AbstractToken getEndToken();
    
    
    @NotNull
    ASTType getAstType();
    
    
    @NotNull
    ICompilerSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST);
    
    
    void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSyntaxAST[] compilerSyntaxASTs, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSyntaxAST compilerSyntaxAST, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, final int start, final int end, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final AbstractToken abstractToken, @NotNull final String message, @NotNull final Object... arguments);
    
}
