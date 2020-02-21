/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;

import java.io.PrintStream;

public class ReturnStatementSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Getter
    private ExpressionSyntaxAST returnExpression;
    
    
    /**
     * This constructor is used to initialize the AST-Type "RETURN_STATEMENT_AST" for this
     * class. This will help to debug problems or check the AST for correct Syntax.
     */
    public ReturnStatementSyntaxAST() {
        super(ASTType.RETURN_STATEMENT);
    }
    
    
    /**
     * This method will parse the "return" statement and checks it for the correct syntax.
     * This statement can just be used inside a BlockAST because it will define the return
     * type of it. You can't use a "this" statement in-front of it and it needs to end
     * with a semicolon.
     * <p>
     * An example for correct usage:
     * <p>
     * fun main<int>(args: string[]) = 0;
     *
     * @param parentAST
     *         The parent of this AST which just can be a BlockAST.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an ReturnStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public ReturnStatementSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(syntaxAnalyzer.getArkoiClass(), parentAST, "Couldn't parse the \"return\" statement because it isn't declared inside a block."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("return")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because the parsing doesn't start with the \"return\" keyword."));
            return null;
        } else {
            this.setStart(syntaxAnalyzer.currentToken().getStart());
            syntaxAnalyzer.nextToken(); // This will skip to the followed token after the "return" keyword, so we can check if the next token is an expression.
        }
        
        if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because the keyword isn't followed by an valid expression."));
            return null;
        }
    
        final ExpressionSyntaxAST expressionAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
        if (expressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because an error occurred during the parsing of the expression."));
            return null;
        } else this.returnExpression = expressionAST;
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because it doesn't end with a semicolon."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "└── expression:");
        this.getReturnExpression().printSyntaxAST(printStream, indents + "    ");
    }
    
}
