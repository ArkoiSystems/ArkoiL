/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;

import java.io.PrintStream;

public class NumberOperableSyntaxAST extends AbstractOperableSyntaxAST<NumberToken>
{
    
    public NumberOperableSyntaxAST() {
        super(ASTType.NUMBER_OPERABLE);
    }
    
    
    @Override
    public NumberOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.NUMBER_LITERAL) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the number operable because the parsing doesn't start with a number."));
            return null;
        } else {
            this.setOperableObject((NumberToken) syntaxAnalyzer.currentToken());
            this.setStart(this.getOperableObject().getStart());
            this.setEnd(this.getOperableObject().getEnd());
        }
        return this;
    }
    
    
    @Override
    public void printAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── operable: " + this.getOperableObject().getTokenContent());
    }
    
    //    @Override
    //    public TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
    //        if (rightSideOperable instanceof NumberOperableSyntaxAST)
    //            return TypeKind.combineKinds(this, rightSideOperable);
    //        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
    //            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
    //            if (abstractExpressionAST.getOperableObject() == null) {
    //                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the addition because the expression result is null."));
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
//    public TypeKind prefixNegate(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
//        return TypeKind.getTypeKind(this);
//    }
    
}
