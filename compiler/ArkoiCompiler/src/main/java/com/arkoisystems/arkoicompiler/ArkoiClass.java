package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
@Setter
public class ArkoiClass
{
    
    @Expose
    private final String content;
    
    private final boolean nativeClass;
    
    @Expose
    private LexicalAnalyzer lexicalAnalyzer;
    
    @Expose
    private SyntaxAnalyzer syntaxAnalyzer;
    
    public ArkoiClass(final byte[] content, final boolean nativeClass) {
        this.nativeClass = nativeClass;
        
        this.content = new String(content, StandardCharsets.UTF_8);
    }
    
    public ArkoiClass(final byte[] content) {
        this.content = new String(content, StandardCharsets.UTF_8);
        this.nativeClass = false;
    }
    
    public ArkoiClass(final String content, final boolean nativeClass) {
        this.nativeClass = nativeClass;
        this.content = content;
    }
    
    public ArkoiClass(final String content) {
        this.content = content;
        
        this.nativeClass = false;
    }
    
    public LexicalAnalyzer analyseLexical() {
        return this.lexicalAnalyzer = new LexicalAnalyzer(this);
    }
    
    public SyntaxAnalyzer analyseSyntax() {
        if(this.lexicalAnalyzer == null) {
            System.err.println("You can't analyse the syntax before lexing the file.");
            System.exit(-1);
            return null;
        }
        return this.syntaxAnalyzer = new SyntaxAnalyzer(this);
    }
    
}
