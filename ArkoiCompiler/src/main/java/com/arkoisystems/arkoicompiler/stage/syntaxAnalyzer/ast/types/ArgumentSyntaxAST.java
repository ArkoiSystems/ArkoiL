/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ArgumentParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ArgumentSyntaxAST extends AbstractSyntaxAST
{
    
    public static ArgumentParser ARGUMENT_DEFINITION_PARSER = new ArgumentParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private IdentifierToken argumentName = IdentifierToken
            .builder()
            .content("Undefined identifier for \"argumentName\"")
            .crash()
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ExpressionSyntaxAST argumentExpression;
    
    
    protected ArgumentSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ARGUMENT_DEFINITION);
    }
    
    
    @Override
    public Optional<ArgumentSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_WRONG_START
            );
            return Optional.empty();
        }
        
        this.argumentName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.EQUAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENT_NO_SEPARATOR
            );
            return Optional.empty();
        }
        
        this.getSyntaxAnalyzer().nextToken();
        
        if (!ExpressionSyntaxAST.EXPRESSION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_VALID_EXPRESSION
            );
            return Optional.empty();
        }
        
        final Optional<ExpressionSyntaxAST> optionalExpressionSyntaxAST = ExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (optionalExpressionSyntaxAST.isEmpty())
            return Optional.empty();
        this.argumentExpression = optionalExpressionSyntaxAST.get();
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getArgumentName().getTokenContent());
        printStream.println(indents + "└── expression: " + (this.getArgumentExpression() == null ? null : ""));
        if (this.getArgumentExpression() != null)
            this.getArgumentExpression().printSyntaxAST(printStream, indents + "     ");
    }
    
    
    @NotNull
    public static Optional<List<ArgumentSyntaxAST>> parseArguments(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final List<ArgumentSyntaxAST> arguments = new ArrayList<>();
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    SyntaxErrorType.ARGUMENTS_WRONG_START
            );
            return Optional.empty();
        } else syntaxAnalyzer.nextToken();
        
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (!ArgumentSyntaxAST.ARGUMENT_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
            
            final Optional<ArgumentSyntaxAST> optionalArgumentSyntaxAST = ArgumentSyntaxAST.ARGUMENT_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (optionalArgumentSyntaxAST.isEmpty())
                return Optional.empty();
            else arguments.add(optionalArgumentSyntaxAST.get());
            
            if (syntaxAnalyzer.matchesNextToken(SymbolType.COMMA) == null)
                break;
            else syntaxAnalyzer.nextToken();
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    SyntaxErrorType.ARGUMENTS_WRONG_ENDING
            );
            return Optional.empty();
        }
        return Optional.of(arguments);
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
        private ExpressionSyntaxAST argumentExpression;
        
        
        private int start, end;
        
        
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
        
        
        public ArgumentSyntaxASTBuilder expression(final ExpressionSyntaxAST argumentExpression) {
            this.argumentExpression = argumentExpression;
            return this;
        }
        
        
        public ArgumentSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public ArgumentSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public ArgumentSyntaxAST build() {
            final ArgumentSyntaxAST parameterSyntaxAST = new ArgumentSyntaxAST(this.syntaxAnalyzer);
            if (this.argumentName != null)
                parameterSyntaxAST.setArgumentName(this.argumentName);
            if (this.argumentExpression != null)
                parameterSyntaxAST.setArgumentExpression(this.argumentExpression);
            parameterSyntaxAST.setStart(this.start);
            parameterSyntaxAST.setEnd(this.end);
            return parameterSyntaxAST;
        }
        
    }
    
}
