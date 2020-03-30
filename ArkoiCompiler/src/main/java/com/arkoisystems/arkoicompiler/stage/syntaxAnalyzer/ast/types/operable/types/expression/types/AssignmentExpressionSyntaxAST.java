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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class AssignmentExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AssignmentOperatorType assignmentOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    protected AssignmentExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    public AssignmentExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NotNull final AssignmentOperatorType assignmentOperatorType) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
        
        this.assignmentOperatorType = assignmentOperatorType;
        this.leftSideOperable = leftSideOperable;
    
        this.getMarkerFactory().addFactory(this.leftSideOperable.getMarkerFactory());
        
        this.setStartToken(this.leftSideOperable.getStartToken());
        this.getMarkerFactory().mark(this.getStartToken());
    }
    
    
    @NotNull
    @Override
    public AssignmentExpressionSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        this.getSyntaxAnalyzer().nextToken(2);
        
        final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = this.parseAdditive(parentAST);
        this.getMarkerFactory().addFactory(abstractOperableSyntaxAST.getMarkerFactory());
        
        if (abstractOperableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.rightSideOperable = abstractOperableSyntaxAST;
        
        this.setEndToken(this.rightSideOperable.getEndToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getAssignmentOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
    
    public static AssignmentExpressionSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new AssignmentExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static AssignmentExpressionSyntaxASTBuilder builder() {
        return new AssignmentExpressionSyntaxASTBuilder();
    }
    
    
    public static class AssignmentExpressionSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private AbstractOperableSyntaxAST<?> leftSideOperable;
        
        
        @Nullable
        private AssignmentOperatorType assignmentOperatorType;
        
        
        @Nullable
        private AbstractOperableSyntaxAST<?> rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
        
        
        public AssignmentExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder left(final AbstractOperableSyntaxAST<?> leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder operator(final AssignmentOperatorType assignmentOperatorType) {
            this.assignmentOperatorType = assignmentOperatorType;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder right(final AbstractOperableSyntaxAST<?> rightSideOperable) {
            this.rightSideOperable = rightSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxAST build() {
            final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST = new AssignmentExpressionSyntaxAST(this.syntaxAnalyzer);
            if (this.leftSideOperable != null)
                assignmentExpressionSyntaxAST.setLeftSideOperable(this.leftSideOperable);
            if (this.assignmentOperatorType != null)
                assignmentExpressionSyntaxAST.setAssignmentOperatorType(this.assignmentOperatorType);
            if (this.rightSideOperable != null)
                assignmentExpressionSyntaxAST.setRightSideOperable(this.rightSideOperable);
            assignmentExpressionSyntaxAST.setStartToken(this.startToken);
            assignmentExpressionSyntaxAST.setEndToken(this.endToken);
            return assignmentExpressionSyntaxAST;
        }
        
    }
    
}
