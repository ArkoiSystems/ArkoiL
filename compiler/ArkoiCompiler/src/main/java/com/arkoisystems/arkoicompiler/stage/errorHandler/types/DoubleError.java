/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class DoubleError<T1, T2> extends AbstractError
{
    
    @Expose
    private final T1 firstAbstractAST;
    
    @Expose
    private final T2 secondAbstractAST;
    
    public DoubleError(final T1 firstAbstractAST, final T2 secondAbstractAST, final int start, final int end, final String message, final Object... arguments) {
        super(start, end, message, arguments);
        
        this.secondAbstractAST = secondAbstractAST;
        this.firstAbstractAST = firstAbstractAST;
    }
    
}
