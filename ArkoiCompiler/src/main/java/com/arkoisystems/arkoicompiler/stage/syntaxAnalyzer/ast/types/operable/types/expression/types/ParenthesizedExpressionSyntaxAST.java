/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParenthesizedExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST parenthesizedExpression;
    
    
    protected ParenthesizedExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public ParenthesizedExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        final OperableSyntaxAST operableSyntaxAST = ExpressionSyntaxAST.EXPRESSION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableSyntaxAST.getMarkerFactory());
        
        if (operableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.parenthesizedExpression = operableSyntaxAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.CLOSING_PARENTHESIS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_ENDING
            );
        } else this.getSyntaxAnalyzer().nextToken();
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getParenthesizedExpression());
        
        printStream.println(indents + "├── factory:");
        printStream.println(indents + "│    ├── next: " + this.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        printStream.println(indents + "│    ├── start: " + this.getMarkerFactory().getCurrentMarker().getStart().getStart());
        printStream.println(indents + "│    └── end: " + this.getMarkerFactory().getCurrentMarker().getEnd().getEnd());
        printStream.println(indents + "│");
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getParenthesizedExpression().getClass().getSimpleName());
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
        private OperableSyntaxAST parenthesizedExpression;
        
        
        private AbstractToken startToken, endToken;
    
    
        public ParenthesizedExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public ParenthesizedExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public ParenthesizedExpressionSyntaxASTBuilder expression(final OperableSyntaxAST parenthesizedExpression) {
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
            parenthesizedExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(parenthesizedExpressionSyntaxAST.getStartToken());
            parenthesizedExpressionSyntaxAST.setEndToken(this.endToken);
            parenthesizedExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(parenthesizedExpressionSyntaxAST.getEndToken());
            return parenthesizedExpressionSyntaxAST;
        }
        
    }
    
}
