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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeywordToken extends ArkoiToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private KeywordType keywordType;
    
    @Builder
    public KeywordToken(
            @Nullable final LexicalAnalyzer lexicalAnalyzer,
            @NotNull final String tokenContent,
            @Nullable final KeywordType keywordType,
            final int startLine,
            final int charStart,
            final int endLine,
            final int charEnd
    ) {
        super(lexicalAnalyzer, TokenType.KEYWORD, tokenContent, startLine, charStart, endLine, charEnd);
        
        this.setKeywordType(keywordType);
    }
    
    @Override
    public @Nullable KeywordToken parseToken() {
        switch (this.getTokenContent()) {
            case "this":
                this.setKeywordType(KeywordType.THIS);
                return this;
            case "var":
                this.setKeywordType(KeywordType.VAR);
                return this;
            case "return":
                this.setKeywordType(KeywordType.RETURN);
                return this;
            case "fun":
                this.setKeywordType(KeywordType.FUN);
                return this;
            case "as":
                this.setKeywordType(KeywordType.AS);
                return this;
            case "import":
                this.setKeywordType(KeywordType.IMPORT);
                return this;
            default:
                return null;
        }
    }
    
}