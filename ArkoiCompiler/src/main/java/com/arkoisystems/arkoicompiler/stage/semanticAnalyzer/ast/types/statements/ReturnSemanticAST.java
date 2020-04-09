/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 08, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class ReturnSemanticAST extends ArkoiSemanticAST<ReturnSyntaxAST>
{
    
    @Getter
    @NotNull
    private final ExpressionSemanticAST<?> returnExpression = this.computeReturnExpression();
    
    
    public ReturnSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final ReturnSyntaxAST syntaxAST) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, ASTType.RETURN);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s└── expression:%n", indents);
        this.getReturnExpression().printSemanticAST(printStream, indents + "    ");
    }
    
    
    @NotNull
    private ExpressionSemanticAST<?> computeReturnExpression() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getArkoiClass(), this.getFailedSupplier("semanticAnalyzer.arkoiClass must not be null"));
        Objects.requireNonNull(this.getSyntaxAST().getReturnExpression(), this.getFailedSupplier("syntaxAST.returnExpression must not be null"));
        
        final ExpressionSemanticAST<?> expressionSemanticAST = new ExpressionSemanticAST<>(
                this.getSemanticAnalyzer(),
                this.getLastContainerAST(),
                this.getSyntaxAST().getReturnExpression()
        );
        
        if (expressionSemanticAST.isFailed())
            this.failed();
        return expressionSemanticAST;
    }
    
}
