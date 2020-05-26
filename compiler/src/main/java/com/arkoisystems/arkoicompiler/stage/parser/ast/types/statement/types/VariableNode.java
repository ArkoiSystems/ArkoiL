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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.StatementNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class VariableNode extends StatementNode
{
    
    public static VariableNode GLOBAL_NODE = new VariableNode(null, null, null, null, null);
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "expression")
    @Nullable
    private OperableNode expression;
    
    @Builder
    protected VariableNode(
            final @Nullable OperableNode expression,
            final @Nullable Parser parser,
            final @Nullable IdentifierToken name,
            final @Nullable ArkoiToken startToken,
            final @Nullable ArkoiToken endToken
    ) {
        super(parser, ASTType.VARIABLE, startToken, endToken);
        
        this.expression = expression;
        this.name = name;
    }
    
    @NotNull
    @Override
    public VariableNode parseAST(final @Nullable ArkoiNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        if (this.getParser().matchesCurrentToken(KeywordType.VAR) == null && this.getParser().matchesCurrentToken(KeywordType.CONST) == null) {
            final ArkoiToken currentToken = this.getParser().currentToken();
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    currentToken,
                
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'var' or 'const'", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        this.startAST(this.getParser().currentToken());
        
        if (this.getParser().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
            
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "<identifier>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.name = (IdentifierToken) this.getParser().nextToken();
        
        if (this.getParser().matchesPeekToken(1, OperatorType.EQUALS) == null) {
            final ArkoiToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
            
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'='", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.getParser().nextToken(2);
        
//        if (!ExpressionNode.EXPRESSION_PARSER.canParse(this, this.getParser())) {
//            final ArkoiToken currentToken = this.getParser().currentToken();
//            return this.addError(
//                    this,
//                    this.getParser().getCompilerClass(),
//                    currentToken,
//
//                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
//                    "Variable", "<expression>", currentToken != null ? currentToken.getTokenContent() : "nothing"
//            );
//        }
        
        final OperableNode operableAST = ExpressionNode.expressionBuilder()
                .parser(this.getParser())
                .build()
                .parseAST(this);
        if (operableAST.isFailed()) {
            this.setFailed(true);
            return this;
        }
        
        this.expression = operableAST;
        this.endAST(this.getParser().currentToken());
        return this;
    }
    
    @Override
    public boolean canParse(final @NotNull Parser parser, final int offset) {
        return parser.matchesPeekToken(offset, KeywordType.VAR) != null ||
                parser.matchesPeekToken(offset, KeywordType.CONST) != null;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getExpression(), "variableExpression must not be null.");
        
        return this.getExpression().getTypeKind();
    }
    
}
