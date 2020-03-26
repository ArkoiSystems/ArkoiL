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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.PrefixExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class PrefixExpressionSemanticAST extends AbstractExpressionSemanticAST<PrefixExpressionSyntaxAST>
{
    
    @Nullable
    private AbstractOperableSemanticAST<?> rightSideOperable;
    
    
    @Nullable
    private TypeKind expressionType;
    
    
    public PrefixExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, prefixExpressionSyntaxAST, ASTType.PREFIX_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── operator: " + this.getPrefixOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Override
    public TypeKind getTypeKind() {
        if (this.expressionType == null) {
            if (this.getRightSideOperable() == null)
                return null;
            if(this.getPrefixOperatorType() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getPrefixOperatorType()) {
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
    
    
    @Nullable
    public PrefixOperatorType getPrefixOperatorType() {
        return this.getSyntaxAST().getPrefixOperatorType();
    }
    
    
    @Nullable
    public AbstractOperableSemanticAST<?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeOperable(@Nullable final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            return new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
            
            if (prefixExpressionSemanticAST.getTypeKind() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else if(abstractOperableSyntaxAST != null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.PREFIX_OPERABLE_NOT_SUPPORTED
            );
        }
        return null;
    }
    
    
    @Nullable
    @Override
    public TypeKind prefixAdd(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
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
    
    
    @Nullable
    @Override
    public TypeKind prefixSub(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
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
    
    
    @Nullable
    @Override
    public TypeKind prefixNegate(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
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
    
    
    @Nullable
    @Override
    public TypeKind prefixAffirm(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeNumericOperable(abstractOperableSemanticAST);
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
        } else if (abstractOperableSemanticAST instanceof PrefixExpressionSemanticAST) {
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST = (PrefixExpressionSemanticAST) abstractOperableSemanticAST;
            if (prefixExpressionSemanticAST.getTypeKind() == null)
                return null;
            
            switch (prefixExpressionSemanticAST.getTypeKind()) {
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
