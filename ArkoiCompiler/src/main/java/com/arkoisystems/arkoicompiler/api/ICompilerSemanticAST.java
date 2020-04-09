/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 06, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.api.utils.IInitiable;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.function.Supplier;

public interface ICompilerSemanticAST<T extends ICompilerSyntaxAST> extends IFailed, IInitiable
{
    
    @Nullable
    T getSyntaxAST();
    
    
    @Nullable
    SemanticAnalyzer getSemanticAnalyzer();
    
    
    @NotNull
    ASTType getAstType();
    
    
    @Nullable
    AbstractToken getStartToken();
    
    
    @Nullable
    AbstractToken getEndToken();
    
    
    @Nullable
    ICompilerSemanticAST<?> getLastContainerAST();
    
    
    void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents);
    
    
    @Nullable
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final AbstractToken abstractToken, @NotNull final String message, @NotNull final Object... arguments);
    
    
    @Nullable
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSyntaxAST[] compilerSyntaxASTs, @NotNull final String message, @NotNull final Object... arguments);
    
    
    @Nullable
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSyntaxAST compilerSyntaxAST, @NotNull final String message, @NotNull final Object... arguments);
    
    
    @Nullable
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSemanticAST<?>[] compilerSemanticASTs, @NotNull final String message, @NotNull final Object... arguments);
    
    
    @Nullable
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ICompilerSemanticAST<?> compilerSemanticAST, @NotNull final String message, @NotNull final Object... arguments);
    
    
    @NotNull
    Supplier<String> getFailedSupplier(@NotNull final String message);
    
}