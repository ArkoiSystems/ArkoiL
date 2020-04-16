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
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class NumberToken extends AbstractToken
{
    
    protected NumberToken(@Nullable final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.NUMBER_LITERAL);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer());
    
        if (!Character.isDigit(this.getLexicalAnalyzer().currentChar()) && this.getLexicalAnalyzer().currentChar() != '.')
            return this.addError(
                    BadToken.builder(this.getLexicalAnalyzer())
                            .start(this.getLexicalAnalyzer().getPosition())
                            .end(this.getLexicalAnalyzer().getPosition() + 1)
                            .build()
                            .parseToken(),
                
                    this.getLexicalAnalyzer().getCompilerClass(),
                    this.getLexicalAnalyzer().getPosition(),
                    "Couldn't lex the number because it doesn't start with a digit or dot."
            );
    
        this.setStart(this.getLexicalAnalyzer().getPosition());
    
        if (this.getLexicalAnalyzer().currentChar() == '0' && this.getLexicalAnalyzer().peekChar(1) == 'x') {
            this.getLexicalAnalyzer().next(2);
            for (int i = 0; i < 8; i++) {
                final int currentChar = this.getLexicalAnalyzer().currentChar();
                switch (currentChar) {
                    case 'a':
                    case 'A':
                    case 'b':
                    case 'B':
                    case 'c':
                    case 'C':
                    case 'd':
                    case 'D':
                    case 'e':
                    case 'E':
                    case 'f':
                    case 'F':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        continue;
                    default:
                        break;
                }
            }
        } else {
            while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getCompilerClass().getContent().length) {
                final int currentChar = this.getLexicalAnalyzer().currentChar();
                if (!Character.isDigit(currentChar))
                    break;
                this.getLexicalAnalyzer().next();
            }
            
            if (this.getLexicalAnalyzer().currentChar() == '.') {
                this.getLexicalAnalyzer().next();
    
                while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getCompilerClass().getContent().length) {
                    final int currentChar = this.getLexicalAnalyzer().currentChar();
                    if (!Character.isDigit(currentChar))
                        break;
                    this.getLexicalAnalyzer().next();
                }
            }
        }
        this.setEnd(this.getLexicalAnalyzer().getPosition());
    
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getCompilerClass().getContent(), this.getStart(), this.getEnd())).intern());
        if (this.getTokenContent().equals(".")) {
            this.getLexicalAnalyzer().undo();
            return BadToken
                    .builder(this.getLexicalAnalyzer())
                    .start(this.getStart())
                    .end(this.getEnd())
                    .build()
                    .parseToken();
        }
        return Optional.of(this);
    }
    
    
    public static NumberTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new NumberTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static NumberTokenBuilder builder() {
        return new NumberTokenBuilder();
    }
    
    
    public static class NumberTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public NumberTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public NumberTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public NumberTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public NumberTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public NumberTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public NumberToken build() {
            final NumberToken numberToken = new NumberToken(this.lexicalAnalyzer);
            if (this.tokenContent != null)
                numberToken.setTokenContent(this.tokenContent);
            numberToken.setStart(this.start);
            numberToken.setEnd(this.end);
            return numberToken;
        }
        
    }
    
}