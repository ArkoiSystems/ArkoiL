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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class BinaryExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BinaryOperatorType binaryOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST rightSideOperable;
    
    
    protected BinaryExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.BINARY_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public BinaryExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getLeftSideOperable());
        
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
        
        this.getSyntaxAnalyzer().nextToken(2);
        
        final OperableSyntaxAST operableSyntaxAST = this.parseMultiplicative(parentAST);
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
        Objects.requireNonNull(this.getLeftSideOperable());
    
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
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getBinaryOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
    
    public static BinaryExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new BinaryExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static BinaryExpressionSyntaxASTBuilder builder() {
        return new BinaryExpressionSyntaxASTBuilder();
    }
    
    
    public static class BinaryExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private OperableSyntaxAST leftSideOperable;
        
        
        @Nullable
        private BinaryOperatorType binaryOperatorType;
    
    
        @Nullable
        private OperableSyntaxAST rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
    
    
        public BinaryExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder left(final OperableSyntaxAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder operator(final BinaryOperatorType binaryOperatorType) {
            this.binaryOperatorType = binaryOperatorType;
            return this;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder right(final OperableSyntaxAST rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public BinaryExpressionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public BinaryExpressionSyntaxAST build() {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = new BinaryExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                binaryExpressionSyntaxAST.setLeftSideOperable(this.leftSideOperable);
            if (this.binaryOperatorType != null)
                binaryExpressionSyntaxAST.setBinaryOperatorType(this.binaryOperatorType);
            if (this.rightSideOperable != null)
                binaryExpressionSyntaxAST.setRightSideOperable(this.rightSideOperable);
            binaryExpressionSyntaxAST.setStartToken(this.startToken);
            binaryExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(binaryExpressionSyntaxAST.getStartToken());
            binaryExpressionSyntaxAST.setEndToken(this.endToken);
            binaryExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(binaryExpressionSyntaxAST.getEndToken());
            return binaryExpressionSyntaxAST;
        }
        
    }
    
}
