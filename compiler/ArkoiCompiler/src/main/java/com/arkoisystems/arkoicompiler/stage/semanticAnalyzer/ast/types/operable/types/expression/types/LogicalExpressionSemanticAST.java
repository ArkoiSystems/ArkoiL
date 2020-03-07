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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.LogicalExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

import java.io.PrintStream;

public class LogicalExpressionSemanticAST extends AbstractExpressionSemanticAST<LogicalExpressionSyntaxAST>
{
    
    public LogicalExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final LogicalExpressionSyntaxAST logicalExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, logicalExpressionSyntaxAST, ASTType.LOGICAL_EXPRESSION);
    }
    
    
    // TODO: Check null safety.
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) { }
    
    
    @Override
    public TypeKind getOperableObject() {
        return null;
    }
    
    
    @Override
    public TypeKind logicalAnd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.logicalAnd(leftSideOperable, rightSideOperable);
    }
    
    @Override
    public TypeKind logicalOr(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.logicalOr(leftSideOperable, rightSideOperable);
    }
    
}
