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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PrefixExpressionAST extends ExpressionAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private PrefixOperatorType prefixOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST rightSideOperable;
    
    
    protected PrefixExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PREFIX_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public PrefixExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken(1);
        
        final OperableAST operableAST = this.parseOperable(parentAST);
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
    
    
    public static PrefixExpressionASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new PrefixExpressionASTBuilder(syntaxAnalyzer);
    }
    
    
    public static PrefixExpressionASTBuilder builder() {
        return new PrefixExpressionASTBuilder();
    }
    
    
    public static class PrefixExpressionASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private PrefixOperatorType prefixOperatorType;
        
        
        @Nullable
        private OperableAST rightSideOperable;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public PrefixExpressionASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public PrefixExpressionASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public PrefixExpressionASTBuilder right(final OperableAST rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
        
        
        public PrefixExpressionASTBuilder operator(final PrefixOperatorType prefixOperatorType) {
            this.prefixOperatorType = prefixOperatorType;
            return this;
        }
        
        
        public PrefixExpressionASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public PrefixExpressionASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public PrefixExpressionAST build() {
            final PrefixExpressionAST prefixExpressionAST = new PrefixExpressionAST(this.syntaxAnalyzer);
            if (this.rightSideOperable != null)
                prefixExpressionAST.setRightSideOperable(this.rightSideOperable);
            if (this.prefixOperatorType != null)
                prefixExpressionAST.setPrefixOperatorType(this.prefixOperatorType);
            prefixExpressionAST.setStartToken(this.startToken);
            prefixExpressionAST.getMarkerFactory().getCurrentMarker().setStart(prefixExpressionAST.getStartToken());
            prefixExpressionAST.setEndToken(this.endToken);
            prefixExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(prefixExpressionAST.getEndToken());
            return prefixExpressionAST;
        }
        
    }
    
}
