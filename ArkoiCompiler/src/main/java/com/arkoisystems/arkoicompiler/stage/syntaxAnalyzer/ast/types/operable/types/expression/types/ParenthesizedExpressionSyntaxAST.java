/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> parenthesizedExpression;
    
    
    protected ParenthesizedExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public ParenthesizedExpressionSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_START
            );
            return this;
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(abstractOperableSyntaxAST.getMarkerFactory());
        
        if (abstractOperableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.parenthesizedExpression = abstractOperableSyntaxAST;
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_ENDING
            );
            return this;
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + (this.getParenthesizedExpression() != null ? this.getParenthesizedExpression().getClass().getSimpleName() : null));
        if (this.getParenthesizedExpression() != null)
            this.getParenthesizedExpression().printSyntaxAST(printStream, indents + "        ");
    }
    
    
    public static ParenthesizedExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParenthesizedExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParenthesizedExpressionSyntaxASTBuilder builder() {
        return new ParenthesizedExpressionSyntaxASTBuilder();
    }
    
    
    public static class ParenthesizedExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private AbstractOperableSyntaxAST<?> parenthesizedExpression;
        
        
        private AbstractToken startToken, endToken;
    
    
        public ParenthesizedExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public ParenthesizedExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public ParenthesizedExpressionSyntaxASTBuilder expression(final AbstractOperableSyntaxAST<?> parenthesizedExpression) {
            this.parenthesizedExpression = parenthesizedExpression;
            return this;
        }
    
    
        public ParenthesizedExpressionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxAST build() {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = new ParenthesizedExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.parenthesizedExpression != null)
                parenthesizedExpressionSyntaxAST.setParenthesizedExpression(this.parenthesizedExpression);
            parenthesizedExpressionSyntaxAST.setStartToken(this.startToken);
            parenthesizedExpressionSyntaxAST.setEndToken(this.endToken);
            return parenthesizedExpressionSyntaxAST;
        }
        
    }
    
}
