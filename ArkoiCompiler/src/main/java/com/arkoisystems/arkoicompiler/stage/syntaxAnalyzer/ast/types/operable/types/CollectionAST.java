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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionAST extends OperableAST
{
    
    @Getter
    @NotNull
    private final List<OperableAST> collectionExpressions;
    
    
    @Builder
    private CollectionAST(
            @Nullable final List<OperableAST> collectionExpressions,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(syntaxAnalyzer, ASTType.COLLECTION, startToken, endToken);
        
        this.collectionExpressions = collectionExpressions == null ? new ArrayList<>() : collectionExpressions;
    }
    
    
    @NotNull
    @Override
    public CollectionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Collection", "'['", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                break;
            if (!ExpressionAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer()))
                break;
            
            final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
    
            if (operableAST.isFailed()) {
                this.failed();
                return this;
            }
            
            this.getCollectionExpressions().add(operableAST);
    
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COMMA) == null)
                break;
            this.getSyntaxAnalyzer().nextToken(1);
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Collection", "']'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
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
        return TypeKind.COLLECTION;
    }
    
}

