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
package com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stages.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stages.parser.Parser;
import com.arkoisystems.arkoicompiler.stages.parser.ParserErrorType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.operable.types.expression.Expression;
import com.arkoisystems.arkoicompiler.stages.parser.ast.types.statement.Statement;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.ASTType;
import com.arkoisystems.arkoicompiler.stages.parser.ast.enums.TypeKind;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class VariableStatement extends Statement
{
    
    public static VariableStatement GLOBAL_NODE = new VariableStatement(null, null, null, null, null);
    
    @Printable(name = "name")
    @Nullable
    private IdentifierToken name;
    
    @Printable(name = "expression")
    @Nullable
    private Operable expression;
    
    @Builder
    protected VariableStatement(
            final @Nullable Operable expression,
            final @Nullable Parser parser,
            final @Nullable IdentifierToken name,
            final @Nullable LexerToken startToken,
            final @Nullable LexerToken endToken
    ) {
        super(parser, ASTType.VARIABLE, startToken, endToken);
        
        this.expression = expression;
        this.name = name;
    }
    
    @NotNull
    @Override
    public VariableStatement parseAST(final @Nullable ParserNode parentAST) {
        Objects.requireNonNull(this.getParser(), "parser must not be null.");
    
        if (this.getParser().matchesCurrentToken(KeywordType.VAR) == null && this.getParser().matchesCurrentToken(KeywordType.CONST) == null) {
            final LexerToken currentToken = this.getParser().currentToken();
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
            final LexerToken peekedToken = this.getParser().peekToken(1);
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
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
            
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'='", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.getParser().nextToken();
        
        if(!Expression.GLOBAL_NODE.canParse(this.getParser(), 1)) {
            final LexerToken peekedToken = this.getParser().peekToken(1);
            return this.addError(
                    this,
                    this.getParser().getCompilerClass(),
                    peekedToken,
                
                    ParserErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "<expression>", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.getParser().nextToken();
        
        final Operable operableAST = Expression.expressionBuilder()
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
    @NotNull
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getExpression(), "expression must not be null.");
        return this.getExpression().getTypeKind();
    }
    
}
