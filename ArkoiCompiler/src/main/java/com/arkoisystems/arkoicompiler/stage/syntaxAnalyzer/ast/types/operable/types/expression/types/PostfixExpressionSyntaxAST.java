/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostfixExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private PostfixOperatorType postfixOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST leftSideOperable;
    
    
    protected PostfixExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.POSTFIX_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public PostfixExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {  Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getLeftSideOperable());
    
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
    
        this.getSyntaxAnalyzer().nextToken(2);
    
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
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
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getPostfixOperatorType());
    }
    
    
    public static PostfixExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new PostfixExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static PostfixExpressionSyntaxASTBuilder builder() {
        return new PostfixExpressionSyntaxASTBuilder();
    }
    
    
    public static class PostfixExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private PostfixOperatorType postfixOperatorType;
        
        
        @Nullable
        private OperableSyntaxAST leftSideOperable;
        
        
        private AbstractToken startToken, endToken;
        
        
        public PostfixExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public PostfixExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public PostfixExpressionSyntaxASTBuilder left(final OperableSyntaxAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public PostfixExpressionSyntaxASTBuilder operator(final PostfixOperatorType postfixOperatorType) {
            this.postfixOperatorType = postfixOperatorType;
            return this;
        }
        
        
        public PostfixExpressionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public PostfixExpressionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public PostfixExpressionSyntaxAST build() {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = new PostfixExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                postfixExpressionSyntaxAST.setLeftSideOperable(this.leftSideOperable);
            if (this.postfixOperatorType != null)
                postfixExpressionSyntaxAST.setPostfixOperatorType(this.postfixOperatorType);
            postfixExpressionSyntaxAST.setStartToken(this.startToken);
            postfixExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(postfixExpressionSyntaxAST.getStartToken());
            postfixExpressionSyntaxAST.setEndToken(this.endToken);
            postfixExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(postfixExpressionSyntaxAST.getEndToken());
            return postfixExpressionSyntaxAST;
        }
        
    }
    
}
