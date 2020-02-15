/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class AssignmentExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Expose
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    @Expose
    private final AssignmentOperator assignmentOperator;
    
    @Expose
    private final AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public AssignmentExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> leftSideOperable, final AssignmentOperator assignmentOperator, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        super(ASTType.ASSIGNMENT_EXPRESSION);
        
        this.assignmentOperator = assignmentOperator;
        this.rightSideOperable = rightSideOperable;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(leftSideOperable.getStart());
        this.setEnd(rightSideOperable.getEnd());
    }
    
    @Override
    public AssignmentExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    public enum AssignmentOperator
    {
        
        ASSIGN,
        ADD_ASSIGN,
        SUB_ASSIGN,
        MUL_ASSIGN,
        DIV_ASSIGN,
        MOD_ASSIGN
        
    }
    
}
