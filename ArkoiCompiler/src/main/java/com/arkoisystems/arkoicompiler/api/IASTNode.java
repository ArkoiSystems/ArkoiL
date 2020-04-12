/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 06, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IASTNode extends IFailed
{
    
    @NotNull
    MarkerFactory<? extends IASTNode, AbstractToken, AbstractToken> getMarkerFactory();
    
    
    @Nullable
    SyntaxAnalyzer getSyntaxAnalyzer();
    
    
    @Nullable
    AbstractToken getStartToken();
    
    
    @Nullable
    AbstractToken getEndToken();
    
    
    @NotNull
    ASTType getAstType();
    
    
    @NotNull
    IASTNode parseAST(@NotNull final IASTNode parentAST);
    
    
    void accept(@NotNull final IVisitor visitor);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode[] astNodes, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, final int start, final int end, @NotNull final String message, @NotNull final Object... arguments);
    
    
    <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final AbstractToken abstractToken, @NotNull final String message, @NotNull final Object... arguments);
    
}
