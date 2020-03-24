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
import java.util.Optional;

public class AnnotationSyntaxAST extends AbstractSyntaxAST
{
    
    public static AnnotationParser ANNOTATION_PARSER = new AnnotationParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<AnnotationSyntaxAST> annotationStorage;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private IdentifierToken annotationName = IdentifierToken
            .builder()
            .content("Undefined identifier for \"annotationName\"")
            .crash()
            .build();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private List<IdentifierToken> annotationArguments;
    
    
    protected AnnotationSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.ANNOTATION);
    }
    
    
    @Override
    public Optional<? extends AbstractSyntaxAST> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (!(parentAST instanceof RootSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_PARENT
            );
            return Optional.empty();
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.AT_SIGN) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_WRONG_START
            );
            return Optional.empty();
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_NAME
            );
            return Optional.empty();
        }
        this.annotationName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_BRACKET) != null) {
            this.getSyntaxAnalyzer().nextToken(2);
            
            while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                    break;
                
                if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            SyntaxErrorType.ANNOTATION_NO_COMMA_SEPARATION
                    );
                    return Optional.empty();
                } else {
                    this.annotationArguments.add((IdentifierToken) this.getSyntaxAnalyzer().currentToken());
                    this.getSyntaxAnalyzer().nextToken();
                }
                
                if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.COMMA) != null)
                    this.getSyntaxAnalyzer().nextToken();
                else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) != null)
                    break;
                else {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            SyntaxErrorType.ANNOTATION_UNSUPPORTED_TOKEN_INSIDE
                    );
                    return Optional.empty();
                }
            }
            
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACKET) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.ANNOTATION_WRONG_ENDING
                );
                return Optional.empty();
            }
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        this.getSyntaxAnalyzer().nextToken();
        
        if (ANNOTATION_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.annotationStorage.add(this);
            return AnnotationSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .annotations(this.annotationStorage)
                    .build()
                    .parseAST(parentAST);
        }
        
        if (!AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_PARSEABLE_STATEMENT
            );
            return Optional.empty();
        }
        
        if (!this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun") && !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("var")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.ANNOTATION_NO_VARIABLE_OR_FUNCTION
            );
            return Optional.empty();
        } else if (this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("fun")) {
            return new FunctionDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage()).parseAST(parentAST);
        } else {
            return new VariableDefinitionSyntaxAST(this.getSyntaxAnalyzer(), this.getAnnotationStorage()).parseAST(parentAST);
        }
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── name: " + this.getAnnotationName().getTokenContent());
        printStream.println(indents + "└── arguments: " + (this.getAnnotationArguments().isEmpty() ? "N/A" : ""));
        for (final IdentifierToken identifierToken : this.getAnnotationArguments())
            printStream.println(indents + "    └── " + identifierToken.getTokenContent());
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
        private List<IdentifierToken> annotationArguments;
        
        
        @Nullable
        private IdentifierToken annotationName;
        
        
        private int start, end;
        
        
        public AnnotationSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
            
            this.annotationArguments = new ArrayList<>();
            this.annotationStorage = new ArrayList<>();
        }
        
        
        public AnnotationSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public AnnotationSyntaxASTBuilder annotations(final @NotNull List<AnnotationSyntaxAST> annotationStorage) {
            this.annotationStorage = annotationStorage;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder arguments(final @NotNull List<IdentifierToken> annotationArguments) {
            this.annotationArguments = annotationArguments;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder name(final @NotNull IdentifierToken annotationName) {
            this.annotationName = annotationName;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public AnnotationSyntaxASTBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public AnnotationSyntaxAST build() {
            final AnnotationSyntaxAST annotationSyntaxAST = new AnnotationSyntaxAST(this.syntaxAnalyzer);
            if (this.annotationArguments != null)
                annotationSyntaxAST.setAnnotationArguments(this.annotationArguments);
            if (this.annotationStorage != null)
                annotationSyntaxAST.setAnnotationStorage(this.annotationStorage);
            if (this.annotationName != null)
                annotationSyntaxAST.setAnnotationName(this.annotationName);
            annotationSyntaxAST.setStart(this.start);
            annotationSyntaxAST.setEnd(this.end);
            return annotationSyntaxAST;
        }
        
    }
    
}
