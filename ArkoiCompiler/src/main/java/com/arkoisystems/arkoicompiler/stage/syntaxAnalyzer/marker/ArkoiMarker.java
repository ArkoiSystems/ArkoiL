/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 28, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArkoiMarker
{
    
    @Getter
    @Setter
    private AbstractToken startToken, endToken;
    
    
    @Getter
    @Setter
    @Nullable
    private Object[] errorArguments;
    
    
    @Getter
    @NotNull
    private final ASTType astType;
    
    
    @Getter
    @Setter
    @Nullable
    private String errorMessage;
    
    
    public ArkoiMarker(@NotNull final ASTType astType) {
        this.astType = astType;
    }
    
}
