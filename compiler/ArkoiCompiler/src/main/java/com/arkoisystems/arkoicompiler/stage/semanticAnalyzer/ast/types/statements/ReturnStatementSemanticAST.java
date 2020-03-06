/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

public class ReturnStatementSemanticAST extends AbstractSemanticAST<ReturnStatementSyntaxAST>
{
    
    private ExpressionSemanticAST returnExpression;
    
    
    public ReturnStatementSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ReturnStatementSyntaxAST returnStatementSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, returnStatementSyntaxAST, ASTType.RETURN_STATEMENT);
    }
    
    
    public ExpressionSemanticAST getReturnExpression() {
        if(this.returnExpression == null) {
            this.returnExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), this.getSyntaxAST().getReturnExpression());

            this.returnExpression.getOperableObject();

            if (this.returnExpression.isFailed())
                this.setFailed(true);
        }
        return this.returnExpression;
    }
    
}
