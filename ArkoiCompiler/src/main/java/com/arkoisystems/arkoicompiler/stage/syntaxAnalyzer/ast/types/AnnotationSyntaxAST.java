/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.AnnotationParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnnotationSyntaxAST extends AbstractSyntaxAST
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationSyntaxAST> annotationStorage = new ArrayList<>();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierCallOperableSyntaxAST annotationCall;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<ArgumentSyntaxAST> annotationArguments = new ArrayList<>();
    
    
    protected AnnotationSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
    }
    
    
    @NotNull
    @Override
    public AbstractSyntaxAST parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_PARENT
            );
            return this;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.AT_SIGN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_START
            );
            return this;
        }
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_NAME
            );
            return this;
        } else this.getSyntaxAnalyzer().nextToken();
        
        final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = IdentifierCallOperableSyntaxAST
                .builder(this.getSyntaxAnalyzer())
                .build()
                .parseAST(this);
        this.getMarkerFactory().addFactory(identifierCallOperableSyntaxAST.getMarkerFactory());
        
        if (identifierCallOperableSyntaxAST.isFailed()) {
            this.failed();
            return this;
        } else this.annotationCall = identifierCallOperableSyntaxAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken();
            
            final List<ArgumentSyntaxAST> arguments = ArgumentSyntaxAST.parseArguments(this, this.getSyntaxAnalyzer());
            if (arguments == null) {
                this.failed();
                return this;
            } this.annotationArguments = arguments;
            
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "6"
                );
                return this;
            }
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        
        this.getSyntaxAnalyzer().nextToken();
        this.getAnnotationStorage().add(this);
        
        if (ANNOTATION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            final AbstractSyntaxAST annotationSyntaxAST = AnnotationSyntaxAST
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
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_PARSEABLE_STATEMENT
            );
            return this;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) != null && this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.VAR) != null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_VARIABLE_OR_FUNCTION
            );
            return this;
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.FUN) != null) {
            final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST = FunctionDefinitionSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .annotations(this.getAnnotationStorage())
                    .build()
                    .parseAST(parentAST);
            this.getMarkerFactory().addFactory(functionDefinitionSyntaxAST.getMarkerFactory());
            
            if (functionDefinitionSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else return functionDefinitionSyntaxAST;
        } else {
            final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST = new VariableDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage())
                    .parseAST(parentAST);
            this.getMarkerFactory().addFactory(variableDefinitionSyntaxAST.getMarkerFactory());
            
            if (variableDefinitionSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else return variableDefinitionSyntaxAST;
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + (this.getAnnotationName() != null ? this.getAnnotationName().getTokenContent() : null));
        printStream.println(indents + "└── arguments: " + (this.getAnnotationArguments().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getAnnotationArguments().size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = this.getAnnotationArguments().get(index);
            if (index == this.getAnnotationArguments().size() - 1) {
                printStream.println(indents + "    └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "        ");
            } else {
                printStream.println(indents + "    ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, indents + "    │   ");
                printStream.println(indents + "    │   ");
            }
        }
    }
    
    
    @Nullable
    public IdentifierToken getAnnotationName() {
        if (this.getAnnotationCall() == null)
            return null;
        
        IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = this.getAnnotationCall();
        while (identifierCallOperableSyntaxAST.getNextIdentifierCall() != null)
            identifierCallOperableSyntaxAST = identifierCallOperableSyntaxAST.getNextIdentifierCall();
        return identifierCallOperableSyntaxAST.getCalledIdentifier();
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
        private List<ArgumentSyntaxAST> annotationArguments;
    
    
        @Nullable
        private IdentifierCallOperableSyntaxAST annotationCall;
    
    
        private AbstractToken startToken, endToken;
    
    
        public AnnotationSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        
            this.annotationArguments = new ArrayList<>();
            this.annotationStorage = new ArrayList<>();
        }
    
    
        public AnnotationSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
    
    
        public AnnotationSyntaxASTBuilder annotations(final List<AnnotationSyntaxAST> annotationStorage) {
            this.annotationStorage = annotationStorage;
            return this;
        }
    
    
        public AnnotationSyntaxASTBuilder call(final IdentifierCallOperableSyntaxAST annotationCall) {
            this.annotationCall = annotationCall;
            return this;
        }
    
    
        public AnnotationSyntaxASTBuilder arguments(final List<ArgumentSyntaxAST> annotationArguments) {
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
            annotationSyntaxAST.setEndToken(this.endToken);
            return annotationSyntaxAST;
        }
        
    }
    
}
