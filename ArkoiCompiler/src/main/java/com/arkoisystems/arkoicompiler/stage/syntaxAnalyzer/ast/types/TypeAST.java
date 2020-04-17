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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.TypeParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TypeAST extends ArkoiASTNode
{
    
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKeywordToken typeKeywordToken;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean isArray;
    
    
    protected TypeAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.TYPE);
    }
    
    
    @NotNull
    @Override
    public TypeAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.TYPE_KEYWORD) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Type", "<type keyword>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.setTypeKeywordToken((TypeKeywordToken) this.getSyntaxAnalyzer().currentToken());
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null && this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.CLOSING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken(2);
            this.isArray = true;
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
        Objects.requireNonNull(this.getTypeKeywordToken(), "typeKeywordToken must not be null.");
        Objects.requireNonNull(this.getTypeKeywordToken().getTypeKind(), "typeKeywordToken.typeKind must not be null.");
        
        return this.getTypeKeywordToken().getTypeKind();
    }
    
    
    public static TypeASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new TypeASTBuilder(syntaxAnalyzer);
    }
    
    
    public static TypeASTBuilder builder() {
        return new TypeASTBuilder();
    }
    
    
    public static class TypeASTBuilder
    {
        
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private TypeKeywordToken typeKeywordToken;
        
        
        private boolean isArray;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public TypeASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public TypeASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public TypeASTBuilder array(final boolean isArray) {
            this.isArray = isArray;
            return this;
        }
        
        
        public TypeASTBuilder type(final TypeKeywordToken typeKeywordToken) {
            this.typeKeywordToken = typeKeywordToken;
            return this;
        }
        
        
        public TypeASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public TypeASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public TypeAST build() {
            final TypeAST typeAST = new TypeAST(this.syntaxAnalyzer);
            if (this.typeKeywordToken != null)
                typeAST.setTypeKeywordToken(this.typeKeywordToken);
            typeAST.setArray(this.isArray);
            typeAST.setStartToken(this.startToken);
            typeAST.getMarkerFactory().getCurrentMarker().setStart(typeAST.getStartToken());
            typeAST.setEndToken(this.endToken);
            typeAST.getMarkerFactory().getCurrentMarker().setEnd(typeAST.getEndToken());
            return typeAST;
        }
        
    }
    
}
