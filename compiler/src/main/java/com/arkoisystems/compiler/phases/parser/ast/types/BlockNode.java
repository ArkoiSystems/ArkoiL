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
package com.arkoisystems.compiler.phases.parser.ast.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.OperatorType;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.IfNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class BlockNode extends ParserNode
{
    
    public static BlockNode BRACE_NODE = new BlockNode(null, null, null, false, null, null);
    
    public static BlockNode INLINED_NODE = new BlockNode(null, null, null, true, null, null);
    
    @Printable(name = "nodes")
    @NotNull
    private final List<ParserNode> nodes;
    
    @Printable(name = "is inlined")
    private boolean isInlined;
    
    @Builder
    protected BlockNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            final boolean isInlined,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.nodes = new ArrayList<>();
        this.isInlined = isInlined;
    }
    
    @NotNull
    @Override
    public BlockNode parse() {
        Objects.requireNonNull(this.getParser());
        
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_BRACE) != null) {
            return this.parseBlock();
        } else if (this.getParser().matchesCurrentToken(OperatorType.EQUALS) != null) {
            return this.parseInlinedBlock();
        } else {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Parameter",
                            "'{' or '='",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    }
    
    @NotNull
    public BlockNode parseBlock() {
        Objects.requireNonNull(this.getParser());
        this.isInlined = false;
        
        this.startAST(this.getParser().currentToken());
        
        this.getParser().nextToken();
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                break;
            
            this.parseStatement();
            this.getParser().nextToken();
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @NotNull
    public BlockNode parseStatement() {
        Objects.requireNonNull(this.getParser());
        
        final ParserNode parserNode;
        if (IfNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            parserNode = IfNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .build()
                    .parse();
        } else if (VariableNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            final VariableNode variableNode = VariableNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .build()
                    .parse();
            variableNode.setLocal(true);
            parserNode = variableNode;
        } else if (IdentifierNode.PARSER_NODE.canParse(this.getParser(), 0)) {
            parserNode = IdentifierNode.identifierBuilder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .parseFunction(true)
                    .build()
                    .parse();
        } else if (ReturnNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            parserNode = ReturnNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .build()
                    .parse();
        } else {
            this.addError(
                    null,
                    this.getParser().getCompilerClass(),
                    this.getParser().currentToken(),
                    ParserErrorType.BLOCK_NO_PARSER_FOUND
            );
            this.findValidToken();
            return this;
        }
        
        if (parserNode.isFailed()) {
            this.setFailed(true);
            this.findValidToken();
            return this;
        }
        
        this.getNodes().add(parserNode);
        return this;
    }
    
    @NotNull
    public BlockNode parseInlinedBlock() {
        Objects.requireNonNull(this.getParser());
        this.isInlined = true;
        
        this.startAST(this.getParser().currentToken());
        
        if (!ExpressionNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Inline block",
                            "<expression>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.getParser().nextToken();
        
        final OperableNode operableNode = ExpressionNode.expressionBuilder()
                .parentNode(this)
                .currentScope(this.getCurrentScope())
                .parser(this.getParser())
                .build()
                .parse();
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.getNodes().add(ReturnNode.builder()
                .parentNode(this)
                .parser(operableNode.getParser())
                .currentScope(operableNode.getCurrentScope())
                .expression(operableNode)
                .startToken(operableNode.getStartToken())
                .endToken(operableNode.getEndToken())
                .build());
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return (this.isInlined() && parser.matchesPeekToken(offset, OperatorType.EQUALS) != null) ||
                (!this.isInlined() && parser.matchesPeekToken(offset, SymbolType.OPENING_BRACE) != null);
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @NotNull
    public TypeNode getTypeNode() {
        final TypeNode typeNode;
        if (this.isInlined()) {
            final ParserNode parserNode = this.getNodes().get(0);
            if (!(parserNode instanceof ReturnNode))
                throw new NullPointerException();
            
            final ReturnNode returnNode = (ReturnNode) parserNode;
            typeNode = returnNode.getTypeNode();
        } else {
            final List<ReturnNode> returns = this.getNodes().stream()
                    .filter(node -> node instanceof ReturnNode)
                    .map(node -> (ReturnNode) node)
                    .collect(Collectors.toList());
            if (returns.size() == 0)
                return TypeNode.builder()
                        .parentNode(this)
                        .currentScope(this.getCurrentScope())
                        .parser(this.getParser())
                        .dataKind(DataKind.VOID)
                        .startToken(this.getStartToken())
                        .endToken(this.getEndToken())
                        .build();
    
            return returns.get(0).getTypeNode();
        }
        return typeNode;
    }
    
    private void findValidToken() {
        Objects.requireNonNull(this.getParser());
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (ReturnNode.GLOBAL_NODE.canParse(this.getParser(), 0) ||
                    VariableNode.GLOBAL_NODE.canParse(this.getParser(), 0) ||
                    IdentifierNode.PARSER_NODE.canParse(this.getParser(), 0) ||
                    this.getParser().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null)
                break;
    
            this.getParser().nextToken();
        }
    }
    
}
