/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class NumberOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private NumberToken numberToken = NumberToken
            .builder()
            .content("Undefined number for \"numberToken\"")
            .crash()
            .build();
    
    
    protected NumberOperableSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.NUMBER_OPERABLE);
    }
    
    
    @NotNull
    @Override
    public NumberOperableSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.NUMBER_LITERAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.NUMBER_OPERABLE_NO_NUMBER
            );
            return this;
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
        
        
        public NumberOperableSyntaxAST build() {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = new NumberOperableSyntaxAST(this.syntaxAnalyzer);
            if (this.numberToken != null)
                numberOperableSyntaxAST.setNumberToken(this.numberToken);
            numberOperableSyntaxAST.setStartToken(this.startToken);
            numberOperableSyntaxAST.setEndToken(this.endToken);
            return numberOperableSyntaxAST;
        }
        
    }
    
}
