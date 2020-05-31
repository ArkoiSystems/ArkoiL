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
import com.arkoisystems.arkoicompiler.stages.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stages.lexer.token.enums.TokenType;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class StringToken extends ArkoiToken
{
    
    @Builder
    public StringToken(
            final @NotNull Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.STRING, startLine, endLine, charStart, charEnd);
    
        if(this.getTokenContent().length() < 2 || this.getTokenContent().endsWith("\\\"") || !this.getTokenContent().endsWith("\"")) {
            this.getLexer().setFailed(true);
            this.getLexer().getErrorHandler().addError(ArkoiError.builder()
                    .compilerClass(this.getLexer().getCompilerClass())
                    .positions(Collections.singletonList(ErrorPosition.builder()
                            .lineRange(this.getLineRange())
                            .charStart(this.getCharStart())
                            .charEnd(this.getCharEnd())
                            .build()))
                    .message("A string must be terminated correctly.")
                    .build());
            return;
        }
        
        this.setTokenContent(this.getTokenContent().substring(1, this.getTokenContent().length() - 1));
    }
    
}
