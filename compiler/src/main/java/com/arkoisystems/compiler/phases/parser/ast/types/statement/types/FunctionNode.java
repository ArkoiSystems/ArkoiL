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
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class FunctionNode extends StatementNode
{
    
    public static FunctionNode GLOBAL_NODE = new FunctionNode(null, null, null, null, null, null, null, null, null);
    
    @Printable(name = "built in")
    private boolean builtin;
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "return type")
    @Nullable
    private TypeNode returnType;
    
    @Printable(name = "parameter list")
    @Nullable
    private ParameterListNode parameterList;
    
    @Printable(name = "block")
    @Nullable
    private BlockNode blockNode;
    
    @Builder
    protected FunctionNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final ParameterListNode parameterList,
            @Nullable final IdentifierToken name,
            @Nullable final TypeNode returnType,
            @Nullable final BlockNode blockNode,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
    
        this.parameterList = parameterList;
        this.returnType = returnType;
        this.blockNode = blockNode;
        this.name = name;
    }
    
    @NotNull
    @Override
    public FunctionNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.FUN) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "'fun'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        this.startAST(this.getParser().currentToken());
    
        if (this.getParser().matchesPeekToken(1, SymbolType.AT_SIGN) != null) {
            this.getParser().nextToken();
            this.builtin = true;
        }
    
        if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "<identifier>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        final IdentifierToken identifierToken = (IdentifierToken) this.getParser().nextToken();
        Objects.requireNonNull(identifierToken, "identifierToken must not be null.");
    
        this.name = identifierToken;
    
        Objects.requireNonNull(this.getCurrentScope(), "currentScope must not be null.");
        this.getCurrentScope().insert(identifierToken.getTokenContent(), this);
        this.setCurrentScope(new SymbolTable(this.getCurrentScope()));
    
        if (this.getParser().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) == null) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "'('",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        this.getParser().nextToken(2);
    
        final ParameterListNode parameterListNode = ParameterListNode.builder()
                .parentNode(this)
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .build()
                .parse();
        if (parameterListNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
    
        this.parameterList = parameterListNode;
    
        if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "')'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        if (this.getParser().matchesNextToken(SymbolType.COLON) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "':'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
    
        if (!TypeNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
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
    
        if (BlockNode.BRACE_NODE.canParse(this.getParser(), 1) ||
                BlockNode.INLINED_NODE.canParse(this.getParser(), 1)) {
            if (this.isBuiltin()) {
                final LexerToken nextToken = this.getParser().nextToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        nextToken,
                        "A builtin function can't have a block declaration."
                );
            }
    
            this.getParser().nextToken();
    
            final BlockNode blockNodeAST = BlockNode.builder()
                    .parentNode(this)
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parse();
            if (blockNodeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.blockNode = blockNodeAST;
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.FUN) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        Objects.requireNonNull(this.getReturnType(), "returnType must not be null.");
        return this.getReturnType();
    }
    
    public boolean equalsToFunction(@NotNull final FunctionNode functionNode) {
        Objects.requireNonNull(this.getParameterList(), "identifierNode.parameters must not be null.");
        Objects.requireNonNull(this.getName(), "name must not be null.");
        Objects.requireNonNull(functionNode.getParameterList(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
    
        if (!functionNode.getName().getTokenContent().equals(this.getName().getTokenContent()))
            return false;
    
        for (int index = 0; index < this.getParameterList().getParameters().size(); index++) {
            if (index >= functionNode.getParameterList().getParameters().size())
                return false;
    
            final ParameterNode ownParameter = this.getParameterList().getParameters().get(index);
            final ParameterNode otherParameter = functionNode.getParameterList().getParameters().get(index);
            if (!ownParameter.getTypeNode().equals(otherParameter.getTypeNode()))
                return false;
        }
    
        return true;
    }
    
    public boolean equalsToCall(@NotNull final FunctionCallNode functionCallNode) {
        Objects.requireNonNull(functionCallNode.getIdentifier(), "identifierNode.identifier must not be null.");
        Objects.requireNonNull(this.getParameterList(), "parameters must not be null.");
        Objects.requireNonNull(this.getName(), "name must not be null.");
    
        if (!functionCallNode.getIdentifier().getTokenContent().equals(this.getName().getTokenContent()))
            return false;
    
        Objects.requireNonNull(functionCallNode.getExpressionList(), "identifierNode.expressions must not be null.");
        for (int index = 0; index < this.getParameterList().getParameters().size(); index++) {
            if (index >= functionCallNode.getExpressionList().getExpressions().size()) {
                if (!this.getParameterList().isVariadic())
                    return false;
                break;
            }
            
            final OperableNode identifierExpression = functionCallNode.getExpressionList().getExpressions().get(index);
            final ParameterNode targetParameter = this.getParameterList().getParameters().get(index);
            if (!targetParameter.getTypeNode().equals(identifierExpression.getTypeNode()))
                return false;
        }
        
        return true;
    }
    
}
