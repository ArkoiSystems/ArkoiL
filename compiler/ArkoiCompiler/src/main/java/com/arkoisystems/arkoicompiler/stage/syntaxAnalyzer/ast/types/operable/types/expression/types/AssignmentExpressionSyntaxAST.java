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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public class AssignmentExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter
    private AssignmentOperatorType assignmentOperatorType;
    
    
    @Getter
    @Setter
    private AbstractOperableSyntaxAST<?> leftSideOperable, rightSideOperable;
    
    
    public AssignmentExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final AbstractOperableSyntaxAST<?> leftSideOperable, final AssignmentOperatorType assignmentOperatorType, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
        
        this.rightSideOperable = rightSideOperable;
        this.leftSideOperable = leftSideOperable;
        
        this.assignmentOperatorType = assignmentOperatorType;
        
        this.setStart(this.leftSideOperable.getStart());
        this.setEnd(this.rightSideOperable.getEnd());
    }
    
    
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST) {
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getAssignmentOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
