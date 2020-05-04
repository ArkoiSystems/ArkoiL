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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TypeKeywordToken extends ArkoiToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private TypeKind typeKind;
    
    
    @Builder
    public TypeKeywordToken(
            @Nullable final LexicalAnalyzer lexicalAnalyzer,
            @Nullable final String tokenContent,
            @Nullable final TypeKind typeKind,
            final int startLine,
            final int charStart,
            final int endLine,
            final int charEnd
    ) {
        super(lexicalAnalyzer, TokenType.TYPE_KEYWORD, tokenContent, startLine, charStart, endLine, charEnd);
        
        this.setTypeKind(typeKind);
    }
    
    
    @Override
    public @Nullable TypeKeywordToken parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer(), "lexicalAnalyzer must not be null.");
        
        this.setTokenContent(Objects.requireNonNull(this.getLineRange().getSourceCode(), "lineRange.sourceCode must not be null.").substring(
                this.getCharStart(),
                this.getCharEnd()
        ));
        
        switch (this.getTokenContent()) {
            case "char":
                this.setTypeKind(TypeKind.CHAR);
                return this;
            case "boolean":
                this.setTypeKind(TypeKind.BOOLEAN);
                return this;
            case "byte":
                this.setTypeKind(TypeKind.BYTE);
                return this;
            case "int":
                this.setTypeKind(TypeKind.INTEGER);
                return this;
            case "long":
                this.setTypeKind(TypeKind.LONG);
                return this;
            case "short":
                this.setTypeKind(TypeKind.SHORT);
                return this;
            case "string":
                this.setTypeKind(TypeKind.STRING);
                return this;
            default:
                return null;
        }
    }
    
}