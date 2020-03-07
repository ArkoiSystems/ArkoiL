/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.LogicalOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;

public class PostfixExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter
    private PostfixOperatorType postfixOperatorType;
    
    
    @Getter
    @Setter
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    public PostfixExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final AbstractOperableSyntaxAST<?> leftSideOperable, final PostfixOperatorType postfixOperatorType, final int end) {
        super(syntaxAnalyzer, ASTType.POSTFIX_EXPRESSION);
        
        this.postfixOperatorType = postfixOperatorType;
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(this.leftSideOperable.getStart());
        this.setEnd(end);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getPostfixOperatorType());
    }
    
}
