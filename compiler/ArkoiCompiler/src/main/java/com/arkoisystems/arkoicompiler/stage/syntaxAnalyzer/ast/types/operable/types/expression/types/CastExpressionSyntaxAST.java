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
import lombok.Getter;

import java.io.PrintStream;

public class CastExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    private final CastOperator castOperator;
    
    
    public CastExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> leftSideOperable, final CastOperator castOperator) {
        super(ASTType.CAST_EXPRESSION);
        
        this.castOperator = castOperator;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(leftSideOperable.getStart());
        this.setEnd(leftSideOperable.getStart() + 1);
    }
    
    
    @Override
    public CastExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getCastOperator());
    }
    
    
    public enum CastOperator
    {
        
        BYTE,
        FLOAT,
        SHORT,
        DOUBLE,
        INTEGER
        
    }
    
}
