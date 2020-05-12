/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class BinaryExpressionAST extends ExpressionAST
{
    
    @Getter
    @Nullable
    private final OperableAST leftSideOperable;
    
    @Getter
    @Nullable
    private final BinaryOperatorType binaryOperatorType;
    
    @Getter
    @Nullable
    private OperableAST rightSideOperable;
    
    @Builder
    private BinaryExpressionAST(
            @Nullable final BinaryOperatorType binaryOperatorType,
            @Nullable final OperableAST rightSideOperable,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final OperableAST leftSideOperable,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken
    ) {
        super(syntaxAnalyzer, null, startToken, endToken, ASTType.BINARY_EXPRESSION);
        
        this.binaryOperatorType = binaryOperatorType;
        this.rightSideOperable = rightSideOperable;
        this.leftSideOperable = leftSideOperable;
    }
    
    @NotNull
    @Override
    public BinaryExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getLeftSideOperable(), "leftSideOperable must not be null.");
        
        this.startAST(this.getLeftSideOperable().getStartToken());
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        final OperableAST operableAST = this.parseMultiplicative(parentAST);
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.rightSideOperable = operableAST;
        this.endAST(this.rightSideOperable.getEndToken());
        return this;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
}
