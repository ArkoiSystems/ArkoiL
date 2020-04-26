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

import java.util.Objects;

public class ParenthesizedExpressionAST extends ExpressionAST
{
    
    @Getter
    @Nullable
    private OperableAST parenthesizedExpression;
    
    
    @Builder
    private ParenthesizedExpressionAST(
            @Nullable final OperableAST parenthesizedExpression,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(syntaxAnalyzer, null, ASTType.PARENTHESIZED_EXPRESSION, startToken, endToken);
    
        this.parenthesizedExpression = parenthesizedExpression;
    }
    
    
    @NotNull
    @Override
    public ParenthesizedExpressionAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parenthesized expression", "'('", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        this.getSyntaxAnalyzer().nextToken();
        
        final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.parenthesizedExpression = operableAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.CLOSING_PARENTHESIS) == null) {
            final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    peekedToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parenthesized expression", "')'", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.endAST(this.getSyntaxAnalyzer().nextToken());
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
    
}
