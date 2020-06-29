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
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.compiler.phases.semantic.routines.TypeVisitor;
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
    
    public static VariableNode GLOBAL_NODE = new VariableNode(null, null, null, null, false, false, null, false, null, null);
    
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
    
    @Printable(name = "is optional")
    private boolean isOptional;
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Builder
    protected VariableNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final OperableNode expression,
            final boolean isConstant,
            final boolean isLocal,
            @Nullable final IdentifierToken name,
            final boolean isOptional,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.expression = expression;
        this.isConstant = isConstant;
        this.isOptional = isOptional;
        this.isLocal = isLocal;
        this.name = name;
    }
    
    @NotNull
    @Override
    public VariableNode parse() {
        Objects.requireNonNull(this.getParser());
        
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
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Variable",
                            "<identifier>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        final IdentifierToken identifierToken = (IdentifierToken) this.getParser().nextToken();
        Objects.requireNonNull(identifierToken, "identifierToken must not be null.");
    
        this.name = identifierToken;
    
        Objects.requireNonNull(this.getCurrentScope());
        this.getCurrentScope().insert(identifierToken.getTokenContent(), this);
    
        if (this.getParser().matchesPeekToken(1, SymbolType.COLON) != null) {
            this.getParser().nextToken();
    
            if (!TypeNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
                final LexerToken nextToken = this.getParser().nextToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        nextToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Variable",
                                "<type>",
                                nextToken != null ? nextToken.getTokenContent() : "nothing"
                        )
                );
            }
            
            this.getParser().nextToken();
            
            final TypeNode typeNodeAST = TypeNode.builder()
                    .parentNode(this)
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parse();
            if (typeNodeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            this.returnType = typeNodeAST;
    
            if (this.getParser().matchesPeekToken(1, SymbolType.QUESTION_MARK) != null) {
                this.getParser().nextToken();
                this.isOptional = true;
            }
        }
        
        if (this.getParser().matchesPeekToken(1, OperatorType.EQUALS) != null) {
            this.getParser().nextToken();
            
            if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
                final LexerToken nextToken = this.getParser().nextToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        nextToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Variable",
                                "<expression>",
                                nextToken != null ? nextToken.getTokenContent() : "nothing"
                        )
                );
            }
            
            this.getParser().nextToken();
            
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
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.VAR) != null ||
                parser.matchesPeekToken(offset, KeywordType.CONST) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        if (this.getReturnType() != null)
            return this.getReturnType();
        if (this.getExpression() != null)
            return this.getExpression().getTypeNode();
    
        return TypeVisitor.ERROR_NODE;
    }
    
}
