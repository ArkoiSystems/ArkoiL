/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.BadToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.AnnotationParser;
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

public class AnnotationSyntaxAST extends ArkoiSyntaxAST
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationSyntaxAST> annotationStorage = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierCallSyntaxAST annotationCall;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private ArgumentListSyntaxAST annotationArguments;
    
    
    protected AnnotationSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
    }
    
    
    @NotNull
    @Override
    public ICompilerSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST)) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_PARENT
            );
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.AT_SIGN) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_START
            );
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_NAME
            );
        } else this.getSyntaxAnalyzer().nextToken();
        
        final IdentifierCallSyntaxAST identifierCallSyntaxAST = IdentifierCallSyntaxAST
                .builder(this.getSyntaxAnalyzer())
                .build()
                .parseAST(this);
        this.getMarkerFactory().addFactory(identifierCallSyntaxAST.getMarkerFactory());
        
        if (identifierCallSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.annotationCall = identifierCallSyntaxAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken();
            
            final ArgumentListSyntaxAST arguments = ArgumentListSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(this);
            this.getMarkerFactory().addFactory(arguments.getMarkerFactory());
            
            if (arguments.isFailed()) {
                this.failed();
                return this;
            } else this.annotationArguments = arguments;
            
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
               return this.addError(
                       this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "6"
                );
            }
        } else {
            this.annotationArguments = ArgumentListSyntaxAST.builder()
                    .start(BadToken.builder()
                            .start(-1)
                            .end(-1)
                            .build())
                    .end(BadToken.builder()
                            .start(-1)
                            .end(-1)
                            .build())
                    .build();
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        
        this.getSyntaxAnalyzer().nextToken();
        this.getAnnotationStorage().add(this);
        
        if (ANNOTATION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            final ICompilerSyntaxAST annotationSyntaxAST = AnnotationSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .annotations(this.annotationStorage)
                    .build()
                    .parseAST(parentAST);
            this.getMarkerFactory().addFactory(annotationSyntaxAST.getMarkerFactory());
            
            if (annotationSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else return annotationSyntaxAST;
        }
        
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_PARSEABLE_STATEMENT
            );
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) != null && this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) != null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_VARIABLE_OR_FUNCTION
            );
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) != null) {
            final FunctionSyntaxAST functionSyntaxAST = FunctionSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .annotations(this.getAnnotationStorage())
                    .build()
                    .parseAST(parentAST);
            this.getMarkerFactory().addFactory(functionSyntaxAST.getMarkerFactory());
    
            if (functionSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else return functionSyntaxAST;
        } else {
            final VariableSyntaxAST variableSyntaxAST = VariableSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .annotations(this.getAnnotationStorage())
                    .build()
                    .parseAST(parentAST);
            this.getMarkerFactory().addFactory(variableSyntaxAST.getMarkerFactory());
    
            if (variableSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else return variableSyntaxAST;
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getAnnotationArguments());
    
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
        printStream.println(indents + "├── name: " + (this.getAnnotationName() != null ? this.getAnnotationName().getTokenContent() : null));
        printStream.println(indents + "└── arguments: " + (this.getAnnotationArguments().getArguments().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getAnnotationArguments().getArguments().size(); index++) {
            final ArkoiSyntaxAST arkoiSyntaxAST = this.getAnnotationArguments().getArguments().get(index);
            if (index == this.getAnnotationArguments().getArguments().size() - 1) {
                printStream.println(indents + "    └── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + arkoiSyntaxAST.getClass().getSimpleName());
                arkoiSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    @Nullable
    public IdentifierToken getAnnotationName() {
        if (this.getAnnotationCall() == null)
            return null;
    
        IdentifierCallSyntaxAST identifierCallSyntaxAST = this.getAnnotationCall();
        while (identifierCallSyntaxAST.getNextIdentifierCall() != null)
            identifierCallSyntaxAST = identifierCallSyntaxAST.getNextIdentifierCall();
        return identifierCallSyntaxAST.getCalledIdentifier();
    }
    
    
    public static AnnotationSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new AnnotationSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static AnnotationSyntaxASTBuilder builder() {
        return new AnnotationSyntaxASTBuilder();
    }
    
    
    public static class AnnotationSyntaxASTBuilder
    {
    
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
    
    
        @Nullable
        private List<AnnotationSyntaxAST> annotationStorage;
    
    
        @Nullable
        private ArgumentListSyntaxAST annotationArguments;
    
    
        @Nullable
        private IdentifierCallSyntaxAST annotationCall;
    
    
        private AbstractToken startToken, endToken;
    
    
        public AnnotationSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
    
    
        public AnnotationSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public AnnotationSyntaxASTBuilder annotations(final List<AnnotationSyntaxAST> annotationStorage) {
            this.annotationStorage = annotationStorage;
            return this;
        }
    
    
        public AnnotationSyntaxASTBuilder call(final IdentifierCallSyntaxAST annotationCall) {
            this.annotationCall = annotationCall;
            return this;
        }
    
    
        public AnnotationSyntaxASTBuilder arguments(final ArgumentListSyntaxAST annotationArguments) {
            this.annotationArguments = annotationArguments;
            return this;
        }
    
    
        public AnnotationSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
        
        
        public AnnotationSyntaxAST build() {
            final AnnotationSyntaxAST annotationSyntaxAST = new AnnotationSyntaxAST(this.syntaxAnalyzer);
            if (this.annotationStorage != null)
                annotationSyntaxAST.setAnnotationStorage(this.annotationStorage);
            if (this.annotationCall != null)
                annotationSyntaxAST.setAnnotationCall(this.annotationCall);
            if (this.annotationArguments != null)
                annotationSyntaxAST.setAnnotationArguments(this.annotationArguments);
            annotationSyntaxAST.setStartToken(this.startToken);
            annotationSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(annotationSyntaxAST.getStartToken());
            annotationSyntaxAST.setEndToken(this.endToken);
            annotationSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(annotationSyntaxAST.getEndToken());
            return annotationSyntaxAST;
        }
        
    }
    
}
