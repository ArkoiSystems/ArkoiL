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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.RelationalOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Optional;

public class RelationalExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private RelationalOperatorType relationalOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> rightSideOperable;
    
    
    public RelationalExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final AbstractOperableSyntaxAST<?> leftSideOperable, @NotNull final RelationalOperatorType relationalOperatorType) {
        super(syntaxAnalyzer, ASTType.RELATIONAL_EXPRESSION);
        
        this.relationalOperatorType = relationalOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.getMarkerFactory().addFactory(this.leftSideOperable.getMarkerFactory());
        
        this.setStartToken(this.leftSideOperable.getStartToken());
        this.getMarkerFactory().mark(this.getStartToken());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        return Optional.empty();
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "├── operator: " + this.getRelationalOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSyntaxAST(printStream, indents + "        ");
    }
    
}
