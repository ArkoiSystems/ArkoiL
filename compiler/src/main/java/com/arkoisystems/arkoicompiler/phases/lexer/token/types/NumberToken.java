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
package com.arkoisystems.arkoicompiler.phases.lexer.token.types;

import com.arkoisystems.arkoicompiler.phases.lexer.Lexer;
import com.arkoisystems.arkoicompiler.phases.lexer.token.LexerToken;
import com.arkoisystems.arkoicompiler.phases.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class NumberToken extends LexerToken
{
    
    @NotNull
    private final TypeKind typeKind;
    
    @Builder
    public NumberToken(
            final @NotNull Lexer lexer,
            final @Nullable TypeKind typeKind,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.NUMBER, startLine, endLine, charStart, charEnd);
    
        this.typeKind = typeKind == null ?
                this.getTokenContent().contains(".") ? TypeKind.FLOAT : TypeKind.INTEGER :
                typeKind;
    }
    
    public NumberToken(
            final @NotNull Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        this(lexer, null, startLine, endLine, charStart, charEnd);
    }
    
}
