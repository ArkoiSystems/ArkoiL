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
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.CollectionOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.StringOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.CollectionOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class BinaryExpressionSemanticAST extends AbstractExpressionSemanticAST<BinaryExpressionSyntaxAST>
{
    
    @Nullable
    private AbstractOperableSemanticAST<?> leftSideOperable, rightSideOperable;
    
    
    @Nullable
    private TypeKind expressionType;
    
    
    public BinaryExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, binaryExpressionSyntaxAST, ASTType.BINARY_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getLeftSideOperable());
        Objects.requireNonNull(this.getRightSideOperable());
    
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSemanticAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getBinaryOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Nullable
    @Override
    public TypeKind getTypeKind() {
        if (this.expressionType == null) {
            if (this.getLeftSideOperable() == null) {
                this.getRightSideOperable();
                return null;
            } else if (this.getRightSideOperable() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getBinaryOperatorType()) {
                case ADDITION:
                    typeKind = this.binAdd(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case SUBTRACTION:
                    typeKind = this.binSub(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case MULTIPLICATION:
                    typeKind = this.binMul(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case DIVISION:
                    typeKind = this.binDiv(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case MODULO:
                    typeKind = this.binMod(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case EXPONENTIAL:
                    typeKind = this.binExp(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                default:
                    this.addError(
                            this.getSemanticAnalyzer().getArkoiClass(),
                            this.getSyntaxAST(),
                            "Unknown binary operator."
                    );
                    return null;
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
    
    
    @NotNull
    public BinaryOperatorType getBinaryOperatorType() {
        return this.getSyntaxAST().getBinaryOperatorType();
    }
    
    
    @Nullable
    public AbstractOperableSemanticAST<?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeOperable(@NotNull final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
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
        } else if (abstractOperableSyntaxAST instanceof StringOperableSyntaxAST) {
            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) abstractOperableSyntaxAST;
            return new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
        } else if (abstractOperableSyntaxAST instanceof CollectionOperableSyntaxAST) {
            final CollectionOperableSyntaxAST collectionOperableSyntaxAST = (CollectionOperableSyntaxAST) abstractOperableSyntaxAST;
            final CollectionOperableSemanticAST collectionOperableSemanticAST
                    = new CollectionOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), collectionOperableSyntaxAST);
            
            if (collectionOperableSemanticAST.getTypeKind() == null)
                return null;
            return collectionOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof BinaryExpressionSyntaxAST) {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) abstractOperableSyntaxAST;
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                    = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
            
            if (binaryExpressionSemanticAST.getTypeKind() == null)
                return null;
            return binaryExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
            
            if (prefixExpressionSemanticAST.getTypeKind() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PostfixExpressionSyntaxAST) {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST
                    = new PostfixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), postfixExpressionSyntaxAST);
            
            if (postfixExpressionSemanticAST.getTypeKind() == null)
                return null;
            return postfixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
            
            if (identifierCallOperableSemanticAST.getTypeKind() == null)
                return null;
            return identifierCallOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof CastExpressionSyntaxAST) {
            final CastExpressionSyntaxAST castExpressionSyntaxAST = (CastExpressionSyntaxAST) abstractOperableSyntaxAST;
            final CastExpressionSemanticAST castExpressionSemanticAST
                    = new CastExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), castExpressionSyntaxAST);
            
            if (castExpressionSemanticAST.getTypeKind() == null)
                return null;
            return castExpressionSemanticAST;
        } else {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.BINARY_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
    }
    
    
    @Nullable
    @Override
    public TypeKind binAdd(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.BINARY_ADDITION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        } else if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.BINARY_ADDITION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind binSub(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.BINARY_SUBTRACTION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        } else if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.BINARY_SUBTRACTION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind binMul(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.BINARY_MULTIPLICATION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        } else if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.BINARY_MULTIPLICATION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind binDiv(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.BINARY_DIVISION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        } else if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.BINARY_DIVISION_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind binMod(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftExpressionOperable;
        if (leftSideOperable instanceof StringOperableSemanticAST)
            leftExpressionOperable = leftSideOperable;
        else leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable);
        
        final AbstractOperableSemanticAST<?> rightExpressionOperable;
        if (rightSideOperable instanceof CollectionOperableSemanticAST)
            rightExpressionOperable = rightSideOperable;
        else rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.BINARY_MODULO_OPERABLE_NOT_SUPPORTED
            );
            return null;
        } else if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.BINARY_MODULO_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
    }
    
    
    @Nullable
    @Override
    public TypeKind binExp(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        final AbstractOperableSemanticAST<?>
                leftExpressionOperable = this.analyzeNumericOperable(leftSideOperable),
                rightExpressionOperable = this.analyzeNumericOperable(rightSideOperable);
        if (leftExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.BINARY_EXPONENTIAL_OPERABLE_NOT_SUPPORTED
            );
            return null;
        } else if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.BINARY_EXPONENTIAL_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return TypeKind.combineKinds(leftExpressionOperable, rightExpressionOperable);
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
        } else if (abstractOperableSemanticAST instanceof BinaryExpressionSemanticAST) {
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST = (BinaryExpressionSemanticAST) abstractOperableSemanticAST;
            if (binaryExpressionSemanticAST.getTypeKind() == null)
                return null;
            
            switch (binaryExpressionSemanticAST.getTypeKind()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return binaryExpressionSemanticAST;
                default:
                    return null;
            }
        } else if (abstractOperableSemanticAST instanceof PostfixExpressionSemanticAST) {
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST = (PostfixExpressionSemanticAST) abstractOperableSemanticAST;
            if (postfixExpressionSemanticAST.getTypeKind() == null)
                return null;
            
            switch (postfixExpressionSemanticAST.getTypeKind()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return postfixExpressionSemanticAST;
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
        } else if (abstractOperableSemanticAST instanceof NumberOperableSemanticAST){
            return abstractOperableSemanticAST;
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
        } else if (abstractOperableSemanticAST instanceof CastExpressionSemanticAST) {
            final CastExpressionSemanticAST castExpressionSemanticAST = (CastExpressionSemanticAST) abstractOperableSemanticAST;
            if (castExpressionSemanticAST.getTypeKind() == null)
                return null;
            
            switch (castExpressionSemanticAST.getTypeKind()) {
                case BYTE:
                case FLOAT:
                case DOUBLE:
                case INTEGER:
                case SHORT:
                    return castExpressionSemanticAST;
                default:
                    return null;
            }
        }
        return null;
    }
    
}
