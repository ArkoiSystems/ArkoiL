/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ArgumentParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArgumentSyntaxAST extends ArkoiSyntaxAST
{
    
    public static ArgumentParser ARGUMENT_DEFINITION_PARSER = new ArgumentParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken argumentName;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST argumentExpression;
    
    
    protected ArgumentSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ARGUMENT);
    }
    
    
    @NotNull
    @Override
    public ArgumentSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_WRONG_START
            );
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
    
        this.argumentName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_NO_SEPARATOR
            );
        } else this.getSyntaxAnalyzer().nextToken(2);
    
        if (!ExpressionSyntaxAST.EXPRESSION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_VALID_EXPRESSION
            );
        }
    
        final OperableSyntaxAST operableSyntaxAST = ExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        this.getMarkerFactory().addFactory(operableSyntaxAST.getMarkerFactory());
    
        if (operableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.argumentExpression = operableSyntaxAST;
    
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getArgumentExpression());
        Objects.requireNonNull(this.getArgumentName());
        
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
        printStream.println(indents + "├── name: " + this.getArgumentName().getTokenContent());
        printStream.println(indents + "└── expression: ");
        this.getArgumentExpression().printSyntaxAST(printStream, indents + "     ");
    }
    
    
    public static ArgumentSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ArgumentSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ArgumentSyntaxASTBuilder builder() {
        return new ArgumentSyntaxASTBuilder();
    }
    
    
    public static class ArgumentSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private IdentifierToken argumentName;
        
        
        @Nullable
        private OperableSyntaxAST argumentExpression;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ArgumentSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ArgumentSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ArgumentSyntaxASTBuilder name(final IdentifierToken argumentName) {
            this.argumentName = argumentName;
            return this;
        }
        
        
        public ArgumentSyntaxASTBuilder expression(final OperableSyntaxAST argumentExpression) {
            this.argumentExpression = argumentExpression;
            return this;
        }
        
        
        public ArgumentSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ArgumentSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ArgumentSyntaxAST build() {
            final ArgumentSyntaxAST parameterSyntaxAST = new ArgumentSyntaxAST(this.syntaxAnalyzer);
            if (this.argumentName != null)
                parameterSyntaxAST.setArgumentName(this.argumentName);
            if (this.argumentExpression != null)
                parameterSyntaxAST.setArgumentExpression(this.argumentExpression);
            parameterSyntaxAST.setStartToken(this.startToken);
            parameterSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(parameterSyntaxAST.getStartToken());
            parameterSyntaxAST.setEndToken(this.endToken);
            parameterSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(parameterSyntaxAST.getEndToken());
            return parameterSyntaxAST;
        }
        
    }
    
}
