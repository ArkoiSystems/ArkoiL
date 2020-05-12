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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
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
    private ArkoiToken calledIdentifier;
    
    @Getter
    @Nullable
    private FunctionCallPartAST calledFunctionPart;
    
    @Getter
    @Nullable
    private IdentifierCallAST nextIdentifierCall;
    
    @Builder
    private IdentifierCallAST(
            @Nullable final FunctionCallPartAST calledFunctionPart,
            @Nullable final IdentifierCallAST nextIdentifierCall,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final ArkoiToken calledIdentifier,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken,
            final boolean isFileLocal
    ) {
        super(syntaxAnalyzer, startToken, endToken, ASTType.IDENTIFIER_CALL);
        
        this.calledFunctionPart = calledFunctionPart;
        this.nextIdentifierCall = nextIdentifierCall;
        this.calledIdentifier = calledIdentifier;
        this.isFileLocal = isFileLocal;
    }
    
    @NotNull
    @Override
    public IdentifierCallAST parseAST(@Nullable final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.THIS) != null) {
            this.isFileLocal = true;
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) == null) {
                final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        currentToken,
                        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "'.'", currentToken != null ? currentToken.getData() : "nothing"
                );
            }
            
            this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        currentToken,
                        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier>", currentToken != null ? currentToken.getData() : "nothing"
                );
            }
            
            this.getSyntaxAnalyzer().nextToken();
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Identifier call", "<identifier>", currentToken != null ? currentToken.getData() : "nothing"
            );
        }
        
        this.calledIdentifier = this.getSyntaxAnalyzer().currentToken();
        
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
    
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        peekedToken,
                
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier>", peekedToken != null ? peekedToken.getData() : "nothing"
                );
            }
    
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
    
        final StringBuilder descriptorBuilder = new StringBuilder(this.getCalledIdentifier().getData());
        if (this.getCalledFunctionPart() != null)
            descriptorBuilder.append("(").append(this.getCalledFunctionPart().getCalledExpressions().size()).append(")");
        return descriptorBuilder.toString();
    }
    
}
