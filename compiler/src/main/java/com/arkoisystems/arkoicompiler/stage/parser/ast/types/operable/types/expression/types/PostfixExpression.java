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
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class PostfixExpression extends Expression
{
    
    public static PostfixExpression ADD_GLOBAL_NODE = new PostfixExpression(PostfixOperatorType.POSTFIX_ADD, null, null, null, null);
    
    public static PostfixExpression SUB_GLOBAL_NODE = new PostfixExpression(PostfixOperatorType.POSTFIX_SUB, null, null, null, null);
    
    @Printable(name = "operation")
    @NotNull
    private final PostfixOperatorType operatorType;
    
    @Printable(name = "lhs")
    @Nullable
    private final Operable leftHandSide;
    
    @Builder
    protected PostfixExpression(
            final @NotNull PostfixOperatorType operatorType,
            final @Nullable Operable leftHandSide,
            final @Nullable Parser parser,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, null, ASTType.POSTFIX_EXPRESSION, startToken, endToken);
        
        this.operatorType = operatorType;
        this.leftHandSide = leftHandSide;
    }
    
    @NotNull
    @Override
    public PostfixExpression parseAST(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        Objects.requireNonNull(this.getLeftHandSide(), "leftSideOperable must not be null.");
        
        this.startAST(this.getLeftHandSide().getStartToken());
        this.getParser().nextToken(2);
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        switch (this.getOperatorType()) {
            case POSTFIX_ADD:
                return parser.matchesPeekToken(1, OperatorType.PLUS_PLUS) != null;
            case POSTFIX_SUB:
                return parser.matchesPeekToken(1, OperatorType.MINUS_MINUS) != null;
            default:
            return true;
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
