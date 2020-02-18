/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.PrintStream;

@Getter
public class BinaryExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Expose
    private final AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Expose
    private final BinaryOperator binaryOperator;
    
    
    @Expose
    private final AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public BinaryExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> leftSideOperable, final BinaryOperator binaryOperator, final AbstractOperableSyntaxAST<?> rightSideOperable) {
        super(ASTType.BINARY_EXPRESSION);
        
        this.binaryOperator = binaryOperator;
        this.rightSideOperable = rightSideOperable;
        this.leftSideOperable = leftSideOperable;
    
        this.setStart(leftSideOperable.getStart());
        this.setEnd(rightSideOperable.getEnd());
    }
    
    
    @Override
    public BinaryExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getBinaryOperator());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printAST(printStream, indents + "        ");
    }
    
    
    //    @Override
    //    public TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
    //        if (rightSideOperable instanceof NumberOperableSyntaxAST)
    //            return TypeKind.combineKinds(this, rightSideOperable);
    //        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
    //            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
    //            if (abstractExpressionAST.getOperableObject() == null) {
    //                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the addition because the expression result is null."));
    //                return null;
    //            }
//
//            switch (abstractExpressionAST.getOperableObject()) {
//                case FLOAT:
//                case INTEGER:
//                case SHORT:
//                case DOUBLE:
//                case BYTE:
//                    break;
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the addition because the expression result isn't a number."));
//                    return null;
//            }
//            return TypeKind.combineKinds(this, abstractExpressionAST.getOperableObject());
//        }
//        return super.binAdd(leftSideOperable, rightSideOperable);
//    }
//
//    @Override
//    public TypeKind binSub(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
//        if (rightSideOperable instanceof NumberOperableSyntaxAST)
//            return TypeKind.combineKinds(this, rightSideOperable);
//        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
//            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
//            if (abstractExpressionAST.getOperableObject() == null) {
//                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the subtraction because the expression result is null."));
//                return null;
//            }
//
//            switch (abstractExpressionAST.getOperableObject()) {
//                case FLOAT:
//                case INTEGER:
//                case SHORT:
//                case DOUBLE:
//                case BYTE:
//                    break;
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the subtraction because the expression result isn't a number."));
//                    return null;
//            }
//            return TypeKind.combineKinds(this, abstractExpressionAST.getOperableObject());
//        }
//        return super.binSub(leftSideOperable, rightSideOperable);
//    }
//
//    @Override
//    public TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
//        if (rightSideOperable instanceof NumberOperableSyntaxAST)
//            return TypeKind.combineKinds(this, rightSideOperable);
//        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
//            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
//            if (abstractExpressionAST.getOperableObject() == null) {
//                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result is null."));
//                return null;
//            }
//
//            switch (abstractExpressionAST.getOperableObject()) {
//                case FLOAT:
//                case INTEGER:
//                case SHORT:
//                case DOUBLE:
//                case BYTE:
//                    break;
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result isn't a number."));
//                    return null;
//            }
//            return TypeKind.combineKinds(this, abstractExpressionAST.getOperableObject());
//        }
//        return super.binMul(leftSideOperable, rightSideOperable);
//    }
//
//    @Override
//    public TypeKind binDiv(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
//        if (rightSideOperable instanceof NumberOperableSyntaxAST)
//            return TypeKind.combineKinds(this, rightSideOperable);
//        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
//            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
//            if (abstractExpressionAST.getOperableObject() == null) {
//                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the division because the expression result is null."));
//                return null;
//            }
//
//            switch (abstractExpressionAST.getOperableObject()) {
//                case FLOAT:
//                case INTEGER:
//                case SHORT:
//                case DOUBLE:
//                case BYTE:
//                    break;
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the division because the expression result isn't a number."));
//                    return null;
//            }
//            return TypeKind.combineKinds(this, abstractExpressionAST.getOperableObject());
//        }
//        return super.binDiv(leftSideOperable, rightSideOperable);
//    }
//
//    @Override
//    public TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
//        if (rightSideOperable instanceof NumberOperableSyntaxAST)
//            return TypeKind.combineKinds(this, rightSideOperable);
//        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
//            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
//            if (abstractExpressionAST.getOperableObject() == null) {
//                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the modular operation because the expression result is null."));
//                return null;
//            }
//
//            switch (abstractExpressionAST.getOperableObject()) {
//                case FLOAT:
//                case INTEGER:
//                case SHORT:
//                case DOUBLE:
//                case BYTE:
//                    break;
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the modular operation because the expression result isn't a number."));
//                    return null;
//            }
//            return TypeKind.combineKinds(this, abstractExpressionAST.getOperableObject());
//        }
//        return super.binMod(leftSideOperable, rightSideOperable);
//    }
    
    public enum BinaryOperator
    {
        
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        MODULO
        
    }
    
}
