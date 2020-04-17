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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class BinaryExpressionAST extends ExpressionAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BinaryOperatorType binaryOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST rightSideOperable;
    
    
    protected BinaryExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.BINARY_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public BinaryExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getLeftSideOperable(), "leftSideOperable must not be null.");
        
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        final OperableAST operableAST = this.parseMultiplicative(parentAST);
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setRightSideOperable(operableAST);
        
        this.setEndToken(this.getRightSideOperable().getEndToken());
        this.getMarkerFactory().done(this.getEndToken());
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
    
    
    public static BinaryExpressionBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new BinaryExpressionBuilder(syntaxAnalyzer);
    }
    
    
    public static BinaryExpressionBuilder builder() {
        return new BinaryExpressionBuilder();
    }
    
    
    public static class BinaryExpressionBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private OperableAST leftSideOperable;
        
        
        @Nullable
        private BinaryOperatorType binaryOperatorType;
    
    
        @Nullable
        private OperableAST rightSideOperable;
        
        
        private ArkoiToken startToken, endToken;
    
    
        public BinaryExpressionBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public BinaryExpressionBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public BinaryExpressionBuilder left(final OperableAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
    
    
        public BinaryExpressionBuilder operator(final BinaryOperatorType binaryOperatorType) {
            this.binaryOperatorType = binaryOperatorType;
            return this;
        }
    
    
        public BinaryExpressionBuilder right(final OperableAST rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
    
    
        public BinaryExpressionBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public BinaryExpressionBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public BinaryExpressionAST build() {
            final BinaryExpressionAST binaryExpressionAST = new BinaryExpressionAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                binaryExpressionAST.setLeftSideOperable(this.leftSideOperable);
            if (this.binaryOperatorType != null)
                binaryExpressionAST.setBinaryOperatorType(this.binaryOperatorType);
            if (this.rightSideOperable != null)
                binaryExpressionAST.setRightSideOperable(this.rightSideOperable);
            binaryExpressionAST.setStartToken(this.startToken);
            binaryExpressionAST.getMarkerFactory().getCurrentMarker().setStart(binaryExpressionAST.getStartToken());
            binaryExpressionAST.setEndToken(this.endToken);
            binaryExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(binaryExpressionAST.getEndToken());
            return binaryExpressionAST;
        }
        
    }
    
}
