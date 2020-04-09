/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ParameterListSyntaxAST extends ArkoiSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ParameterSyntaxAST> parameters = new ArrayList<>();
    
    
    protected ParameterListSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.PARAMETER_LIST);
    }
    
    
    @NotNull
    @Override
    public ParameterListSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "13"
            );
        }
    
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
    
        this.getSyntaxAnalyzer().nextToken();
    
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (!ParameterSyntaxAST.PARAMETER_DEFINITION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
                break;
        
            final ParameterSyntaxAST parameterSyntaxAST = ParameterSyntaxAST.PARAMETER_DEFINITION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(parameterSyntaxAST.getMarkerFactory());
        
            if (parameterSyntaxAST.isFailed()) {
                return this;
            } else parameters.add(parameterSyntaxAST);
        
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COMMA) == null)
                break;
            this.getSyntaxAnalyzer().nextToken();
        }
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_PARENTHESIS) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    "12"
            );
        }
    
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        
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
        printStream.println(indents + "└── parameters: " + (this.getParameters().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getParameters().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getParameters().get(index);
            if (index == this.getParameters().size() - 1) {
                printStream.println(indents + "    └── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    public static ParameterListSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ParameterListSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ParameterListSyntaxASTBuilder builder() {
        return new ParameterListSyntaxASTBuilder();
    }
    
    
    public static class ParameterListSyntaxASTBuilder
    {
        
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<ParameterSyntaxAST> parameters;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ParameterListSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ParameterListSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ParameterListSyntaxASTBuilder parameters(final List<ParameterSyntaxAST> parameters) {
            this.parameters = parameters;
            return this;
        }
        
        
        public ParameterListSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ParameterListSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ParameterListSyntaxAST build() {
            final ParameterListSyntaxAST parameterListSyntaxAST = new ParameterListSyntaxAST(this.syntaxAnalyzer);
            if (this.parameters != null)
                parameterListSyntaxAST.setParameters(this.parameters);
            parameterListSyntaxAST.setStartToken(this.startToken);
            parameterListSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(parameterListSyntaxAST.getStartToken());
            parameterListSyntaxAST.setEndToken(this.endToken);
            parameterListSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(parameterListSyntaxAST.getEndToken());
            return parameterListSyntaxAST;
        }
        
    }
    
}
