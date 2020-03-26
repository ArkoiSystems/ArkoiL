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
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class ParenthesizedExpressionSemanticAST extends AbstractExpressionSemanticAST<ParenthesizedExpressionSyntaxAST>
{
    
    @Nullable
    private AbstractExpressionSemanticAST<?> parenthesizedExpression;
    
    
    public ParenthesizedExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parenthesizedExpressionSyntaxAST, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + (this.getParenthesizedExpression() != null ? this.getParenthesizedExpression().getClass().getSimpleName() : null));
        if (this.getParenthesizedExpression() != null)
            this.getParenthesizedExpression().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Nullable
    @Override
    public TypeKind getTypeKind() {
        if (this.getParenthesizedExpression() == null)
            return null;
        return this.getParenthesizedExpression().getTypeKind();
    }
    
    
    @Nullable
    public AbstractExpressionSemanticAST<?> getParenthesizedExpression() {
        if (this.parenthesizedExpression == null) {
            final ExpressionSyntaxAST expressionSyntaxAST = this.getSyntaxAST().getExpressionSyntaxAST();
            if(expressionSyntaxAST == null)
                return null;
            
            this.parenthesizedExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
            if (this.parenthesizedExpression.getTypeKind() == null)
                return null;
        }
        return this.parenthesizedExpression;
    }
    
}
