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

import com.arkoisystems.compiler.error.ErrorHandler;
import com.arkoisystems.compiler.phases.linker.Linker;
import com.arkoisystems.utils.general.FileUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Compiler
{
    
    @NotNull
    private final List<CompilerClass> classes = new ArrayList<>();
    
    @NotNull
    private final List<File> libraryPaths = new ArrayList<>();
    
    @NotNull
    private final ErrorHandler errorHandler;
    
    @NotNull
    private final String outputPath;
    
    public Compiler(final @NotNull String outputPath) {
        this.outputPath = outputPath;
        
        this.getLibraryPaths().add(new File("../natives"));
        this.addNativeFiles();
        
        this.errorHandler = new ErrorHandler();
    }
    
    public void addFile(final @NotNull File file) {
        this.getClasses().add(new CompilerClass(this, file));
    }
    
    public boolean compile(final PrintStream printStream) {
        final long compileStart = System.nanoTime();
        {
            final long lexerStart = System.nanoTime();
            boolean lexerFailed = false;
            for (final CompilerClass compilerClass : this.getClasses())
                if (!compilerClass.getLexer().processStage())
                    lexerFailed = true;
            if (lexerFailed)
                return false;
            printStream.printf("The lexical analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - lexerStart) / 1_000_000D, this.classes.size());
    
            final long parserStart = System.nanoTime();
            boolean parserFailed = false;
            for (final CompilerClass compilerClass : this.getClasses())
                if (!compilerClass.getParser().processStage())
                    parserFailed = true;
            if (parserFailed)
                return false;
            printStream.printf("The syntax analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - parserStart) / 1_000_000D, this.getClasses().size());
    
            final long semanticStart = System.nanoTime();
            boolean semanticFailed = false;
            for (final CompilerClass compilerClass : this.getClasses())
                if (!compilerClass.getSemantic().processStage())
                    semanticFailed = true;
            if (semanticFailed)
                return false;
            printStream.printf("The semantic analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - semanticStart) / 1_000_000D, this.getClasses().size());
    
            final long codeGenTime = System.nanoTime();
            boolean codeGenFailed = false;
            for (final CompilerClass compilerClass : this.getClasses())
                if (!compilerClass.getIrGenerator().processStage())
                    codeGenFailed = true;
            if (codeGenFailed)
                return false;
            printStream.printf("The code generation took %sms for all classes (%s in total)\n", (System.nanoTime() - codeGenTime) / 1_000_000D, this.getClasses().size());
    
            final long linkerTime = System.nanoTime();
            final Linker linker = new Linker(this, this.getClasses().stream()
                    .map(clazz -> clazz.getIrGenerator().getModule())
                    .collect(Collectors.toList()));
            if (!linker.processStage())
                return false;
            printStream.printf("The linker took %sms for all classes (%s in total)\n", (System.nanoTime() - linkerTime) / 1_000_000D, this.getClasses().size());
        }
        printStream.printf("The compilation took %sms for all classes (%s in total)\n", (System.nanoTime() - compileStart) / 1_000_000D, this.getClasses().size());
        return true;
    }
    
    private void addNativeFiles() {
        final File nativeDirectory = new File("../natives");
        if (!nativeDirectory.exists())
            throw new NullPointerException("Couldn't find a native directory. Please try to fix the problem with reinstalling the compiler.");
        
        final List<File> files = FileUtils.getAllFiles(nativeDirectory, ".ark");
        for (final File file : files) {
            final CompilerClass compilerClass = new CompilerClass(this, file);
            compilerClass.setNative(true);
            this.getClasses().add(compilerClass);
        }
    }
    
}
