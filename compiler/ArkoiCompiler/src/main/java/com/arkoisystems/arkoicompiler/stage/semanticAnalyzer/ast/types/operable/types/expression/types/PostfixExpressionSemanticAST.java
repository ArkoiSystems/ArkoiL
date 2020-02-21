/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PostfixExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

public class PostfixExpressionSemanticAST extends AbstractExpressionSemanticAST<PostfixExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> leftSideOperable;
    
    
    private TypeKind expressionType;
    
    
    public PostfixExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, postfixExpressionSyntaxAST, ASTType.POSTFIX_EXPRESSION);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            if (this.getPostfixUnaryOperator() == null)
                return null;
            if (this.getLeftSideOperable() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getPostfixUnaryOperator()) {
                case POSTFIX_ADD:
                    typeKind = this.postfixAdd(this.getLeftSideOperable());
                    break;
                case POSTFIX_SUB:
                    typeKind = this.postfixSub(this.getLeftSideOperable());
                    break;
                default:
                    typeKind = null;
                    break;
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
    
    
    public PostfixExpressionSyntaxAST.PostfixUnaryOperator getPostfixUnaryOperator() {
        return this.getSyntaxAST().getPostfixUnaryOperator();
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
        } else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
    
            if (identifierCallOperableSemanticAST.getOperableObject() == null)
                return null;
            return identifierCallOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST) {
            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST = (IdentifierInvokeOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST
                    = new IdentifierInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierInvokeOperableSyntaxAST);
    
            if (identifierInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            return identifierInvokeOperableSemanticAST;
        } else {
            this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSemanticAnalyzer().getArkoiClass(), abstractOperableSyntaxAST, "Couldn't analyze this operable because it isn't supported by the postfix expression."));
            return null;
        }
    }
    
    
    @Override
    public TypeKind postfixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> leftExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if(leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { abstractOperableSemanticAST },
                    "Couldn't analyze this postfix expression because the left side operable isn't supported by the post addition operator."
            ));
            return null;
        }
        return TypeKind.getTypeKind(leftExpressionOperable);
    }
    
    
    @Override
    public TypeKind postfixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> leftExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if(leftExpressionOperable == null) {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    new AbstractSemanticAST[] { abstractOperableSemanticAST },
                    "Couldn't analyze this postfix expression because the left side operable isn't supported by the post subtraction operator."
            ));
            return null;
        }
        return TypeKind.getTypeKind(leftExpressionOperable);
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
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST) {
            return abstractOperableSemanticAST;
        }
        return null;
    }
    
}
