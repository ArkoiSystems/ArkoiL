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
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.FunctionInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PrefixExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

public class PrefixExpressionSemanticAST extends AbstractExpressionSemanticAST<PrefixExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> rightSideOperable;
    
    
    private TypeKind expressionType;
    
    
    public PrefixExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, prefixExpressionSyntaxAST, ASTType.PREFIX_EXPRESSION);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            if (this.getRightSideOperable() == null)
                return null;
            if (this.getPrefixUnaryOperator() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getPrefixUnaryOperator()) {
                case AFFIRM:
                    typeKind = this.prefixAffirm(this.getRightSideOperable());
                    break;
                case NEGATE:
                    typeKind = this.prefixNegate(this.getRightSideOperable());
                    break;
                case PREFIX_ADD:
                    typeKind = this.prefixAdd(this.getRightSideOperable());
                    break;
                case PREFIX_SUB:
                    typeKind = this.prefixSub(this.getRightSideOperable());
                    break;
                default:
                    typeKind = null;
                    break;
            }
            return (this.expressionType = typeKind);
        }
        return this.expressionType;
    }
    
    
    public PrefixOperatorType getPrefixUnaryOperator() {
        return this.getSyntaxAST().getPrefixOperatorType();
    }
    
    
    public AbstractOperableSemanticAST<?, ?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    
    private AbstractOperableSemanticAST<?, ?> analyzeOperable(final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
        
            if (numberOperableSemanticAST.getOperableObject() == null)
                return null;
            return numberOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
        
            if (prefixExpressionSemanticAST.getOperableObject() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.PREFIX_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
    }
    
    
    @Override
    public TypeKind prefixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.PREFIX_ADD_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.getTypeKind(rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind prefixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.PREFIX_SUB_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.getTypeKind(rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind prefixNegate(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.PREFIX_NEGATE_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.getTypeKind(rightExpressionOperable);
    }
    
    
    @Override
    public TypeKind prefixAffirm(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.PREFIX_AFFIRM_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.getTypeKind(rightExpressionOperable);
    }
    
    
    private AbstractOperableSemanticAST<?, ?> analyzeNumericOperable(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof ParenthesizedExpressionSemanticAST) {
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST = (ParenthesizedExpressionSemanticAST) abstractOperableSemanticAST;
            if (parenthesizedExpressionSemanticAST.getOperableObject() == null)
                return null;
            
            switch (parenthesizedExpressionSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return parenthesizedExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof FunctionInvokeOperableSemanticAST) {
            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST = (FunctionInvokeOperableSemanticAST) abstractOperableSemanticAST;
            if (functionInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            
            switch (functionInvokeOperableSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return functionInvokeOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof IdentifierCallOperableSemanticAST) {
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) abstractOperableSemanticAST;
            if (identifierCallOperableSemanticAST.getOperableObject() == null)
                return null;
            
            switch (identifierCallOperableSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return identifierCallOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof IdentifierInvokeOperableSemanticAST) {
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST = (IdentifierInvokeOperableSemanticAST) abstractOperableSemanticAST;
            if (identifierInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            
            switch (identifierInvokeOperableSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return identifierInvokeOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof PrefixExpressionSemanticAST) {
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST = (PrefixExpressionSemanticAST) abstractOperableSemanticAST;
            if (prefixExpressionSemanticAST.getOperableObject() == null)
                return null;
    
            switch (prefixExpressionSemanticAST.getOperableObject()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return prefixExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST) {
            return abstractOperableSemanticAST;
        } else return null;
    }
    
}
