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
package com.arkoisystems.arkoicompiler.stages.lexer.token.types;

import com.arkoisystems.arkoicompiler.stages.lexer.Lexer;
import com.arkoisystems.arkoicompiler.stages.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.TokenType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class KeywordToken extends ArkoiToken
{
    
    @NotNull
    private final KeywordType keywordType;
    
    @Builder
    public KeywordToken(
            final @NotNull Lexer lexer,
            final @Nullable KeywordType keywordType,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.KEYWORD, startLine, endLine, charStart, charEnd);
    
        if (keywordType == null) {
            for (final KeywordType type : KeywordType.values())
                if (type.getName().equals(this.getTokenContent())) {
                    this.keywordType = type;
                    return;
                }
        
            throw new NullPointerException("keywordType must not be null.");
        } else this.keywordType = keywordType;
    }
    
    public KeywordToken(
            final @NotNull Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        this(lexer, null, startLine, endLine, charStart, charEnd);
    }
    
}
