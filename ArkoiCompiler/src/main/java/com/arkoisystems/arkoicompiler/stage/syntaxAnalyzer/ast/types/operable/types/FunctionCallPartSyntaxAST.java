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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
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

public class FunctionCallPartSyntaxAST extends AbstractSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AbstractOperableSyntaxAST<?>> calledExpressions = new ArrayList<>();
    
    
    protected FunctionCallPartSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.FUNCTION_CALL_PART);
    }
    
    
    @NotNull
    @Override
    public FunctionCallPartSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "2"
            );
            return this;
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) != null)
                break;
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, getSyntaxAnalyzer()))
                break;
            
            final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(abstractOperableSyntaxAST.getMarkerFactory());
            
            if (abstractOperableSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else this.calledExpressions.add(abstractOperableSyntaxAST);
            
            this.getSyntaxAnalyzer().nextToken();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "4"
            );
            return this;
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
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
        private List<AbstractOperableSyntaxAST<?>> calledExpressions;
        
        
        private AbstractToken startToken, endToken;
    
    
        public FunctionCallPartSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public FunctionCallPartSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public FunctionCallPartSyntaxASTBuilder expressions(final List<AbstractOperableSyntaxAST<?>> expressions) {
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
