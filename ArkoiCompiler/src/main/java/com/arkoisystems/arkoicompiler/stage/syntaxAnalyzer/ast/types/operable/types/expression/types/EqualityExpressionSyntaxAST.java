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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.EqualityOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Optional;

public class EqualityExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private EqualityOperatorType equalityOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public EqualityExpressionSyntaxAST(@NotNull final SyntaxAnalyzer syntaxAnalyzer, @NotNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NotNull final EqualityOperatorType equalityOperatorType) {
        super(syntaxAnalyzer, ASTType.EQUALITY_EXPRESSION);
        
        this.equalityOperatorType = equalityOperatorType;
        this.leftSideOperable = leftSideOperable;
    
        this.getMarkerFactory().addFactory(this.leftSideOperable.getMarkerFactory());
        
        this.setStartToken(this.leftSideOperable.getStartToken());
        this.getMarkerFactory().mark(this.getStartToken());
    }
    
    
    @NotNull
    @Override
    public EqualityExpressionSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        return null;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "├── operator: " + this.getEqualityOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
