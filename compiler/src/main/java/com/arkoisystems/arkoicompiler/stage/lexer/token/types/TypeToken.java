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
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@Setter
public class TypeToken extends ArkoiToken
{
    
    @NotNull
    private TypeKind typeKind;
    
    @Builder
    public TypeToken(
            final @NotNull Lexer lexer,
            final @NotNull TypeKind typeKind,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.TYPE, startLine, endLine, charStart, charEnd);
        
        this.typeKind = typeKind;
    }
    
    public TypeToken(
            final @NotNull Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.TYPE, startLine, endLine, charStart, charEnd);
        
        for (final TypeKind typeKind : TypeKind.values())
            if (typeKind.getName().equals(this.getTokenContent())) {
                this.typeKind = typeKind;
                break;
            }
        
        Objects.requireNonNull(this.getTypeKind(), "typeKind must not be null. ");
    }
    
}
