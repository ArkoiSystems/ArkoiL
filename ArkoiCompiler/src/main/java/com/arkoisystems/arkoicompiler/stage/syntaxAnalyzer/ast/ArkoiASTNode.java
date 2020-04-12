/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class ArkoiASTNode implements IASTNode
{
    
    @Getter
    @NotNull
    private final MarkerFactory<? extends ArkoiASTNode, AbstractToken, AbstractToken> markerFactory;
    
    
    @Getter
    @Nullable
    private final SyntaxAnalyzer syntaxAnalyzer;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @NotNull
    private final ASTType astType;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Nullable
    private AbstractToken startToken, endToken;
    
    
    @Getter
    private boolean failed;
    
    
    public ArkoiASTNode(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        this.syntaxAnalyzer = syntaxAnalyzer;
        this.astType = astType;
        
        this.markerFactory = new MarkerFactory<>(new ArkoiMarker<>(astType), this);
    }
    
    
    @NotNull
    @Override
    public abstract IASTNode parseAST(@NotNull final IASTNode parentAST);
    
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode[] astNodes, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getMarkerFactory());
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Arrays.stream(astNodes)
                        .map(astNode -> new int[] {
                                Objects.requireNonNull(astNode.getStartToken()).getStart(),
                                Objects.requireNonNull(astNode.getEndToken()).getEnd()
                        })
                        .toArray(size -> new int[size][1])
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.getMarkerFactory().error(this.getSyntaxAnalyzer().currentToken(), this.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getMarkerFactory());
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        Objects.requireNonNull(astNode.getStartToken()).getStart(),
                        Objects.requireNonNull(astNode.getEndToken()).getEnd()
                } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.getMarkerFactory().error(this.getSyntaxAnalyzer().currentToken(), this.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, final int start, final int end, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getMarkerFactory());
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { { start, end } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.getMarkerFactory().error(this.getSyntaxAnalyzer().currentToken(), this.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final AbstractToken abstractToken, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        Objects.requireNonNull(this.getMarkerFactory());
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { { abstractToken.getStart(), abstractToken.getEnd() } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.getMarkerFactory().error(this.getSyntaxAnalyzer().currentToken(), this.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    protected void skipToNextValidToken() {
        this.failed();
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        int openBraces = 0;
        while (this.getSyntaxAnalyzer().getPosition() < this.getSyntaxAnalyzer().getTokens().length) {
            if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_BRACE) != null)
                openBraces++;
            else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.CLOSING_BRACE) != null) {
                openBraces--;
                if (openBraces <= 0) {
                    this.getSyntaxAnalyzer().nextToken();
                    break;
                }
            }
            this.getSyntaxAnalyzer().nextToken();
        }
    }
    
    
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        ArkoiASTPrinter.builder()
                .printStream(printStream)
                .indents("")
                .build()
                .visit(this);
        return byteArrayOutputStream.toString();
    }
    
}
