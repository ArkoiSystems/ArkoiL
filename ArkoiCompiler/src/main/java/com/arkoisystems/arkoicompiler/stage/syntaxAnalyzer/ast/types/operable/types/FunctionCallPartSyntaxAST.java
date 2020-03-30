/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 24, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FunctionCallPartSyntaxAST extends AbstractSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ExpressionSyntaxAST> calledExpressions = new ArrayList<>();
    
    
    protected FunctionCallPartSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION_CALL_PART);
    }
    
    
    @Override
    public Optional<FunctionCallPartSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "2"
            );
            return Optional.empty();
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) != null)
                break;
            if (!ExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, getSyntaxAnalyzer()))
                break;
            
            final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = ExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, getSyntaxAnalyzer());
            if (optionalExpressionSyntaxAST.isEmpty()) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "3"
                );
                return Optional.empty();
            }
            
            this.getMarkerFactory().addFactory(optionalExpressionSyntaxAST.get().getMarkerFactory());
            this.calledExpressions.add(optionalExpressionSyntaxAST.get());
            this.getSyntaxAnalyzer().nextToken();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "4"
            );
            return Optional.empty();
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
    public static FunctionCallPartSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new FunctionCallPartSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static FunctionCallPartSyntaxASTBuilder builder() {
        return new FunctionCallPartSyntaxASTBuilder();
    }
    
    
    public static class FunctionCallPartSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<ExpressionSyntaxAST> calledExpressions;
        
        
        private AbstractToken startToken, endToken;
        
        
        public FunctionCallPartSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public FunctionCallPartSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public FunctionCallPartSyntaxASTBuilder expressions(final List<ExpressionSyntaxAST> expressions) {
            this.calledExpressions = expressions;
            return this;
        }
    
    
        public FunctionCallPartSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
    
    
        public FunctionCallPartSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public FunctionCallPartSyntaxAST build() {
            final FunctionCallPartSyntaxAST functionCallPartSyntaxAST = new FunctionCallPartSyntaxAST(this.syntaxAnalyzer);
            if (this.calledExpressions != null)
                functionCallPartSyntaxAST.setCalledExpressions(this.calledExpressions);
            functionCallPartSyntaxAST.setStartToken(this.startToken);
            functionCallPartSyntaxAST.setEndToken(this.endToken);
            return functionCallPartSyntaxAST;
        }
        
    }
    
}
