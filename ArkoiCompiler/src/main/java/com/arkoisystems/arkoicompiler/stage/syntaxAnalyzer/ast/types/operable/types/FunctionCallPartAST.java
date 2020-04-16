/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 24, 2020
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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionCallPartAST extends OperableAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<OperableAST> calledExpressions = new ArrayList<>();
    
    
    protected FunctionCallPartAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION_CALL_PART);
    }
    
    
    @NotNull
    @Override
    public FunctionCallPartAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function call", "'('", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
    
        this.getSyntaxAnalyzer().nextToken();
    
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) != null)
                break;
            if (!ExpressionAST.EXPRESSION_PARSER.canParse(this, getSyntaxAnalyzer()))
                break;
        
            final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
            if (operableAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.getCalledExpressions().add(operableAST);
        
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COMMA) == null)
                break;
            this.getSyntaxAnalyzer().nextToken();
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function call", "')'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
    
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    @Override
    public TypeKind getTypeKind() {
        throw new NullPointerException();
    }
    
    
    public static FunctionCallPartASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new FunctionCallPartASTBuilder(syntaxAnalyzer);
    }
    
    
    public static FunctionCallPartASTBuilder builder() {
        return new FunctionCallPartASTBuilder();
    }
    
    
    public static class FunctionCallPartASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<OperableAST> calledExpressions;
        
        
        private AbstractToken startToken, endToken;
        
        
        public FunctionCallPartASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public FunctionCallPartASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public FunctionCallPartASTBuilder expressions(final List<OperableAST> expressions) {
            this.calledExpressions = expressions;
            return this;
        }
        
        
        public FunctionCallPartASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public FunctionCallPartASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public FunctionCallPartAST build() {
            final FunctionCallPartAST functionCallPartAST = new FunctionCallPartAST(this.syntaxAnalyzer);
            if (this.calledExpressions != null)
                functionCallPartAST.setCalledExpressions(this.calledExpressions);
            functionCallPartAST.setStartToken(this.startToken);
            functionCallPartAST.getMarkerFactory().getCurrentMarker().setStart(functionCallPartAST.getStartToken());
            functionCallPartAST.setEndToken(this.endToken);
            functionCallPartAST.getMarkerFactory().getCurrentMarker().setEnd(functionCallPartAST.getEndToken());
            return functionCallPartAST;
        }
        
    }
    
}
