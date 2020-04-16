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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParenthesizedExpressionAST extends ExpressionAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST parenthesizedExpression;
    
    
    protected ParenthesizedExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public ParenthesizedExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parenthesized expression", "'('", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setParenthesizedExpression(operableAST);
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.CLOSING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().peekToken(1),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parenthesized expression", "')'", this.getSyntaxAnalyzer().peekToken(1).getTokenContent()
            );
        
        this.setEndToken(this.getSyntaxAnalyzer().nextToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public static ParenthesizedExpressionASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParenthesizedExpressionASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParenthesizedExpressionASTBuilder builder() {
        return new ParenthesizedExpressionASTBuilder();
    }
    
    
    public static class ParenthesizedExpressionASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private OperableAST parenthesizedExpression;
        
        
        private AbstractToken startToken, endToken;
    
    
        public ParenthesizedExpressionASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public ParenthesizedExpressionASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public ParenthesizedExpressionASTBuilder expression(final OperableAST parenthesizedExpression) {
            this.parenthesizedExpression = parenthesizedExpression;
            return this;
        }
    
    
        public ParenthesizedExpressionASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ParenthesizedExpressionASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ParenthesizedExpressionAST build() {
            final ParenthesizedExpressionAST parenthesizedExpressionAST = new ParenthesizedExpressionAST(this.syntaxAnalyzer);
            if (this.parenthesizedExpression != null)
                parenthesizedExpressionAST.setParenthesizedExpression(this.parenthesizedExpression);
            parenthesizedExpressionAST.setStartToken(this.startToken);
            parenthesizedExpressionAST.getMarkerFactory().getCurrentMarker().setStart(parenthesizedExpressionAST.getStartToken());
            parenthesizedExpressionAST.setEndToken(this.endToken);
            parenthesizedExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(parenthesizedExpressionAST.getEndToken());
            return parenthesizedExpressionAST;
        }
        
    }
    
}
