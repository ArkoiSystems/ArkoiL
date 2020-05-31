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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stages.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class BinaryExpression extends Expression
{
    
    public static BinaryExpression ADD_GLOBAL_NODE = new BinaryExpression(BinaryOperatorType.ADD, null, null, null, null, null);
    
    public static BinaryExpression SUB_GLOBAL_NODE = new BinaryExpression(BinaryOperatorType.SUB, null, null, null, null, null);
    
    public static BinaryExpression MUL_GLOBAL_NODE = new BinaryExpression(BinaryOperatorType.MUL, null, null, null, null, null);
    
    public static BinaryExpression DIV_GLOBAL_NODE = new BinaryExpression(BinaryOperatorType.DIV, null, null, null, null, null);
    
    public static BinaryExpression EXP_GLOBAL_NODE = new BinaryExpression(BinaryOperatorType.EXP, null, null, null, null, null);
    
    public static BinaryExpression MOD_GLOBAL_NODE = new BinaryExpression(BinaryOperatorType.MOD, null, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final Operable leftHandSide;
    
    @Printable(name = "operation")
    @NotNull
    private final BinaryOperatorType operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private Operable rightHandSide;
    
    @Builder
    protected BinaryExpression(
            final @NotNull BinaryOperatorType operatorType,
            final @Nullable Operable rightHandSide,
            final @Nullable Parser parser,
            final @Nullable Operable leftHandSide,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, null, ASTType.BINARY_EXPRESSION, startToken, endToken);
    
        this.rightHandSide = rightHandSide;
        this.operatorType = operatorType;
        this.leftHandSide = leftHandSide;
    }
    
    @NotNull
    @Override
    public BinaryExpression parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getLeftHandSide(), "leftSideOperable must not be null.");
        
        this.startAST(this.getLeftHandSide().getStartToken());
        this.getParser().nextToken(2);
        
        final Operable operableAST = this.parseMultiplicative(parentAST);
        if (operableAST.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.rightHandSide = operableAST;
        this.endAST(this.rightHandSide.getEndToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        switch (this.getOperatorType()) {
            case ADD:
                return parser.matchesPeekToken(offset, OperatorType.PLUS) != null;
            case SUB:
                return parser.matchesPeekToken(offset, OperatorType.MINUS) != null;
            case MUL:
                return parser.matchesPeekToken(offset, OperatorType.ASTERISK) != null;
            case DIV:
                return parser.matchesPeekToken(offset, OperatorType.SLASH) != null;
            case EXP:
                return parser.matchesPeekToken(offset, OperatorType.ASTERISK_ASTERISK) != null;
            case MOD:
                return parser.matchesPeekToken(offset, OperatorType.PERCENT) != null;
            default:
                return false;
        }
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    // TODO: 5/28/20 Think about it if it's right or not.
    @Override
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getLeftHandSide(), "leftHandSide must not be null.");
        return this.getLeftHandSide().getTypeKind();
    }
    
}
