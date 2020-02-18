/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.PrintStream;

@Getter
public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Expose
    private final SymbolToken openParenthesis;
    
    
    @Expose
    private final ExpressionSyntaxAST parenthesizedExpression;
    
    
    @Expose
    private final SymbolToken closeParenthesis;
    
    
    public ParenthesizedExpressionSyntaxAST(final SymbolToken openParenthesis, final ExpressionSyntaxAST parenthesizedExpression, final SymbolToken closeParenthesis) {
        super(ASTType.PARENTHESIZED_EXPRESSION);
        
        this.parenthesizedExpression = parenthesizedExpression;
        this.closeParenthesis = closeParenthesis;
        this.openParenthesis = openParenthesis;
        
        this.setStart(openParenthesis.getStart());
        this.setEnd(closeParenthesis.getEnd());
    }
    
    
    @Override
    public ParenthesizedExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getParenthesizedExpression().getClass().getSimpleName());
        this.getParenthesizedExpression().printAST(printStream, indents + "        ");
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
//                    return TypeKind.combineKinds(this, TypeKind.FLOAT);
//                case INTEGER:
//                    return TypeKind.combineKinds(this, TypeKind.INTEGER);
//                case SHORT:
//                    return TypeKind.combineKinds(this, TypeKind.SHORT);
//                case DOUBLE:
//                    return TypeKind.combineKinds(this, TypeKind.DOUBLE);
//                case BYTE:
//                    return TypeKind.combineKinds(this, TypeKind.BYTE);
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result isn't a number."));
//                    return null;
//            }
//        }
//        return super.binMul(leftSideOperable, rightSideOperable);
//    }
    
}
