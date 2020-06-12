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
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

@Getter
public class NumberToken extends LexerToken
{
    
    @NotNull
    private final DataKind dataKind;
    
    private final int bits;
    
    @Builder
    public NumberToken(
            @NonNull
            @NotNull final Lexer lexer,
            @Nullable final Matcher matcher,
            @Nullable final DataKind dataKind,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.NUMBER, matcher, startLine, endLine, charStart, charEnd);
        
        if (dataKind != null) {
            this.dataKind = dataKind;
            this.bits = 0;
        } else if (matcher != null && matcher.group("fp") != null) {
            this.dataKind = DataKind.FLOAT;
            this.bits = 0;
        } else if (matcher != null && matcher.group("hex") != null) {
            this.dataKind = DataKind.INTEGER;
            this.bits = 64;
            
            try {
                final long value = Long.parseLong(this.getTokenContent().substring(2), 16);
                this.setTokenContent(String.valueOf(value));
            } catch (final Exception ex) {
                this.getLexer().setFailed(true);
                this.getLexer().getCompilerClass().getCompiler().getErrorHandler().addError(CompilerError.builder()
                        .causePosition(ErrorPosition.builder()
                                .sourceCode(this.getLexer().getCompilerClass().getContent())
                                .filePath(this.getLexer().getCompilerClass().getFilePath())
                                .lineRange(this.getLineRange())
                                .charStart(this.getCharStart())
                                .charEnd(this.getCharEnd())
                                .build())
                        .causeMessage("The given hexadecimal value is higher than 2^63 - 1.")
                        .build());
            }
        } else {
            this.dataKind = DataKind.INTEGER;
            this.bits = 32;
        }
    }
    
    public NumberToken(
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
