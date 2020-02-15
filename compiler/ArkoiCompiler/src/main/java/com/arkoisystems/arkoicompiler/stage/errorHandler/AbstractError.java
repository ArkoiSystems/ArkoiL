/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler;

import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractError
{
    
    @Expose
    private final String message;
    
    @Expose
    private final int start, end;
    
    public AbstractError(final int start, final int end, final String message, final Object... arguments) {
        this.start = start;
        this.end = end;
    
        this.message = String.format(message, arguments);
    }
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
