/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter
    private ExpressionSyntaxAST expressionSyntaxAST;
    
    
    @Getter
    @Setter
    private SymbolToken openParenthesis, closeParenthesis;
    
    
    public ParenthesizedExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final SymbolToken openParenthesis, final ExpressionSyntaxAST expressionSyntaxAST, final SymbolToken closeParenthesis) {
        super(syntaxAnalyzer, ASTType.PARENTHESIZED_EXPRESSION);
        
        this.closeParenthesis = closeParenthesis;
        this.openParenthesis = openParenthesis;
        
        this.expressionSyntaxAST = expressionSyntaxAST;
        
        this.setStart(this.openParenthesis.getStart());
        this.setEnd(this.closeParenthesis.getEnd());
    }
    
    
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST) {
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getExpressionSyntaxAST().getClass().getSimpleName());
        this.getExpressionSyntaxAST().printSyntaxAST(printStream, indents + "        ");
    }
    
}
