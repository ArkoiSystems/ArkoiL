/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
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
    @NotNull
    private TypeKind typeKind = TypeKind.UNDEFINED;
    
    
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
    public Optional<TypeSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.TYPE_DOES_NOT_START_WITH_IDENTIFIER
            );
            return Optional.empty();
        }
        
        final TypeKind typeKind = TypeKind.getTypeKind(this.getSyntaxAnalyzer().currentToken());
        if (typeKind == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.TYPE_NOT_A_VALID_TYPE
            );
            return Optional.empty();
        } else this.typeKind = typeKind;
        
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        // This will check if the next two Tokens are an opening and closing bracket aka. "[]". If it is, then skip these two Tokens and set the "isArray" boolean to true.
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null && this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.CLOSING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken(2);
            this.isArray = true;
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
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
        private TypeKind typeKind;
        
        
        private boolean isArray;
        
        
        private int start, end;
        
        
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
        
        
        public TypeSyntaxASTBuilder typeKind(@NotNull final TypeKind typeKind) {
            this.typeKind = typeKind;
            return this;
        }
        
        
        public TypeSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public TypeSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public TypeSyntaxAST build() {
            final TypeSyntaxAST typeSyntaxAST = new TypeSyntaxAST(this.syntaxAnalyzer);
            if (this.typeKind != null)
                typeSyntaxAST.setTypeKind(this.typeKind);
            typeSyntaxAST.setArray(this.isArray);
            typeSyntaxAST.setStart(this.start);
            typeSyntaxAST.setEnd(this.end);
            return typeSyntaxAST;
        }
        
    }
    
}
