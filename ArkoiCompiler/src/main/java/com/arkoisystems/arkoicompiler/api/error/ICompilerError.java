/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 06, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api.error;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import org.jetbrains.annotations.NotNull;

public interface ICompilerError
{
    
    @NotNull
    ICompilerClass getCompilerClass();
    
    
    @NotNull
    int[][] getPositions();
    
    
    @NotNull
    String getFinalError();
    
}
