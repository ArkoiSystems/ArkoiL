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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class IdentifierCallAST extends OperableAST
{
    
    @Getter
    @Setter
    private boolean isFileLocal;
    
    
    @Getter
    @Nullable
    private IdentifierToken calledIdentifier;
    
    
    @Getter
    @Nullable
    private FunctionCallPartAST calledFunctionPart;
    
    
    @Getter
    @Nullable
    private IdentifierCallAST nextIdentifierCall;
    
    
    @Builder
    public IdentifierCallAST(
            @Nullable final FunctionCallPartAST calledFunctionPart,
            @Nullable final IdentifierCallAST nextIdentifierCall,
            @Nullable final IdentifierToken calledIdentifier,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken,
            final boolean isFileLocal
    ) {
        super(null, syntaxAnalyzer, ASTType.IDENTIFIER_CALL, startToken, endToken);
        
        this.calledFunctionPart = calledFunctionPart;
        this.nextIdentifierCall = nextIdentifierCall;
        this.calledIdentifier = calledIdentifier;
        this.isFileLocal = isFileLocal;
        
        this.setMarkerFactory(new MarkerFactory<>(new ArkoiMarker<>(this.getAstType()), this));
    }
    
    
    @NotNull
    @Override
    public IdentifierCallAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.THIS) != null) {
            this.isFileLocal = true;
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) == null)
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "'.'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                );
            
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null)
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                );
            
            this.getSyntaxAnalyzer().nextToken();
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Identifier call", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.calledIdentifier = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null) {
            this.getSyntaxAnalyzer().nextToken();
    
            final FunctionCallPartAST functionCallPartAST = FunctionCallPartAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(this);
            this.getMarkerFactory().addFactory(functionCallPartAST.getMarkerFactory());
    
            if (functionCallPartAST.isFailed()) {
                this.failed();
                return this;
            }
    
            this.calledFunctionPart = functionCallPartAST;
        }
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null) {
            this.getSyntaxAnalyzer().nextToken();
    
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null)
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().peekToken(1),
        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier>", this.getSyntaxAnalyzer().peekToken(1).getTokenContent()
                );
    
            this.getSyntaxAnalyzer().nextToken();
    
            final IdentifierCallAST identifierCallAST = IdentifierCallAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(this);
            this.getMarkerFactory().addFactory(identifierCallAST.getMarkerFactory());
    
            if (identifierCallAST.isFailed()) {
                this.failed();
                return this;
            }
    
            this.nextIdentifierCall = identifierCallAST;
        }
        
        this.endAST(this.getSyntaxAnalyzer().currentToken());
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
    
    
    @NotNull
    public String getDescriptor() {
        Objects.requireNonNull(this.getCalledIdentifier(), "calledIdentifier must not be null.");
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
    
        final StringBuilder descriptorBuilder = new StringBuilder(this.getCalledIdentifier().getTokenContent());
        if (this.getCalledFunctionPart() != null)
            descriptorBuilder.append("(").append(this.getCalledFunctionPart().getCalledExpressions().size()).append(")");
        return descriptorBuilder.toString();
    }
    
}
