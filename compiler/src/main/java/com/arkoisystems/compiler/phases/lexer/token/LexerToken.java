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
package com.arkoisystems.compiler.phases.lexer.token;

import com.arkoisystems.compiler.error.LineRange;
import com.arkoisystems.compiler.phases.lexer.Lexer;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.utils.printer.annotations.Printable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class LexerToken
{
    
    private final int charStart, charEnd;
    
    @Printable(name = "type")
    @NotNull
    private final TokenType tokenType;
    
    @NotNull
    private final LineRange lineRange;
    
    @NotNull
    private final Lexer lexer;
    
    @Printable(name = "content")
    @NotNull
    private String tokenContent;
    
    private boolean failed;
    
    public LexerToken(
            @NotNull final Lexer lexer,
            @NotNull final TokenType tokenType,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        this.tokenType = tokenType;
        this.charStart = charStart;
        this.charEnd = charEnd;
        this.lexer = lexer;
        
        this.lineRange = LineRange.make(this.getLexer().getCompilerClass(), startLine, endLine);
        this.tokenContent = this.getLineRange().getSourceCode().substring(charStart, charEnd);
        this.failed = false;
    }
    
}
