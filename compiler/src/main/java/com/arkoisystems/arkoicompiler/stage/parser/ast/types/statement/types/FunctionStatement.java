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
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Block;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.parameter.ParameterList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Type;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.Statement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class FunctionStatement extends Statement
{
    
    public static FunctionStatement GLOBAL_NODE = new FunctionStatement(null, null, null, null, null, null, null);
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "return type")
    @Nullable
    private Type returnType;
    
    @Nullable
    private ParameterList parameters;
    
    @Printable(name = "block")
    @Nullable
    private Block block;
    
    @Builder
    protected FunctionStatement(
            final @Nullable ParameterList parameters,
            final @Nullable Parser parser,
            final @Nullable IdentifierToken name,
            final @Nullable Type returnType,
            final @Nullable Block block,
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
    public FunctionStatement parseAST(final @NotNull ArkoiNode parentAST) {
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
        
        if (!ParameterList.GLOBAL_NODE.canParse(this.getParser(), 1)) {
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
        
        final ParameterList parameterList = ParameterList.builder()
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (parameterList.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.parameters = parameterList;
        
        if (this.getParser().matchesPeekToken(1, SymbolType.COLON) != null) {
            this.getParser().nextToken();
            
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
            
            final Type typeAST = Type.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (typeAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.returnType = typeAST;
        } else this.returnType = Type.builder()
                .parser(this.getParser())
                .typeToken(TypeToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .typeKind(TypeKind.AUTO)
                        .build())
                .startToken(UndefinedToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .build())
                .endToken(UndefinedToken.builder()
                        .lexer(this.getParser().getCompilerClass().getLexer())
                        .build())
                .isArray(false)
                .build();
        
        if (Block.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
            
            final Block blockAST = Block.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
            if (blockAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.block = blockAST;
        } else this.block = Block.builder()
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
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getReturnType(), "returnType must not be null.");
        return this.getReturnType().getTypeKind();
    }
    
    
    public String getFunctionDescription() {
        Objects.requireNonNull(this.getName(), "functionName must not be null.");
        Objects.requireNonNull(this.getParameters(), "functionParameters must not be null.");
        
        return this.getName().getTokenContent() + "(" + this.getParameters().getParameters().size() + ")";
    }
    
}
