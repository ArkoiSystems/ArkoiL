/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
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
import java.util.Optional;

public class ExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private AbstractOperableSyntaxAST<?> expressionOperable;
    
    
    protected ExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.EXPRESSION);
    }
    
    
    @Override
    public Optional<ExpressionSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalAbstractOperableSyntaxAST = this.parseAssignment(this);
        if (optionalAbstractOperableSyntaxAST.isEmpty())
            return Optional.empty();
        this.expressionOperable = optionalAbstractOperableSyntaxAST.get();
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        this.getExpressionOperable().printSyntaxAST(printStream, indents);
    }
    
    
    public static ExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ExpressionSyntaxASTBuilder builder() {
        return new ExpressionSyntaxASTBuilder();
    }
    
    
    public static class ExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private AbstractOperableSyntaxAST<?> expressionOperable;
        
        
        private int start, end;
        
        
        public ExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ExpressionSyntaxASTBuilder operable(@NotNull final AbstractOperableSyntaxAST<?> expressionOperable) {
            this.expressionOperable = expressionOperable;
            return this;
        }
        
        
        public ExpressionSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public ExpressionSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public ExpressionSyntaxAST build() {
            final ExpressionSyntaxAST typeSyntaxAST = new ExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.expressionOperable != null)
                typeSyntaxAST.setExpressionOperable(this.expressionOperable);
            typeSyntaxAST.setStart(this.start);
            typeSyntaxAST.setEnd(this.end);
            return typeSyntaxAST;
        }
        
    }
    
}
