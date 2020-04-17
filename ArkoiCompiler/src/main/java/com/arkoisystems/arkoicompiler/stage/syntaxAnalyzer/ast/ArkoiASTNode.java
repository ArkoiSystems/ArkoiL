/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
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
    private final MarkerFactory<? extends ArkoiASTNode, ArkoiToken, ArkoiToken> markerFactory;
    
    
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
    private ArkoiToken startToken, endToken;
    
    
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
    
    
    @NotNull
    public abstract TypeKind getTypeKind();
    
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode[] astNodes, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getMarkerFactory(), "markerFactory must not be null.");
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Arrays.stream(astNodes)
                        .map(astNode -> new int[] {
                                Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getStart(),
                                Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getEnd()
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
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getMarkerFactory(), "markerFactory must not be null.");
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getStart(),
                        Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getEnd()
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
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getMarkerFactory(), "markerFactory must not be null.");
        
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
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final ArkoiToken arkoiToken, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        Objects.requireNonNull(this.getMarkerFactory(), "markerFactory must not be null.");
        
        this.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { { arkoiToken.getStart(), arkoiToken.getEnd() } })
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
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
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
