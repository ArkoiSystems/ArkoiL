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
public class CharError extends AbstractError
{
    
    @Expose
    private final char character;
    
    public CharError(final char character, final int index, final String message, final Object... arguments) {
        super(index, index + 1, message, arguments);
        
        this.character = character;
    }
    
}
