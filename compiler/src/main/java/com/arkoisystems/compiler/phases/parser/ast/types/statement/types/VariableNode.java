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
package com.arkoisystems.compiler.phases.parser.ast.types.statement.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.compiler.phases.lexer.token.enums.OperatorType;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class VariableNode extends StatementNode
{
    
    public static VariableNode GLOBAL_NODE = new VariableNode(null, null, null, null, null, null, false, false);
    
    @Printable(name = "is constant")
    private boolean isConstant;
    
    @Printable(name = "is local")
    @Setter
    private boolean isLocal;
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "return type")
    @Nullable
    private TypeNode returnType;
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Builder
    protected VariableNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable OperableNode expression,
            final @Nullable IdentifierToken name,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken,
            final boolean isConstant,
            final boolean isLocal
    ) {
        super(parser, currentScope, startToken, endToken);
        
        this.expression = expression;
        this.isConstant = isConstant;
        this.isLocal = isLocal;
        this.name = name;
    }
    
    @NotNull
    @Override
    public VariableNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.VAR) == null && this.getParser().matchesCurrentToken(KeywordType.CONST) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Variable",
                            "'var' or 'const'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        this.isConstant = this.getParser().matchesCurrentToken(KeywordType.CONST) != null;
        
        if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Variable",
                            "<identifier>",
                            peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.name = (IdentifierToken) this.getParser().nextToken();
        
        Objects.requireNonNull(this.getCurrentScope(), "currentScope must not be null.");
        Objects.requireNonNull(this.getName(), "name must not be null.");
        this.getCurrentScope().insert(this.getName().getTokenContent(), this);
    
        if (this.getParser().matchesPeekToken(1, SymbolType.COLON) != null) {
            this.getParser().nextToken();
        
            if (this.getParser().matchesPeekToken(1, TokenType.TYPE) == null) {
                final LexerToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Variable",
                                "<identifier>",
                                peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                        )
                );
            }
        
            this.getParser().nextToken();
    
            final TypeNode typeNodeAST = TypeNode.builder()
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parse();
            if (typeNodeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
        
            this.returnType = typeNodeAST;
        }
        
        if (this.getParser().matchesPeekToken(1, OperatorType.EQUALS) != null) {
            this.getParser().nextToken();
    
            if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
                final LexerToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Variable",
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
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.VAR) != null ||
                parser.matchesPeekToken(offset, KeywordType.CONST) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        if (this.getReturnType() != null)
            return this.getReturnType();
        
        Objects.requireNonNull(this.getExpression(), "expression must not be null.");
        return this.getExpression().getTypeNode();
    }
    
}
