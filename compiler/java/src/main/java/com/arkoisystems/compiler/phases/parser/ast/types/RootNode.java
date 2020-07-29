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
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class RootNode extends ParserNode
{
    
    @Printable(name = "nodes")
    @NotNull
    private final List<ParserNode> nodes;
    
    @Builder
    protected RootNode(
            @NonNull
            @NotNull final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, parser.getCompilerClass().getRootScope(), startToken, endToken);
        
        this.nodes = new ArrayList<>();
    }
    
    @SneakyThrows
    @NotNull
    @Override
    public RootNode parse() {
        Objects.requireNonNull(this.getParser());
        
        this.startAST(this.getParser().currentToken(false));
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            final ParserNode parserNode;
            if (FunctionNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
                parserNode = FunctionNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .build()
                        .parse();
            } else if (ImportNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
                parserNode = ImportNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .build()
                        .parse();
            } else if (VariableNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
                parserNode = VariableNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .build()
                        .parse();
            } else if (StructNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
                parserNode = StructNode.builder()
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
                        ParserErrorType.ROOT_NO_PARSER_FOUND
                );
                this.findValidToken();
                continue;
            }
    
            if (parserNode.isFailed()) {
                this.setFailed(true);
                this.findValidToken();
                continue;
            }
    
            this.getNodes().add(parserNode);
            this.getParser().nextToken();
        }
        
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    private void findValidToken() {
        Objects.requireNonNull(this.getParser());
        
        while (this.getParser().getPosition() < this.getParser().getTokens().length) {
            if (FunctionNode.GLOBAL_NODE.canParse(this.getParser(), 0) ||
                    ImportNode.GLOBAL_NODE.canParse(this.getParser(), 0) ||
                    VariableNode.GLOBAL_NODE.canParse(this.getParser(), 0) ||
                    StructNode.GLOBAL_NODE.canParse(this.getParser(), 0))
                break;
    
            this.getParser().nextToken();
        }
    }
    
}
