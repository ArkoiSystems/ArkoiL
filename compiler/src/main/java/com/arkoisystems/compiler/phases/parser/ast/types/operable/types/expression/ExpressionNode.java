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
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.AssignmentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.ParenthesizedNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.PrefixNode;
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
    
    public static ExpressionNode GLOBAL_NODE = new ExpressionNode(null, null, null, null, null);
    
    @Builder(builderMethodName = "expressionBuilder")
    protected ExpressionNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
    }
    
    @NotNull
    @Override
    public OperableNode parse() {
        return AssignmentNode.builder()
                .parser(this.getParser())
                .parentNode(this.getParentNode())
                .currentScope(this.getCurrentScope())
                .build()
                .parse();
    }
    
    @SneakyThrows
    @NotNull
    protected OperableNode parseOperable() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        if (PrefixNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            return PrefixNode.builder()
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
            final OperableNode foundNode = this.getValidNode(
                    StringNode.GLOBAL_NODE,
                    NumberNode.GLOBAL_NODE,
                    IdentifierNode.PARSER_NODE
            );
            
            if (foundNode == null)
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        this.getParser().currentToken(),
                        
                        ParserErrorType.OPERABLE_NOT_SUPPORTED
                );
            
            final OperableNode operableNode = foundNode.clone();
            operableNode.setParentNode(this);
            operableNode.setCurrentScope(this.getCurrentScope());
            operableNode.setParser(this.getParser());
            
            return (OperableNode) operableNode.parse();
        }
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return super.canParse(parser, offset) ||
                PrefixNode.GLOBAL_NODE.canParse(parser, offset) ||
                ParenthesizedNode.GLOBAL_NODE.canParse(parser, offset);
    }
    
}
