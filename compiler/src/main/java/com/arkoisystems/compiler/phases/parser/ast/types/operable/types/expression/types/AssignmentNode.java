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
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.AssignmentOperators;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class AssignmentNode extends ExpressionNode
{
    
    public static AssignmentNode ASSIGN_GLOBAL_NODE = new AssignmentNode(null, null, AssignmentOperators.ASSIGN, null, null, null, null);
    
    public static AssignmentNode ADD_GLOBAL_NODE = new AssignmentNode(null, null, AssignmentOperators.ADD_ASSIGN, null, null, null, null);
    
    public static AssignmentNode SUB_GLOBAL_NODE = new AssignmentNode(null, null, AssignmentOperators.SUB_ASSIGN, null, null, null, null);
    
    public static AssignmentNode MUL_GLOBAL_NODE = new AssignmentNode(null, null, AssignmentOperators.MUL_ASSIGN, null, null, null, null);
    
    public static AssignmentNode DIV_GLOBAL_NODE = new AssignmentNode(null, null, AssignmentOperators.DIV_ASSIGN, null, null, null, null);
    
    public static AssignmentNode MOD_GLOBAL_NODE = new AssignmentNode(null, null, AssignmentOperators.MOD_ASSIGN, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final OperableNode leftHandSide;
    
    @Printable(name = "operation")
    @NotNull
    private final AssignmentOperators operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private OperableNode rightHandSide;
    
    @Builder
    protected AssignmentNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            @NonNull
            @NotNull final AssignmentOperators operatorType,
            final @Nullable OperableNode rightHandSide,
            final @Nullable OperableNode leftHandSide,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, currentScope, startToken, endToken);
        
        this.rightHandSide = rightHandSide;
        this.operatorType = operatorType;
        this.leftHandSide = leftHandSide;
    }
    
    @NotNull
    @Override
    public AssignmentNode parse() {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getLeftHandSide(), "leftSideOperable must not be null.");
        
        this.startAST(this.getLeftHandSide().getStartToken());
        this.getParser().nextToken(2);
        
        final OperableNode operableNode = this.parseAdditive();
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.rightHandSide = operableNode;
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
    
    @Override
    public @NotNull TypeNode getTypeNode() {
        Objects.requireNonNull(this.getLeftHandSide(), "leftHandSide must not be null.");
        return this.getLeftHandSide().getTypeNode();
    }
    
}
