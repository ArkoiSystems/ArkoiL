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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class IdentifierCallNode extends OperableNode
{
    
    public static IdentifierCallNode GLOBAL_NODE = new IdentifierCallNode(null, null, null, null, null, null, false);
    
    @Printable(name = "file local")
    @Setter
    private boolean isFileLocal;
    
    @Printable(name = "called identifier")
    @Nullable
    private IdentifierToken identifier;
    
    @Printable(name = "function part")
    @Nullable
    private FunctionCallPartNode functionPart;
    
    @Printable(name = "next call")
    @Nullable
    private IdentifierCallNode nextIdentifierCall;
    
    @Builder
    protected IdentifierCallNode(
            final @Nullable FunctionCallPartNode functionPart,
            final @Nullable IdentifierCallNode nextIdentifierCall,
            final @Nullable IdentifierToken identifier,
            final @Nullable Parser parser,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken,
            final boolean isFileLocal
    ) {
        super(parser, ASTType.IDENTIFIER_CALL, startToken, endToken);
        
        this.nextIdentifierCall = nextIdentifierCall;
        this.functionPart = functionPart;
        this.isFileLocal = isFileLocal;
        this.identifier = identifier;
    }
    
    @NotNull
    @Override
    public IdentifierCallNode parseAST(final @Nullable ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesCurrentToken(KeywordType.THIS) != null) {
            this.isFileLocal = true;
            
            if (this.getParser().matchesPeekToken(1, SymbolType.PERIOD) == null) {
                final ArkoiToken currentToken = this.getParser().currentToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        currentToken,
                        
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "'.'", currentToken != null ? currentToken.getTokenContent() : "nothing"
                );
            }
            
            this.getParser().nextToken();
            
            if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken currentToken = this.getParser().currentToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        currentToken,
                        
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier>", currentToken != null ? currentToken.getTokenContent() : "nothing"
                );
            }
            
            this.getParser().nextToken();
        } else if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Identifier call", "<identifier>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.identifier = (IdentifierToken) this.getParser().currentToken();
        
        if (this.getParser().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null) {
            this.getParser().nextToken();
            
            final FunctionCallPartNode functionCallPartAST = FunctionCallPartNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (functionCallPartAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.functionPart = functionCallPartAST;
        }
        
        if (this.getParser().matchesPeekToken(1, SymbolType.PERIOD) != null) {
            this.getParser().nextToken();
            
            if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final ArkoiToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                        
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                );
            }
            
            this.getParser().nextToken();
            
            final IdentifierCallNode identifierCallAST = IdentifierCallNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (identifierCallAST.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            this.nextIdentifierCall = identifierCallAST;
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.THIS) != null ||
                parser.matchesPeekToken(offset, TokenType.IDENTIFIER) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
    @NotNull
    public String getDescriptor() {
        Objects.requireNonNull(this.getIdentifier(), "calledIdentifier must not be null.");
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        final StringBuilder descriptorBuilder = new StringBuilder(this.getIdentifier().getTokenContent());
        if (this.getFunctionPart() != null)
            descriptorBuilder.append("(").append(this.getFunctionPart().getExpressions().size()).append(")");
        return descriptorBuilder.toString();
    }
    
}
