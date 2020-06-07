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
package com.arkoisystems.compiler.phases.parser.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@AllArgsConstructor
@Getter
public enum DataKind
{
    
    /* Integral data types */
    
    INTEGER("int", true),
    LONG("long", true),
    SHORT("short", true),
    CHAR("char", true),
    BYTE("byte", true),
    BOOLEAN("bool", false),
    
    /* Floating-Point data types */
    
    FLOAT("float", true),
    DOUBLE("double", true),
    
    /* Data based data types  */
    
    STRUCT("struct", false),
    ENUM("enum", true),
    UNION("union", false),
    
    /* Special data types with no real meaning */
    
    VARIADIC("...", false),
    VOID("void", false),
    
    /* Compiler only data types */
    
    UNDEFINED("undefined", false),
    ERROR("error", false);
    
    @NotNull
    private final String name;
    
    private final boolean isNumeric;
    
}