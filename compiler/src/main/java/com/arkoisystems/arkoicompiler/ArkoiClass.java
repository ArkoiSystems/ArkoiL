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

import com.arkoisystems.arkoicompiler.stage.codegen.CodeGen;
import com.arkoisystems.arkoicompiler.stage.lexer.Lexer;
import com.arkoisystems.arkoicompiler.stage.parser.Parser;
import com.arkoisystems.arkoicompiler.stage.semantic.Semantic;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class ArkoiClass
{
    
    @NotNull
    private final ArkoiCompiler compiler;
    
    @NotNull
    private final Semantic semantic = new Semantic(this);
    
    @NotNull
    private final CodeGen codeGen = new CodeGen(this);
    
    @NotNull
    private final Parser parser = new Parser(this);
    
    @NotNull
    private final Lexer lexer = new Lexer(this);
    
    private final boolean detailed;
    
    private boolean isNative;
    
    @NotNull
    private String filePath;
    
    @NotNull
    private String content;
    
    public ArkoiClass(final @NotNull ArkoiCompiler compiler, final @NotNull String filePath, final @NotNull byte[] content, final boolean detailed) {
        this.compiler = compiler;
        this.detailed = detailed;
        this.filePath = filePath;
        
        this.content = new String(content, StandardCharsets.UTF_8);
        this.isNative = false;
    }
    
}
