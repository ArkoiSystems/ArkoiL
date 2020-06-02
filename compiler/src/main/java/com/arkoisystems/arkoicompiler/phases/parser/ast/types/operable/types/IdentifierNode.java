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
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    
    @Printable(name = "function call")
    private boolean isFunctionCall;
    
    @Printable(name = "expressions")
    @Nullable
    private ExpressionListNode expressions;
    
    @Printable(name = "next call")
    @Nullable
    private IdentifierNode nextIdentifier;
    
    @Builder
    protected IdentifierNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable IdentifierNode nextIdentifier,
            final @Nullable ExpressionListNode expressions,
            final @Nullable IdentifierToken identifier,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken,
            final boolean isFileLocal
    ) {
        super(parser, currentScope, startToken, endToken);
    
        this.nextIdentifier = nextIdentifier;
        this.expressions = expressions;
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
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Identifier call",
                                "'.'",
                                currentToken != null ? currentToken.getTokenContent() : "nothing"
                        )
                );
            }
            
            this.getParser().nextToken();
            
            if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                final LexerToken currentToken = this.getParser().currentToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        currentToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Identifier call",
                                "<identifier>",
                                currentToken != null ? currentToken.getTokenContent() : "nothing"
                        )
                );
            }
            
            this.getParser().nextToken();
        } else if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Identifier call",
                            "<identifier>",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.identifier = (IdentifierToken) this.getParser().currentToken();
        
        if(ExpressionListNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
            
            this.isFunctionCall = true;
            
            final ExpressionListNode expressionListNode = ExpressionListNode.builder()
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parseAST(this);
            if (expressionListNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            this.expressions = expressionListNode;
        }
        
        
        if (this.getParser().matchesPeekToken(1, SymbolType.PERIOD) != null) {
            this.getParser().nextToken();
    
            if (!IdentifierNode.GLOBAL_NODE.canParse(this.getParser(), 1) ||
                    this.getParser().matchesPeekToken(1, KeywordType.THIS) != null) {
                final LexerToken peekedToken = this.getParser().peekToken(1);
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        peekedToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Identifier call",
                                "<identifier call>",
                                peekedToken != null ? peekedToken.getTokenContent() : "nothing"
                        )
                );
            }
    
            this.getParser().nextToken();
    
            final IdentifierNode identifierOperable = IdentifierNode.builder()
                    .currentScope(this.getCurrentScope())
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
        Objects.requireNonNull(this.getIdentifier(), "identifier must not be null.");
    
        if (this.isFileLocal()) {
            Objects.requireNonNull(this.getParser().getRootNode().getCurrentScope(), "parser.rootNode.currentScope must not be null.");
        
            final List<ParserNode> nodes = this.getParser().getRootNode().getCurrentScope().lookupScope(this.getIdentifier().getTokenContent());
            if (nodes == null || nodes.isEmpty())
                return TypeKind.UNDEFINED;
            return nodes.get(0).getTypeKind();
        } else if (this.isFunctionCall()) {
            Objects.requireNonNull(this.getParser().getRootNode().getCurrentScope(), "parser.rootNode.currentScope must not be null.");
        
            final List<ParserNode> nodes = this.getParser().getRootNode().getCurrentScope().lookupScope(this.getIdentifier().getTokenContent());
            if (nodes == null || nodes.isEmpty())
                return TypeKind.UNDEFINED;
            
            final List<FunctionNode> functions = nodes.stream()
                    .filter(node -> node instanceof FunctionNode)
                    .map(node -> (FunctionNode) node)
                    .filter(node -> node.equalsToIdentifier(this))
                    .collect(Collectors.toList());
            if(functions.isEmpty())
                return TypeKind.UNDEFINED;
            return functions.get(0).getTypeKind();
        } else {
            Objects.requireNonNull(this.getCurrentScope(), "parser.currentScope must not be null.");
            
            final List<ParserNode> nodes = this.getCurrentScope().lookupScope(this.getIdentifier().getTokenContent());
            if(nodes == null || nodes.isEmpty())
                return TypeKind.UNDEFINED;
    
            nodes.sort((o1, o2) -> o2.getStartLine() - o1.getStartLine());
            return nodes.get(0).getTypeKind();
        }
    }
    
}
