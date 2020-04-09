/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.api.ISyntaxParser;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RootSyntaxAST extends ArkoiSyntaxAST
{
    
    public static ISyntaxParser[] ROOT_PARSERS = new ISyntaxParser[] {
            AnnotationSyntaxAST.ANNOTATION_PARSER,
            AbstractStatementSyntaxAST.STATEMENT_PARSER,
    };
    
    
    @Getter
    @NotNull
    private final List<ImportSyntaxAST> importStorage = new ArrayList<>();
    
    
    @Getter
    @NotNull
    private final List<VariableSyntaxAST> variableStorage = new ArrayList<>();
    
    
    @Getter
    @NotNull
    private final List<FunctionSyntaxAST> functionStorage = new ArrayList<>();
    
    
    @NotNull
    private final List<ICompilerSyntaxAST> sortedStorage = new ArrayList<>();
    
    
    public RootSyntaxAST(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ROOT);
    }
    
    
    @NotNull
    @Override
    public RootSyntaxAST parseAST(@NotNull ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getMarkerFactory());
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken(false));
        this.getMarkerFactory().mark(this.getStartToken());
        
        main_loop:
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            for (final ISyntaxParser parser : ROOT_PARSERS) {
                if (!parser.canParse(this, this.getSyntaxAnalyzer()))
                    continue;
                
                final ICompilerSyntaxAST syntaxAST = parser.parse(this, this.getSyntaxAnalyzer());
                this.getMarkerFactory().addFactory(syntaxAST.getMarkerFactory());
                
                if (!syntaxAST.isFailed()) {
                    if (syntaxAST instanceof FunctionSyntaxAST) {
                        final FunctionSyntaxAST functionDefinitionAST = (FunctionSyntaxAST) syntaxAST;
                        this.functionStorage.add(functionDefinitionAST);
                    } else {
                        if (syntaxAST instanceof VariableSyntaxAST)
                            this.variableStorage.add((VariableSyntaxAST) syntaxAST);
                        else if (syntaxAST instanceof ImportSyntaxAST)
                            this.importStorage.add((ImportSyntaxAST) syntaxAST);
                    }
                    
                    this.sortedStorage.add(syntaxAST);
                    this.getSyntaxAnalyzer().nextToken();
                } else this.skipToNextValidToken();
                continue main_loop;
            }
            
            this.addError(
                    null,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ROOT_NO_PARSER_FOUND
            );
            this.skipToNextValidToken();
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
        printStream.println(indents + "├── imports: " + (this.getImportStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getImportStorage().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getImportStorage().get(index);
            if (index == this.getImportStorage().size() - 1) {
                printStream.println(indents + "│   └── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        
        printStream.println(indents + "│");
        printStream.println(indents + "├── variables: " + (this.getVariableStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getVariableStorage().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getVariableStorage().get(index);
            if (index == this.getVariableStorage().size() - 1) {
                printStream.println(indents + "│   └── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │   ");
            }
        }
        
        printStream.println(indents + "│");
        printStream.println(indents + "└── functions: " + (this.getFunctionStorage().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getFunctionStorage().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getFunctionStorage().get(index);
            if (index == this.getFunctionStorage().size() - 1) {
                printStream.println(indents + "    └── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    public Stream<ICompilerSyntaxAST> getSortedStorage() {
        return this.sortedStorage.stream()
                .sorted(Comparator.comparingInt(arkoiSyntaxAST -> Objects.requireNonNull(arkoiSyntaxAST.getStartToken()).getStart()));
    }
    
}
