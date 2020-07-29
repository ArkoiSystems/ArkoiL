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
package com.arkoisystems.compiler.phases.parser.ast.types.argument;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.TypedNode;
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
public class ArgumentNode extends TypedNode
{
    
    public static ArgumentNode NAMED_NODE = new ArgumentNode(null, null, null, null, null, true, null, null, null);
    
    public static ArgumentNode UNNAMED_NODE = new ArgumentNode(null, null, null, null, null, false, null, null, null);
    
    private final boolean named;
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Builder
    protected ArgumentNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            @Nullable final IdentifierToken name,
            final boolean named,
            @Nullable final OperableNode expression,
            @Nullable final TypeNode givenType,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, givenType, endToken);
    
        this.expression = expression;
        this.named = named;
        this.name = name;
    }
    
    @NotNull
    @Override
    public ArgumentNode parse() {
        Objects.requireNonNull(this.getParser());
    
        this.startAST(this.getParser().currentToken());
    
        if (this.isNamed()) {
            if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
                final LexerToken currentToken = this.getParser().currentToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        currentToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Parameter",
                                "<identifier>",
                                currentToken != null ? currentToken.getTokenContent() : "nothing"
                        )
                );
            }
        
            final IdentifierToken identifierToken = (IdentifierToken) this.getParser().currentToken();
            Objects.requireNonNull(identifierToken, "identifierToken must not be null.");
        
            this.name = identifierToken;
        
            Objects.requireNonNull(this.getCurrentScope());
            this.getCurrentScope().insert(identifierToken.getTokenContent(), this);
        
            if (this.getParser().matchesPeekToken(1, SymbolType.COLON) == null) {
                final LexerToken nextToken = this.getParser().nextToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        nextToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Argument",
                                "':'",
                                nextToken != null ? nextToken.getTokenContent() : "nothing"
                        )
                );
            }
        
            this.getParser().nextToken(2);
        }
    
        if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Argument",
                            "<expression>",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        final OperableNode operableNode = ExpressionNode.expressionBuilder()
                .parentNode(this)
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .build()
                .parse();
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.expression = operableNode;
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return this.isNamed() ?
                (parser.matchesPeekToken(offset, TokenType.IDENTIFIER) != null && parser.matchesPeekToken(offset + 1, SymbolType.COLON) != null) :
                ExpressionNode.GLOBAL_NODE.canParse(parser, offset);
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @NotNull
    @Override
    public TypeNode getTypeNode() {
        Objects.requireNonNull(this.getExpression());
        return this.getExpression().getTypeNode();
    }
    
}
