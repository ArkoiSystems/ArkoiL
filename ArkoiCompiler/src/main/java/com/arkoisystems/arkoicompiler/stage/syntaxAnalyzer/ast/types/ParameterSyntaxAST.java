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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ParameterParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * already has an {@link ParameterSyntaxAST} with the same name as the
     * other.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private IdentifierToken parameterName;
    
    
    /**
     * Defines the {@link TypeSyntaxAST} of this {@link ParameterSyntaxAST}
     * which is useful for checking the semantic in later stages.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private TypeSyntaxAST parameterType;
    
    
    /**
     * Constructs a new {@link ParameterSyntaxAST} with the {@link
     * SyntaxAnalyzer} as a parameter which is used to check for correct syntax with
     * methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} * or {@link
     * SyntaxAnalyzer#nextToken()}.
     *
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to check for correct syntax with
     *         methods like {@link SyntaxAnalyzer#matchesNextToken(SymbolType)} or {@link
     *         * SyntaxAnalyzer#nextToken()}.
     */
    protected ParameterSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARAMETER_DEFINITION);
    }
    
    
    @Override
    public Optional<ParameterSyntaxAST> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_WRONG_START
            );
            return Optional.empty();
        }
        
        this.parameterName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_SEPARATOR
            );
            return Optional.empty();
        }
        
        this.getSyntaxAnalyzer().nextToken();
        
        if (!TypeSyntaxAST.TYPE_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.PARAMETER_NO_VALID_TYPE
            );
            return Optional.empty();
        }
        
        final Optional<TypeSyntaxAST> optionalTypeSyntaxAST = TypeSyntaxAST.TYPE_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (optionalTypeSyntaxAST.isEmpty())
            return Optional.empty();
        this.parameterType = optionalTypeSyntaxAST.get();
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) {
        printStream.println(indents + "├── name: " + this.getParameterName().getTokenContent());
        printStream.println(indents + "└── type: " + this.getParameterType().getTypeKind().getName() + (this.getParameterType().isArray() ? "[]" : ""));
    }
    
    
    /**
     * This method provides the ability to parse a list of arguments which is used by the
     * FunctionDefinitionAST for the function arguments. It will throw an error if the
     * arguments didn't get separated by a semicolon or if it doesn't start/end with a
     * parenthesis.
     *
     * @param parentAST
     *         The ParentAST defines the AST in which the arguments should be getting
     *         parsed. This is useful to check if the AST is supported or to report
     *         errors.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or the given "argumentsASTs" list
     *         if it parsed until to the end.
     */
    public static Optional<List<ParameterSyntaxAST>> parseParameters(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final List<ParameterSyntaxAST> parameters = new ArrayList<>();
        if (syntaxAnalyzer.matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            parentAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    syntaxAnalyzer.currentToken(),
                    SyntaxErrorType.ARGUMENTS_WRONG_START
            );
            return Optional.empty();
        } else syntaxAnalyzer.nextToken();
    
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (!ParameterSyntaxAST.PARAMETER_DEFINITION_PARSER.canParse(parentAST, syntaxAnalyzer))
                break;
        
            final Optional<ParameterSyntaxAST> optionalParameterDefinitionSyntaxAST = ParameterSyntaxAST.PARAMETER_DEFINITION_PARSER.parse(parentAST, syntaxAnalyzer);
            if (optionalParameterDefinitionSyntaxAST.isEmpty())
                return Optional.empty();
            else parameters.add(optionalParameterDefinitionSyntaxAST.get());
        
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
            return Optional.empty();
        }
        return Optional.of(parameters);
    }
    
    
    public static ParameterSyntaxASTBuilder builder(final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParameterSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParameterSyntaxASTBuilder builder() {
        return new ParameterSyntaxASTBuilder();
    }
    
    
    public static class ParameterSyntaxASTBuilder
    {
        
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private IdentifierToken argumentName;
        
        
        private TypeSyntaxAST argumentType;
        
        
        private int start, end;
        
        
        public ParameterSyntaxASTBuilder(SyntaxAnalyzer syntaxAnalyzer) {
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
        
        
        public ParameterSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public ParameterSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public ParameterSyntaxAST build() {
            final ParameterSyntaxAST parameterSyntaxAST = new ParameterSyntaxAST(this.syntaxAnalyzer);
            parameterSyntaxAST.setParameterType(this.argumentType);
            parameterSyntaxAST.setParameterName(this.argumentName);
            parameterSyntaxAST.setStart(this.start);
            parameterSyntaxAST.setEnd(this.end);
            return parameterSyntaxAST;
        }
        
    }
    
}
