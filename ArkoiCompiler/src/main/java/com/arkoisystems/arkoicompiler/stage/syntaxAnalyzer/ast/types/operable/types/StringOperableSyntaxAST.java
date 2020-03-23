/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private StringToken stringToken;
    
    
    protected StringOperableSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.STRING_OPERABLE);
    }
    
    
    @Override
    public Optional<StringOperableSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.STRING_LITERAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.STRING_OPERABLE_NO_STRING
            );
            return Optional.empty();
        }
        
        this.setStringToken((StringToken) this.getSyntaxAnalyzer().currentToken());
        this.setStart(this.getStringToken().getStart());
        this.setEnd(this.getStringToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "└── operable: " + this.getStringToken().getTokenContent());
    }
    
    
    public static StringOperableSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new StringOperableSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static StringOperableSyntaxASTBuilder builder() {
        return new StringOperableSyntaxASTBuilder();
    }
    
    
    public static class StringOperableSyntaxASTBuilder {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        private StringToken stringToken;
        
        
        private int start, end;
        
        
        public StringOperableSyntaxASTBuilder(SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public StringOperableSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public StringOperableSyntaxASTBuilder literal(final StringToken stringToken) {
            this.stringToken = stringToken;
            return this;
        }
        
        
        public StringOperableSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public StringOperableSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public StringOperableSyntaxAST build() {
            final StringOperableSyntaxAST stringOperableSyntaxAST = new StringOperableSyntaxAST(this.syntaxAnalyzer);
            stringOperableSyntaxAST.setStringToken(this.stringToken);
            stringOperableSyntaxAST.setStart(this.start);
            stringOperableSyntaxAST.setEnd(this.end);
            return stringOperableSyntaxAST;
        }
        
    }
    
}
