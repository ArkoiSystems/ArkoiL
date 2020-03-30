/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.TypeKeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.TypeParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

/**
 * Used if you want to create a new {@link TypeSyntaxAST}. But it is recommend to use the
 * {@link TypeSyntaxAST#TYPE_PARSER} to parse a new {@link TypeSyntaxAST} because with it
 * you can check if the current {@link AbstractToken} is capable to parse this AST.
 */
public class TypeSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is static because we just want a single instance of the {@link
     * TypeParser}.
     */
    public static TypeParser TYPE_PARSER = new TypeParser();
    
    
    /**
     * The {@link TypeKind} specifies the type of this {@link TypeSyntaxAST}. It is used
     * for later usage in the {@link SemanticAnalyzer}.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKeywordToken typeKeywordToken;
    
    
    /**
     * Defines if this {@link TypeSyntaxAST} is an array or not. Useful for later usage
     * when generating pseudo code etc.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean isArray;
    
    
    /**
     * Constructs a new {@link TypeSyntaxAST} without pre-defining any variables. You use
     * this constructor for parsing, so you also need to use the {@link
     * AbstractSyntaxAST#parseAST(AbstractSyntaxAST)} method to initialize all variables
     * correctly.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         * SyntaxAnalyzer#nextToken()}.
     */
    protected TypeSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.TYPE);
    }
    
    
    /**
     * Parses a new {@link TypeSyntaxAST} with the given {@link AbstractSyntaxAST} as an
     * parent AST. It is used to see if this AST can be created inside it or not. Another
     * part of this method is, to check if an array assignment was made or not. At the end
     * it will return itself with information about the start/end, the {@link TypeKind}
     * and if it's an array.
     *
     * @param parentAST
     *         the parent {@link AbstractSyntaxAST} which is used used to check if the
     *         {@link AbstractSyntaxAST} can be created inside it.
     *
     * @return {@code null} if an error occurred or this {@link TypeSyntaxAST} if
     *         everything worked correctly.
     */
    @Override
    public @NotNull TypeSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.TYPE_KEYWORD) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.TYPE_DOES_NOT_START_WITH_TYPE_KEYWORD
            );
            return this;
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
    
    
    /**
     * This method will do nothing because it isn't relevant for the future.
     *
     * @param printStream
     *         the {@link PrintStream} which is used used for the output.
     * @param indents
     *         the {@code indents} which is used used when printing a new line.
     */
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
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
            typeSyntaxAST.setEndToken(this.endToken);
            return typeSyntaxAST;
        }
        
    }
    
}
