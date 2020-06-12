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

import com.arkoisystems.compiler.error.CompilerError;
import com.arkoisystems.compiler.error.ErrorPosition;
import com.arkoisystems.compiler.phases.lexer.Lexer;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import lombok.Builder;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class UndefinedToken extends LexerToken
{
    
    @Builder
    public UndefinedToken(
            @NonNull
            @NotNull final Lexer lexer,
            @Nullable final Matcher matcher,
            final boolean dummy,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.UNDEFINED, matcher, startLine, endLine, charStart, charEnd);
    
        if (!dummy) {
            this.getLexer().setFailed(true);
            this.getLexer().getCompilerClass().getCompiler().getErrorHandler().addError(CompilerError.builder()
                    .causePosition(ErrorPosition.builder()
                            .sourceCode(this.getLexer().getCompilerClass().getContent())
                            .filePath(this.getLexer().getCompilerClass().getFilePath())
                            .lineRange(this.getLineRange())
                            .charStart(charStart)
                            .charEnd(charEnd)
                            .build())
                    .causeMessage("This character is unknown to the lexer")
                    .build()
            );
        }
    }
    
    public UndefinedToken(
            @NotNull final Lexer lexer,
            @Nullable final Matcher matcher,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        this(lexer, matcher, false, startLine, endLine, charStart, charEnd);
    }
    
}
