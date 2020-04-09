/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class NumberSyntaxAST extends OperableSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private NumberToken numberToken;
    
    
    protected NumberSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.NUMBER);
    }
    
    
    @NotNull
    @Override
    public NumberSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.NUMBER_LITERAL) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.NUMBER_OPERABLE_NO_NUMBER
            );
        }
    
        this.setNumberToken((NumberToken) this.getSyntaxAnalyzer().currentToken());
        
        this.setStartToken(this.getNumberToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.setEndToken(this.getNumberToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getNumberToken());
    
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
        printStream.println(indents + "└── operable: " + this.getNumberToken().getTokenContent());
    }
    
    
    public static NumberOperableSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new NumberOperableSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static NumberOperableSyntaxASTBuilder builder() {
        return new NumberOperableSyntaxASTBuilder();
    }
    
    
    public static class NumberOperableSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private NumberToken numberToken;
        
        
        private AbstractToken startToken, endToken;
        
        
        public NumberOperableSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public NumberOperableSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public NumberOperableSyntaxASTBuilder literal(final NumberToken numberToken) {
            this.numberToken = numberToken;
            return this;
        }
    
    
        public NumberOperableSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
    
    
        public NumberOperableSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
    
    
        public NumberSyntaxAST build() {
            final NumberSyntaxAST numberSyntaxAST = new NumberSyntaxAST(this.syntaxAnalyzer);
            if (this.numberToken != null)
                numberSyntaxAST.setNumberToken(this.numberToken);
            numberSyntaxAST.setStartToken(this.startToken);
            numberSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(numberSyntaxAST.getStartToken());
            numberSyntaxAST.setEndToken(this.endToken);
            numberSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(numberSyntaxAST.getEndToken());
            return numberSyntaxAST;
        }
    
    }
    
}
