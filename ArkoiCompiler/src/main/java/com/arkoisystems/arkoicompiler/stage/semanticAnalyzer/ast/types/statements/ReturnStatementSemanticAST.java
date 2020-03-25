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
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class ReturnStatementSemanticAST extends AbstractSemanticAST<ReturnStatementSyntaxAST>
{
    
    @Nullable
    private ExpressionSemanticAST returnExpression;
    
    
    public ReturnStatementSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NonNull final ReturnStatementSyntaxAST returnStatementSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, returnStatementSyntaxAST, ASTType.RETURN_STATEMENT);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "└── expression:");
        this.getReturnExpression().printSemanticAST(printStream, indents + "    ");
    }
    
    
    @NonNull
    public ExpressionSemanticAST getReturnExpression() {
        if (this.returnExpression == null) {
            this.returnExpression
                    = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), this.getSyntaxAST().getReturnExpression());

            this.returnExpression.getTypeKind();
            if (this.returnExpression.isFailed())
                this.failed();
        }
        return this.returnExpression;
    }
    
}
