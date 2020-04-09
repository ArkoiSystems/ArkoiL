/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class ParenthesizedExpressionSemanticAST extends ExpressionSemanticAST<ParenthesizedExpressionSyntaxAST>
{
    
    @Getter
    @Nullable
    private final ExpressionSemanticAST<?> parenthesizedExpression = this.checkParenthesizedExpression();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public ParenthesizedExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, parenthesizedExpressionSyntaxAST, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s└── operable:%n", indents);
        printStream.printf("%s    └── %s%n", indents, this.getParenthesizedExpression() != null ? this.getParenthesizedExpression().getClass().getSimpleName() : null);
        if (this.getParenthesizedExpression() != null)
            this.getParenthesizedExpression().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @NotNull
    private ExpressionSemanticAST<?> checkParenthesizedExpression() {
        Objects.requireNonNull(this.getSyntaxAST().getParenthesizedExpression(), this.getFailedSupplier("syntaxAST.parenthesizedExpression must not be null."));
        
        final ExpressionSemanticAST<?> expressionSemanticAST = new ExpressionSemanticAST<>(
                this.getSemanticAnalyzer(),
                this.getLastContainerAST(),
                this.getSyntaxAST().getParenthesizedExpression()
        );
        
        if (expressionSemanticAST.isFailed())
            this.failed();
        return expressionSemanticAST;
    }
    
    
    @NotNull
    private TypeKind checkTypeKind() {
        if (this.getParenthesizedExpression() == null)
            return TypeKind.UNDEFINED;
        return this.getParenthesizedExpression().getTypeKind();
    }
    
}
