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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TypeKeywordToken extends AbstractToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKind typeKind;
    
    
    protected TypeKeywordToken(@Nullable final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.TYPE_KEYWORD);
    }
    
    
    @NotNull
    @Override
    public Optional<? extends AbstractToken> parseToken() {
        switch (this.getTokenContent()) {
            case "char":
                this.setTypeKind(TypeKind.CHAR);
                return Optional.of(this);
            case "boolean":
                this.setTypeKind(TypeKind.BOOLEAN);
                return Optional.of(this);
            case "byte":
                this.setTypeKind(TypeKind.BYTE);
                return Optional.of(this);
            case "int":
                this.setTypeKind(TypeKind.INTEGER);
                return Optional.of(this);
            case "long":
                this.setTypeKind(TypeKind.LONG);
                return Optional.of(this);
            case "short":
                this.setTypeKind(TypeKind.SHORT);
                return Optional.of(this);
            case "string":
                this.setTypeKind(TypeKind.STRING);
                return Optional.of(this);
            default:
                return Optional.empty();
        }
    }
    
    
    public static TypeKeywordTokenBuilder builder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
        return new TypeKeywordTokenBuilder(lexicalAnalyzer);
    }
    
    
    public static TypeKeywordTokenBuilder builder() {
        return new TypeKeywordTokenBuilder();
    }
    
    
    public static class TypeKeywordTokenBuilder
    {
        
        @Nullable
        private final LexicalAnalyzer lexicalAnalyzer;
        
        
        @Nullable
        private TypeKind typeKind;
        
        
        @Nullable
        private String tokenContent;
        
        
        private int start, end;
        
        
        public TypeKeywordTokenBuilder(@NotNull final LexicalAnalyzer lexicalAnalyzer) {
            this.lexicalAnalyzer = lexicalAnalyzer;
        }
        
        
        public TypeKeywordTokenBuilder() {
            this.lexicalAnalyzer = null;
        }
        
        
        public TypeKeywordTokenBuilder content(final String tokenContent) {
            this.tokenContent = tokenContent;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder type(final TypeKind typeKind) {
            this.typeKind = typeKind;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder start(final int start) {
            this.start = start;
            return this;
        }
        
        
        public TypeKeywordTokenBuilder end(final int end) {
            this.end = end;
            return this;
        }
        
        
        public TypeKeywordToken build() {
            final TypeKeywordToken keywordToken = new TypeKeywordToken(this.lexicalAnalyzer);
            if (this.tokenContent != null)
                keywordToken.setTokenContent(this.tokenContent);
            if (this.typeKind != null)
                keywordToken.setTypeKind(this.typeKind);
            keywordToken.setStart(this.start);
            keywordToken.setEnd(this.end);
            return keywordToken;
        }
        
    }
    
}