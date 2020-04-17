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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CastExpressionAST extends ExpressionAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKind typeKind;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST leftSideOperable;
    
    
    protected CastExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.CAST_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public CastExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getLeftSideOperable(), "leftSideOperable must not be null.");
        
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().peekToken(1),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Cast expression", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        final IdentifierToken identifierToken = (IdentifierToken) this.getSyntaxAnalyzer().nextToken(false);
        switch (identifierToken.getTokenContent()) {
            case "i":
            case "I":
                this.typeKind = TypeKind.INTEGER;
                break;
            case "d":
            case "D":
                this.typeKind = TypeKind.DOUBLE;
                break;
            case "f":
            case "F":
                this.typeKind = TypeKind.FLOAT;
                break;
            case "b":
            case "B":
                this.typeKind = TypeKind.BYTE;
                break;
            case "s":
            case "S":
                this.typeKind = TypeKind.SHORT;
                break;
            default:
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        identifierToken,
        
                        SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Cast expression", "<cast type> (eg. i, S, d, B)", this.getSyntaxAnalyzer().currentToken().getTokenContent()
                );
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
    
    
    public static CastExpressionASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new CastExpressionASTBuilder(syntaxAnalyzer);
    }
    
    
    public static CastExpressionASTBuilder builder() {
        return new CastExpressionASTBuilder();
    }
    
    
    public static class CastExpressionASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private TypeKind typeKind;
        
        
        @Nullable
        private OperableAST leftSideOperable;
        
        
        private ArkoiToken startToken, endToken;
        
        
        public CastExpressionASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public CastExpressionASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public CastExpressionASTBuilder left(final OperableAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public CastExpressionASTBuilder operator(final TypeKind typeKind) {
            this.typeKind = typeKind;
            return this;
        }
        
        
        public CastExpressionASTBuilder start(final ArkoiToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public CastExpressionASTBuilder end(final ArkoiToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public CastExpressionAST build() {
            final CastExpressionAST castExpressionAST = new CastExpressionAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                castExpressionAST.setLeftSideOperable(this.leftSideOperable);
            if (this.typeKind != null)
                castExpressionAST.setTypeKind(this.typeKind);
            castExpressionAST.setStartToken(this.startToken);
            castExpressionAST.getMarkerFactory().getCurrentMarker().setStart(castExpressionAST.getStartToken());
            castExpressionAST.setEndToken(this.endToken);
            castExpressionAST.getMarkerFactory().getCurrentMarker().setEnd(castExpressionAST.getEndToken());
            return castExpressionAST;
        }
        
    }
    
}
