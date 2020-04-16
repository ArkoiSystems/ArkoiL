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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
    private AbstractToken[] tokens = new AbstractToken[0];
    
    
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
        
        final List<AbstractToken> tokens = new ArrayList<>();
        while (this.position < this.getCompilerClass().getContent().length) {
            final char currentChar = this.currentChar();
            if (Character.isWhitespace(currentChar)) {
                WhitespaceToken.builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse(tokens::add, this.errorRoutine);
            } else if (currentChar == '#') {
                CommentToken.builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                            if (abstractToken instanceof BadToken)
                                errorRoutine.run();
                            tokens.add(abstractToken);
                        }, this.errorRoutine);
            } else if (currentChar == '"') {
                StringToken.builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                            if (abstractToken instanceof BadToken)
                                errorRoutine.run();
                            tokens.add(abstractToken);
                        }, this.errorRoutine);
            } else if (Character.isDigit(currentChar) || currentChar == '.') {
                final Optional<? extends AbstractToken> numberToken = NumberToken.builder(this)
                        .build()
                        .parseToken();
                if (numberToken.isPresent() && !(numberToken.get() instanceof BadToken))
                    tokens.add(numberToken.get());
                else {
                    final Optional<? extends AbstractToken> symbolToken = SymbolToken.builder(this)
                            .build()
                            .parseToken();
                    if (symbolToken.isEmpty() || symbolToken.get() instanceof BadToken)
                        this.errorRoutine.run();
                    symbolToken.ifPresent(tokens::add);
                }
            } else if (Character.isJavaIdentifierStart(currentChar)) {
                IdentifierToken.builder(this)
                        .build()
                        .parseToken()
                        .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                            if (abstractToken instanceof BadToken)
                                errorRoutine.run();
                            tokens.add(abstractToken);
                        }, this.errorRoutine);
            } else {
                switch (currentChar) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '%':
                    case '!':
                    case '=': {
                        OperatorToken.builder(this)
                                .build()
                                .parseToken()
                                .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                                    if (abstractToken instanceof BadToken)
                                        errorRoutine.run();
                                    tokens.add(abstractToken);
                                }, this.errorRoutine);
                        continue;
                    }
                    
                    case '@':
                    case '^':
                    case '|':
                    case ':':
                        //                    case ';':
                    case '{':
                    case '}':
                    case '(':
                    case ')':
                    case '[':
                    case ']':
                    case ',':
                    case '<':
                    case '>': {
                        SymbolToken.builder(this)
                                .build()
                                .parseToken()
                                .ifPresentOrElse((Consumer<AbstractToken>) abstractToken -> {
                                    if (abstractToken instanceof BadToken)
                                        errorRoutine.run();
                                    tokens.add(abstractToken);
                                }, this.errorRoutine);
                        continue;
                    }
                    
                    default:
                        BadToken.builder(this)
                                .start(this.getPosition())
                                .end(this.getPosition() + 1)
                                .build()
                                .parseToken()
                                .ifPresent(tokens::add);
                        
                        this.getErrorHandler().addError(ArkoiError.builder()
                                .compilerClass(this.getCompilerClass())
                                .positions(new int[][] { { this.position, this.position + 1 } })
                                .message("The defined character is unknown for the lexical analyzer:")
                                .build()
                        );
                        this.failed();
                        this.next();
                        break;
                }
            }
        }
        
        this.tokens = tokens.toArray(new AbstractToken[] { });
        return !this.isFailed();
    }
    
    
    @Override
    public void reset() {
        this.errorHandler = new LexicalErrorHandler();
        this.tokens = new AbstractToken[0];
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
        for (final AbstractToken abstractToken : this.getTokens()) {
            if (!whitespaces && abstractToken.getTokenType() == TokenType.WHITESPACE)
                continue;
            tokenTypes.add(abstractToken.getTokenType());
        }
        return tokenTypes.toArray(new TokenType[] { });
    }
    
    
    public void next(final int positions) {
        this.position += positions;
        
        if (this.position >= this.getCompilerClass().getContent().length)
            this.position = this.getCompilerClass().getContent().length;
    }
    
    
    public void next() {
        this.position++;
        
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
        this.position--;
        if (this.position < 0)
            this.position = 0;
    }
    
}
