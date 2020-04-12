/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTNode;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.TypeParser;
import com.arkoisystems.arkoicompiler.utils.Variables;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class TypeAST extends ArkoiASTNode
{
    
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKeywordToken typeKeywordToken;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean isArray;
    
    
    protected TypeAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.TYPE);
    }
    
    
    @NotNull
    @Override
    public TypeAST parseAST(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.TYPE_KEYWORD) == null)
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    
                    SyntaxErrorType.SYNTAX_ERROR_TEMPLATE,
                    "Type", "<type keyword>", this.getSyntaxAnalyzer().currentToken().getTokenContent()
            );
    
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
    public void accept(@NotNull final IVisitor visitor) {
        visitor.visit(this);
    }
    
    
    public static TypeASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new TypeASTBuilder(syntaxAnalyzer);
    }
    
    
    public static TypeASTBuilder builder() {
        return new TypeASTBuilder();
    }
    
    
    public static class TypeASTBuilder
    {
        
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private TypeKeywordToken typeKeywordToken;
        
        
        private boolean isArray;
        
        
        private AbstractToken startToken, endToken;
        
        
        public TypeASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public TypeASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public TypeASTBuilder array(final boolean isArray) {
            this.isArray = isArray;
            return this;
        }
        
        
        public TypeASTBuilder type(final TypeKeywordToken typeKeywordToken) {
            this.typeKeywordToken = typeKeywordToken;
            return this;
        }
        
        
        public TypeASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public TypeASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public TypeAST build() {
            final TypeAST typeAST = new TypeAST(this.syntaxAnalyzer);
            if (this.typeKeywordToken != null)
                typeAST.setTypeKeywordToken(this.typeKeywordToken);
            typeAST.setArray(this.isArray);
            typeAST.setStartToken(this.startToken);
            typeAST.getMarkerFactory().getCurrentMarker().setStart(typeAST.getStartToken());
            typeAST.setEndToken(this.endToken);
            typeAST.getMarkerFactory().getCurrentMarker().setEnd(typeAST.getEndToken());
            return typeAST;
        }
        
    }
    
}
