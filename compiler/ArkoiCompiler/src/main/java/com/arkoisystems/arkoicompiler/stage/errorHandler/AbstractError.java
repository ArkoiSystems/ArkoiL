/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler;

import lombok.Getter;

public abstract class AbstractError
{
    
    @Getter
    private final String message;
    
    
    @Getter
    private final int start, end;
    
    
    public AbstractError(final int start, final int end, final String message, final Object... arguments) {
        this.start = start;
        this.end = end;
    
        this.message = String.format(message, arguments);
    }
    
}
