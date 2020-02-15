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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.EqualityExpressionSyntaxAST;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EqualityExpressionSemanticAST extends AbstractExpressionSemanticAST<EqualityExpressionSyntaxAST>
{
    
    public EqualityExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final EqualityExpressionSyntaxAST equalityExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, equalityExpressionSyntaxAST, ASTType.EQUALITY_EXPRESSION);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind getExpressionType() {
        System.out.println("Equality Expression Semantic AST");
        return null;
    }
    
    @Override
    public TypeSyntaxAST.TypeKind equal(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.equal(leftSideOperable, rightSideOperable);
    }
    
    @Override
    public TypeSyntaxAST.TypeKind notEqual(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        return super.notEqual(leftSideOperable, rightSideOperable);
    }
    
}
