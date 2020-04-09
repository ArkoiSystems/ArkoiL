/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ParameterParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParameterSyntaxAST extends ArkoiSyntaxAST
{
    
    public static ParameterParser PARAMETER_DEFINITION_PARSER = new ParameterParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken parameterName;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeSyntaxAST parameterType;
    
    
    protected ParameterSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARAMETER);
    }
    
    
    @NotNull
    @Override
    public ParameterSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.parameterName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.COLON) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_SEPARATOR
            );
        } else this.getSyntaxAnalyzer().nextToken(2);
        
        if (!TypeSyntaxAST.TYPE_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_VALID_TYPE
            );
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
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getParameterName());
        Objects.requireNonNull(this.getParameterType());
    
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
        printStream.println(indents + "├── name: " + this.getParameterName().getTokenContent());
        printStream.println(indents + "└── type:");
        this.getParameterType().printSyntaxAST(printStream, indents + "     ");
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
            parameterSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(parameterSyntaxAST.getStartToken());
            parameterSyntaxAST.setEndToken(this.endToken);
            parameterSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(parameterSyntaxAST.getEndToken());
            return parameterSyntaxAST;
        }
        
    }
    
}
