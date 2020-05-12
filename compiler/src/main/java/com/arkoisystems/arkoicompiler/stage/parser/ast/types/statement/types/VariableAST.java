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

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.AnnotationAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class VariableAST extends StatementAST
{
    
    @Getter
    @Nullable
    private final List<AnnotationAST> variableAnnotations;
    
    @Getter
    @Nullable
    private ArkoiToken variableName;
    
    @Getter
    @Nullable
    private OperableAST variableExpression;
    
    @Builder
    private VariableAST(
            @Nullable final List<AnnotationAST> variableAnnotations,
            @Nullable final OperableAST variableExpression,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final ArkoiToken variableName,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken
    ) {
        super(syntaxAnalyzer, startToken, endToken, ASTType.VARIABLE);
        
        this.variableAnnotations = variableAnnotations;
        this.variableExpression = variableExpression;
        this.variableName = variableName;
    }
    
    @NotNull
    @Override
    public VariableAST parseAST(@Nullable final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
                
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'var'", currentToken != null ? currentToken.getData() : "nothing"
            );
        }
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    peekedToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "<identifier>", peekedToken != null ? peekedToken.getData() : "nothing"
            );
        }
        
        this.variableName = this.getSyntaxAnalyzer().nextToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null) {
            final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    peekedToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'='", peekedToken != null ? peekedToken.getData() : "nothing"
            );
        }
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        if (!ExpressionAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "<expression>", currentToken != null ? currentToken.getData() : "nothing"
            );
        }
        
        final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.variableExpression = operableAST;
        this.endAST(this.getSyntaxAnalyzer().currentToken());
        return this;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getVariableExpression(), "variableExpression must not be null.");
        
        return this.getVariableExpression().getTypeKind();
    }
    
}
