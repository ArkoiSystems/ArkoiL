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
package com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ParenthesizedNode extends ExpressionNode
{
    
    public static ParenthesizedNode GLOBAL_NODE = new ParenthesizedNode(null, null, null, null, null);
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Builder
    protected ParenthesizedNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable OperableNode expression,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, currentScope, startToken, endToken);
        
        this.expression = expression;
    }
    
    @NotNull
    @Override
    public ParenthesizedNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Parenthesized expression",
                            "'('",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Parenthesized expression",
                            "<expression>",
                            peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.getParser().nextToken();
        
        final OperableNode operableNode = ExpressionNode.expressionBuilder()
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .build()
                .parse();
        
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.expression = operableNode;
        
        if (this.getParser().matchesPeekToken(1, SymbolType.CLOSING_PARENTHESIS) == null) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Parenthesized expression",
                            "')'",
                            peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                    )
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
    public @NotNull TypeNode getTypeNode() {
        Objects.requireNonNull(this.getExpression(), "expression must not be null.");
        return this.getExpression().getTypeNode();
    }
    
}
