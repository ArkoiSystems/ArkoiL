/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ArgumentParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArgumentAST extends ArkoiASTNode
{
    
    public static ArgumentParser ARGUMENT_DEFINITION_PARSER = new ArgumentParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken argumentName;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST argumentExpression;
    
    
    protected ArgumentAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ARGUMENT);
    }
    
    
    @NotNull
    @Override
    public ArgumentAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument", "<identifier>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.setArgumentName((IdentifierToken) this.getSyntaxAnalyzer().currentToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument", "'='", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        if (!ExpressionAST.EXPRESSION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Argument", "<expression>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
        
        final OperableAST operableAST = ExpressionAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableAST.getMarkerFactory());
        
        if (operableAST.isFailed()) {
            this.failed();
            return this;
        }
        
        this.setArgumentExpression(operableAST);
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public static ArgumentASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ArgumentASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ArgumentASTBuilder builder() {
        return new ArgumentASTBuilder();
    }
    
    
    public static class ArgumentASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private IdentifierToken argumentName;
        
        
        @Nullable
        private OperableAST argumentExpression;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ArgumentASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ArgumentASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ArgumentASTBuilder name(final IdentifierToken argumentName) {
            this.argumentName = argumentName;
            return this;
        }
        
        
        public ArgumentASTBuilder expression(final OperableAST argumentExpression) {
            this.argumentExpression = argumentExpression;
            return this;
        }
        
        
        public ArgumentASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ArgumentASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ArgumentAST build() {
            final ArgumentAST parameterAST = new ArgumentAST(this.syntaxAnalyzer);
            if (this.argumentName != null)
                parameterAST.setArgumentName(this.argumentName);
            if (this.argumentExpression != null)
                parameterAST.setArgumentExpression(this.argumentExpression);
            parameterAST.setStartToken(this.startToken);
            parameterAST.getMarkerFactory().getCurrentMarker().setStart(parameterAST.getStartToken());
            parameterAST.setEndToken(this.endToken);
            parameterAST.getMarkerFactory().getCurrentMarker().setEnd(parameterAST.getEndToken());
            return parameterAST;
        }
        
    }
    
}
