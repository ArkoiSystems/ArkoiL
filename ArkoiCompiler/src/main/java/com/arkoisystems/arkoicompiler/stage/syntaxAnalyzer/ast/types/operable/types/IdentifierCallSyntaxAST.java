/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdentifierCallSyntaxAST extends OperableSyntaxAST
{
    
    @Getter
    @Setter
    private boolean isFileLocal;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private IdentifierToken calledIdentifier;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private FunctionCallPartSyntaxAST calledFunctionPart;
    
    
    @Getter
    @Setter
    @Nullable
    private IdentifierCallSyntaxAST nextIdentifierCall;
    
    
    protected IdentifierCallSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.IDENTIFIER_CALL);
    }
    
    
    @NotNull
    @Override
    public IdentifierCallSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        this.setStartToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().mark(this.getStartToken());
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(KeywordType.THIS) != null) {
            this.isFileLocal = true;
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) == null) {
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IDENTIFIER_THIS_NO_DOT
                );
            } else this.getSyntaxAnalyzer().nextToken();
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        SyntaxErrorType.IDENTIFIER_CALL_NO_IDENTIFIER
                );
            } else this.getSyntaxAnalyzer().nextToken();
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null) {
            return this.addError(
                    this,
                    this.getSyntaxAnalyzer().getCompilerClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.IDENTIFIER_CALL_NO_IDENTIFIER
            );
        }
        
        this.calledIdentifier = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.OPENING_PARENTHESIS) != null) {
            this.getSyntaxAnalyzer().nextToken();
    
            final FunctionCallPartSyntaxAST functionCallPartSyntaxAST = new FunctionCallPartSyntaxAST(this.getSyntaxAnalyzer()).parseAST(this);
            this.getMarkerFactory().addFactory(functionCallPartSyntaxAST.getMarkerFactory());
    
            if (functionCallPartSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else this.calledFunctionPart = functionCallPartSyntaxAST;
        }
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERIOD) != null) {
            final AbstractToken periodToken = this.getSyntaxAnalyzer().nextToken();
    
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER) == null) {
                return this.addError(
                        this,
                        this.getSyntaxAnalyzer().getCompilerClass(),
                        periodToken.getStart(),
                        this.getSyntaxAnalyzer().currentToken().getEnd(),
                        SyntaxErrorType.IDENTIFIER_CALL_WRONG_CALL_APPEND
                );
            } else this.getSyntaxAnalyzer().nextToken();
    
            final IdentifierCallSyntaxAST identifierCallSyntaxAST = new IdentifierCallSyntaxAST(this.getSyntaxAnalyzer())
                    .parseAST(this);
            this.getMarkerFactory().addFactory(identifierCallSyntaxAST.getMarkerFactory());
    
            if (identifierCallSyntaxAST.isFailed()) {
                this.failed();
                return this;
            } else this.nextIdentifierCall = identifierCallSyntaxAST;
        }
        
        this.setEndToken(this.getSyntaxAnalyzer().currentToken());
        this.getMarkerFactory().done(this.getEndToken());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getStart());
        Objects.requireNonNull(this.getMarkerFactory().getCurrentMarker().getEnd());
        Objects.requireNonNull(this.getCalledIdentifier());
    
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
        printStream.println(indents + "├── fileLocal: " + this.isFileLocal());
        printStream.println(indents + "└── identifier: " + this.getCalledIdentifier().getTokenContent());
    }
    
    
    public static IdentifierCallOperableSyntaxASTBuilder builder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return new IdentifierCallOperableSyntaxASTBuilder(syntaxAnalyzer);
    }
    
    
    public static IdentifierCallOperableSyntaxASTBuilder builder() {
        return new IdentifierCallOperableSyntaxASTBuilder();
    }
    
    
    public static class IdentifierCallOperableSyntaxASTBuilder
    {
        
        @Nullable
        private final SyntaxAnalyzer syntaxAnalyzer;
        
        
        private boolean isFileLocal;
        
        
        @Nullable
        private IdentifierToken calledIdentifier;
        
        
        @Nullable
        private FunctionCallPartSyntaxAST calledFunctionPart;
    
    
        @Nullable
        private IdentifierCallSyntaxAST nextIdentifierCall;
        
        
        private AbstractToken startToken, endToken;
        
        
        public IdentifierCallOperableSyntaxASTBuilder(@NotNull final SyntaxAnalyzer syntaxAnalyzer) {
            this.syntaxAnalyzer = syntaxAnalyzer;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder() {
            this.syntaxAnalyzer = null;
        }
        
        
        public IdentifierCallOperableSyntaxASTBuilder fileLocal(final boolean isFileLocal) {
            this.isFileLocal = isFileLocal;
            return this;
        }
    
    
        public IdentifierCallOperableSyntaxASTBuilder called(final IdentifierToken calledIdentifier) {
            this.calledIdentifier = calledIdentifier;
            return this;
        }
    
    
        public IdentifierCallOperableSyntaxASTBuilder functionPart(final FunctionCallPartSyntaxAST calledFunctionPart) {
            this.calledFunctionPart = calledFunctionPart;
            return this;
        }
    
    
        public IdentifierCallOperableSyntaxASTBuilder nextCall(final IdentifierCallSyntaxAST nextIdentifierCall) {
            this.nextIdentifierCall = nextIdentifierCall;
            return this;
        }
    
    
        public IdentifierCallOperableSyntaxASTBuilder start(final AbstractToken startToken) {
            this.startToken = startToken;
            return this;
        }
    
    
        public IdentifierCallOperableSyntaxASTBuilder end(final AbstractToken endToken) {
            this.endToken = endToken;
            return this;
        }
    
    
        public IdentifierCallSyntaxAST build() {
            final IdentifierCallSyntaxAST identifierCallSyntaxAST = new IdentifierCallSyntaxAST(this.syntaxAnalyzer);
            identifierCallSyntaxAST.setFileLocal(this.isFileLocal);
            if (this.calledIdentifier != null)
                identifierCallSyntaxAST.setCalledIdentifier(this.calledIdentifier);
            if (this.calledFunctionPart != null)
                identifierCallSyntaxAST.setCalledFunctionPart(this.calledFunctionPart);
            if (this.nextIdentifierCall != null)
                identifierCallSyntaxAST.setNextIdentifierCall(this.nextIdentifierCall);
            identifierCallSyntaxAST.setStartToken(this.startToken);
            identifierCallSyntaxAST.getMarkerFactory().getCurrentMarker().setStart(identifierCallSyntaxAST.getStartToken());
            identifierCallSyntaxAST.setEndToken(this.endToken);
            identifierCallSyntaxAST.getMarkerFactory().getCurrentMarker().setEnd(identifierCallSyntaxAST.getEndToken());
            return identifierCallSyntaxAST;
        }
        
    }
    
}
