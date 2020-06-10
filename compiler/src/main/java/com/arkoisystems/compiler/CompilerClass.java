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
package com.arkoisystems.compiler;

import com.arkoisystems.compiler.phases.irgen.IRGenerator;
import com.arkoisystems.compiler.phases.lexer.Lexer;
import com.arkoisystems.compiler.phases.parser.Parser;
import com.arkoisystems.compiler.phases.parser.SymbolTable;
import com.arkoisystems.compiler.phases.semantic.Semantic;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;

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
    private final IRGenerator irGenerator;
    
    @NotNull
    private final Parser parser;
    
    @NotNull
    private final Lexer lexer;
    
    @NotNull
    private final String name;
    
    private boolean isNative;
    
    @NotNull
    private String filePath;
    
    @NotNull
    private String content;
    
    @SneakyThrows
    public CompilerClass(@NotNull final Compiler compiler, @NotNull final File file) {
        this.compiler = compiler;
    
        this.name = file.getName().substring(0, file.getName().length() - 4);
        this.content = Files.readString(file.toPath());
        this.filePath = file.getCanonicalPath();
        this.isNative = false;
    
        this.irGenerator = new IRGenerator(this);
        this.rootScope = new SymbolTable(null);
        this.semantic = new Semantic(this);
        this.parser = new Parser(this);
        this.lexer = new Lexer(this);
    }
    
}
