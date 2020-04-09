/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.RelationalOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class RelationalExpressionSyntaxAST extends ExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private RelationalOperatorType relationalOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST rightSideOperable;
    
    
    public RelationalExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final OperableSyntaxAST leftSideOperable, @NotNull final RelationalOperatorType relationalOperatorType) {
        super(syntaxAnalyzer, ASTType.RELATIONAL_EXPRESSION);
        
        this.relationalOperatorType = relationalOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.getMarkerFactory().addFactory(this.leftSideOperable.getMarkerFactory());
        
        this.setStartToken(this.leftSideOperable.getStartToken());
        this.getMarkerFactory().mark(this.getStartToken());
    }
    
    
    @NotNull
    @Override
    public RelationalExpressionSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        return null;
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
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "├── operator: " + this.getRelationalOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + this.getRightSideOperable().getClass().getSimpleName());
        this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
