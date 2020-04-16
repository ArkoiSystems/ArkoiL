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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReturnAST extends StatementAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST returnExpression;
    
    
    protected ReturnAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.RETURN);
    }
    
    
    @NotNull
    @Override
    public ReturnAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.RETURN) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Return", "'return'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        if (!ExpressionAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer()))
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Return", "<expression>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setReturnExpression(operableAST);
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public static ReturnASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ReturnASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ReturnASTBuilder builder() {
        return new ReturnASTBuilder();
    }
    
    
    public static class ReturnASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private OperableAST operableAST;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ReturnASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ReturnASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ReturnASTBuilder operable(final OperableAST operableAST) {
            this.operableAST = operableAST;
            return this;
        }
        
        
        public ReturnASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ReturnASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ReturnAST build() {
            final ReturnAST returnAST = new ReturnAST(this.syntaxAnalyzer);
            if (this.operableAST != null)
                returnAST.setReturnExpression(this.operableAST);
            returnAST.setStartToken(this.startToken);
            returnAST.getMarkerFactory().getCurrentMarker().setStart(returnAST.getStartToken());
            returnAST.setEndToken(this.endToken);
            returnAST.getMarkerFactory().getCurrentMarker().setEnd(returnAST.getEndToken());
            return returnAST;
        }
        
    }
    
}
