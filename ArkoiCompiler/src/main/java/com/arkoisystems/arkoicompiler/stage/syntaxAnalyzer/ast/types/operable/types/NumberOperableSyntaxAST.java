/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.Optional;

public class NumberOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private NumberToken numberToken;
    
    
    protected NumberOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.NUMBER_OPERABLE);
    }
    
    
    @Override
    public Optional<NumberOperableSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.NUMBER_LITERAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.NUMBER_OPERABLE_NO_NUMBER
            );
            return Optional.empty();
        }
        
        this.setNumberToken((NumberToken) this.getSyntaxAnalyzer().currentToken());
        this.setStart(this.getNumberToken().getStart());
        this.setEnd(this.getNumberToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "└── operable: " + this.getNumberToken().getTokenContent());
    }
    
    
    public static NumberOperableSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new NumberOperableSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static NumberOperableSyntaxASTBuilder builder() {
        return new NumberOperableSyntaxASTBuilder();
    }
    
    
    public static class NumberOperableSyntaxASTBuilder {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private NumberToken numberToken;
        
        
        private int start, end;
        
        
        public NumberOperableSyntaxASTBuilder(SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public NumberOperableSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public NumberOperableSyntaxASTBuilder literal(final NumberToken numberToken) {
            this.numberToken = numberToken;
            return this;
        }
        
        
        public NumberOperableSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public NumberOperableSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public NumberOperableSyntaxAST build() {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = new NumberOperableSyntaxAST(this.syntaxAnalyzer);
            numberOperableSyntaxAST.setNumberToken(this.numberToken);
            numberOperableSyntaxAST.setStart(this.start);
            numberOperableSyntaxAST.setEnd(this.end);
            return numberOperableSyntaxAST;
        }
        
    }
    
}
