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
package com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.semantic.routines.TypeVisitor;
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
public class FunctionCallNode extends IdentifierNode
{
    
    public static FunctionCallNode GLOBAL_NODE = new FunctionCallNode(null, null, null, null, null, false, false, null, null);
    
    @Printable(name = "arguments")
    @Nullable
    private ArgumentListNode argumentList;
    
    @Builder
    protected FunctionCallNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final IdentifierNode nextIdentifier,
            @Nullable final IdentifierToken identifier,
            final boolean isDereference,
            final boolean isPointer,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, nextIdentifier, identifier, true, isDereference, isPointer, startToken, endToken);
    }
    
    @Override
    public @NotNull FunctionCallNode parse() {
        Objects.requireNonNull(this.getParser());
    
        if (this.getParser().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function call",
                            "'('",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        if (!ArgumentListNode.MIXED_NAMED_NODE.canParse(this.getParser(), 1)) {
            final LexerToken nextToken = this.getParser().nextToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    nextToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function call",
                            "<argument list>",
                            nextToken != null ? nextToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        this.getParser().nextToken();
    
        final ArgumentListNode argumentListNode = ArgumentListNode.builder()
                .parentNode(this)
                .currentScope(new SymbolTable(this.getCurrentScope()))
                .parser(this.getParser())
                .allNamed(false)
                .build()
                .parse();
        if (argumentListNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
    
        this.argumentList = argumentListNode;
    
        if (this.getParser().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function call",
                            "')'",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
    
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, SymbolType.OPENING_PARENTHESIS) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeNode getTypeNode() {
        Objects.requireNonNull(this.getIdentifier());
        Objects.requireNonNull(this.getParser());
        Objects.requireNonNull(this.getParser().getRootNode().getCurrentScope());
    
        final List<ParserNode> nodes = this.getParser().getRootNode().getCurrentScope().lookupScope(
                this.getIdentifier().getTokenContent()
        );
    
        FunctionNode foundNode = null;
        if (nodes != null && !nodes.isEmpty()) {
            final List<FunctionNode> functions = nodes.stream()
                    .filter(node -> node instanceof FunctionNode)
                    .map(node -> (FunctionNode) node)
                    .filter(node -> node.equalsToCall(this))
                    .collect(Collectors.toList());
    
            if (!functions.isEmpty())
                foundNode = functions.get(0);
        }
    
        if (foundNode != null)
            return foundNode.getTypeNode();
    
        return TypeVisitor.ERROR_NODE;
    }
    
    @NotNull
    public List<ArgumentNode> getSortedArguments(
            @NotNull final FunctionNode functionNode
    ) {
        Objects.requireNonNull(functionNode.getParameterList());
        Objects.requireNonNull(this.getArgumentList());
        
        final List<ArgumentNode> sortedArguments = new ArrayList<>(this.getArgumentList().getArguments());
        for (final ArgumentNode argumentNode : this.getArgumentList().getArguments()) {
            if (argumentNode.getName() == null)
                continue;
            
            ParameterNode foundParameter = null;
            for (int index = 0; index < functionNode.getParameterList().getParameters().size(); index++) {
                final ParameterNode parameterNode = functionNode.getParameterList().getParameters().get(index);
                Objects.requireNonNull(parameterNode.getName());
                
                if (parameterNode.getName().getTokenContent().equals(argumentNode.getName().getTokenContent())) {
                    foundParameter = parameterNode;
                    break;
                }
            }
            
            if (foundParameter == null)
                throw new NullPointerException();
            
            final int parameterIndex = functionNode.getParameterList().getParameters().indexOf(foundParameter);
            sortedArguments.remove(argumentNode);
            sortedArguments.add(parameterIndex, argumentNode);
        }
        
        return sortedArguments;
    }
    
}
