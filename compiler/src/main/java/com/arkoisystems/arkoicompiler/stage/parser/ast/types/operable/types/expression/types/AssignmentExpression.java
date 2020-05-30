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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.AssignmentOperatorType;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class AssignmentExpression extends Expression
{
    
    public static AssignmentExpression ASSIGN_GLOBAL_NODE = new AssignmentExpression(AssignmentOperatorType.ASSIGN, null, null, null, null, null);
    
    public static AssignmentExpression ADD_GLOBAL_NODE = new AssignmentExpression(AssignmentOperatorType.ADD_ASSIGN, null, null, null, null, null);
    
    public static AssignmentExpression SUB_GLOBAL_NODE = new AssignmentExpression(AssignmentOperatorType.SUB_ASSIGN, null, null, null, null, null);
    
    public static AssignmentExpression MUL_GLOBAL_NODE = new AssignmentExpression(AssignmentOperatorType.MUL_ASSIGN, null, null, null, null, null);
    
    public static AssignmentExpression DIV_GLOBAL_NODE = new AssignmentExpression(AssignmentOperatorType.DIV_ASSIGN, null, null, null, null, null);
    
    public static AssignmentExpression MOD_GLOBAL_NODE = new AssignmentExpression(AssignmentOperatorType.MOD_ASSIGN, null, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final Operable leftHandSide;
    
    @Printable(name = "operation")
    @NotNull
    private final AssignmentOperatorType operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private Operable rightHandSide;
    
    @Builder
    protected AssignmentExpression(
            final @NotNull AssignmentOperatorType operatorType,
            final @Nullable Operable rightHandSide,
            final @Nullable Parser parser,
            final @Nullable Operable leftHandSide,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, null, ASTType.ASSIGNMENT_EXPRESSION, startToken, endToken);
        
        this.rightHandSide = rightHandSide;
        this.operatorType = operatorType;
        this.leftHandSide = leftHandSide;
    }
    
    @NotNull
    @Override
    public AssignmentExpression parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getLeftHandSide(), "leftSideOperable must not be null.");
        
        this.startAST(this.getLeftHandSide().getStartToken());
        this.getParser().nextToken(2);
        
        final Operable operableAST = this.parseAdditive(parentAST);
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
            case ASSIGN:
                return parser.matchesPeekToken(offset, OperatorType.EQUALS) != null;
            case ADD_ASSIGN:
                return parser.matchesPeekToken(1, OperatorType.PLUS_EQUALS) != null;
            case SUB_ASSIGN:
                return parser.matchesPeekToken(1, OperatorType.MINUS_EQUALS) != null;
            case MUL_ASSIGN:
                return parser.matchesPeekToken(1, OperatorType.ASTERISK_EQUALS) != null;
            case DIV_ASSIGN:
                return parser.matchesPeekToken(1, OperatorType.SLASH_EQUALS) != null;
            case MOD_ASSIGN:
                return parser.matchesPeekToken(1, OperatorType.PERCENT_EQUALS) != null;
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
