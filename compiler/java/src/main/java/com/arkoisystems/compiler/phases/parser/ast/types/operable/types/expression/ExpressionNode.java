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
package com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.ParserErrorType;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.BinaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.ParenthesizedNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.UnaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ExpressionNode extends OperableNode
{
    
    public static ExpressionNode GLOBAL_NODE = new ExpressionNode(null, null, null, null, null, null);
    
    @Builder(builderMethodName = "expressionBuilder")
    protected ExpressionNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            @Nullable final TypeNode givenType,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, givenType, endToken);
    }
    
    @NotNull
    @Override
    public OperableNode parse() {
        return BinaryNode.builder()
                .parser(this.getParser())
                .parentNode(this.getParentNode())
                .currentScope(this.getCurrentScope())
                .build()
                .parseRelational();
    }
    
    @SneakyThrows
    @NotNull
    protected OperableNode parseOperable() {
        Objects.requireNonNull(this.getParser());
    
        if (UnaryNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            return UnaryNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .build()
                    .parse();
        } else if (ParenthesizedNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            return ParenthesizedNode.builder()
                    .parser(this.getParser())
                    .currentScope(this.getCurrentScope())
                    .parentNode(this)
                    .build()
                    .parse();
        } else {
            final OperableNode operableNode;
            if (StringNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
                operableNode = StringNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .build()
                        .parse();
            } else if (NumberNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
                operableNode = NumberNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .build()
                        .parse();
            } else if (IdentifierNode.PARSER_NODE.canParse(this.getParser(), 0)) {
                operableNode = IdentifierNode.identifierBuilder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .parseFunction(true)
                        .build()
                        .parse();
            } else return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    this.getParser().currentToken(),
                    ParserErrorType.OPERABLE_NOT_SUPPORTED
            );
    
            if (operableNode.isFailed()) {
                this.setFailed(true);
                return this;
            }
    
            return operableNode;
        }
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return super.canParse(parser, offset) ||
                UnaryNode.GLOBAL_NODE.canParse(parser, offset) ||
                ParenthesizedNode.GLOBAL_NODE.canParse(parser, offset);
    }
    
}
