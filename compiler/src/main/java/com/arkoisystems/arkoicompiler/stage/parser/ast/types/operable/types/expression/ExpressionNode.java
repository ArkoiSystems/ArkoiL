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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.CollectionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.IdentifierCallNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ExpressionNode extends OperableNode
{
    
    @Printable(name = "operable")
    @Nullable
    private final OperableNode operableAST;
    
    @Builder(builderMethodName = "expressionBuilder")
    protected ExpressionNode(
            final @Nullable Parser parser,
            final @Nullable OperableNode operableAST,
            final @NotNull ASTType astType,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, astType, startToken, endToken);
        
        this.operableAST = operableAST;
    }
    
    @NotNull
    @Override
    public OperableNode parseAST(final @NotNull ArkoiNode parentAST) {
        return this.parseAssignment(parentAST);
    }
    
    @NotNull
    public OperableNode parseAssignment(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableAST = this.parseAdditive(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.EQUALS) != null) {
                operableAST = AssignmentExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.PLUS_EQUALS) != null) {
                operableAST = AssignmentExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.ADD_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.MINUS_EQUALS) != null) {
                operableAST = AssignmentExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.SUB_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.ASTERISK_EQUALS) != null) {
                operableAST = AssignmentExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.MUL_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.DIV_EQUALS) != null) {
                operableAST = AssignmentExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(AssignmentOperatorType.DIV_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.PERCENT_EQUALS) != null) {
                operableAST = AssignmentExpressionNode.builder()
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
    public OperableNode parseAdditive(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableAST = this.parseMultiplicative(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.PLUS) != null) {
                operableAST = BinaryExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.ADDITION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.MINUS) != null) {
                operableAST = BinaryExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.SUBTRACTION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    protected OperableNode parseMultiplicative(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableAST = this.parseExponential(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.ASTERISK) != null) {
                operableAST = BinaryExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.MULTIPLICATION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.DIV) != null) {
                operableAST = BinaryExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.DIVISION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getParser().matchesPeekToken(1, OperatorType.PERCENT) != null) {
                operableAST = BinaryExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.MODULO)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    private OperableNode parseExponential(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableAST = this.parseOperable(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.ASTERISK_ASTERISK) != null) {
                operableAST = BinaryExpressionNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableAST)
                        .operatorType(BinaryOperatorType.EXPONENTIAL)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @SneakyThrows
    @NotNull
    public OperableNode parseOperable(final @NotNull ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableAST = null;
        if (this.getParser().matchesCurrentToken(OperatorType.MINUS_MINUS) != null)
            operableAST = PrefixExpressionNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.PREFIX_SUB)
                    .build()
                    .parseAST(parentAST);
        else if (this.getParser().matchesCurrentToken(OperatorType.PLUS_PLUS) != null)
            operableAST = PrefixExpressionNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.PREFIX_ADD)
                    .build()
                    .parseAST(parentAST);
        else if (this.getParser().matchesCurrentToken(OperatorType.MINUS) != null)
            operableAST = PrefixExpressionNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.NEGATE)
                    .build()
                    .parseAST(parentAST);
        else if (this.getParser().matchesCurrentToken(OperatorType.PLUS) != null)
            operableAST = PrefixExpressionNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperatorType.AFFIRM)
                    .build()
                    .parseAST(parentAST);
        else if (this.getParser().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null) {
            operableAST = ParenthesizedExpressionNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
        }
        
        if (operableAST == null) {
            final OperableNode foundNode = this.getValidNode(
                    StringNode.GLOBAL_NODE,
                    NumberNode.GLOBAL_NODE,
                    IdentifierCallNode.GLOBAL_NODE,
                    CollectionNode.GLOBAL_NODE
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
            operableAST = (OperableNode) operableAST.parseAST(parentAST);
        }
        if (operableAST.isFailed())
            return operableAST;
        
        if (this.getParser().matchesPeekToken(1, OperatorType.MINUS_MINUS) != null) {
            return PostfixExpressionNode.builder()
                    .parser(this.getParser())
                    .leftHandSide(operableAST)
                    .operatorType(PostfixOperatorType.POSTFIX_SUB)
                    .startToken(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getParser().matchesPeekToken(1, OperatorType.PLUS_PLUS) != null) {
            return PostfixExpressionNode.builder()
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
