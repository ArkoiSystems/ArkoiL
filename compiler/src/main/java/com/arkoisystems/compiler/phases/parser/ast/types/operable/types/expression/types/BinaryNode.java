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
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.BinaryOperators;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class BinaryNode extends ExpressionNode
{
    
    public static BinaryNode ADD_GLOBAL_NODE = new BinaryNode(null, null, null, BinaryOperators.ADD, null, null, null, null);
    
    public static BinaryNode SUB_GLOBAL_NODE = new BinaryNode(null, null, null, BinaryOperators.SUB, null, null, null, null);
    
    public static BinaryNode MUL_GLOBAL_NODE = new BinaryNode(null, null, null, BinaryOperators.MUL, null, null, null, null);
    
    public static BinaryNode DIV_GLOBAL_NODE = new BinaryNode(null, null, null, BinaryOperators.DIV, null, null, null, null);
    
    public static BinaryNode MOD_GLOBAL_NODE = new BinaryNode(null, null, null, BinaryOperators.MOD, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final OperableNode leftHandSide;
    
    @Printable(name = "operation")
    @NotNull
    private final BinaryOperators operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private OperableNode rightHandSide;
    
    @Builder
    protected BinaryNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @NonNull
            @NotNull final BinaryOperators operatorType,
            @Nullable final OperableNode rightHandSide,
            @Nullable final OperableNode leftHandSide,
            @Nullable final LexerToken startToken,
            @Nullable final LexerToken endToken
    ) {
        super(parser, parentNode, currentScope, startToken, endToken);
        
        this.rightHandSide = rightHandSide;
        this.operatorType = operatorType;
        this.leftHandSide = leftHandSide;
    }
    
    @NotNull
    @Override
    public BinaryNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getLeftHandSide(), "leftSideOperable must not be null.");
        
        this.startAST(this.getLeftHandSide().getStartToken());
        this.getParser().nextToken(2);
        
        final OperableNode operableNode = this.parseMultiplicative();
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
            case ADD:
                return parser.matchesPeekToken(offset, OperatorType.PLUS) != null;
            case SUB:
                return parser.matchesPeekToken(offset, OperatorType.MINUS) != null;
            case MUL:
                return parser.matchesPeekToken(offset, OperatorType.ASTERISK) != null;
            case DIV:
                return parser.matchesPeekToken(offset, OperatorType.SLASH) != null;
            case MOD:
                return parser.matchesPeekToken(offset, OperatorType.PERCENT) != null;
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
        Objects.requireNonNull(this.getLeftHandSide(), "leftHandSide must not be null.");
        Objects.requireNonNull(this.getRightHandSide(), "rightHandSide must not be null.");
        if (this.getRightHandSide().getTypeNode().getDataKind().isFloating())
            return this.getRightHandSide().getTypeNode();
        return this.getLeftHandSide().getTypeNode();
    }
    
}
