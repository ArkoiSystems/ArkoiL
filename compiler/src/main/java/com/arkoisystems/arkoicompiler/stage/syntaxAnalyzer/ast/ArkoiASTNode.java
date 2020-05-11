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
import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.ArkoiMarker;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker.MarkerFactory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArkoiASTNode implements IASTNode
{
    
    @Getter
    @NotNull
    private final MarkerFactory<IToken, IToken> markerFactory;
    
    @Getter
    @Nullable
    private final SyntaxAnalyzer syntaxAnalyzer;
    
    @EqualsAndHashCode.Include
    @Getter
    @NotNull
    private final ASTType astType;
    
    @Getter
    @Nullable
    private ArkoiError.ErrorPosition.LineRange lineRange;
    
    @EqualsAndHashCode.Include
    @Getter
    @Setter
    @Nullable
    private IToken startToken, endToken;
    
    @Getter
    private int startLine;
    
    @Getter
    private boolean failed;
    
    @Builder(builderMethodName = "nodeBuilder")
    public ArkoiASTNode(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @NotNull final ASTType astType,
            @Nullable final IToken startToken,
            @Nullable final IToken endToken
    ) {
        this.markerFactory = new MarkerFactory<>(new ArkoiMarker<>(astType));
        
        this.syntaxAnalyzer = syntaxAnalyzer;
        this.astType = astType;
        
        this.startToken = startToken;
        this.endToken = endToken;
        
        this.startAST(startToken);
        this.endAST(endToken);
    }
    
    public void startAST(@Nullable final IToken token) {
        if(token == null)
            return;
        
        this.setStartToken(token);
        this.getMarkerFactory().mark(this.getStartToken());
        this.startLine = token.getLineRange().getStartLine();
    }
    
    public void endAST(@Nullable final IToken token) {
        if(token == null)
            return;
        
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        this.setEndToken(token);
        this.getMarkerFactory().done(this.getEndToken());
        
        this.lineRange = ArkoiError.ErrorPosition.LineRange.make(
                this.getSyntaxAnalyzer().getCompilerClass(),
                this.startLine,
                token.getLineRange().getEndLine()
        );
    }
    
    @NotNull
    @Override
    public IASTNode parseAST(final IASTNode parentAST) {
        return this;
    }
    
    @NotNull
    public TypeKind getTypeKind() {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public void accept(final @NotNull IVisitor<?> visitor) {
    
    }
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, final @Nullable IASTNode[] astNodes, @NotNull final String message, @NotNull final Object... arguments) {
        compilerClass.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Arrays.stream(astNodes).map(astNode -> ArkoiError.ErrorPosition.builder()
                                .lineRange(Objects.requireNonNull(astNode, "astNode must not be null.").getLineRange())
                                .charStart(Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart())
                                .charEnd(Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getCharEnd())
                                .build()
                        ).collect(Collectors.toList())
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
    
        this.getMarkerFactory().error(compilerClass.getSyntaxAnalyzer().currentToken(), compilerClass.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, final @Nullable IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments) {
        final ArkoiError.ErrorPosition.LineRange lineRange;
        final int charStart, charEnd;
        if (astNode != null) {
            lineRange = astNode.getLineRange();
            charStart = Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart();
            charEnd = Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharEnd();
        } else {
            final String[] sourceSplit = new String(compilerClass.getContent()).split(System.getProperty("line.separator"));
            lineRange = ArkoiError.ErrorPosition.LineRange.make(compilerClass, sourceSplit.length - 1, sourceSplit.length - 1);
            charStart = sourceSplit[sourceSplit.length - 1].length() - 1;
            charEnd = sourceSplit[sourceSplit.length - 1].length();
        }
        
        compilerClass.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ArkoiError.ErrorPosition.builder()
                        .lineRange(lineRange)
                        .charStart(charStart)
                        .charEnd(charEnd)
                        .build())
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
    
        this.getMarkerFactory().error(compilerClass.getSyntaxAnalyzer().currentToken(), compilerClass.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    @Override
    public <E> E addError(@Nullable final E errorSource, @NotNull final ICompilerClass compilerClass, final @Nullable ArkoiToken arkoiToken, @NotNull final String message, @NotNull final Object... arguments) {
        final ArkoiError.ErrorPosition.LineRange lineRange;
        final int charStart, charEnd;
        if (arkoiToken != null) {
            lineRange = arkoiToken.getLineRange();
            charStart = arkoiToken.getCharStart();
            charEnd = arkoiToken.getCharEnd();
        } else {
            final String[] sourceSplit = new String(compilerClass.getContent()).split(System.getProperty("line.separator"));
            lineRange = ArkoiError.ErrorPosition.LineRange.make(compilerClass, sourceSplit.length - 1, sourceSplit.length - 1);
            charStart = sourceSplit[sourceSplit.length - 1].length() - 1;
            charEnd = sourceSplit[sourceSplit.length - 1].length();
        }
        
        compilerClass.getSyntaxAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ArkoiError.ErrorPosition.builder()
                        .lineRange(lineRange)
                        .charStart(charStart)
                        .charEnd(charEnd)
                        .build())
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
    
        this.getMarkerFactory().error(compilerClass.getSyntaxAnalyzer().currentToken(), compilerClass.getSyntaxAnalyzer().currentToken(), message, arguments);
        this.failed();
        return errorSource;
    }
    
    @Override
    public void failed() {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
    
        this.endAST(this.getSyntaxAnalyzer().currentToken());
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
