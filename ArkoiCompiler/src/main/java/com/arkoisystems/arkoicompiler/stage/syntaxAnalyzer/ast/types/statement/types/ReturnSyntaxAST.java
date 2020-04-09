/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReturnSyntaxAST extends AbstractStatementSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST returnExpression;
    
    
    protected ReturnSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.RETURN);
    }
    
    
    @NotNull
    @Override
    public ReturnSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof BlockSyntaxAST)) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_WRONG_PARENT
            );
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.RETURN) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        if (!ExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.RETURN_STATEMENT_NO_VALID_EXPRESSION
            );
        }
        
        final OperableSyntaxAST operableSyntaxAST = ExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableSyntaxAST.getMarkerFactory());
        
        if (operableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.returnExpression = operableSyntaxAST;
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getReturnExpression());
    
        printStream.println(indents + "├── factory:");
        printStream.println(indents + "│    ├── next: " + this.getMarkerFactory()
                .getNextMarkerFactories()
                .stream()
                .map(markerFactory -> markerFactory.getCurrentMarker().getAstType().name())
                .collect(Collectors.joining(", "))
        );
        printStream.println(indents + "│    ├── start: " + this.getMarkerFactory().getCurrentMarker().getStart().getStart());
        printStream.println(indents + "│    └── end: " + this.getMarkerFactory().getCurrentMarker().getEnd().getEnd());
        printStream.println(indents + "│");
        printStream.println(indents + "└── expression: ");
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
        private OperableSyntaxAST expressionSyntaxAST;
        
        
        private AbstractToken startToken, endToken;
    
    
        public ReturnStatementSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public ReturnStatementSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public ReturnStatementSyntaxASTBuilder expression(final OperableSyntaxAST expressionSyntaxAST) {
            this.expressionSyntaxAST = expressionSyntaxAST;
            return this;
        }
    
    
        public ReturnStatementSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
    
    
        public ReturnStatementSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
    
    
        public ReturnSyntaxAST build() {
            final ReturnSyntaxAST returnSyntaxAST = new ReturnSyntaxAST(this.syntaxAnalyzer);
            if (this.expressionSyntaxAST != null)
                returnSyntaxAST.setReturnExpression(this.expressionSyntaxAST);
            returnSyntaxAST.setStartToken(this.startToken);
            returnSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(returnSyntaxAST.getStartToken());
            returnSyntaxAST.setEndToken(this.endToken);
            returnSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(returnSyntaxAST.getEndToken());
            return returnSyntaxAST;
        }
    
    }
    
}
