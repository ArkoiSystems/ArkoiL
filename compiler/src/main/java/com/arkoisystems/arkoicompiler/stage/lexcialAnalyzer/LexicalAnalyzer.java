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
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LexicalAnalyzer implements ICompilerStage
{
    
    @Getter
    @NonNull
    private final ICompilerClass compilerClass;
    
    @Getter
    @NotNull
    private LexicalErrorHandler errorHandler = new LexicalErrorHandler();
    
    @Getter
    @NotNull
    private ArkoiToken[] tokens = new ArkoiToken[0];
    
    @Getter
    @Setter
    private int lineIndex, charIndex;
    
    @Getter
    private boolean failed;
    
    @Getter
    @Setter
    private int position;
    
    @Getter
    @NotNull
    private final Runnable errorRoutine = () -> {
        this.failed();
        this.next();
    };
    
    public LexicalAnalyzer(@NotNull final ICompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.reset();
        final List<ArkoiToken> tokens = new ArrayList<>();
        while (this.position < this.getCompilerClass().getContent().length) {
            final char currentChar = this.currentChar();
            if (Character.isWhitespace(currentChar)) {
                final WhitespaceToken whitespaceToken = WhitespaceToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                tokens.add(whitespaceToken);
    
                if (currentChar == '\n') {
                    this.charIndex = 0;
                    this.lineIndex++;
                }
            } else if (currentChar == '#') {
                final ArkoiToken commentToken = CommentToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (commentToken instanceof BadToken)
                    this.errorRoutine.run();
                tokens.add(commentToken);
            } else if (currentChar == '"') {
                final ArkoiToken stringToken = StringToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (stringToken instanceof BadToken)
                    this.errorRoutine.run();
                tokens.add(stringToken);
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
                final ArkoiToken numberToken = NumberToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (!(numberToken instanceof BadToken)) {
                    tokens.add(numberToken);
                    continue;
                }
    
                final ArkoiToken symbolToken = SymbolToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (symbolToken instanceof BadToken)
                    this.errorRoutine.run();
                tokens.add(symbolToken);
            } else if (Character.isJavaIdentifierStart(currentChar)) {
                final ArkoiToken identifierToken = IdentifierToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (identifierToken instanceof BadToken)
                    this.errorRoutine.run();
                tokens.add(identifierToken);
            } else if (this.isOperatorChar(currentChar)) {
                final ArkoiToken operatorToken = OperatorToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (operatorToken instanceof BadToken)
                    this.errorRoutine.run();
                tokens.add(operatorToken);
            } else if (this.isSymbolChar(currentChar)) {
                final ArkoiToken symbolToken = SymbolToken.builder()
                        .lexicalAnalyzer(this)
                        .build()
                        .parseToken();
                if (symbolToken instanceof BadToken)
                    this.errorRoutine.run();
                tokens.add(symbolToken);
            } else {
                tokens.add(BadToken.builder()
                        .lexicalAnalyzer(this)
                        .startLine(this.getLineIndex())
                        .charStart(this.getCharIndex())
                        .endLine(this.getLineIndex())
                        .charEnd(this.getCharIndex() + 1)
                        .build()
                        .parseToken()
                );
    
                this.getErrorHandler().addError(ArkoiError.builder()
                        .compilerClass(this.getCompilerClass())
                        .positions(Collections.singletonList(ArkoiError.ErrorPosition.builder()
                                .lineRange(ArkoiError.ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex))
                                .charStart(this.getCharIndex())
                                .charEnd(this.getCharIndex() + 1)
                                .build()
                        ))
                        .message("The defined character is unknown for the lexical analyzer:")
                        .build()
                );
                this.failed();
                this.next();
            }
        }
    
        this.tokens = tokens.toArray(new ArkoiToken[] { });
        return !this.isFailed();
    }
    
    @Override
    public void reset() {
        this.errorHandler = new LexicalErrorHandler();
        this.tokens = new ArkoiToken[0];
        this.failed = false;
        this.position = 0;
    }
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    @NotNull
    public TokenType[] getTokenTypes(final boolean whitespaces) {
        final List<TokenType> tokenTypes = new ArrayList<>();
        for (final ArkoiToken arkoiToken : this.getTokens()) {
            if (!whitespaces && arkoiToken.getTokenType() == TokenType.WHITESPACE)
                continue;
            tokenTypes.add(arkoiToken.getTokenType());
        }
        return tokenTypes.toArray(new TokenType[] { });
    }
    
    private boolean isOperatorChar(final char currentChar) {
        return currentChar == '+' ||
                currentChar == '-' ||
                currentChar == '*' ||
                currentChar == '/' ||
                currentChar == '%' ||
                currentChar == '!' ||
                currentChar == '=';
    }
    
    private boolean isSymbolChar(final char currentChar) {
        return currentChar == '@' ||
                currentChar == '^' ||
                currentChar == '|' ||
                currentChar == ':' ||
                currentChar == '{' ||
                currentChar == '}' ||
                currentChar == '(' ||
                currentChar == ')' ||
                currentChar == '[' ||
                currentChar == ']' ||
                currentChar == ',' ||
                currentChar == '<' ||
                currentChar == '>';
    }
    
    public void next(final int positions) {
        this.charIndex += positions;
        this.position += positions;
    
        if (this.position >= this.getCompilerClass().getContent().length)
            this.charIndex = this.getCompilerClass().getContent().length;
        if (this.position >= this.getCompilerClass().getContent().length)
            this.position = this.getCompilerClass().getContent().length;
    }
    
    public void next() {
        this.charIndex++;
        this.position++;
    
        if (this.position >= this.getCompilerClass().getContent().length)
            this.charIndex = this.getCompilerClass().getContent().length;
        if (this.position >= this.getCompilerClass().getContent().length)
            this.position = this.getCompilerClass().getContent().length;
    }
    
    public char peekChar(final int offset) {
        if (this.position + offset >= this.getCompilerClass().getContent().length)
            return this.getCompilerClass().getContent()[this.getCompilerClass().getContent().length - 1];
        return this.getCompilerClass().getContent()[this.position + offset];
    }
    
    public char currentChar() {
        if (this.position >= this.getCompilerClass().getContent().length)
            return this.getCompilerClass().getContent()[this.getCompilerClass().getContent().length - 1];
        return this.getCompilerClass().getContent()[this.position];
    }
    
    public void undo() {
        this.charIndex--;
        this.position--;
    
        if (this.charIndex < 0)
            this.charIndex = 0;
        if (this.position < 0)
            this.position = 0;
    }
    
}
