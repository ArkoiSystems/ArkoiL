/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

public class SyntaxASTError<T extends AbstractSyntaxAST> extends AbstractError
{
    
    @Getter
    private final T abstractAST;
    
    
    public SyntaxASTError(final T abstractAST, final String message, final Object... arguments) {
        super(abstractAST.getStart(), abstractAST.getEnd(), message, arguments);
        
        this.abstractAST = abstractAST;
    }
    
}
