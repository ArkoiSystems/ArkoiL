/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

@Setter
public class ReturnStatementSemanticAST extends AbstractSemanticAST<ReturnStatementSyntaxAST>
{
    
    @Expose
    private ExpressionSemanticAST returnExpression;
    
    public ReturnStatementSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ReturnStatementSyntaxAST returnStatementSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, returnStatementSyntaxAST, ASTType.RETURN_STATEMENT);
    }
    
    public ExpressionSemanticAST getReturnExpression() {
        if(this.returnExpression == null) {
            final ExpressionSyntaxAST expressionSyntaxAST = this.getSyntaxAST().getReturnExpression();
            this.returnExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
            if(this.returnExpression.getExpressionType() == null)
                return null;
        }
        return this.returnExpression;
    }
    
    //    @Override
//    public ReturnStatementSemanticAST analyseAST(final SemanticAnalyzer semanticAnalyzer) {
//        System.out.println("Return Statement Semantic AST");
//        return null;
//    }
    
}
