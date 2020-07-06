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
package com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.AssignNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.StructCreateNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.compiler.phases.semantic.routines.TypeVisitor;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Getter
public class IdentifierNode extends OperableNode
{
    
    public static IdentifierNode PARSER_NODE = new IdentifierNode(null, null, null, null, null, true, false, false, null, null);
    
    @Printable(name = "is dereference")
    private boolean isDereference;
    
    @Printable(name = "is pointer")
    private boolean isPointer;
    
    @Setter
    @Nullable
    private ParserNode targetNode;
    
    @Printable(name = "called identifier")
    @Nullable
    private IdentifierToken identifier;
    
    @Printable(name = "next call")
    @Setter
    @Nullable
    private IdentifierNode nextIdentifier;
    
    private final boolean parseFunction;
    
    @Builder(builderMethodName = "identifierBuilder")
    protected IdentifierNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final IdentifierNode nextIdentifier,
            @Nullable final IdentifierToken identifier,
            final boolean parseFunction,
            final boolean isDereference,
            final boolean isPointer,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
    
        this.nextIdentifier = nextIdentifier;
        this.isDereference = isDereference;
        this.parseFunction = parseFunction;
        this.identifier = identifier;
        this.isPointer = isPointer;
    }
    
    @NotNull
    @Override
    public IdentifierNode parse() {
        Objects.requireNonNull(this.getParser());
    
        this.startAST(this.getParser().currentToken());
    
        if (this.getParser().matchesCurrentToken(SymbolType.AMPERSAND) != null) {
            this.getParser().nextToken();
            this.isPointer = true;
        } else if (this.getParser().matchesCurrentToken(SymbolType.AT_SIGN) != null) {
            this.getParser().nextToken();
            this.isDereference = true;
        }
    
        if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Identifier",
                            "<identifier>",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        this.identifier = (IdentifierToken) this.getParser().currentToken();
    
        final IdentifierNode identifierNode;
        if (this.isParseFunction() && FunctionCallNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
    
            identifierNode = FunctionCallNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this.getParentNode())
                    .startToken(this.getStartToken())
                    .identifier(this.getIdentifier())
                    .build()
                    .parse();
        } else identifierNode = this;
    
        IdentifierNode lastNode = identifierNode;
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (this.getParser().matchesPeekToken(1, SymbolType.PERIOD) == null)
                break;
        
            this.getParser().nextToken();
        
            if (!this.canParse(this.getParser(), 1)) {
                final LexerToken nextToken = this.getParser().nextToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        nextToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Identifier",
                                "<identifier>",
                                nextToken != null ? nextToken.getTokenContent() : "nothing"
                        )
                );
            }
        
            this.getParser().nextToken();
        
            if (this.getParser().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
                final LexerToken currentToken = this.getParser().currentToken();
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        currentToken,
                        String.format(
                                ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                                "Identifier",
                                "<identifier>",
                                currentToken != null ? currentToken.getTokenContent() : "nothing"
                        )
                );
            }
        
            final IdentifierToken identifierToken = (IdentifierToken) this.getParser().currentToken();
            final IdentifierNode nextNode;
            if (this.isParseFunction() && FunctionCallNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
                this.getParser().nextToken();
            
                nextNode = FunctionCallNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(lastNode)
                        .startToken(identifierToken)
                        .identifier(identifierToken)
                        .build()
                        .parse();
            } else nextNode = IdentifierNode.identifierBuilder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(lastNode)
                    .parseFunction(this.isParseFunction())
                    .startToken(identifierToken)
                    .identifier(identifierToken)
                    .endToken(identifierToken)
                    .build();
    
            lastNode.setNextIdentifier(nextNode);
            lastNode = nextNode;
        }
    
        this.endAST(this.getParser().currentToken());
    
        if (lastNode instanceof FunctionCallNode)
            return identifierNode;
    
        if (AssignNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
        
            return AssignNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this.getParentNode())
                    .isDereference(this.isDereference())
                    .isPointer(this.isPointer())
                    .startToken(this.getStartToken())
                    .identifier(this.getIdentifier())
                    .nextIdentifier(this.getNextIdentifier())
                    .endToken(this.getEndToken())
                    .build()
                    .parse();
        } else if (StructCreateNode.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            this.getParser().nextToken();
    
            return StructCreateNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this.getParentNode())
                    .isDereference(this.isDereference())
                    .isPointer(this.isPointer())
                    .startToken(this.getStartToken())
                    .identifier(this.getIdentifier())
                    .nextIdentifier(this.getNextIdentifier())
                    .endToken(this.getEndToken())
                    .build()
                    .parse();
        }
    
        return identifierNode;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, SymbolType.AMPERSAND) != null ||
                parser.matchesPeekToken(offset, SymbolType.AT_SIGN) != null ||
                parser.matchesPeekToken(offset, TokenType.IDENTIFIER) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @SneakyThrows
    @NotNull
    public TypeNode getTypeNode() {
        Objects.requireNonNull(this.getCurrentScope());
        Objects.requireNonNull(this.getIdentifier());
        Objects.requireNonNull(this.getParser());
        
        final List<ParserNode> nodes = this.getCurrentScope().lookup(
                this.getIdentifier().getTokenContent(),
                node -> !(node instanceof ArgumentNode)
        );
        ParserNode foundNode = null;
        if (!nodes.isEmpty())
            foundNode = nodes.get(0);
        
        final TypeNode typeNode;
        if (foundNode instanceof VariableNode) {
            final VariableNode variableNode = (VariableNode) foundNode;
            if (variableNode.isGettingInitialized())
                return TypeVisitor.LOOPING_NODE;
            
            final ParserNode targetNode = variableNode.getTypeNode().getTargetNode();
            if (this.getNextIdentifier() != null && targetNode != null) {
                this.getNextIdentifier().setCurrentScope(targetNode.getCurrentScope());
                this.getNextIdentifier().setParser(targetNode.getParser());
                typeNode = this.getNextIdentifier().getTypeNode().clone();
            } else typeNode = variableNode.getTypeNode().clone();
        } else if (foundNode instanceof ParameterNode) {
            final ParameterNode parameterNode = (ParameterNode) foundNode;
            typeNode = parameterNode.getTypeNode().clone();
        } else if (foundNode instanceof FunctionNode) {
            final FunctionNode functionNode = (FunctionNode) foundNode;
            typeNode = functionNode.getTypeNode().clone();
        } else if (foundNode instanceof ImportNode) {
            final ImportNode importNode = (ImportNode) foundNode;
            if (this.getNextIdentifier() != null) {
                final CompilerClass compilerClass = Objects.requireNonNull(importNode.resolveClass());
                this.getNextIdentifier().setCurrentScope(compilerClass.getRootScope());
                this.getNextIdentifier().setParser(compilerClass.getParser());
                return this.getNextIdentifier().getTypeNode();
            }
            
            return TypeVisitor.ERROR_NODE;
        } else if (foundNode instanceof StructNode) {
            final StructNode structNode = (StructNode) foundNode;
            return structNode.getTypeNode();
        } else return TypeVisitor.ERROR_NODE;
        
        typeNode.setPointers(typeNode.getPointers() + (this.isPointer() ? 1 : 0));
        typeNode.setPointers(typeNode.getPointers() - (this.isDereference() ? 1 : 0));
        return typeNode;
    }
    
}
