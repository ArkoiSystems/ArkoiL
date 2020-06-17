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
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.AssignmentOperators;
import com.arkoisystems.compiler.visitor.IVisitor;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class AssignmentNode extends ExpressionNode
{
    
    public static AssignmentNode GLOBAL_NODE = new AssignmentNode(null, null, null, null, null, null, null, null);
    
    @Printable(name = "lhs")
    @Nullable
    private final OperableNode leftHandSide;
    
    @Printable(name = "operation")
    @Nullable
    private final AssignmentOperators operatorType;
    
    @Printable(name = "rhs")
    @Nullable
    private final OperableNode rightHandSide;
    
    @Builder
    protected AssignmentNode(
            @Nullable final Parser parser,
            @Nullable final ParserNode parentNode,
            @Nullable final SymbolTable currentScope,
            @Nullable final AssignmentOperators operatorType,
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
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
        
        OperableNode operableNode = BinaryNode.builder()
                .parser(this.getParser())
                .currentScope(this.getCurrentScope())
                .parentNode(this.getParentNode())
                .build()
                .parseAdditive();
        if (operableNode.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        while (true) {
            if (this.getParser().matchesPeekToken(1, OperatorType.EQUALS) != null) {
                this.getParser().nextToken(2);
                
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.ASSIGN)
                        .build()
                        .parse();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.PLUS_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.ADD_ASSIGN)
                        .build()
                        .parse();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.MINUS_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.SUB_ASSIGN)
                        .build()
                        .parse();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.ASTERISK_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.MUL_ASSIGN)
                        .build()
                        .parse();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.SLASH_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.DIV_ASSIGN)
                        .build()
                        .parse();
            } else if (this.getParser().matchesPeekToken(1, OperatorType.PERCENT_EQUALS) != null) {
                this.getParser().nextToken(2);
                
                operableNode = AssignmentNode.builder()
                        .parser(this.getParser())
                        .currentScope(this.getCurrentScope())
                        .parentNode(this)
                        .startToken(operableNode.getStartToken())
                        .leftHandSide(operableNode)
                        .operatorType(AssignmentOperators.MOD_ASSIGN)
                        .build()
                        .parse();
            } else {
                this.setEndToken(this.getParser().currentToken());
                return operableNode;
            }
        }
    }
    
    @Override
    public boolean canParse(@NotNull final Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, OperatorType.EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.PLUS_EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.MINUS_EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.ASTERISK_EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.SLASH_EQUALS) != null ||
                parser.matchesPeekToken(offset, OperatorType.PERCENT_EQUALS) != null;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeNode getTypeNode() {
        Objects.requireNonNull(this.getLeftHandSide(), "leftHandSide must not be null.");
        return this.getLeftHandSide().getTypeNode();
    }
    
}
