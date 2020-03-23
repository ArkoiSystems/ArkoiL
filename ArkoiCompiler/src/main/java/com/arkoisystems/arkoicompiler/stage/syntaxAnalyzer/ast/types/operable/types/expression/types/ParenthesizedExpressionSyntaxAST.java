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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class ParenthesizedExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private SymbolToken openParenthesis;
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private ExpressionSyntaxAST expressionSyntaxAST;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private SymbolToken closeParenthesis;
    
    
    protected ParenthesizedExpressionSyntaxAST(@NonNull final SyntaxAnalyzer syntaxAnalyzer) {
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
        
        final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = new ExpressionSyntaxAST(this.getSyntaxAnalyzer()).parseAST(parentAST);
        if(optionalExpressionSyntaxAST.isEmpty())
            return Optional.empty();
        this.expressionSyntaxAST = optionalExpressionSyntaxAST.get();
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.EXPRESSION_PARENTHESIZED_WRONG_ENDING
            );
            return Optional.empty();
        }
        this.closeParenthesis = (SymbolToken) this.getSyntaxAnalyzer().currentToken();
        
        return Optional.of(this);
    }
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "└── operable:");
        printStream.println(indents + "    └── " + this.getExpressionSyntaxAST().getClass().getSimpleName());
        this.getExpressionSyntaxAST().printSyntaxAST(printStream, indents + "        ");
    }
    
    
    public static ParenthesizedExpressionSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParenthesizedExpressionSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParenthesizedExpressionSyntaxASTBuilder builder() {
        return new ParenthesizedExpressionSyntaxASTBuilder();
    }
    
    
    public static class ParenthesizedExpressionSyntaxASTBuilder {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        private SymbolToken openParenthesis;
    
        
        private ExpressionSyntaxAST expressionSyntaxAST;
    
    
        private SymbolToken closeParenthesis;
        
        
        private int start, end;
        
        
        public ParenthesizedExpressionSyntaxASTBuilder(SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder open(final SymbolToken openParenthesis) {
            this.openParenthesis = openParenthesis;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder expression(final ExpressionSyntaxAST expressionSyntaxAST) {
            this.expressionSyntaxAST = expressionSyntaxAST;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder close(final SymbolToken closeParenthesis) {
            this.closeParenthesis = closeParenthesis;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public ParenthesizedExpressionSyntaxAST build() {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = new ParenthesizedExpressionSyntaxAST(this.syntaxAnalyzer);
            parenthesizedExpressionSyntaxAST.setExpressionSyntaxAST(this.expressionSyntaxAST);
            parenthesizedExpressionSyntaxAST.setCloseParenthesis(this.closeParenthesis);
            parenthesizedExpressionSyntaxAST.setOpenParenthesis(this.openParenthesis);
            parenthesizedExpressionSyntaxAST.setStart(this.start);
            parenthesizedExpressionSyntaxAST.setEnd(this.end);
            return parenthesizedExpressionSyntaxAST;
        }
        
    }
    
}
