/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
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
package com.arkoisystems.arkoicompiler.stage.parser.ast.utils;

import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum TypeKind
{
    
    FLOAT("float", true, 2),
    DOUBLE("double", true, 2.5),
    LONG("long", true, 1.5),
    INTEGER("int", true, 1),
    SHORT("short", true, 1),
    CHAR("char", true, 1),
    BYTE("byte", true, 0.5),
    
    STRING("string", false, 0),
    BOOLEAN("bool", false, 0),
    
    COLLECTION("[]", false, 0),
    VOID("void", false, 0),
    VARIADIC("...", false, 0),
    UNDEFINED("undefined", false, 0);
    
    @NotNull
    @Getter
    private final String name;
    
    @Getter
    private final boolean isNumeric;
 
    @Getter
    private final double precision;
    
    TypeKind(final @NotNull String name, final boolean isNumeric, final double precision) {
        this.isNumeric = isNumeric;
        this.precision = precision;
        this.name = name;
    }
    
    @Nullable
    public static TypeKind getTypeKind(final ArkoiToken arkoiToken) {
        for (final TypeKind typeKind : TypeKind.values()) {
            if (typeKind == COLLECTION || typeKind == UNDEFINED)
                continue;
            if (typeKind.getName().equals(arkoiToken.getTokenContent()))
                return typeKind;
        }
        return null;
    }
    
}