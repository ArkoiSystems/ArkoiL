/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.PrintStream;

public class ExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final AbstractOperableSyntaxAST<?> expressionOperable;
    
    
    public ExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> expressionOperable) {
        super(ASTType.BASIC_EXPRESSION);
        
        this.expressionOperable = expressionOperable;
        
        this.setStart(expressionOperable.getStart());
        this.setEnd(expressionOperable.getEnd());
    }
    
    
    @Override
    public ExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        this.getExpressionOperable().printSyntaxAST(printStream, indents);
    }
    
}
