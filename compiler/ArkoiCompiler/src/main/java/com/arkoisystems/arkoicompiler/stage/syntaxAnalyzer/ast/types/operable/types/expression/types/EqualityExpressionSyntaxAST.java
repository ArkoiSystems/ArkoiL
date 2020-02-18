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

@Getter
public class EqualityExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Expose
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Expose
    private final EqualityOperator equalityOperator;
    
    
    @Expose
    private final AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public EqualityExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> leftSideOperable, final EqualityOperator equalityOperator, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        super(ASTType.EQUALITY_EXPRESSION);
        
        this.equalityOperator = equalityOperator;
        this.rightSideOperable = rightSideOperable;
        this.leftSideOperable = leftSideOperable;
    
        this.setStart(leftSideOperable.getStart());
        this.setEnd(rightSideOperable.getEnd());
    }
    
    
    @Override
    public EqualityExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printAST(printStream, indents + "│       ");
        printStream.println(indents + "├── operator: " + this.getEqualityOperator());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printAST(printStream, indents + "        ");
    }
    
    
    public enum EqualityOperator
    {
        
        EQUAL,
        NOT_EQUAL
        
    }
    
}
