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
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.visitors.ScopeVisitor;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.visitors.TypeVisitor;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
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
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken calledIdentifier;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private FunctionCallPartAST calledFunctionPart;
    
    
    @Getter
    @Setter
    @Nullable
    private IdentifierCallAST nextIdentifierCall;
    
    
    protected IdentifierCallAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IDENTIFIER_CALL);
    }
    
    
    @NotNull
    @Override
    public IdentifierCallAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
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
        
        this.setCalledIdentifier((IdentifierToken) this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null) {
            this.getSyntaxAnalyzer().nextToken();
    
            final FunctionCallPartAST functionCallPartAST = new FunctionCallPartAST(this.getSyntaxAnalyzer()).parseAST(this);
            this.getMarkerFactory().addFactory(functionCallPartAST.getMarkerFactory());
    
            if (functionCallPartAST.isFailed()) {
                this.failed();
                return this;
            }
    
            this.setCalledFunctionPart(functionCallPartAST);
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
    
            final IdentifierCallAST identifierCallAST = new IdentifierCallAST(this.getSyntaxAnalyzer()).parseAST(this);
            this.getMarkerFactory().addFactory(identifierCallAST.getMarkerFactory());
    
            if (identifierCallAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.setNextIdentifierCall(identifierCallAST);
        }
        
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
    
    
    @NotNull
    public String getDescriptor() {
        Objects.requireNonNull(this.getCalledIdentifier(), "calledIdentifier must not be null.");
        return this.getCalledIdentifier().getTokenContent() + (this.getCalledFunctionPart() != null ? "()" : "");
    }
    
    
    public static IdentifierCallASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new IdentifierCallASTBuilder(syntaxAnalyzer);
    }
    
    
    public static IdentifierCallASTBuilder builder() {
        return new IdentifierCallASTBuilder();
    }
    
    
    public static class IdentifierCallASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private boolean isFileLocal;
        
        
        @Nullable
        private IdentifierToken calledIdentifier;
        
        
        @Nullable
        private FunctionCallPartAST calledFunctionPart;
        
        
        @Nullable
        private IdentifierCallAST nextIdentifierCall;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public IdentifierCallASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public IdentifierCallASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public IdentifierCallASTBuilder fileLocal(final boolean isFileLocal) {
            this.isFileLocal = isFileLocal;
            return this;
        }
        
        
        public IdentifierCallASTBuilder called(final IdentifierToken calledIdentifier) {
            this.calledIdentifier = calledIdentifier;
            return this;
        }
        
        
        public IdentifierCallASTBuilder functionPart(final FunctionCallPartAST calledFunctionPart) {
            this.calledFunctionPart = calledFunctionPart;
            return this;
        }
        
        
        public IdentifierCallASTBuilder nextCall(final IdentifierCallAST nextIdentifierCall) {
            this.nextIdentifierCall = nextIdentifierCall;
            return this;
        }
        
        
        public IdentifierCallASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public IdentifierCallASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public IdentifierCallAST build() {
            final IdentifierCallAST identifierCallAST = new IdentifierCallAST(this.syntaxAnalyzer);
            identifierCallAST.setFileLocal(this.isFileLocal);
            if (this.calledIdentifier != null)
                identifierCallAST.setCalledIdentifier(this.calledIdentifier);
            if (this.calledFunctionPart != null)
                identifierCallAST.setCalledFunctionPart(this.calledFunctionPart);
            if (this.nextIdentifierCall != null)
                identifierCallAST.setNextIdentifierCall(this.nextIdentifierCall);
            identifierCallAST.setStartToken(this.startToken);
            identifierCallAST.getMarkerFactory().getCurrentMarker().setStart(identifierCallAST.getStartToken());
            identifierCallAST.setEndToken(this.endToken);
            identifierCallAST.getMarkerFactory().getCurrentMarker().setEnd(identifierCallAST.getEndToken());
            return identifierCallAST;
        }
        
    }
    
}
