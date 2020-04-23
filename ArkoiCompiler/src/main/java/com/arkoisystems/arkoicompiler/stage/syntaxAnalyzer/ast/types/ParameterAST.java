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
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ParameterParser;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ParameterAST extends ArkoiASTNode
{
    
    public static ParameterParser PARAMETER_DEFINITION_PARSER = new ParameterParser();
    
    
    @Getter
    @Nullable
    private IdentifierToken parameterName;
    
    
    @Getter
    @Nullable
    private TypeAST parameterType;
    
    
    @Builder
    private ParameterAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final IdentifierToken parameterName,
            @Nullable final TypeAST parameterType,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(null, syntaxAnalyzer, ASTType.PARAMETER, startToken, endToken);
        
        this.parameterName = parameterName;
        this.parameterType = parameterType;
        
        this.setMarkerFactory(new MarkerFactory<>(new ArkoiMarker<>(this.getAstType()), this));
    }
    
    
    @NotNull
    @Override
    public ParameterAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        this.parameterName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.COLON) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "':'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        if (!TypeAST.TYPE_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "<type>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        final TypeAST typeAST = TypeAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(typeAST.getMarkerFactory());
        
        if (typeAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.parameterType = typeAST;
        
        this.endAST(this.getSyntaxAnalyzer().currentToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getParameterType(), "parameterType must not be null.");
        
        return this.getParameterType().getTypeKind();
    }
    
}
