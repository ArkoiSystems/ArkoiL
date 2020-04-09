/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.api.error.IErrorHandler;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.api.utils.IResettable;
import org.jetbrains.annotations.NotNull;

public interface ICompilerStage extends IFailed, IResettable
{
    
    @NotNull
    IErrorHandler getErrorHandler();
    
    
    boolean processStage();
    
}
