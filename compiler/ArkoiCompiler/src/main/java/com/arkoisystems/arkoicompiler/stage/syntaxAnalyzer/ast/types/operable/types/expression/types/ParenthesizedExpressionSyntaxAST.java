/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.PrintStream;

public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    private final SymbolToken openParenthesis;
    
    
    @Getter
    private final ExpressionSyntaxAST parenthesizedExpression;
    
    
    @Getter
    private final SymbolToken closeParenthesis;
    
    
    public ParenthesizedExpressionSyntaxAST(final SymbolToken openParenthesis, final ExpressionSyntaxAST parenthesizedExpression, final SymbolToken closeParenthesis) {
        super(ASTType.PARENTHESIZED_EXPRESSION);
        
        this.parenthesizedExpression = parenthesizedExpression;
        this.closeParenthesis = closeParenthesis;
        this.openParenthesis = openParenthesis;
        
        this.setStart(openParenthesis.getStart());
        this.setEnd(closeParenthesis.getEnd());
    }
    
    
    @Override
    public ParenthesizedExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getParenthesizedExpression().getClass().getSimpleName());
        this.getParenthesizedExpression().printSyntaxAST(printStream, indents + "        ");
    }
    
}
