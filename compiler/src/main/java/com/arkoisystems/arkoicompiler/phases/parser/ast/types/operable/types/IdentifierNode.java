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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.arkoicompiler.phases.semantic.routines.ScopeVisitor;
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class IdentifierNode extends OperableNode
{
    
    public static IdentifierNode GLOBAL_NODE = new IdentifierNode(null, null, null, null, null, null, null, false);
    
    @Printable(name = "file local")
    @Setter
    private boolean isFileLocal;
    
    @Printable(name = "called identifier")
    @Nullable
    private IdentifierToken identifier;
    
    @Printable(name = "is function call")
    private boolean isFunctionCall;
    
    @Printable(name = "expressions")
    @Nullable
    private ExpressionListNode expressionListNode;
    
    @Printable(name = "next call")
    @Nullable
    private IdentifierNode nextIdentifier;
    
    @Builder
    protected IdentifierNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable IdentifierNode nextIdentifier,
            final @Nullable ExpressionListNode expressionListNode,
            final @Nullable IdentifierToken identifier,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken,
            final boolean isFileLocal
    ) {
        super(parser, currentScope, startToken, endToken);
    
        this.nextIdentifier = nextIdentifier;
        this.expressionListNode = expressionListNode;
        this.isFileLocal = isFileLocal;
        this.identifier = identifier;
    }
    
    @NotNull
    @Override
    public IdentifierNode parseAST(final @Nullable ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesCurrentToken(KeywordType.THIS) != null) {
            this.isFileLocal = true;
            
            if (this.getParser().matchesPeekToken(1, SymbolType.PERIOD) == null) {
                final LexerToken currentToken = this.getParser().currentToken();
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
                final LexerToken currentToken = this.getParser().currentToken();
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
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Identifier call", "<identifier>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.identifier = (IdentifierToken) this.getParser().currentToken();
        
        if(ExpressionListNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
            
            this.isFunctionCall = true;
            
            final ExpressionListNode expressionListNode = ExpressionListNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (expressionListNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            this.expressionListNode = expressionListNode;
        }
        
        
        if (this.getParser().matchesPeekToken(1, SymbolType.PERIOD) != null) {
            this.getParser().nextToken();
            
            if(!IdentifierNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
                final LexerToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
            
                        ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                        "Identifier call", "<identifier call>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                );
            }
    
            this.getParser().nextToken();
    
            final IdentifierNode identifierOperable = IdentifierNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (identifierOperable.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            this.nextIdentifier = identifierOperable;
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
    @NotNull
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        final ScopeVisitor scopeVisitor = new ScopeVisitor(this.getParser().getCompilerClass().getSemantic());
        scopeVisitor.visit(this.getParser().getRootNodeAST());
        
        final ParserNode resultNode = scopeVisitor.visit(this);
        if (scopeVisitor.isFailed())
            this.setFailed(true);
        if (resultNode != null)
            return resultNode.getTypeKind();
        return TypeKind.UNDEFINED;
    }
    
    @NotNull
    public String getDescriptor() {
        Objects.requireNonNull(this.getIdentifier(), "calledIdentifier must not be null.");
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        final StringBuilder descriptorBuilder = new StringBuilder(this.getIdentifier().getTokenContent());
        if (this.isFunctionCall()) {
            Objects.requireNonNull(this.getExpressionListNode(), "expressionList must not be null.");
            descriptorBuilder.append("(").append(this.getExpressionListNode().getExpressions().size()).append(")");
        }
        
        return descriptorBuilder.toString();
    }
    
}
