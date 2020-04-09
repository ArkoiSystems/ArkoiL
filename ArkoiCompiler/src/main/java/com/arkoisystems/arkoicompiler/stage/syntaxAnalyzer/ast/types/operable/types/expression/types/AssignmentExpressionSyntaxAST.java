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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class AssignmentExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AssignmentOperatorType assignmentOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST rightSideOperable;
    
    
    protected AssignmentExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    @NotNull
    @Override
    public AssignmentExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getLeftSideOperable());
    
        this.getMarkerFactory().addFactory(this.getLeftSideOperable().getMarkerFactory());
    
        this.getSyntaxAnalyzer().nextToken(2);
    
        final OperableSyntaxAST operableSyntaxAST = this.parseAdditive(parentAST);
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
        printStream.println(indents + "├── operator: " + this.getAssignmentOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
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
        private OperableSyntaxAST leftSideOperable;
        
        
        @Nullable
        private AssignmentOperatorType assignmentOperatorType;
        
        
        @Nullable
        private OperableSyntaxAST rightSideOperable;
        
        
        private AbstractToken startToken, endToken;
        
        
        public AssignmentExpressionSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder left(final OperableSyntaxAST leftSideOperable) {
            this.leftSideOperable = leftSideOperable;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder operator(final AssignmentOperatorType assignmentOperatorType) {
            this.assignmentOperatorType = assignmentOperatorType;
            return this;
        }
        
        
        public AssignmentExpressionSyntaxASTBuilder right(final OperableSyntaxAST rightSideOperable) {
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
            assignmentExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(assignmentExpressionSyntaxAST.getStartToken());
            assignmentExpressionSyntaxAST.setEndToken(this.endToken);
            assignmentExpressionSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(assignmentExpressionSyntaxAST.getEndToken());
            return assignmentExpressionSyntaxAST;
        }
        
    }
    
}
