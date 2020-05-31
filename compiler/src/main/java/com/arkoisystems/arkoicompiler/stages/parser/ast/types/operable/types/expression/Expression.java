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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stages.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.CollectionOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.IdentifierOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.NumberOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.StringOperable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.types.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class Expression extends Operable
{
    
    public static Expression GLOBAL_NODE = new Expression(null, null, ASTType.EXPRESSION, null, null);
    
    @Printable(name = "operable")
    @Nullable
    private final Operable operableAST;
    
    @Builder(builderMethodName = "expressionBuilder")
    protected Expression(
            final @Nullable Parser parser,
            final @Nullable Operable operableAST,
            final @NotNull ASTType astType,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, astType, startToken, endToken);
        
        this.operableAST = operableAST;
    }
    
    @NotNull
    @Override
    public Operable parseAST(final @NotNull ArkoiNode parentAST) {
        return this.parseAssignment(parentAST);
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return super.canParse(parser, offset) ||
                PrefixExpression.ADD_GLOBAL_NODE.canParse(parser, offset) ||
                PrefixExpression.SUB_GLOBAL_NODE.canParse(parser, offset) ||
                PrefixExpression.AFFIRM_GLOBAL_NODE.canParse(parser, offset) ||
                PrefixExpression.NEGATE_GLOBAL_NODE.canParse(parser, offset) ||
                ParenthesizedExpression.GLOBAL_NODE.canParse(parser, offset);
    }
    
    @NotNull
    public Operable parseAssignment(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        Operable operableAST = this.parseAdditive(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (AssignmentExpression.ASSIGN_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = AssignmentExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentExpression.ADD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = AssignmentExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.ADD_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentExpression.SUB_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = AssignmentExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.SUB_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentExpression.MUL_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = AssignmentExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.MUL_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentExpression.DIV_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = AssignmentExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.DIV_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentExpression.MOD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = AssignmentExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.MOD_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    public Operable parseAdditive(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        Operable operableAST = this.parseMultiplicative(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (BinaryExpression.ADD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = BinaryExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.ADD)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (BinaryExpression.SUB_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = BinaryExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.SUB)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    protected Operable parseMultiplicative(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        Operable operableAST = this.parseExponential(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (BinaryExpression.MUL_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = BinaryExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.MUL)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (BinaryExpression.DIV_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = BinaryExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.DIV)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (BinaryExpression.MOD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = BinaryExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.MOD)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    private Operable parseExponential(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        Operable operableAST = this.parseOperable(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (BinaryExpression.EXP_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableAST = BinaryExpression.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.EXP)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @SneakyThrows
    @NotNull
    public Operable parseOperable(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        Operable operableAST = null;
        if (PrefixExpression.SUB_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableAST = PrefixExpression.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.PREFIX_SUB)
                    .build()
                    .parseAST(parentAST);
        else if (PrefixExpression.ADD_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableAST = PrefixExpression.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.PREFIX_ADD)
                    .build()
                    .parseAST(parentAST);
        else if (PrefixExpression.NEGATE_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableAST = PrefixExpression.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.NEGATE)
                    .build()
                    .parseAST(parentAST);
        else if (PrefixExpression.AFFIRM_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableAST = PrefixExpression.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.AFFIRM)
                    .build()
                    .parseAST(parentAST);
        else if (ParenthesizedExpression.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            operableAST = ParenthesizedExpression.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
        }
        
        if (operableAST == null) {
            final Operable foundNode = this.getValidNode(
                    StringOperable.GLOBAL_NODE,
                    NumberOperable.GLOBAL_NODE,
                    IdentifierOperable.GLOBAL_NODE,
                    CollectionOperable.GLOBAL_NODE
            );
            
            if (foundNode == null)
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        this.getParser().currentToken(),
                        
                        ParserErrorType.OPERABLE_NOT_SUPPORTED
                );
            
            operableAST = foundNode.clone();
            operableAST.setParser(this.getParser());
            operableAST = (Operable) operableAST.parseAST(parentAST);
        }
        if (operableAST.isFailed())
            return operableAST;
        
        if (PostfixExpression.SUB_GLOBAL_NODE.canParse(this.getParser(), 1)) {
            return PostfixExpression.builder()
                    .parser(this.getParser())
                    .leftHandSide(operableAST)
                    .operatorType(PostfixOperatorType.POSTFIX_SUB)
                    .startToken(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (PostfixExpression.ADD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
            return PostfixExpression.builder()
                    .parser(this.getParser())
                    .leftHandSide(operableAST)
                    .operatorType(PostfixOperatorType.POSTFIX_ADD)
                    .startToken(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        }
        return operableAST;
    }
    
}
