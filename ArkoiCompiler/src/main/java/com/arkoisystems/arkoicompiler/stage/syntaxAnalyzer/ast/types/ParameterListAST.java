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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParameterListAST extends ArkoiASTNode
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ParameterAST> parameters = new ArrayList<>();
    
    
    protected ParameterListAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARAMETER_LIST);
    }
    
    
    @NotNull
    @Override
    public ParameterListAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter list", "'('", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
    
        this.getSyntaxAnalyzer().nextToken();
    
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (!ParameterAST.PARAMETER_DEFINITION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
                break;
    
            final ParameterAST parameterAST = ParameterAST.PARAMETER_DEFINITION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(parameterAST.getMarkerFactory());
    
            if (parameterAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.getParameters().add(parameterAST);
    
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
                    "Parameter list", "')'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public static ParameterListASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParameterListASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParameterListASTBuilder builder() {
        return new ParameterListASTBuilder();
    }
    
    
    public static class ParameterListASTBuilder
    {
        
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<ParameterAST> parameters;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ParameterListASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ParameterListASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ParameterListASTBuilder parameters(final List<ParameterAST> parameters) {
            this.parameters = parameters;
            return this;
        }
        
        
        public ParameterListASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ParameterListASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ParameterListAST build() {
            final ParameterListAST parameterListAST = new ParameterListAST(this.syntaxAnalyzer);
            if (this.parameters != null)
                parameterListAST.setParameters(this.parameters);
            parameterListAST.setStartToken(this.startToken);
            parameterListAST.getMarkerFactory().getCurrentMarker().setStart(parameterListAST.getStartToken());
            parameterListAST.setEndToken(this.endToken);
            parameterListAST.getMarkerFactory().getCurrentMarker().setEnd(parameterListAST.getEndToken());
            return parameterListAST;
        }
        
    }
    
}
