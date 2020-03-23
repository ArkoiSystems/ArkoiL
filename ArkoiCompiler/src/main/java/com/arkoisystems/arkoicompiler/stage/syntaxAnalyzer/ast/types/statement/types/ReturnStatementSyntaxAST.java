/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class ReturnStatementSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Getter
    @Setter(value = AccessLevel.PROTECTED)
    private ExpressionSyntaxAST returnExpression;
    
    
    /**
     * This constructor is used to initialize the AST-Type "RETURN_STATEMENT_AST" for this
     * class. This will help to debug problems or check the AST for correct Syntax.
     */
    protected ReturnStatementSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.RETURN_STATEMENT);
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
     * @return It will return null if an error occurred or an ReturnStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public Optional<ReturnStatementSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_WRONG_PARENT
            );
            return Optional.empty();
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null || !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("return")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_WRONG_START
            );
            return Optional.empty();
        }
    
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        this.getSyntaxAnalyzer().nextToken(); // This will skip to the followed token after the "return" keyword, so we can check if the next token is an expression.
    
        if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_NO_VALID_EXPRESSION
            );
            return Optional.empty();
        }
    
        final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (optionalExpressionSyntaxAST.isEmpty())
            return Optional.empty();
        this.returnExpression = optionalExpressionSyntaxAST.get();
    
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.SEMICOLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_WRONG_ENDING
            );
            return Optional.empty();
        }
    
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "└── expression:");
        this.getReturnExpression().printSyntaxAST(printStream, indents + "    ");
    }
    
    
    public static ReturnStatementSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new ReturnStatementSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static class ReturnStatementSyntaxASTBuilder {
        
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private ExpressionSyntaxAST expressionSyntaxAST;
        
        
        private int start, end;
        
        
        public ReturnStatementSyntaxASTBuilder(final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ReturnStatementSyntaxASTBuilder expression(final ExpressionSyntaxAST expressionSyntaxAST) {
            this.expressionSyntaxAST = expressionSyntaxAST;
            return this;
        }
        
        
        public ReturnStatementSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
    
        
        public ReturnStatementSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public ReturnStatementSyntaxAST build() {
            final ReturnStatementSyntaxAST returnStatementSyntaxAST = new ReturnStatementSyntaxAST(this.syntaxAnalyzer);
            returnStatementSyntaxAST.setReturnExpression(this.expressionSyntaxAST);
            returnStatementSyntaxAST.setStart(this.start);
            returnStatementSyntaxAST.setEnd(this.end);
            return returnStatementSyntaxAST;
        }
    
    }
    
}
