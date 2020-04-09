/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrefixExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private PrefixOperatorType prefixOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST rightSideOperable;
    
    
    protected PrefixExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PREFIX_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public PrefixExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken(1);
        
        final OperableSyntaxAST operableSyntaxAST = this.parseOperable(parentAST);
        this.getMarkerFactory().addFactory(operableSyntaxAST.getMarkerFactory());
        
        if (operableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.rightSideOperable = operableSyntaxAST;
        
        this.setEndToken(this.rightSideOperable.getEndToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getRightSideOperable());
    
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
        printStream.println(indents + "├── operator: " + this.getPrefixOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
    
    public static PrefixExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new PrefixExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static PrefixExpressionSyntaxASTBuilder builder() {
        return new PrefixExpressionSyntaxASTBuilder();
    }
    
    
    public static class PrefixExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private PrefixOperatorType prefixOperatorType;
        
        
        @Nullable
        private OperableSyntaxAST rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
        
        
        public PrefixExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public PrefixExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public PrefixExpressionSyntaxASTBuilder right(final OperableSyntaxAST rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
        
        
        public PrefixExpressionSyntaxASTBuilder operator(final PrefixOperatorType prefixOperatorType) {
            this.prefixOperatorType = prefixOperatorType;
            return this;
        }
        
        
        public PrefixExpressionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public PrefixExpressionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public PrefixExpressionSyntaxAST build() {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = new PrefixExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.rightSideOperable != null)
                prefixExpressionSyntaxAST.setRightSideOperable(this.rightSideOperable);
            if (this.prefixOperatorType != null)
                prefixExpressionSyntaxAST.setPrefixOperatorType(this.prefixOperatorType);
            prefixExpressionSyntaxAST.setStartToken(this.startToken);
            prefixExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(prefixExpressionSyntaxAST.getStartToken());
            prefixExpressionSyntaxAST.setEndToken(this.endToken);
            prefixExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(prefixExpressionSyntaxAST.getEndToken());
            return prefixExpressionSyntaxAST;
        }
        
    }
    
}
