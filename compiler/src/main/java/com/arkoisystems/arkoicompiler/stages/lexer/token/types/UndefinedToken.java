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

import com.arkoisystems.arkoicompiler.error.ArkoiError;
import com.arkoisystems.arkoicompiler.error.ErrorPosition;
import com.arkoisystems.arkoicompiler.stages.lexer.Lexer;
import com.arkoisystems.arkoicompiler.stages.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.TokenType;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class UndefinedToken extends LexerToken
{
    
    @Builder
    public UndefinedToken(
            final @NotNull Lexer lexer,
            final boolean dummy,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.UNDEFINED, startLine, endLine, charStart, charEnd);
    
        if (!dummy) {
            this.getLexer().setFailed(true);
            this.getLexer().getCompilerClass().getCompiler().getErrorHandler().addError(ArkoiError.builder()
                    .compilerClass(this.getLexer().getCompilerClass())
                    .positions(Collections.singletonList(ErrorPosition.builder()
                            .lineRange(this.getLineRange())
                            .charStart(charStart)
                            .charEnd(charEnd)
                            .build()))
                    .message("This character is unknown to the lexer: %s")
                    .arguments(new Object[] { this.getTokenContent() })
                    .build()
            );
        }
    }
    
    public UndefinedToken(
            final @NotNull Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        this(lexer, false, startLine, endLine, charStart, charEnd);
    }
    
}
