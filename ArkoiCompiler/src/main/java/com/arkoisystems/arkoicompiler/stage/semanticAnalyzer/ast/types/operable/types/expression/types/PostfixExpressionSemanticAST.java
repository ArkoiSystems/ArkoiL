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
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ParenthesizedExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PostfixExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class PostfixExpressionSemanticAST extends AbstractExpressionSemanticAST<PostfixExpressionSyntaxAST>
{
    
    @Nullable
    private AbstractOperableSemanticAST<?> leftSideOperable;
    
    
    @Nullable
    private TypeKind expressionType;
    
    
    public PostfixExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, postfixExpressionSyntaxAST, ASTType.POSTFIX_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSemanticAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getPostfixOperatorType());
    }
    
    
    @Nullable
    @Override
    public TypeKind getTypeKind() {
        if (this.expressionType == null) {
            if (this.getLeftSideOperable() == null)
                return null;
            if(this.getPostfixOperatorType() == null)
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
    
    
    @Nullable
    public AbstractOperableSemanticAST<?> getLeftSideOperable() {
        if (this.leftSideOperable == null)
            return (this.leftSideOperable = this.analyzeOperable(this.getSyntaxAST().getLeftSideOperable()));
        return this.leftSideOperable;
    }
    
    
    @Nullable
    public PostfixOperatorType getPostfixOperatorType() {
        return this.getSyntaxAST().getPostfixOperatorType();
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeOperable(@Nullable final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        if (abstractOperableSyntaxAST instanceof ParenthesizedExpressionSyntaxAST) {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) abstractOperableSyntaxAST;
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST
                    = new ParenthesizedExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), parenthesizedExpressionSyntaxAST);
            
            if (parenthesizedExpressionSemanticAST.getTypeKind() == null)
                return null;
            return parenthesizedExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            return new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
        } else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
            
            if (identifierCallOperableSemanticAST.getTypeKind() == null)
                return null;
            return identifierCallOperableSemanticAST;
        } else if (abstractOperableSyntaxAST != null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.POSTFIX_OPERABLE_NOT_SUPPORTED
            );
        }
        return null;
    }
    
    
    @Nullable
    @Override
    public TypeKind postfixAdd(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (leftExpressionOperable == null) {
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
    
    
    @Nullable
    @Override
    public TypeKind postfixSub(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSemanticAST,
                    SemanticErrorType.POSTFIX_SUB_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.getTypeKind(leftExpressionOperable);
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeNumericOperable(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof ParenthesizedExpressionSemanticAST) {
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST = (ParenthesizedExpressionSemanticAST) abstractOperableSemanticAST;
            if (parenthesizedExpressionSemanticAST.getTypeKind() == null)
                return null;
            
            switch (parenthesizedExpressionSemanticAST.getTypeKind()) {
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
            if (identifierCallOperableSemanticAST.getTypeKind() == null)
                return null;
            
            switch (identifierCallOperableSemanticAST.getTypeKind()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return identifierCallOperableSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST) {
            return abstractOperableSemanticAST;
        } else return null;
    }
    
}
