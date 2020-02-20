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

public class PrefixExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final PrefixUnaryOperator prefixUnaryOperator;
    
    
    @Getter
    private final AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public PrefixExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> rightSideOperable, final PrefixUnaryOperator prefixUnaryOperator, final int start) {
        super(ASTType.PREFIX_EXPRESSION);
        
        this.prefixUnaryOperator = prefixUnaryOperator;
        this.rightSideOperable = rightSideOperable;
        
        this.setStart(start);
        this.setEnd(rightSideOperable.getEnd());
    }
    
    
    @Override
    public PrefixExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── operator: " + this.getPrefixUnaryOperator());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
    public enum PrefixUnaryOperator
    {
        
        PREFIX_ADD,
        PREFIX_SUB,
        NEGATE,
        AFFIRM
        
    }
    
}
