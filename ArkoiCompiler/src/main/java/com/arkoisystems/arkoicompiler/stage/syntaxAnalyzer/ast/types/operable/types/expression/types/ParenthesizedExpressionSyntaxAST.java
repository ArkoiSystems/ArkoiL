/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

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
import java.util.Optional;

public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ExpressionSyntaxAST expressionSyntaxAST;
    
    
    protected ParenthesizedExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_START
            );
            return Optional.empty();
        } else this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
    
        this.getSyntaxAnalyzer().nextToken();
    
        final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = new ExpressionSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
        if (optionalExpressionSyntaxAST.isEmpty())
            return Optional.empty();
        this.expressionSyntaxAST = optionalExpressionSyntaxAST.get();
    
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_ENDING
            );
            return Optional.empty();
        } else this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + (this.getExpressionSyntaxAST() != null ? this.getExpressionSyntaxAST().getClass().getSimpleName() : null));
        if (this.getExpressionSyntaxAST() != null)
            this.getExpressionSyntaxAST().printSyntaxAST(printStream, indents + "        ");
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
        private ExpressionSyntaxAST expressionSyntaxAST;
        
        
        private int start, end;
        
        
        public ParenthesizedExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public ParenthesizedExpressionSyntaxASTBuilder expression(final ExpressionSyntaxAST expressionSyntaxAST) {
            this.expressionSyntaxAST = expressionSyntaxAST;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxAST build() {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = new ParenthesizedExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.expressionSyntaxAST != null)
                parenthesizedExpressionSyntaxAST.setExpressionSyntaxAST(this.expressionSyntaxAST);
            parenthesizedExpressionSyntaxAST.setStart(this.start);
            parenthesizedExpressionSyntaxAST.setEnd(this.end);
            return parenthesizedExpressionSyntaxAST;
        }
        
    }
    
}
