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
public class PrefixExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Expose
    private final PrefixUnaryOperator prefixUnaryOperator;
    
    
    @Expose
    private final AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public PrefixExpressionSyntaxAST(final AbstractOperableSyntaxAST<?> rightSideOperable, final PrefixUnaryOperator prefixUnaryOperator, final int start) {
        super(ASTType.PREFIX_EXPRESSION);
        
        this.prefixUnaryOperator = prefixUnaryOperator;
        this.rightSideOperable = rightSideOperable;
        
        this.setStart(start);
        this.setEnd(rightSideOperable.getEnd());
    }
    
    
    @Override
    public PrefixExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── operator: " + this.getPrefixUnaryOperator());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printAST(printStream, indents + "        ");
    }
    
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
    
    public enum PrefixUnaryOperator
    {
        
        PREFIX_ADD,
        PREFIX_SUB,
        NEGATE,
        AFFIRM
        
    }
    
}
