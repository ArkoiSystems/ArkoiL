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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ParameterNode extends ArkoiNode
{
    
    public static ParameterNode GLOBAL_NODE = new ParameterNode(null, null, null, null, null);
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "type")
    @Nullable
    private TypeNode type;
    
    @Builder
    protected ParameterNode(
            final @Nullable Parser parser,
            final @Nullable IdentifierToken name,
            final @Nullable TypeNode type,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.PARAMETER, startToken, endToken);
        
        this.name = name;
        this.type = type;
    }
    
    @NotNull
    @Override
    public ParameterNode parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "<identifier>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
    
        this.startAST(this.getParser().currentToken());
        this.name = (IdentifierToken) this.getParser().currentToken();
    
        if (this.getParser().matchesPeekToken(1, SymbolType.COLON) == null) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "':'", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
    
        this.getParser().nextToken(2);
    
        if (!TypeNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
            
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Parameter", "<type>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        final TypeNode typeAST = TypeNode.builder()
                .parser(this.getParser())
                .build()
                .parseAST(parentAST);
        if (typeAST.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.type = typeAST;
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, TokenType.IDENTIFIER) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getType(), "parameterType must not be null.");
        
        return this.getType().getTypeKind();
    }
    
}
