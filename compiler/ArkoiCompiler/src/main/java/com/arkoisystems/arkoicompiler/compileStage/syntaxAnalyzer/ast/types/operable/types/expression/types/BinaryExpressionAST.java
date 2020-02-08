package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.types.expressions.BinaryExpressionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.TypeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.NumberOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionAST;
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
public class BinaryExpressionAST extends AbstractExpressionAST<BinaryExpressionSemantic>
{
    
    @Expose
    private final AbstractOperableAST<?, ?> leftSideAST;
    
    @Expose
    private final BinaryOperator binaryOperator;
    
    @Expose
    private final AbstractOperableAST<?, ?> rightSideAST;
    
    
    public BinaryExpressionAST(final AbstractOperableAST<?, ?> leftSideAST, final BinaryOperator binaryOperator, final AbstractOperableAST<?, ?> rightSideAST) {
        super(ASTType.BINARY_EXPRESSION);
        
        this.binaryOperator = binaryOperator;
        this.rightSideAST = rightSideAST;
        this.leftSideAST = leftSideAST;
        
        this.setStart(leftSideAST.getStart());
        this.setEnd(rightSideAST.getEnd());
    }
    
    @Override
    public BinaryExpressionAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    @Override
    public TypeAST.TypeKind binAdd(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        if (rightSideOperable instanceof NumberOperableAST)
            return TypeAST.TypeKind.combineKinds(this, rightSideOperable);
        else if (rightSideOperable instanceof AbstractExpressionAST) {
            final AbstractExpressionAST<?> abstractExpressionAST = (AbstractExpressionAST<?>) rightSideOperable;
            if (abstractExpressionAST.getOperableObject() == null) {
                semanticAnalyzer.errorHandler().addError(new ASTError<>(rightSideOperable, "Can't perform the addition because the expression result is null."));
                return null;
            }
            
            switch (abstractExpressionAST.getOperableObject()) {
                case FLOAT:
                    return TypeAST.TypeKind.combineKinds(this, TypeAST.TypeKind.FLOAT);
                case INTEGER:
                    return TypeAST.TypeKind.combineKinds(this, TypeAST.TypeKind.INTEGER);
                case SHORT:
                    return TypeAST.TypeKind.combineKinds(this, TypeAST.TypeKind.SHORT);
                case DOUBLE:
                    return TypeAST.TypeKind.combineKinds(this, TypeAST.TypeKind.DOUBLE);
                case BYTE:
                    return TypeAST.TypeKind.combineKinds(this, TypeAST.TypeKind.BYTE);
                default:
                    semanticAnalyzer.errorHandler().addError(new ASTError<>(rightSideOperable, "Can't perform the addition because the expression result isn't a number."));
                    return null;
            }
        }
        return super.binAdd(semanticAnalyzer, rightSideOperable);
    }
    
    public enum BinaryOperator
    {
        
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        MODULO
        
    }
    
}
