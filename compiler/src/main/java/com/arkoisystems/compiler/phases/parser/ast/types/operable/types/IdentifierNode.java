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
package com.arkoisystems.compiler.phases.parser.ast.types.operable.types;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class IdentifierNode extends OperableNode
{
    
    public static IdentifierNode GLOBAL_NODE = new IdentifierNode(null, null, null, null, null, null, null, null, false);
    
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
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final IdentifierNode nextIdentifier,
            @Nullable final ExpressionListNode expressions,
            @Nullable final IdentifierToken identifier,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken,
            final boolean isFileLocal
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
    
        this.nextIdentifier = nextIdentifier;
        this.expressions = expressions;
        this.identifier = identifier;
    }
    
    @NotNull
    @Override
    public IdentifierNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        this.startAST(this.getParser().currentToken());
    
        if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
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
    
        if (ExpressionListNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
        
            this.isFunctionCall = true;
        
            final ExpressionListNode expressionListNode = ExpressionListNode.builder()
                    .parentNode(this)
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parse();
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
                    .parse();
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
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.THIS) != null ||
                parser.matchesPeekToken(offset, TokenType.IDENTIFIER) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getIdentifier(), "identifier must not be null.");
        
        ParserNode foundNode = null;
        if (this.isFunctionCall()) {
            Objects.requireNonNull(this.getParser().getRootNode().getCurrentScope(), "parser.rootNode.currentScope must not be null.");
        
            final List<ParserNode> nodes = this.getParser().getRootNode().getCurrentScope().lookupScope(this.getIdentifier().getTokenContent());
            if (nodes != null && !nodes.isEmpty()) {
                final List<FunctionNode> functions = nodes.stream()
                        .filter(node -> node instanceof FunctionNode)
                        .map(node -> (FunctionNode) node)
                        .filter(node -> node.equalsToIdentifier(this))
                        .collect(Collectors.toList());
                if (!functions.isEmpty())
                    foundNode = functions.get(0);
            }
        } else {
            Objects.requireNonNull(this.getCurrentScope(), "parser.currentScope must not be null.");
            
            final List<ParserNode> nodes = this.getCurrentScope().lookup(this.getIdentifier().getTokenContent());
            if (nodes != null && !nodes.isEmpty())
                foundNode = nodes.get(0);
        }
        
        if (foundNode instanceof VariableNode)
            return ((VariableNode) foundNode).getTypeNode();
        else if (foundNode instanceof ParameterNode)
            return ((ParameterNode) foundNode).getTypeNode();
        else if (foundNode instanceof FunctionNode)
            return ((FunctionNode) foundNode).getTypeNode();
        else if (foundNode instanceof ImportNode) {
            final ImportNode importNode = (ImportNode) foundNode;
            
            if (this.getNextIdentifier() != null) {
                final CompilerClass compilerClass = Objects.requireNonNull(importNode.resolveClass(), "importNode.resolveClass must not be null.");
                this.getNextIdentifier().setCurrentScope(compilerClass.getRootScope());
                this.getNextIdentifier().setParser(compilerClass.getParser());
                return this.getNextIdentifier().getTypeNode();
            }
        }
        
        return TypeNode.builder()
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .dataKind(DataKind.UNDEFINED)
                .startToken(this.getStartToken())
                .endToken(this.getEndToken())
                .build();
    }
    
}
