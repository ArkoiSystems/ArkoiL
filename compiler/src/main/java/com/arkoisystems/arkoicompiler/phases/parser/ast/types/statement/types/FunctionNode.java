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
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.StatementNode;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class FunctionNode extends StatementNode
{
    
    public static FunctionNode GLOBAL_NODE = new FunctionNode(null, null, null, null, null, null, null, null);
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "return type")
    @Nullable
    private TypeNode returnType;
    
    @Nullable
    private ParameterListNode parameters;
    
    @Printable(name = "block")
    @Nullable
    private BlockNode blockNode;
    
    @Builder
    protected FunctionNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable ParameterListNode parameters,
            final @Nullable IdentifierToken name,
            final @Nullable TypeNode returnType,
            final @Nullable BlockNode blockNode,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, currentScope, startToken, endToken);
    
        this.parameters = parameters;
        this.returnType = returnType;
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
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "'fun'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "<identifier>",
                            peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.name = (IdentifierToken) this.getParser().nextToken();
        
        Objects.requireNonNull(this.getCurrentScope(), "currentScope must not be null.");
        Objects.requireNonNull(this.getName(), "name must not be null.");
        this.getCurrentScope().insert(this.getName().getTokenContent(), this);
        this.setCurrentScope(new SymbolTable(this.getCurrentScope()));
        
        if (!ParameterListNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function",
                            "'('",
                            peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.getParser().nextToken();
        
        final ParameterListNode parameterListNode = ParameterListNode.builder()
                .currentScope(this.getCurrentScope())
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
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Function",
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
                    .parseAST(parentAST);
            if (typeNodeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            this.returnType = typeNodeAST;
        } else this.returnType = TypeNode.builder()
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .typeToken(TypeToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .typeKind(TypeKind.AUTO)
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
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
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
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.FUN) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    @NotNull
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getReturnType(), "returnType must not be null.");
        return this.getReturnType().getTypeKind();
    }
    
    public boolean equalsToFunction(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(this.getParameters(), "identifierNode.parameters must not be null.");
        Objects.requireNonNull(this.getName(), "name must not be null.");
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
        
        if(!functionNode.getName().getTokenContent().equals(this.getName().getTokenContent()))
            return false;
    
        for (int index = 0; index < this.getParameters().getParameters().size(); index++) {
            final ParameterNode ownParameter = this.getParameters().getParameters().get(index);
            if (index >= functionNode.getParameters().getParameters().size())
                return false;
            
            final ParserNode otherParameter = functionNode.getParameters().getParameters().get(index);
            if (ownParameter.getTypeKind() != otherParameter.getTypeKind())
                return false;
        }
        
        return true;
    }
    
    public boolean equalsToIdentifier(final @NotNull IdentifierNode identifierNode) {
        if (!identifierNode.isFunctionCall())
            return false;
    
        Objects.requireNonNull(this.getParameters(), "parameters must not be null.");
        Objects.requireNonNull(this.getName(), "name must not be null.");
        Objects.requireNonNull(identifierNode.getIdentifier(), "identifierNode.identifier must not be null.");
    
        if (!identifierNode.getIdentifier().getTokenContent().equals(this.getName().getTokenContent()))
            return false;
    
        Objects.requireNonNull(identifierNode.getExpressions(), "identifierNode.expressions must not be null.");
        for (int index = 0; index < this.getParameters().getParameters().size(); index++) {
            final ParameterNode parameterNode = this.getParameters().getParameters().get(index);
            // TODO: 6/1/20 Do better VARIADIC check
            if(parameterNode.getTypeKind() == TypeKind.VARIADIC)
                break;
            
            if (index >= identifierNode.getExpressions().getExpressions().size())
                return false;
            
            final OperableNode expressionNode = identifierNode.getExpressions().getExpressions().get(index);
            if (parameterNode.getTypeKind() != expressionNode.getTypeKind())
                return false;
        }
        
        return true;
    }
    
}
