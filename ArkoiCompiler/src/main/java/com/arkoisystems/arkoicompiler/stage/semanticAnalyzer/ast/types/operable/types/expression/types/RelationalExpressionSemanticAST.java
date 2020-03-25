/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.RelationalExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class RelationalExpressionSemanticAST extends AbstractExpressionSemanticAST<RelationalExpressionSyntaxAST>
{
    
    public RelationalExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final RelationalExpressionSyntaxAST relationalExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, relationalExpressionSyntaxAST, ASTType.RELATIONAL_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
    @Nullable
    @Override
    public TypeKind getTypeKind() {
        return null;
    }
    
    
    @Nullable
    @Override
    public TypeKind relationalGreaterThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        return super.relationalGreaterThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind relationalGreaterEqualThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        return super.relationalGreaterEqualThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind relationalLessThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        return super.relationalLessThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind relationalLessEqualThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        return super.relationalLessEqualThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind relationalIs(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        return super.relationalIs(leftSideOperable, rightSideOperable);
    }
    
}
