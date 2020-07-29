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
package com.arkoisystems.compiler.phases.parser.ast.types.parameter;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.SymbolType;
import com.arkoisystems.compiler.phases.lexer.token.types.IdentifierToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class ParameterListNode extends ParserNode
{
    
    public static ParameterListNode GLOBAL_NODE = new ParameterListNode(null, null, null, null, null);
    
    @Printable(name = "parameters")
    @NotNull
    private final List<ParameterNode> parameters;
    
    @Printable(name = "variadic")
    private boolean isVariadic;
    
    @Builder
    protected ParameterListNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.parameters = new ArrayList<>();
    }
    
    @SneakyThrows
    @NotNull
    @Override
    public ParameterListNode parse() {
        Objects.requireNonNull(this.getParser());
        
        this.startAST(this.getParser().currentToken());
        
        final List<IdentifierToken> chainedIdentifiers = new ArrayList<>();
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (!ParameterNode.GLOBAL_NODE.canParse(this.getParser(), 0))
                break;
            
            if (this.getParser().matchesPeekToken(1, SymbolType.COMMA) != null) {
                chainedIdentifiers.add((IdentifierToken) this.getParser().currentToken());
                this.getParser().nextToken(2);
                continue;
            }
            
            final ParameterNode parameterNode = ParameterNode.builder()
                    .parentNode(this)
                    .currentScope(this.getCurrentScope())
                    .parser(this.getParser())
                    .build()
                    .parse();
            if (parameterNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
            
            if (!chainedIdentifiers.isEmpty()) {
                Objects.requireNonNull(this.getCurrentScope());
                
                for (final IdentifierToken nameToken : chainedIdentifiers) {
                    final ParameterNode chainedNode = ParameterNode.builder()
                            .parser(parameterNode.getParser())
                            .parentNode(parameterNode.getParentNode())
                            .currentScope(parameterNode.getCurrentScope())
                            .startToken(nameToken)
                            .typeNode(parameterNode.getTypeNode().clone())
                            .name(nameToken)
                            .endToken(nameToken)
                            .build();
                    
                    this.getCurrentScope().insert(nameToken.getTokenContent(), chainedNode);
                    this.getParameters().add(chainedNode);
                }
                
                chainedIdentifiers.clear();
            }
            
            this.getParameters().add(parameterNode);
            
            if (this.getParser().matchesNextToken(SymbolType.COMMA) == null)
                break;
            this.getParser().nextToken();
        }
        
        if (this.getParser().matchesCurrentToken(SymbolType.PERIOD) != null &&
                this.getParser().matchesPeekToken(1, SymbolType.PERIOD) != null &&
                this.getParser().matchesPeekToken(2, SymbolType.PERIOD) != null) {
            this.getParser().nextToken(3);
            this.isVariadic = true;
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return true;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
}
