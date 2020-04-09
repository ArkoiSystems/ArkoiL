/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 28, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker;

import com.arkoisystems.arkoicompiler.api.ICompilerMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class ArkoiMarker<T1, T2> implements ICompilerMarker<T1, T2>
{
    
    @Nullable
    private T1 start;
    
    
    @Nullable
    private T2 end;
    
    
    @Nullable
    private Object[] errorArguments;
    
    
    @NotNull
    private final ASTType astType;
    
    
    @Nullable
    private String errorMessage;
    
}
