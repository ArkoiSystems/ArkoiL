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

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ParenthesizedExpressionNode extends ExpressionNode
{
    
    public static ParenthesizedExpressionNode GLOBAL_NODE = new ParenthesizedExpressionNode(null, null, null, null);
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Builder
    protected ParenthesizedExpressionNode(
            final @Nullable OperableNode expression,
            final @Nullable Parser parser,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, null, ASTType.PARENTHESIZED_EXPRESSION, startToken, endToken);
        
        this.expression = expression;
    }
    
    @NotNull
    @Override
    public ParenthesizedExpressionNode parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parenthesized expression", "'('", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        this.getParser().nextToken();
        
        final OperableNode operableAST = ExpressionNode.expressionBuilder()
                .parser(this.getParser())
                .build()
                .parseAST(parentAST);
        
        if (operableAST.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.expression = operableAST;
        
        if (this.getParser().matchesPeekToken(1, SymbolType.CLOSING_PARENTHESIS) == null) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parenthesized expression", "')'", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.endAST(this.getParser().nextToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, SymbolType.OPENING_PARENTHESIS) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
}
