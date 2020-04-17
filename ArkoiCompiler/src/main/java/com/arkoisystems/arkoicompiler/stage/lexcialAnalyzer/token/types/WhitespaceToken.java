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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class WhitespaceToken extends ArkoiToken
{
    
    protected WhitespaceToken(@Nullable final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.WHITESPACE);
    }
    
    
    @Override
    public @NotNull WhitespaceToken parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer(), "lexicalAnalyzer must not be null.");
        
        this.setStart(this.getLexicalAnalyzer().getPosition());
        this.setEnd(this.getLexicalAnalyzer().getPosition() + 1);
        this.setTokenContent(new String(Arrays.copyOfRange(this.getLexicalAnalyzer().getCompilerClass().getContent(), this.getStart(), this.getEnd())).intern());
        this.getLexicalAnalyzer().next();
        return this;
    }
    
    
    public static WhitespaceTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new WhitespaceTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static WhitespaceTokenBuilder builder() {
        return new WhitespaceTokenBuilder();
    }
    
    
    public static class WhitespaceTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public WhitespaceTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public WhitespaceTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public WhitespaceTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public WhitespaceTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public WhitespaceTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public WhitespaceToken build() {
            final WhitespaceToken whitespaceToken = new WhitespaceToken(this.lexicalAnalyzer);
            if (this.tokenContent != null)
                whitespaceToken.setTokenContent(this.tokenContent);
            whitespaceToken.setStart(this.start);
            whitespaceToken.setEnd(this.end);
            return whitespaceToken;
        }
        
    }
    
}