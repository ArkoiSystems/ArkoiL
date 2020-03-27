/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
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
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class ReturnStatementSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ExpressionSyntaxAST returnExpression;
    
    
    protected ReturnStatementSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.RETURN_STATEMENT);
    }
    
    
    @Override
    public Optional<ReturnStatementSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_WRONG_PARENT
            );
            return Optional.empty();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.RETURN) == null) {
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
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "└── expression: " + (this.getReturnExpression() == null ? null : ""));
        if (this.getReturnExpression() != null)
            this.getReturnExpression().printSyntaxAST(printStream, indents + "    ");
    }
    
    
    public static ReturnStatementSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ReturnStatementSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ReturnStatementSyntaxASTBuilder builder() {
        return new ReturnStatementSyntaxASTBuilder();
    }
    
    
    public static class ReturnStatementSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private ExpressionSyntaxAST expressionSyntaxAST;
        
        
        private int start, end;
        
        
        public ReturnStatementSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ReturnStatementSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
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
            if (this.expressionSyntaxAST != null)
                returnStatementSyntaxAST.setReturnExpression(this.expressionSyntaxAST);
            returnStatementSyntaxAST.setStart(this.start);
            returnStatementSyntaxAST.setEnd(this.end);
            return returnStatementSyntaxAST;
        }
        
    }
    
}
