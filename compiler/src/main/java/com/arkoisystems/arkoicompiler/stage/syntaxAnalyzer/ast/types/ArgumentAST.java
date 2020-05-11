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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ArgumentParser;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ArgumentAST extends ArkoiASTNode
{
    
    public static ArgumentParser ARGUMENT_DEFINITION_PARSER = new ArgumentParser();
    
    
    @Getter
    @Nullable
    private IdentifierToken argumentName;
    
    @Getter
    @Nullable
    private OperableAST argumentExpression;
    
    @Builder
    private ArgumentAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final OperableAST argumentExpression,
            @Nullable final IdentifierToken argumentName,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        super(syntaxAnalyzer, ASTType.ARGUMENT, startToken, endToken);
    
        this.argumentExpression = argumentExpression;
        this.argumentName = argumentName;
    }
    
    @NotNull
    @Override
    public ArgumentAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
                
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument", "<identifier>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
    
        this.startAST(this.getSyntaxAnalyzer().currentToken());
        this.argumentName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null) {
            final ArkoiToken peekedToken = this.getSyntaxAnalyzer().peekToken(1);
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    peekedToken,
                
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument", "'='", peekedToken != null ? peekedToken.getTokenContent() : "nothing"
            );
        }
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        if (!ExpressionAST.EXPRESSION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            final ArkoiToken currentToken = this.getSyntaxAnalyzer().currentToken();
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    currentToken,
            
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument", "<expression>", currentToken != null ? currentToken.getTokenContent() : "nothing"
            );
        }
        
        final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.argumentExpression = operableAST;
        this.endAST(this.getSyntaxAnalyzer().currentToken());
        return this;
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) {
        visitor.visit(this);
    }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        Objects.requireNonNull(this.getArgumentExpression(), "argumentExpression must not be null.");
        
        return this.getArgumentExpression().getTypeKind();
    }
    
}
