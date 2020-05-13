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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CastExpressionAST extends ExpressionAST
{
    
    
    @Getter
    @Nullable
    private TypeKind typeKind;
    
    @Getter
    @Nullable
    private final OperableAST leftSideOperable;
    
    @Builder
    private CastExpressionAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final OperableAST leftSideOperable,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken,
            @Nullable final TypeKind typeKind
    ) {
        super(syntaxAnalyzer, null, startToken, endToken, ASTType.CAST_EXPRESSION);
    
        this.leftSideOperable = leftSideOperable;
        this.typeKind = typeKind;
    }
    
    @NotNull
    @Override
    public CastExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getLeftSideOperable(), "leftSideOperable must not be null.");
    
        this.startAST(this.getLeftSideOperable().getStartToken());
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) == null) {
            final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    peekedToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Cast expression", "<identifier>", peekedToken != null ? peekedToken.getData() : "nothing"
            );
        }
    
        final ArkoiToken identifierToken = this.getSyntaxAnalyzer().nextToken(false);
        if (identifierToken == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    (@Nullable ArkoiToken) null,
                
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Cast expression", "<identifier>", "nothing"
            );
        }
        
        switch (identifierToken.getData()) {
            case "i":
            case "I":
                this.typeKind = TypeKind.INT;
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
                        "Cast expression", "<cast type> (eg. i, S, d, B)", identifierToken.getData()
                );
        }
    
        this.endAST(this.getSyntaxAnalyzer().currentToken());
        return this;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
}
