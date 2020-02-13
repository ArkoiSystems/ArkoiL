package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
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
    
//    @Override
//    public TypeSyntaxAST.TypeKind binMul(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableSyntaxAST<?> rightSideOperable) {
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
//                case INTEGER:
//                case SHORT:
//                case DOUBLE:
//                case BYTE:
//                    break;
//                default:
//                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result isn't a number."));
//                    return null;
//            }
//            return TypeSyntaxAST.TypeKind.combineKinds(this, abstractExpressionAST.getOperableObject());
//        }
//        return super.binMul(semanticAnalyzer, rightSideOperable);
//    }
    
    public enum PrefixUnaryOperator
    {
        
        PREFIX_ADD,
        PREFIX_SUB,
        NEGATE,
        AFFIRM
        
    }
    
}
