package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
public class ArkoiCompiler
{
    
    @Expose
    private final List< ArkoiClass> arkoiClasses;
    
    public ArkoiCompiler() throws IOException {
        this.arkoiClasses = new ArrayList<>();
        
        this.addNativeFiles();
    }
    
    public void addFile(final File file) throws IOException {
        this.arkoiClasses.add(new ArkoiClass(Files.readAllBytes(file.toPath())));
    }
    
    public boolean compile() {
        final long compileStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.arkoiClasses) {
            final LexicalAnalyzer lexicalAnalyzer = arkoiClass.analyseLexical();
            if (!lexicalAnalyzer.processStage()) {
                lexicalAnalyzer.errorHandler().printStackTrace();
                return false;
            }
        }
        System.out.printf("The lexical analysis took %sms for all classes (%s in total)\n", ((System.nanoTime() - compileStart) / 1000000), this.arkoiClasses.size());
        
        for (final ArkoiClass arkoiClass : this.arkoiClasses) {
            final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.analyseSyntax();
            if (!syntaxAnalyzer.processStage()) {
                syntaxAnalyzer.errorHandler().printStackTrace();
                return false;
            }
        }
        System.out.printf("The compilation took %sms for all classes (%s in total)\n", ((System.nanoTime() - compileStart) / 1000000), this.arkoiClasses.size());
        
        return true;
    }
    
    private void addNativeFiles() throws IOException {
        final File nativeDirectory = new File("natives");
        if (!nativeDirectory.exists()) {
            System.err.println("Couldn't add the native files to the project. Please try to fix the problem with reinstalling the compiler.");
            System.exit(-1);
            return;
        }
        
        final List<File> files = this.getAllFiles(nativeDirectory);
        for(final File file : files)
            this.arkoiClasses.add(new ArkoiClass(Files.readAllBytes(file.toPath()), true));
    }
    
    private List<File> getAllFiles(final File directory) {
        final List<File> files = new ArrayList<>();
        for(final File file : Objects.requireNonNull(directory.listFiles())) {
            if(file.isDirectory())
                files.addAll(this.getAllFiles(file));
            else
                files.add(file);
        }
        return files;
    }
    
}
