/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.DoubleError;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import lombok.Getter;

@Getter
public class DoubleSyntaxASTError<T1 extends AbstractSyntaxAST, T2 extends AbstractSyntaxAST> extends DoubleError<T1, T2>
{
    
    public DoubleSyntaxASTError(final T1 firstAbstractAST, final T2 secondAbstractAST, String message, Object... arguments) {
        super(firstAbstractAST, secondAbstractAST, firstAbstractAST.getStart(), secondAbstractAST.getEnd(), message, arguments);
    }
    
}
