/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.CastOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;

import static com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType.IDENTIFIER;

public class CastExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    
    @Getter
    @Setter
    private CastOperatorType castOperatorType;
    
    
    @Getter
    @Setter
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    public CastExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final AbstractOperableSyntaxAST<?> leftSideOperable, final CastOperatorType castOperatorType, final int end) {
        super(syntaxAnalyzer, ASTType.CAST_EXPRESSION);
        
        this.leftSideOperable = leftSideOperable;
        this.castOperatorType = castOperatorType;
        
        this.setStart(this.leftSideOperable.getStart());
        this.setEnd(end);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + this.getLeftSideOperable().getClass().getSimpleName());
        this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getCastOperatorType());
    }
    
}
