/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 12, 2020
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
package com.arkoisystems.arkoicompiler.stage.lexer.token;

import com.arkoisystems.alt.api.IToken;
import com.arkoisystems.alt.lexer.LexerToken;
import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.stage.lexer.ArkoiLexer;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class ArkoiToken implements IToken
{
    
    @NotNull
    private final ArkoiError.ErrorPosition.LineRange lineRange;
    
    private final int charStart, charEnd;
    
    @NotNull
    private final TokenType tokenType;
    
    @NotNull
    private String data;
    
    public ArkoiToken(
            @NotNull final ArkoiLexer lexer,
            @NotNull final TokenType tokenType
    ) {
        this.tokenType = tokenType;
        
        this.lineRange = ArkoiError.ErrorPosition.LineRange.make(
                lexer.getCompilerClass(),
                lexer.getLine(),
                lexer.getLine()
        );
        this.charStart = 0;
        this.charEnd = 0;
        this.data = "";
    }
    
    public ArkoiToken(
            @NotNull final ArkoiLexer lexer,
            @NotNull final TokenType tokenType,
            @NotNull final LexerToken lexerToken
    ) {
        this.tokenType = tokenType;
        
        this.charStart = lexerToken.getCharStart() - lexer.getStartChar();
        this.charEnd = lexerToken.getCharEnd() - lexer.getStartChar();
        this.data = lexerToken.getData();
        
        this.lineRange = ArkoiError.ErrorPosition.LineRange.make(
                lexer.getCompilerClass(),
                lexer.getLine(),
                lexer.getLine()
        );
    }
    
}
