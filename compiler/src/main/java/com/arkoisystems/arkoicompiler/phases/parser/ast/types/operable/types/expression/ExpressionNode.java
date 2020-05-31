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
package com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.enums.AssignmentOperators;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.enums.BinaryOperators;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.enums.PostfixOperators;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.enums.PrefixOperators;
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class ExpressionNode extends OperableNode
{
    
    public static ExpressionNode GLOBAL_NODE = new ExpressionNode(null, null, null, null);
    
    @Builder(builderMethodName = "expressionBuilder")
    protected ExpressionNode(
            final @Nullable Parser parser,
            final @Nullable SymbolTable currentScope,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, currentScope, startToken, endToken);
    }
    
    @NotNull
    @Override
    public OperableNode parseAST(final @NotNull ParserNode parentAST) {
        return this.parseAssignment(parentAST);
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return super.canParse(parser, offset) ||
                PrefixNode.ADD_GLOBAL_NODE.canParse(parser, offset) ||
                PrefixNode.SUB_GLOBAL_NODE.canParse(parser, offset) ||
                PrefixNode.AFFIRM_GLOBAL_NODE.canParse(parser, offset) ||
                PrefixNode.NEGATE_GLOBAL_NODE.canParse(parser, offset) ||
                ParenthesizedNode.GLOBAL_NODE.canParse(parser, offset);
    }
    
    @NotNull
    public OperableNode parseAssignment(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = this.parseAdditive(parentAST);
        if (operableNode.isFailed())
            return operableNode;
        
        while (true) {
            if (AssignmentNode.ASSIGN_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.ASSIGN)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentNode.ADD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.ADD_ASSIGN)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentNode.SUB_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.SUB_ASSIGN)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentNode.MUL_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.MUL_ASSIGN)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentNode.DIV_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.DIV_ASSIGN)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (AssignmentNode.MOD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.MOD_ASSIGN)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableNode;
        }
    }
    
    @NotNull
    public OperableNode parseAdditive(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = this.parseMultiplicative(parentAST);
        if (operableNode.isFailed())
            return operableNode;
        
        while (true) {
            if (BinaryNode.ADD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.ADD)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (BinaryNode.SUB_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.SUB)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableNode;
        }
    }
    
    @NotNull
    protected OperableNode parseMultiplicative(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = this.parseExponential(parentAST);
        if (operableNode.isFailed())
            return operableNode;
        
        while (true) {
            if (BinaryNode.MUL_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.MUL)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (BinaryNode.DIV_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.DIV)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (BinaryNode.MOD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.MOD)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableNode;
        }
    }
    
    @NotNull
    private OperableNode parseExponential(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = this.parseOperable(parentAST);
        if (operableNode.isFailed())
            return operableNode;
        
        while (true) {
            if (BinaryNode.EXP_GLOBAL_NODE.canParse(this.getParser(), 1)) {
                operableNode = BinaryNode.builder()
                        .parser(this.getParser())
                        .leftHandSide(operableNode)
                        .operatorType(BinaryOperators.EXP)
                        .startToken(operableNode.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableNode;
        }
    }
    
    @SneakyThrows
    @NotNull
    public OperableNode parseOperable(final @NotNull ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = null;
        if (PrefixNode.SUB_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableNode = PrefixNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperators.PREFIX_SUB)
                    .build()
                    .parseAST(parentAST);
        else if (PrefixNode.ADD_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableNode = PrefixNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperators.PREFIX_ADD)
                    .build()
                    .parseAST(parentAST);
        else if (PrefixNode.NEGATE_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableNode = PrefixNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperators.NEGATE)
                    .build()
                    .parseAST(parentAST);
        else if (PrefixNode.AFFIRM_GLOBAL_NODE.canParse(this.getParser(), 0))
            operableNode = PrefixNode.builder()
                    .parser(this.getParser())
                    .operatorType(PrefixOperators.AFFIRM)
                    .build()
                    .parseAST(parentAST);
        else if (ParenthesizedNode.GLOBAL_NODE.canParse(this.getParser(), 0)) {
            operableNode = ParenthesizedNode.builder()
                    .parser(this.getParser())
                    .build()
                    .parseAST(parentAST);
        }
        
        if (operableNode == null) {
            final OperableNode foundNode = this.getValidNode(
                    StringNode.GLOBAL_NODE,
                    NumberNode.GLOBAL_NODE,
                    IdentifierNode.GLOBAL_NODE
            );
            
            if (foundNode == null)
                return this.addError(
                        this,
                        this.getParser().getCompilerClass(),
                        this.getParser().currentToken(),
                        
                        ParserErrorType.OPERABLE_NOT_SUPPORTED
                );
            
            operableNode = foundNode.clone();
            operableNode.setParser(this.getParser());
            operableNode = (OperableNode) operableNode.parseAST(parentAST);
        }
        if (operableNode.isFailed())
            return operableNode;
        
        if (PostfixNode.SUB_GLOBAL_NODE.canParse(this.getParser(), 1)) {
            return PostfixNode.builder()
                    .parser(this.getParser())
                    .leftHandSide(operableNode)
                    .operatorType(PostfixOperators.POSTFIX_SUB)
                    .startToken(operableNode.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (PostfixNode.ADD_GLOBAL_NODE.canParse(this.getParser(), 1)) {
            return PostfixNode.builder()
                    .parser(this.getParser())
                    .leftHandSide(operableNode)
                    .operatorType(PostfixOperators.POSTFIX_ADD)
                    .startToken(operableNode.getStartToken())
                    .build()
                    .parseAST(parentAST);
        }
        return operableNode;
    }
    
}
