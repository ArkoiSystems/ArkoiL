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

import com.arkoisystems.utils.general.FileUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ArkoiCompiler
{
    
    @NotNull
    private final List<ArkoiClass> arkoiClasses = new ArrayList<>();
    
    @NotNull
    private final List<File> libraryPaths = new ArrayList<>();
    
    public ArkoiCompiler() throws IOException {
        this.getLibraryPaths().add(new File("../natives"));
        this.addNativeFiles();
    }
    
    public void addFile(final @NotNull File file, final boolean detailed) throws IOException {
        this.getArkoiClasses().add(new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath()), detailed));
    }
    
    public void printStackTrace(final @NotNull PrintStream errorStream) {
        for (final ArkoiClass arkoiClass : this.getArkoiClasses()) {
            arkoiClass.getLexer().getErrorHandler().printStackTrace(errorStream);
            arkoiClass.getParser().getErrorHandler().printStackTrace(errorStream);
            arkoiClass.getSemantic().getErrorHandler().printStackTrace(errorStream);
        }
    }
    
    public boolean compile(final PrintStream printStream) {
        final long compileStart = System.nanoTime();
        {
            final long lexerStart = System.nanoTime();
            final ArkoiClass lexerFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getLexer().processStage())
                    .findFirst()
                    .orElse(null);
            if (lexerFailed != null)
                return false;
            printStream.printf("The lexical analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - lexerStart) / 1_000_000D, this.arkoiClasses.size());
            
            final long parserStart = System.nanoTime();
            final ArkoiClass syntaxFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getParser().processStage())
                    .findFirst()
                    .orElse(null);
            if (syntaxFailed != null)
                return false;
            printStream.printf("The syntax analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - parserStart) / 1_000_000D, this.getArkoiClasses().size());
            
            final long semanticStart = System.nanoTime();
            final ArkoiClass semanticFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getSemantic().processStage())
                    .findFirst()
                    .orElse(null);
            if (semanticFailed != null)
                return false;
            printStream.printf("The semantic analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - semanticStart) / 1_000_000D, this.getArkoiClasses().size());
            
            final long codeGenTime = System.nanoTime();
            final ArkoiClass codeGenFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getCodeGen().processStage())
                    .findFirst()
                    .orElse(null);
            if (codeGenFailed != null)
                return false;
            printStream.printf("The code generation took %sms for all classes (%s in total)\n", (System.nanoTime() - codeGenTime) / 1_000_000D, this.getArkoiClasses().size());
        }
        printStream.printf("The compilation took %sms for all classes (%s in total)\n", (System.nanoTime() - compileStart) / 1_000_000D, this.getArkoiClasses().size());
        return true;
    }
    
    private void addNativeFiles() throws IOException {
        final File nativeDirectory = new File("../natives");
        if (!nativeDirectory.exists())
            throw new NullPointerException("Couldn't find a native directory. Please try to fix the problem with reinstalling the compiler.");
        
        final List<File> files = FileUtils.getAllFiles(nativeDirectory, ".ark");
        for (final File file : files) {
            final ArkoiClass arkoiClass = new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath()), true);
            arkoiClass.setNative(true);
            this.getArkoiClasses().add(arkoiClass);
        }
    }
    
}
