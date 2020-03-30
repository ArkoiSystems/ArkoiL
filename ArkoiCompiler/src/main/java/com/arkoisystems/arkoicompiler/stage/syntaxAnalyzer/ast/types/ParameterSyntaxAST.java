/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ParameterParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParameterSyntaxAST extends AbstractSyntaxAST
{
    
    /**
     * This variable is static because we just want a single instance of the {@link
     * ParameterParser}.
     */
    public static ParameterParser PARAMETER_DEFINITION_PARSER = new ParameterParser();
    
    
    /**
     * Defines the argument name with an {@link IdentifierToken} which is used to check
     * semantic errors in later stages. E.g. if the stage wants to check if a function
     * already has an {@link ParameterSyntaxAST} with the same name as the other.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private IdentifierToken parameterName = IdentifierToken
            .builder()
            .content("Undefined identifier for \"parameterName\"")
            .crash()
            .build();
    
    
    /**
     * Defines the {@link TypeSyntaxAST} of this {@link ParameterSyntaxAST} which is
     * useful for checking the semantic in later stages.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeSyntaxAST parameterType;
    
    
    /**
     * Constructs a new {@link ParameterSyntaxAST} with the {@link SyntaxAnalyzer} as a
     * parameter which is used to check for correct syntax with methods like {@link
     * SyntaxAnalyzer#matchesNextToken(SymbolType)} * or {@link
     * SyntaxAnalyzer#nextToken()}.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         * SyntaxAnalyzer#nextToken()}.
     */
    protected ParameterSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARAMETER_DEFINITION);
    }
    
    
    @Override
    public @NotNull ParameterSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_WRONG_START
            );
            return this;
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.parameterName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_SEPARATOR
            );
            return this;
        }
        
        this.getSyntaxAnalyzer().nextToken();
    
        if (!TypeSyntaxAST.TYPE_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_VALID_TYPE
            );
            return this;
        }
    
        final TypeSyntaxAST typeSyntaxAST = TypeSyntaxAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(typeSyntaxAST.getMarkerFactory());
        
        if (typeSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.parameterType = typeSyntaxAST;
    
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getParameterName().getTokenContent());
        printStream.println(indents + "└── type: " + (this.getParameterType() != null ? this.getParameterType().getTypeKeywordToken().getKeywordType() + (this.getParameterType().isArray() ? "[]" : "") : null));
    }
    
    
    // TODO: Make a separated class named ParameterListSyntaxAST
    public static @NotNull List<ParameterSyntaxAST> parseParameters(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final List<ParameterSyntaxAST> parameters = new ArrayList<>();
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    SyntaxErrorType.PARAMETERS_WRONG_START
            );
            return null;
        } else syntaxAnalyzer.nextToken();
        
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (!ParameterSyntaxAST.PARAMETER_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
    
            final ParameterSyntaxAST parameterSyntaxAST = ParameterSyntaxAST.PARAMETER_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (parameterSyntaxAST.isFailed()) {
                return null;
            } else parameters.add(parameterSyntaxAST);
    
            if (syntaxAnalyzer.matchesNextToken(SymbolType.COMMA) == null)
                break;
            else syntaxAnalyzer.nextToken();
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    SyntaxErrorType.ARGUMENTS_WRONG_ENDING
            );
            return null;
        }
        return parameters;
    }
    
    
    public static ParameterSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParameterSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParameterSyntaxASTBuilder builder() {
        return new ParameterSyntaxASTBuilder();
    }
    
    
    public static class ParameterSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private IdentifierToken argumentName;
        
        
        @Nullable
        private TypeSyntaxAST argumentType;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ParameterSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ParameterSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ParameterSyntaxASTBuilder name(final IdentifierToken argumentName) {
            this.argumentName = argumentName;
            return this;
        }
        
        
        public ParameterSyntaxASTBuilder type(final TypeSyntaxAST argumentType) {
            this.argumentType = argumentType;
            return this;
        }
        
        
        public ParameterSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ParameterSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ParameterSyntaxAST build() {
            final ParameterSyntaxAST parameterSyntaxAST = new ParameterSyntaxAST(this.syntaxAnalyzer);
            if (this.argumentName != null)
                parameterSyntaxAST.setParameterName(this.argumentName);
            if (this.argumentType != null)
                parameterSyntaxAST.setParameterType(this.argumentType);
            parameterSyntaxAST.setStartToken(this.startToken);
            parameterSyntaxAST.setEndToken(this.endToken);
            return parameterSyntaxAST;
        }
        
    }
    
}
