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
package com.arkoisystems.arkoicompiler.stage.lexer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexer.Lexer;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
public class SymbolToken extends ArkoiToken
{
    
    @NotNull
    private SymbolType symbolType;
    
    @Builder
    public SymbolToken(
            final @NotNull Lexer lexer,
            final @NotNull SymbolType symbolType,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.SYMBOL, startLine, endLine, charStart, charEnd);
        
        this.symbolType = symbolType;
    }
    
    public SymbolToken(
            final @NotNull Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.SYMBOL, startLine, endLine, charStart, charEnd);
        
        for (final SymbolType symbolType : SymbolType.values())
            if (symbolType.getName().equals(this.getTokenContent())) {
                this.symbolType = symbolType;
                break;
            }
        
        Objects.requireNonNull(this.getSymbolType(), "symbolType must not be null.");
    }
    
}
