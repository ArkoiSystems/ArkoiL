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
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class PrefixExpression extends Expression
{
    
    public static PrefixExpression ADD_GLOBAL_NODE = new PrefixExpression(PrefixOperatorType.PREFIX_ADD, null, null, null, null);
    
    public static PrefixExpression SUB_GLOBAL_NODE = new PrefixExpression(PrefixOperatorType.PREFIX_SUB, null, null, null, null);
    
    public static PrefixExpression AFFIRM_GLOBAL_NODE = new PrefixExpression(PrefixOperatorType.AFFIRM, null, null, null, null);
    
    public static PrefixExpression NEGATE_GLOBAL_NODE = new PrefixExpression(PrefixOperatorType.NEGATE, null, null, null, null);
    
    @Printable(name = "operation")
    @NotNull
    private final PrefixOperatorType operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private Operable rightHandSide;
    
    @Builder
    protected PrefixExpression(
            final @NotNull PrefixOperatorType operatorType,
            final @Nullable Operable rightHandSide,
            final @Nullable Parser parser,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, null, ASTType.PREFIX_EXPRESSION, startToken, endToken);
        
        this.rightHandSide = rightHandSide;
        this.operatorType = operatorType;
    }
    
    @NotNull
    @Override
    public PrefixExpression parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        this.startAST(this.getParser().currentToken());
        
        this.getParser().nextToken(1);
        
        final Operable operableAST = this.parseOperable(parentAST);
        
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
            case PREFIX_ADD:
                return parser.matchesCurrentToken(OperatorType.PLUS_PLUS) != null;
            case PREFIX_SUB:
                return parser.matchesCurrentToken(OperatorType.MINUS_MINUS) != null;
            case AFFIRM:
                return parser.matchesCurrentToken(OperatorType.MINUS) != null;
            case NEGATE:
                return parser.matchesCurrentToken(OperatorType.PLUS) != null;
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
        Objects.requireNonNull(this.getRightHandSide(), "rightHandSide must not be null.");
        return this.getRightHandSide().getTypeKind();
    }
    
}
