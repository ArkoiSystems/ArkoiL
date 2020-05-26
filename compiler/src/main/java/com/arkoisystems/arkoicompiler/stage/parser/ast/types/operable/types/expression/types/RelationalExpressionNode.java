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
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.RelationalOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class RelationalExpressionNode extends ExpressionNode
{
    
    public static RelationalExpressionNode GLOBAL_NODE = new RelationalExpressionNode(null, null, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final OperableNode leftHandSide;
    
    @Printable(name = "operation")
    @Nullable
    private final RelationalOperatorType operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private final OperableNode rightHandSide;
    
    @Builder
    protected RelationalExpressionNode(
            final @Nullable RelationalOperatorType operatorType,
            final @Nullable OperableNode rightHandSide,
            final @Nullable Parser parser,
            final @Nullable OperableNode leftHandSide,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, null, ASTType.RELATIONAL_EXPRESSION, startToken, endToken);
    
        this.rightHandSide = rightHandSide;
        this.leftHandSide = leftHandSide;
        this.operatorType = operatorType;
    }
    
    @NotNull
    @Override
    public RelationalExpressionNode parseAST(final @NotNull ArkoiNode parentAST) {
        return null;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return false;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
}
