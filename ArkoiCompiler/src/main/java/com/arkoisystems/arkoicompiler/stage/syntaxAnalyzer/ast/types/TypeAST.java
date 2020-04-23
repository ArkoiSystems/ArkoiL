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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.TypeParser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TypeAST extends ArkoiASTNode
{
    
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    @Getter
    @Nullable
    private TypeKeywordToken typeKeywordToken;
    
    
    @Getter
    private boolean isArray;
    
    
    @Builder
    private TypeAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final TypeKeywordToken typeKeywordToken,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken,
            final boolean isArray
    ) {
        super(null, syntaxAnalyzer, ASTType.TYPE, startToken, endToken);
    
        this.typeKeywordToken = typeKeywordToken;
        this.isArray = isArray;
        
        this.setMarkerFactory(new MarkerFactory<>(new ArkoiMarker<>(this.getAstType()), this));
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
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        this.typeKeywordToken = (TypeKeywordToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null && this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.CLOSING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken(2);
            this.isArray = true;
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
        Objects.requireNonNull(this.getTypeKeywordToken(), "typeKeywordToken must not be null.");
        Objects.requireNonNull(this.getTypeKeywordToken().getTypeKind(), "typeKeywordToken.typeKind must not be null.");
        
        return this.getTypeKeywordToken().getTypeKind();
    }
    
}
