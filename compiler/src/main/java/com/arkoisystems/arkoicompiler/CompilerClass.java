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
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.phases.codegen.CodeGen;
import com.arkoisystems.arkoicompiler.phases.lexer.Lexer;
import com.arkoisystems.arkoicompiler.phases.parser.Parser;
import com.arkoisystems.arkoicompiler.phases.parser.SymbolTable;
import com.arkoisystems.arkoicompiler.phases.semantic.Semantic;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class CompilerClass
{
    
    @NotNull
    private final SymbolTable rootScope;
    
    @NotNull
    private final Semantic semantic;
    
    @NotNull
    private final Compiler compiler;
    
    @NotNull
    private final CodeGen codeGen;
    
    @NotNull
    private final Parser parser;
    
    @NotNull
    private final Lexer lexer;
    
    private boolean isNative;
    
    @NotNull
    private String filePath;
    
    @NotNull
    private String content;
    
    public CompilerClass(final @NotNull Compiler compiler, final @NotNull String filePath, final @NotNull byte[] content) {
        this.compiler = compiler;
        this.filePath = filePath;
    
        this.content = new String(content, StandardCharsets.UTF_8);
        this.isNative = false;
    
        this.rootScope = new SymbolTable(null);
        this.semantic = new Semantic(this);
        this.codeGen = new CodeGen(this);
        this.parser = new Parser(this);
        this.lexer = new Lexer(this);
    }
    
}
