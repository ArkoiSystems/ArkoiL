/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 28, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICompilerMarker<T1, T2>
{
    
    @Nullable
    T1 getStart();
    
    
    void setStart(T1 start);
    
    
    @Nullable
    T2 getEnd();
    
    
    void setEnd(T2 end);
    
    
    @Nullable
    Object[] getErrorArguments();
    
    
    void setErrorArguments(@NotNull Object[] errorArguments);
    
    
    @NotNull
    ASTType getAstType();
    
    
    @Nullable
    String getErrorMessage();
    
    
    void setErrorMessage(@NotNull String errorMessage);
    
}
