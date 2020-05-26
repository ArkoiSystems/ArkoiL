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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.TypeToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.UndefinedToken;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.ParameterListNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.StatementNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
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
    private TypeNode returnType;
    
    @Nullable
    private ParameterListNode parameters;
    
    @Printable(name = "block")
    @Nullable
    private BlockNode block;
    
    @Builder
    protected FunctionNode(
            final @Nullable ParameterListNode parameters,
            final @Nullable Parser parser,
            final @Nullable IdentifierToken name,
            final @Nullable TypeNode returnType,
            final @Nullable BlockNode block,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.FUNCTION, startToken, endToken);
        
        this.parameters = parameters;
        this.returnType = returnType;
        this.block = block;
        this.name = name;
    }
    
    @NotNull
    @Override
    public FunctionNode parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (this.getParser().matchesCurrentToken(KeywordType.FUN) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
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
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.name = (IdentifierToken) this.getParser().nextToken();
        
        if (this.getParser().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) == null) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Function", "'('", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.getParser().nextToken();
        
        final ParameterListNode parameterListAST = ParameterListNode.builder()
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (parameterListAST.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.parameters = parameterListAST;
        this.getParser().nextToken();
        
        if (this.getParser().matchesCurrentToken(SymbolType.COLON) != null) {
            if (this.getParser().matchesPeekToken(1, TokenType.TYPE) == null) {
                final ArkoiToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                        
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Function", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                );
            }
            
            this.getParser().nextToken();
            
            final TypeNode typeAST = TypeNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (typeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.returnType = typeAST;
            this.getParser().nextToken();
        } else this.returnType = TypeNode.builder()
                .parser(this.getParser())
                .typeToken(TypeToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .typeKind(TypeKind.VOID)
                        .build())
                .startToken(UndefinedToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .build())
                .endToken(UndefinedToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .build())
                .isArray(false)
                .build();
        
        if (BlockNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            final BlockNode blockAST = BlockNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (blockAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.block = blockAST;
        } else this.block = BlockNode.builder()
                .parser(this.getParser())
                .blockType(BlockType.NATIVE)
                .startToken(this.getStartToken())
                .endToken(this.getEndToken())
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
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getReturnType(), "functionReturnType must not be null.");
        
        return this.getReturnType().getTypeKind();
    }
    
    public String getFunctionDescription() {
        Objects.requireNonNull(this.getName(), "functionName must not be null.");
        Objects.requireNonNull(this.getParameters(), "functionParameters must not be null.");
        
        return this.getName().getTokenContent() + "(" + this.getParameters().getParameters().size() + ")";
    }
    
}
