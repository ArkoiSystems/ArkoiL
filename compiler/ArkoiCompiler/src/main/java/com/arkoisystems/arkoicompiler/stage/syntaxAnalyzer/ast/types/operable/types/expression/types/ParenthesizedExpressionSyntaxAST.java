package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Expose
    private final SymbolToken openParenthesis;
    
    @Expose
    private final AbstractExpressionSyntaxAST parenthesizedExpression;
    
    @Expose
    private final SymbolToken closeParenthesis;
    
    public ParenthesizedExpressionSyntaxAST(final SymbolToken openParenthesis, final AbstractExpressionSyntaxAST parenthesizedExpression, final SymbolToken closeParenthesis) {
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
    
//    @Override
//    public TypeSyntaxAST.TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
//        if (rightSideOperable instanceof NumberOperableSyntaxAST)
//            return TypeSyntaxAST.TypeKind.combineKinds(this, rightSideOperable);
//        else if (rightSideOperable instanceof AbstractExpressionSyntaxAST) {
//            final AbstractExpressionSyntaxAST abstractExpressionAST = (AbstractExpressionSyntaxAST) rightSideOperable;
//            if (abstractExpressionAST.getOperableObject() == null) {
//                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result is null."));
//                return null;
//            }
//
//            switch (abstractExpressionAST.getOperableObject()) {
//                case FLOAT:
//                    return TypeSyntaxAST.TypeKind.combineKinds(this, TypeSyntaxAST.TypeKind.FLOAT);
//                case INTEGER:
//                    return TypeSyntaxAST.TypeKind.combineKinds(this, TypeSyntaxAST.TypeKind.INTEGER);
//                case SHORT:
//                    return TypeSyntaxAST.TypeKind.combineKinds(this, TypeSyntaxAST.TypeKind.SHORT);
//                case DOUBLE:
//                    return TypeSyntaxAST.TypeKind.combineKinds(this, TypeSyntaxAST.TypeKind.DOUBLE);
//                case BYTE:
//                    return TypeSyntaxAST.TypeKind.combineKinds(this, TypeSyntaxAST.TypeKind.BYTE);
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result isn't a number."));
//                    return null;
//            }
//        }
//        return super.binMul(leftSideOperable, rightSideOperable);
//    }
    
}
