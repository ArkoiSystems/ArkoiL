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

public class RelationalExpressionSemanticAST extends AbstractExpressionSemanticAST<RelationalExpressionSyntaxAST>
{
    
    public RelationalExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final RelationalExpressionSyntaxAST relationalExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, relationalExpressionSyntaxAST, ASTType.RELATIONAL_EXPRESSION);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        System.out.println("Relational Expression Semantic AST");
        return null;
    }
    
    
    @Override
    public TypeKind relationalGreaterThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.relationalGreaterThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Override
    public TypeKind relationalGreaterEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.relationalGreaterEqualThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Override
    public TypeKind relationalLessThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.relationalLessThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Override
    public TypeKind relationalLessEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.relationalLessEqualThan(leftSideOperable, rightSideOperable);
    }
    
    
    @Override
    public TypeKind relationalIs(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.relationalIs(leftSideOperable, rightSideOperable);
    }
    
}
