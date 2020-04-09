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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
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

public class ArgumentListSyntaxAST extends ArkoiSyntaxAST
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ArgumentSyntaxAST> arguments = new ArrayList<>();
    
    
    public ArgumentListSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ARGUMENT_LIST);
    }
    
    
    @NotNull
    @Override
    public ArgumentListSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACKET) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENTS_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        this.getSyntaxAnalyzer().nextToken();
        
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (!ArgumentSyntaxAST.ARGUMENT_DEFINITION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer()))
                break;
            
            final ArgumentSyntaxAST argumentSyntaxAST = ArgumentSyntaxAST.ARGUMENT_DEFINITION_PARSER.parse(parentAST, this.getSyntaxAnalyzer());
            this.getMarkerFactory().addFactory(argumentSyntaxAST.getMarkerFactory());
            
            if (argumentSyntaxAST.isFailed()) {
                return this;
            } else arguments.add(argumentSyntaxAST);
            
            if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.COMMA) == null)
                break;
            else this.getSyntaxAnalyzer().nextToken();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ARGUMENTS_WRONG_ENDING
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
        printStream.println(indents + "└── arguments: " + (this.getArguments().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getArguments().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getArguments().get(index);
            if (index == this.getArguments().size() - 1) {
                printStream.println(indents + "    └── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    public static ArgumentListSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new ArgumentListSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static ArgumentListSyntaxASTBuilder builder() {
        return new ArgumentListSyntaxASTBuilder();
    }
    
    
    public static class ArgumentListSyntaxASTBuilder
    {
        
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        @Nullable
        private List<ArgumentSyntaxAST> arguments;
        
        
        private AbstractToken startToken, endToken;
        
        
        public ArgumentListSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public ArgumentListSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public ArgumentListSyntaxASTBuilder parameters(final List<ArgumentSyntaxAST> arguments) {
            this.arguments = arguments;
            return this;
        }
        
        
        public ArgumentListSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public ArgumentListSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public ArgumentListSyntaxAST build() {
            final ArgumentListSyntaxAST argumentListSyntaxAST = new ArgumentListSyntaxAST(this.syntaxAnalyzer);
            if (this.arguments != null)
                argumentListSyntaxAST.setArguments(this.arguments);
            argumentListSyntaxAST.setStartToken(this.startToken);
            argumentListSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(argumentListSyntaxAST.getStartToken());
            argumentListSyntaxAST.setEndToken(this.endToken);
            argumentListSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(argumentListSyntaxAST.getEndToken());
            return argumentListSyntaxAST;
        }
        
    }
    
}
