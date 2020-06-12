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
import com.arkoisystems.compiler.phases.lexer.token.enums.OperatorType;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.regex.Matcher;

@Getter
public class OperatorToken extends LexerToken
{
    
    @NotNull
    private final OperatorType operatorType;
    
    @Builder
    public OperatorToken(
            @NonNull
            @NotNull final Lexer lexer,
            @Nullable final Matcher matcher,
            @Nullable OperatorType operatorType,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.OPERATOR, matcher, startLine, endLine, charStart, charEnd);
        
        if (operatorType == null) {
            operatorType = Arrays.stream(OperatorType.values())
                    .filter(type -> type.getName().equals(this.getTokenContent()))
                    .findFirst()
                    .orElse(null);
            
            if (operatorType == null)
                throw new NullPointerException("operatorType must not be null. ");
        }
        
        this.operatorType = operatorType;
    }
    
    public OperatorToken(
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
