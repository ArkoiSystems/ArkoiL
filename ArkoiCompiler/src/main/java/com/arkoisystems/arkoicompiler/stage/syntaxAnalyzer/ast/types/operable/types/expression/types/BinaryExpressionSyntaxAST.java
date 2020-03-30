/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

@Getter
public class BinaryExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private BinaryOperatorType binaryOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    protected BinaryExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.BINARY_EXPRESSION);
    }
    
    
    public BinaryExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NotNull final BinaryOperatorType binaryOperatorType) {
        super(syntaxAnalyzer, ASTType.BINARY_EXPRESSION);
        
        this.binaryOperatorType = binaryOperatorType;
        this.leftSideOperable = leftSideOperable;
    
        this.getMarkerFactory().addFactory(this.leftSideOperable.getMarkerFactory());
        
        this.setStartToken(this.leftSideOperable.getStartToken());
        this.getMarkerFactory().mark(this.getStartToken());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        this.getSyntaxAnalyzer().nextToken(2);
        
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalRightSideAST = this.parseMultiplicative(parentAST);
        if (optionalRightSideAST.isEmpty())
            return Optional.empty();
        
        this.getMarkerFactory().addFactory(optionalRightSideAST.get().getMarkerFactory());
        this.rightSideOperable = optionalRightSideAST.get();
        
        this.setEndToken(this.rightSideOperable.getEndToken());
        this.getMarkerFactory().done(this.getEndToken());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null) : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getBinaryOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null) : null));
        if (this.getRightSideOperable() != null)
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
        private AbstractOperableSyntaxAST<?> leftSideOperable;
        
        
        @Nullable
        private BinaryOperatorType binaryOperatorType;
        
        
        @Nullable
        private AbstractOperableSyntaxAST<?> rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
    
    
        public BinaryExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder left(final AbstractOperableSyntaxAST<?> leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder operator(final BinaryOperatorType binaryOperatorType) {
            this.binaryOperatorType = binaryOperatorType;
            return this;
        }
    
    
        public BinaryExpressionSyntaxASTBuilder right(final AbstractOperableSyntaxAST<?> rightSideOperable) {
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
            binaryExpressionSyntaxAST.setEndToken(this.endToken);
            return binaryExpressionSyntaxAST;
        }
        
    }
    
}
