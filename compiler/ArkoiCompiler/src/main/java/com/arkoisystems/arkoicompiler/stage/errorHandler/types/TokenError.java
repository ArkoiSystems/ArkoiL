/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.google.gson.annotations.Expose;
import lombok.Getter;

public class TokenError extends AbstractError
{
    
    @Getter
    private final AbstractToken abstractToken;
    
    
    public TokenError(final AbstractToken abstractToken, final String message, final Object... arguments) {
        super(abstractToken.getStart(), abstractToken.getEnd(), message, arguments);
        
        this.abstractToken = abstractToken;
    }
    
}
