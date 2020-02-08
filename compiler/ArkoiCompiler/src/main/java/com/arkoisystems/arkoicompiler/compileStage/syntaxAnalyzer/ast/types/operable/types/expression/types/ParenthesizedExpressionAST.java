package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.operable.types.expressions.ParenthesizedExpressionSemantic;
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
public class ParenthesizedExpressionAST extends AbstractExpressionAST<ParenthesizedExpressionSemantic>
{
    
    @Expose
    private final SymbolToken openParenthesisToken;
    
    @Expose
    private final AbstractExpressionAST<?> abstractExpressionAST;
    
    @Expose
    private final SymbolToken closeParenthesisToken;
    
    public ParenthesizedExpressionAST(final SymbolToken openParenthesisToken, final AbstractExpressionAST<?> abstractExpressionAST, final SymbolToken closeParenthesisToken) {
        super(ASTType.PARENTHESIZED_EXPRESSION);
        
        this.abstractExpressionAST = abstractExpressionAST;
        this.closeParenthesisToken = closeParenthesisToken;
        this.openParenthesisToken = openParenthesisToken;
        
        this.setStart(openParenthesisToken.getStart());
        this.setEnd(closeParenthesisToken.getEnd());
    }
    
    @Override
    public ParenthesizedExpressionAST parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    @Override
    public TypeAST.TypeKind binMul(final SemanticAnalyzer semanticAnalyzer, final AbstractOperableAST<?, ?> rightSideOperable) {
        if (rightSideOperable instanceof NumberOperableAST)
            return TypeAST.TypeKind.combineKinds(this, rightSideOperable);
        else if (rightSideOperable instanceof AbstractExpressionAST) {
            final AbstractExpressionAST<?> abstractExpressionAST = (AbstractExpressionAST<?>) rightSideOperable;
            if (abstractExpressionAST.getOperableObject() == null) {
                semanticAnalyzer.errorHandler().addError(new ASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result is null."));
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
                    semanticAnalyzer.errorHandler().addError(new ASTError<>(rightSideOperable, "Can't perform the multiplication because the expression result isn't a number."));
                    return null;
            }
        }
        return super.binMul(semanticAnalyzer, rightSideOperable);
    }
    
}
