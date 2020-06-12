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
package com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.OperatorType;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.PrefixOperators;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class PrefixNode extends ExpressionNode
{
    
    public static PrefixNode ADD_GLOBAL_NODE = new PrefixNode(null, null, null, PrefixOperators.PREFIX_ADD, null, null, null);
    
    public static PrefixNode SUB_GLOBAL_NODE = new PrefixNode(null, null, null, PrefixOperators.PREFIX_SUB, null, null, null);
    
    public static PrefixNode NEGATE_GLOBAL_NODE = new PrefixNode(null, null, null, PrefixOperators.NEGATE, null, null, null);
    
    @Printable(name = "operation")
    @NotNull
    private final PrefixOperators operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private OperableNode rightHandSide;
    
    @Builder
    protected PrefixNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @NonNull
            @NotNull final PrefixOperators operatorType,
            @Nullable final OperableNode rightHandSide,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.rightHandSide = rightHandSide;
        this.operatorType = operatorType;
    }
    
    @NotNull
    @Override
    public PrefixNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        this.getParser().nextToken(1);
        
        final OperableNode operableNode = this.parseOperable();
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.rightHandSide = operableNode;
        this.endAST(this.rightHandSide.getEndToken());
        return this;
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        switch (this.getOperatorType()) {
            case PREFIX_ADD:
                return parser.matchesPeekToken(offset, OperatorType.PLUS_PLUS) != null;
            case PREFIX_SUB:
                return parser.matchesPeekToken(offset, OperatorType.MINUS_MINUS) != null;
            case NEGATE:
                return parser.matchesPeekToken(offset, OperatorType.MINUS) != null;
            default:
                return false;
        }
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeNode getTypeNode() {
        Objects.requireNonNull(this.getRightHandSide(), "rightHandSide must not be null.");
        return this.getRightHandSide().getTypeNode();
    }
    
}
