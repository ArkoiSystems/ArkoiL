/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.CastOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.CastExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Setter;

@Setter
public class CastExpressionSemanticAST extends AbstractExpressionSemanticAST<CastExpressionSyntaxAST>
{
    
    
    private AbstractOperableSemanticAST<?, ?> leftSideOperable;
    
    private TypeKind expressionType;
    
    public CastExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final CastExpressionSyntaxAST castExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, castExpressionSyntaxAST, ASTType.CAST_EXPRESSION);
    }
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            if (this.getCastOperator() == null)
                return null;
            if (this.getLeftSideOperable() == null)
                return null;
            
            final TypeKind typeKind = TypeKind.getTypeKind(this.getCastOperator());
            if(this.getLeftSideOperable() instanceof NumberOperableSemanticAST) {
                final NumberOperableSemanticAST numberOperableSemanticAST = (NumberOperableSemanticAST) this.getLeftSideOperable();
                numberOperableSemanticAST.setOperableType(typeKind);
            }
            return (this.expressionType = typeKind);
        }
        return this.expressionType;
    }
    
    
    public AbstractOperableSemanticAST<?, ?> getLeftSideOperable() {
        if (this.leftSideOperable == null)
            return (this.leftSideOperable = this.analyzeOperable(this.getSyntaxAST().getLeftSideOperable()));
        return this.leftSideOperable;
    }
    
    public CastOperatorType getCastOperator() {
        return this.getSyntaxAST().getCastOperatorType();
    }
    
    private AbstractOperableSemanticAST<?, ?> analyzeOperable(final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        if (abstractOperableSyntaxAST instanceof ParenthesizedExpressionSyntaxAST) {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) abstractOperableSyntaxAST;
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST
                    = new ParenthesizedExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), parenthesizedExpressionSyntaxAST);
            
            if (parenthesizedExpressionSemanticAST.getOperableObject() == null)
                return null;
            return parenthesizedExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
            
            if (numberOperableSemanticAST.getOperableObject() == null)
                return null;
            return numberOperableSemanticAST;
        } else {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.CAST_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
    }
    
}
