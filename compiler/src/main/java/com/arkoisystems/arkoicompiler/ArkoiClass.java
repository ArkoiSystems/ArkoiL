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

import com.arkoisystems.alt.ArkoiLT;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.stage.codegen.CodeGen;
import com.arkoisystems.arkoicompiler.stage.lexer.ArkoiLexer;
import com.arkoisystems.arkoicompiler.stage.parser.ArkoiParser;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semantic.SemanticAnalyzer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class ArkoiClass implements ICompilerClass
{
    
    @Getter
    @NotNull
    private final ArkoiCompiler arkoiCompiler;
    
    @Getter
    @Setter
    @NotNull
    private String content;
    
    @Getter
    @Setter
    private boolean isNative;
    
    @Getter
    @Setter
    @NotNull
    private String filePath;
    
    @Getter
    @NotNull
    private final SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(this);
    
    @Getter
    @NotNull
    private final CodeGen codeGen = new CodeGen(this);
    
    @Getter
    @NotNull
    private final ArkoiLT<ArkoiLexer> languageTools;
    
    @Getter
    @NotNull
    private final SemanticAnalyzer semanticAnalyzer;
    
    @Getter
    private final boolean detailed;
    
    public ArkoiClass(@NotNull final ArkoiCompiler arkoiCompiler, @NotNull final String filePath, @NotNull final byte[] content, final boolean detailed) {
        this.arkoiCompiler = arkoiCompiler;
        this.detailed = detailed;
        this.filePath = filePath;
        
        this.languageTools = ArkoiLT.makeLanguage(ArkoiLexer.class, ArkoiParser.class);
        this.getLanguageTools().getLexer().setCompilerClass(this);
        
        this.semanticAnalyzer = new SemanticAnalyzer(this, detailed);
        
        this.content = new String(content, StandardCharsets.UTF_8);
        this.isNative = false;
    }
    
}
