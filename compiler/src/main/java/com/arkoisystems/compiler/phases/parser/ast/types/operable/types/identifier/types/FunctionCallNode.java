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
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.semantic.routines.TypeVisitor;
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
public class FunctionCallNode extends IdentifierNode
{
    
    public static FunctionCallNode GLOBAL_NODE = new FunctionCallNode(null, null, null, null, null, null, null);
    
    @Printable(name = "expression list")
    @Nullable
    private ExpressionListNode expressionList;
    
    @Builder
    protected FunctionCallNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final IdentifierNode nextIdentifier,
            @Nullable final IdentifierToken identifier,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, nextIdentifier, identifier, true, startToken, endToken);
    }
    
    @Override
    public @NotNull FunctionCallNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (!ExpressionListNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            final LexerToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                    String.format(
                            ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                            "Function call",
                            "<expression list>",
                            currentToken != null ? currentToken.getTokenContent() : "nothing"
                    )
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
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
        
        this.expressionList = expressionListNode;
        
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
        Objects.requireNonNull(this.getIdentifier(), "identifier must not be null.");
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getParser().getRootNode().getCurrentScope(), "parser.rootNode.currentScope must not be null.");
        
        final List<ParserNode> nodes = this.getParser().getRootNode().getCurrentScope().lookupScope(
                this.getIdentifier().getTokenContent()
        );
        
        FunctionNode foundNode = null;
        if (nodes != null && !nodes.isEmpty()) {
            final List<FunctionNode> functions = nodes.stream()
                    .filter(node -> node instanceof FunctionNode)
                    .map(node -> (FunctionNode) node)
                    .filter(node -> node.equalsToIdentifier(this))
                    .collect(Collectors.toList());
    
            if (!functions.isEmpty())
                foundNode = functions.get(0);
        }
    
        if (foundNode != null)
            return foundNode.getTypeNode();
    
        return TypeVisitor.ERROR_NODE;
    }
    
}
