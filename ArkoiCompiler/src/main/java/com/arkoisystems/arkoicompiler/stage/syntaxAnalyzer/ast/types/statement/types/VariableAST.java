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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.StatementAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
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
    private IdentifierToken variableName;
    
    
    @Getter
    @Nullable
    private OperableAST variableExpression;
    
    
    @Builder
    public VariableAST(
            @Nullable final List<AnnotationAST> variableAnnotations,
            @Nullable final OperableAST variableExpression,
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final IdentifierToken variableName,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(syntaxAnalyzer, ASTType.VARIABLE, startToken, endToken);
        
        this.variableAnnotations = variableAnnotations;
        this.variableExpression = variableExpression;
        this.variableName = variableName;
    }
    
    
    @NotNull
    @Override
    public VariableAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'var'", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().peekToken(1),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "<identifier>", this.getSyntaxAnalyzer().peekToken(1).getTokenContent()
            );
        
        this.variableName = (IdentifierToken) this.getSyntaxAnalyzer().nextToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "'='", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        if (!ExpressionAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer()))
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
        
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Variable", "<expression>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
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
