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
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.BinaryOperators;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class BinaryNode extends ExpressionNode
{
    
    public static BinaryNode MOD_GLOBAL_NODE = new BinaryNode(null, null, null, null, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final OperableNode leftHandSide;
    
    @Printable(name = "operation")
    @Nullable
    private final BinaryOperators operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private final OperableNode rightHandSide;
    
    @Builder
    protected BinaryNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final BinaryOperators operatorType,
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
    public OperableNode parse() {
        throw new NullPointerException();
    }
    
    @NotNull
    public OperableNode parseRelational() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode lhsNode = this.parseAdditive();
        if (lhsNode.isFailed())
            return lhsNode;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.CLOSING_ARROW) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseRelational();
                lhsNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(lhsNode.getStartToken())
                        .leftHandSide(lhsNode)
                        .operatorType(BinaryOperators.GREATER_THAN)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.OPENING_ARROW) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseRelational();
                lhsNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(lhsNode.getStartToken())
                        .leftHandSide(lhsNode)
                        .operatorType(BinaryOperators.LESS_THAN)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.CLOSING_ARROW_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseRelational();
                lhsNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(lhsNode.getStartToken())
                        .leftHandSide(lhsNode)
                        .operatorType(BinaryOperators.GREATER_EQUAL_THAN)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.OPENING_ARROW_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseRelational();
                lhsNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(lhsNode.getStartToken())
                        .leftHandSide(lhsNode)
                        .operatorType(BinaryOperators.LESS_EQUAL_THAN)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else {
                this.setEndToken(this.getParser().currentToken());
                return lhsNode;
            }
        }
    }
    
    @NotNull
    public OperableNode parseAdditive() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode lhsNode = this.parseMultiplicative();
        if (lhsNode.isFailed())
            return lhsNode;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.PLUS) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseAdditive();
                lhsNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(lhsNode.getStartToken())
                        .leftHandSide(lhsNode)
                        .operatorType(BinaryOperators.ADDITION)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.MINUS) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseAdditive();
                lhsNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(lhsNode.getStartToken())
                        .leftHandSide(lhsNode)
                        .operatorType(BinaryOperators.SUBTRACTION)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else {
                this.setEndToken(this.getParser().currentToken());
                return lhsNode;
            }
        }
    }
    
    @SneakyThrows
    @NotNull
    protected OperableNode parseMultiplicative() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = this.parseOperable();
        if (operableNode.isFailed())
            return operableNode;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.ASTERISK) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseMultiplicative();
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.MULTIPLICATION)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.SLASH) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseMultiplicative();
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.DIVISION)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.PERCENT) != null) {
                this.getParser().nextToken(2);
                
                final OperableNode rhsNode = this.parseMultiplicative();
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.REMAINING)
                        .rightHandSide(rhsNode)
                        .endToken(rhsNode.getEndToken())
                        .build();
            } else {
                this.setEndToken(this.getParser().currentToken());
                return operableNode;
            }
        }
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, OperatorType.PLUS) != null ||
                parser.matchesPeekToken(offset, OperatorType.MINUS) != null ||
                parser.matchesPeekToken(offset, OperatorType.ASTERISK) != null ||
                parser.matchesPeekToken(offset, OperatorType.SLASH) != null ||
                parser.matchesPeekToken(offset, OperatorType.CLOSING_ARROW) != null ||
                parser.matchesPeekToken(offset, OperatorType.CLOSING_ARROW_EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.OPENING_ARROW_EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.OPENING_ARROW) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeNode getTypeNode() {
        Objects.requireNonNull(this.getRightHandSide(), "rightHandSide must not be null.");
        Objects.requireNonNull(this.getRightHandSide().getTypeNode().getDataKind(), "rightHandSide.typeNode.dataKind must not be null.");
        Objects.requireNonNull(this.getLeftHandSide(), "leftHandSide must not be null.");
        Objects.requireNonNull(this.getOperatorType(), "operatorType must not be null.");
    
        switch (this.getOperatorType()) {
            case LESS_THAN:
            case LESS_EQUAL_THAN:
            case GREATER_THAN:
            case GREATER_EQUAL_THAN:
                return TypeNode.builder()
                        .parentNode(this.getParentNode())
                        .currentScope(this.getCurrentScope())
                        .parser(this.getParser())
                        .dataKind(DataKind.INTEGER)
                        .bits(1)
                        .startToken(this.getStartToken())
                        .endToken(this.getEndToken())
                        .build();
            default:
                if (this.getRightHandSide().getTypeNode().getDataKind().isFloating())
                    return this.getRightHandSide().getTypeNode();
                return this.getLeftHandSide().getTypeNode();
        }
    }
    
}
