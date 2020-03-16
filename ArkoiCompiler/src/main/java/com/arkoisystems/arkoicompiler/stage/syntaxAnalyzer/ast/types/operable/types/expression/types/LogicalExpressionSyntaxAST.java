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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.LogicalOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class LogicalExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    private final LogicalOperatorType logicalOperatorType;
    
    
    @Getter
    @Setter
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public LogicalExpressionSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer, final AbstractOperableSyntaxAST<?> leftSideOperable, final LogicalOperatorType logicalOperatorType) {
        super(syntaxAnalyzer, ASTType.LOGICAL_EXPRESSION);
        
        this.logicalOperatorType = logicalOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(this.leftSideOperable.getStart());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        return Optional.empty();
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "├── operator: " + this.getLogicalOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
