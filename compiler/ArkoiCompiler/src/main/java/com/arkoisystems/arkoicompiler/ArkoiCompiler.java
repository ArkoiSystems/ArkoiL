package com.arkoisystems.arkoicompiler;

import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.*;

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
    private final HashMap<String, ArkoiClass> arkoiClasses;
    
    @Expose
    private final String workingDirectory;
    
    public ArkoiCompiler(final String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        this.arkoiClasses = new HashMap<>();
        
        this.addNativeFiles();
    }
    
    public void addFile(final File file) throws IOException {
        this.arkoiClasses.put(file.getCanonicalPath(), new ArkoiClass(this, Files.readAllBytes(file.toPath())));
    }
    
    public void printStackTrace(final PrintStream errorStream) {
        for (final Map.Entry<String, ArkoiClass> classEntry : this.arkoiClasses.entrySet()) {
            System.out.println(classEntry.getKey() + ":");
            if (classEntry.getValue().getLexicalAnalyzer() != null)
                classEntry.getValue().getLexicalAnalyzer().getErrorHandler().printStackTrace(errorStream);
            if (classEntry.getValue().getSyntaxAnalyzer() != null)
                classEntry.getValue().getSyntaxAnalyzer().getErrorHandler().printStackTrace(errorStream);
            if (classEntry.getValue().getSemanticAnalyzer() != null)
                classEntry.getValue().getSemanticAnalyzer().getErrorHandler().printStackTrace(errorStream);
        }
    }
    
    public boolean compile() throws Exception {
        for (final ArkoiClass arkoiClass : this.arkoiClasses.values()) {
            arkoiClass.initializeLexical();
            arkoiClass.initializeSyntax();
            arkoiClass.initializeSemantic();
        }
    
        final long compileStart = System.nanoTime();
        final long lexicalStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.arkoiClasses.values()) {
            if (!arkoiClass.getLexicalAnalyzer().processStage())
                return false;
        }
        System.out.printf("The lexical analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - lexicalStart) / 1000000D, this.arkoiClasses.size());
    
        final long syntaxStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.arkoiClasses.values()) {
            if (!arkoiClass.getSyntaxAnalyzer().processStage())
                return false;
        }
        System.out.printf("The syntax analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - syntaxStart) / 1000000D, this.arkoiClasses.size());
    
        final long semanticStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.arkoiClasses.values()) {
            if (!arkoiClass.getSemanticAnalyzer().processStage())
                return false;
        }
        System.out.printf("The semantic analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - semanticStart) / 1000000D, this.arkoiClasses.size());
    
        System.out.printf("The compilation took %sms for all classes (%s in total)\n", (System.nanoTime() - compileStart) / 1000000D, this.arkoiClasses.size());
        return true;
    }
    
    private void addNativeFiles() throws IOException {
        final File nativeDirectory = new File("../natives");
        if (!nativeDirectory.exists()) {
            System.err.println("Couldn't add the native files to the project. Please try to fix the problem with reinstalling the compiler.");
            System.exit(-1);
            return;
        }
        
        final List<File> files = this.getAllFiles(nativeDirectory);
        for (final File file : files)
            this.arkoiClasses.put(file.getCanonicalPath(), new ArkoiClass(this, Files.readAllBytes(file.toPath()), true));
    }
    
    private List<File> getAllFiles(final File directory) {
        final List<File> files = new ArrayList<>();
        for (final File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                files.addAll(this.getAllFiles(file));
            else
                files.add(file);
        }
        return files;
    }
    
}
