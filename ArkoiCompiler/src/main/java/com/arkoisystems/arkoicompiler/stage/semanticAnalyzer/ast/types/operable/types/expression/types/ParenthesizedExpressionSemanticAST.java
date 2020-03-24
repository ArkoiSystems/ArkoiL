/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class ParenthesizedExpressionSemanticAST extends AbstractExpressionSemanticAST<ParenthesizedExpressionSyntaxAST>
{
    
    private AbstractExpressionSemanticAST<?> parenthesizedExpression;
    
    
    public ParenthesizedExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parenthesizedExpressionSyntaxAST, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    // TODO: Check null safety.
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getParenthesizedExpression().getClass().getSimpleName());
        this.getParenthesizedExpression().printSemanticAST(printStream, indents + "        ");
    }
    
    @Override
    public TypeKind getOperableObject() {
        if (this.getParenthesizedExpression() == null)
            return null;
        return this.getParenthesizedExpression().getOperableObject();
    }
    
    
    public AbstractExpressionSemanticAST<?> getParenthesizedExpression() {
        if (this.parenthesizedExpression == null) {
            final ExpressionSyntaxAST expressionSyntaxAST = this.getSyntaxAST().getExpressionSyntaxAST();
            this.parenthesizedExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
            if (this.parenthesizedExpression.getOperableObject() == null)
                return null;
        }
        return this.parenthesizedExpression;
    }
    
}
