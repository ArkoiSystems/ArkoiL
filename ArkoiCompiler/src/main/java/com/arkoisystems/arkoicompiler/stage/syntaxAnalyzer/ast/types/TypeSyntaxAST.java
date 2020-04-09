/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.TypeParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeSyntaxAST extends ArkoiSyntaxAST
{
    
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKeywordToken typeKeywordToken;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean isArray;

    
    protected TypeSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.TYPE);
    }
    
    
    @NotNull
    @Override
    public TypeSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.TYPE_KEYWORD) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.TYPE_START_NO_VALID_KEYWORD
            );
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.setTypeKeywordToken((TypeKeywordToken) this.getSyntaxAnalyzer().currentToken());
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null && this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.CLOSING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken(2);
            this.isArray = true;
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getTypeKeywordToken());
        
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
        printStream.println(indents + "└── keyword: " + this.getTypeKeywordToken().getTypeKind().name() + (this.isArray() ? "[]" : ""));
    }
    
    
    public static TypeSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new TypeSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static TypeSyntaxASTBuilder builder() {
        return new TypeSyntaxASTBuilder();
    }
    
    
    public static class TypeSyntaxASTBuilder
    {
    
    
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private TypeKeywordToken typeKeywordToken;
        
        
        private boolean isArray;
        
        
        private AbstractToken startToken, endToken;
    
    
        public TypeSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public TypeSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public TypeSyntaxASTBuilder array(final boolean isArray) {
            this.isArray = isArray;
            return this;
        }
        
        
        public TypeSyntaxASTBuilder type(final TypeKeywordToken typeKeywordToken) {
            this.typeKeywordToken = typeKeywordToken;
            return this;
        }
        
        
        public TypeSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public TypeSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public TypeSyntaxAST build() {
            final TypeSyntaxAST typeSyntaxAST = new TypeSyntaxAST(this.syntaxAnalyzer);
            if (this.typeKeywordToken != null)
                typeSyntaxAST.setTypeKeywordToken(this.typeKeywordToken);
            typeSyntaxAST.setArray(this.isArray);
            typeSyntaxAST.setStartToken(this.startToken);
            typeSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(typeSyntaxAST.getStartToken());
            typeSyntaxAST.setEndToken(this.endToken);
            typeSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(typeSyntaxAST.getEndToken());
            return typeSyntaxAST;
        }
        
    }
    
}
