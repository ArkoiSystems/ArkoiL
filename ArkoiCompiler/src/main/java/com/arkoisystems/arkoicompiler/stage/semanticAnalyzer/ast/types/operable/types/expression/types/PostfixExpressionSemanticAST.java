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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

import java.io.PrintStream;

public class PostfixExpressionSemanticAST extends AbstractExpressionSemanticAST<PostfixExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> leftSideOperable;
    
    
    private TypeKind expressionType;
    
    
    public PostfixExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, postfixExpressionSyntaxAST, ASTType.POSTFIX_EXPRESSION);
    }
    
    
    // TODO: Check null safety.
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSemanticAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getPostfixOperatorType());
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            if (this.getPostfixOperatorType() == null)
                return null;
            if (this.getLeftSideOperable() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getPostfixOperatorType()) {
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
    
    
    public PostfixOperatorType getPostfixOperatorType() {
        return this.getSyntaxAST().getPostfixOperatorType();
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
        } else if (abstractOperableSyntaxAST != null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.POSTFIX_OPERABLE_NOT_SUPPORTED
            );
        }
        return null;
    }
    
    
    @Override
    public TypeKind postfixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> leftExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if(leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.POSTFIX_ADD_OPERABLE_NOT_SUPPORTED
            );
            this.failed();
            return null;
        }
        return TypeKind.getTypeKind(leftExpressionOperable);
    }
    
    
    @Override
    public TypeKind postfixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        final AbstractOperableSemanticAST<?, ?> leftExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if(leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.POSTFIX_SUB_OPERABLE_NOT_SUPPORTED
            );
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
