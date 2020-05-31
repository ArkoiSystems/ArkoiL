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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.types.TypeToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.types.UndefinedToken;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.NodeType;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class FunctionNode extends StatementNode
{
    
    public static FunctionNode GLOBAL_NODE = new FunctionNode(null, null, null, null, null, null, null);
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "return type")
    @Nullable
    private TypeNode returnTypeNode;
    
    @Nullable
    private ParameterListNode parameters;
    
    @Printable(name = "block")
    @Nullable
    private BlockNode blockNode;
    
    @Builder
    protected FunctionNode(
            final @Nullable ParameterListNode parameters,
            final @Nullable Parser parser,
            final @Nullable IdentifierToken name,
            final @Nullable TypeNode returnTypeNode,
            final @Nullable BlockNode blockNode,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, startToken, endToken);
        
        this.parameters = parameters;
        this.returnTypeNode = returnTypeNode;
        this.blockNode = blockNode;
        this.name = name;
    }
    
    @NotNull
    @Override
    public FunctionNode parseAST(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.FUN) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'fun'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.name = (IdentifierToken) this.getParser().nextToken();
        
        if (!ParameterListNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'('", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.getParser().nextToken();
        
        final ParameterListNode parameterListNode = ParameterListNode.builder()
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (parameterListNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.parameters = parameterListNode;
        
        if (this.getParser().matchesPeekToken(1, SymbolType.COLON) != null) {
            this.getParser().nextToken();
            
            if (this.getParser().matchesPeekToken(1, TokenType.TYPE) == null) {
                final LexerToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                        
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Function", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                );
            }
            
            this.getParser().nextToken();
            
            final TypeNode typeNodeAST = TypeNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (typeNodeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.returnTypeNode = typeNodeAST;
        } else this.returnTypeNode = TypeNode.builder()
                .parser(this.getParser())
                .typeToken(TypeToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .nodeType(NodeType.AUTO)
                        .build())
                .startToken(UndefinedToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .dummy(true)
                        .build())
                .endToken(UndefinedToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .dummy(true)
                        .build())
                .isArray(false)
                .build();
        
        if (BlockNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
            
            final BlockNode blockNodeAST = BlockNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (blockNodeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.blockNode = blockNodeAST;
        } else this.blockNode = BlockNode.builder()
                .parser(this.getParser())
                .blockType(BlockType.NATIVE)
                .startToken(this.getStartToken())
                .endToken(this.getParser().currentToken())
                .build();
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.FUN) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public NodeType getTypeKind() {
        Objects.requireNonNull(this.getReturnTypeNode(), "returnType must not be null.");
        return this.getReturnTypeNode().getTypeKind();
    }
    
    
    public String getFunctionDescription() {
        Objects.requireNonNull(this.getName(), "functionName must not be null.");
        Objects.requireNonNull(this.getParameters(), "functionParameters must not be null.");
        
        return this.getName().getTokenContent() + "(" + this.getParameters().getParameters().size() + ")";
    }
    
}
