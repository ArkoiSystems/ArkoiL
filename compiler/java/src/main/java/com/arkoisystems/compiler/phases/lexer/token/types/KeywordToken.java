/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 25, 2020
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
package com.arkoisystems.compiler.phases.lexer.token.types;

import com.arkoisystems.compiler.phases.lexer.Lexer;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.KeywordType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.regex.Matcher;

@Getter
public class KeywordToken extends LexerToken
{
    
    @NotNull
    private final KeywordType keywordType;
    
    @Builder
    public KeywordToken(
            @NonNull
            @NotNull final Lexer lexer,
            @Nullable final Matcher matcher,
            @Nullable KeywordType keywordType,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.KEYWORD, matcher, startLine, endLine, charStart, charEnd);
        
        if (keywordType == null) {
            keywordType = Arrays.stream(KeywordType.values())
                    .filter(type -> type.getName().equals(this.getTokenContent()))
                    .findFirst()
                    .orElse(null);
            
            if (keywordType == null)
                throw new NullPointerException("keywordType must not be null. ");
        }
        
        this.keywordType = keywordType;
    }
    
    public KeywordToken(
            @NotNull final Lexer lexer,
            @Nullable final Matcher matcher,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        this(lexer, matcher, null, startLine, endLine, charStart, charEnd);
    }
    
}
