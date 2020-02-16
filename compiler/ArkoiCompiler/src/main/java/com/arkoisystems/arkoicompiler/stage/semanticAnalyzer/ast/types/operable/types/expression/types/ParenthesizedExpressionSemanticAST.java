/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.google.gson.annotations.Expose;
import lombok.Setter;

@Setter
public class ParenthesizedExpressionSemanticAST extends AbstractExpressionSemanticAST<ParenthesizedExpressionSyntaxAST>
{
    
    @Expose
    private AbstractExpressionSemanticAST<?> parenthesizedExpression;
    
    public ParenthesizedExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parenthesizedExpressionSyntaxAST, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    @Override
    public TypeKind getExpressionType() {
        if(this.getParenthesizedExpression() == null)
            return null;
        return this.getParenthesizedExpression().getExpressionType();
    }
    
    public AbstractExpressionSemanticAST<?> getParenthesizedExpression() {
        if(this.parenthesizedExpression == null) {
            final ExpressionSyntaxAST expressionSyntaxAST = (ExpressionSyntaxAST) this.getSyntaxAST().getParenthesizedExpression();
            this.parenthesizedExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
            if(this.parenthesizedExpression.getExpressionType() == null)
                return null;
        }
        return this.parenthesizedExpression;
    }
    
}
