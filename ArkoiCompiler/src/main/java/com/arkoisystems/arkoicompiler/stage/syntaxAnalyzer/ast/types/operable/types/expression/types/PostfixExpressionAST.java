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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PostfixExpressionAST extends ExpressionAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private PostfixOperatorType postfixOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST leftSideOperable;
    
    
    protected PostfixExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.POSTFIX_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public PostfixExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getLeftSideOperable(), "leftSideOperable must not be null.");
        
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
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
    
    
    public static PostfixExpressionASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new PostfixExpressionASTBuilder(syntaxAnalyzer);
    }
    
    
    public static PostfixExpressionASTBuilder builder() {
        return new PostfixExpressionASTBuilder();
    }
    
    
    public static class PostfixExpressionASTBuilder
    {
    
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private PostfixOperatorType postfixOperatorType;
    
    
        @Nullable
        private OperableAST leftSideOperable;
    
    
        private ArkoiToken startToken, endToken;
    
    
        public PostfixExpressionASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public PostfixExpressionASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public PostfixExpressionASTBuilder left(final OperableAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
    
    
        public PostfixExpressionASTBuilder operator(final PostfixOperatorType postfixOperatorType) {
            this.postfixOperatorType = postfixOperatorType;
            return this;
        }
    
    
        public PostfixExpressionASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
    
    
        public PostfixExpressionASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
    
    
        public PostfixExpressionAST build() {
            final PostfixExpressionAST postfixExpressionAST = new PostfixExpressionAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                postfixExpressionAST.setLeftSideOperable(this.leftSideOperable);
            if (this.postfixOperatorType != null)
                postfixExpressionAST.setPostfixOperatorType(this.postfixOperatorType);
            postfixExpressionAST.setStartToken(this.startToken);
            postfixExpressionAST.getMarkerFactory().getCurrentMarker().setStart(postfixExpressionAST.getStartToken());
            postfixExpressionAST.setEndToken(this.endToken);
            postfixExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(postfixExpressionAST.getEndToken());
            return postfixExpressionAST;
        }
        
    }
    
}
