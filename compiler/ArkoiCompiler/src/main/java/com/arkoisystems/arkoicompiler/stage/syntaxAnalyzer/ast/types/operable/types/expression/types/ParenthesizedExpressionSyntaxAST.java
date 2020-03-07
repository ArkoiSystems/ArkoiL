/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter
    private ExpressionSyntaxAST expressionSyntaxAST;
    
    
    @Getter
    @Setter
    private SymbolToken openParenthesis, closeParenthesis;
    
    
    public ParenthesizedExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARENTHESIZED_EXPRESSION);
    }
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_START
            );
            return Optional.empty();
        }
        this.openParenthesis = (SymbolToken) this.getSyntaxAnalyzer().currentToken();
        this.getSyntaxAnalyzer().nextToken();
        
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalAbstractOperableSyntaxAST = this.parseAssignment(parentAST);
        if (optionalAbstractOperableSyntaxAST.isEmpty())
            return Optional.empty();
        this.expressionSyntaxAST = new ExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalAbstractOperableSyntaxAST.get());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_ENDING
            );
            return Optional.empty();
        }
        this.closeParenthesis = (SymbolToken) this.getSyntaxAnalyzer().currentToken();
        return Optional.empty();
    }
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getExpressionSyntaxAST().getClass().getSimpleName());
        this.getExpressionSyntaxAST().printSyntaxAST(printStream, indents + "        ");
    }
    
}
