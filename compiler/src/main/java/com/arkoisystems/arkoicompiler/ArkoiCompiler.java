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

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.stage.lexer.ArkoiLexer;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiASTPrinter;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.RootAST;
import com.arkoisystems.arkoicompiler.utils.FileUtils;
import com.arkoisystems.llvm4j.api.LLVMAPI;
import com.sun.jna.Native;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ArkoiCompiler
{
    
    @Getter
    @NonNull
    private final List<ICompilerClass> arkoiClasses = new ArrayList<>();
    
    public ArkoiCompiler() throws IOException {
        this.addNativeFiles();
    }
    
    public void addFile(@NotNull final File file, final boolean detailed) throws IOException {
        this.getArkoiClasses().add(new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath()), detailed));
    }
    
    public void addClass(@NotNull final ArkoiClass arkoiClass) {
        this.getArkoiClasses().add(arkoiClass);
    }
    
    public void printStackTrace(@NotNull final PrintStream errorStream) {
        for (final ICompilerClass arkoiClass : this.getArkoiClasses()) {
            arkoiClass.getLanguageTools().getLexer().getErrorHandler().printStackTrace(errorStream, false);
            arkoiClass.getSyntaxAnalyzer().getErrorHandler().printStackTrace(errorStream, false);
            arkoiClass.getSemanticAnalyzer().getErrorHandler().printStackTrace(errorStream, false);
        }
    }
    
    public boolean compile(final PrintStream printStream) {
        final long compileStart = System.nanoTime();
        {
            final long lexicalStart = System.nanoTime();
            final ICompilerClass lexicalFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getLanguageTools().getLexer().processStage())
                    .findFirst()
                    .orElse(null);
            if (lexicalFailed != null)
                return false;
            printStream.printf("The lexical analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - lexicalStart) / 1_000_000D, this.arkoiClasses.size());
            
            final long syntaxStart = System.nanoTime();
            final ICompilerClass syntaxFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getSyntaxAnalyzer().processStage())
                    .findFirst()
                    .orElse(null);
            if (syntaxFailed != null)
                return false;
            printStream.printf("The syntax analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - syntaxStart) / 1_000_000D, this.getArkoiClasses().size());
    
            final long semanticStart = System.nanoTime();
            final ICompilerClass semanticFailed = this.getArkoiClasses().stream()
                    .filter(compilerClass -> !compilerClass.getSemanticAnalyzer().processStage())
                    .findFirst()
                    .orElse(null);
            if (semanticFailed != null)
                return false;
            printStream.printf("The semantic analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - semanticStart) / 1_000_000D, this.getArkoiClasses().size());
            
            final long codeGenTime = System.nanoTime();
            final ICompilerClass codeGenFailed = this.getArkoiClasses().stream()
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
        
        final List<File> files = FileUtils.getAllFiles(nativeDirectory);
        for (final File file : files) {
            final ArkoiClass arkoiClass = new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath()), true);
            arkoiClass.setNative(true);
            this.getArkoiClasses().add(arkoiClass);
        }
    }
    
    public void printSyntaxTree(@NotNull final PrintStream printStream) throws NullPointerException {
        final List<RootAST> roots = new ArrayList<>();
        for (final ICompilerClass arkoiClass : this.getArkoiClasses())
            roots.add(arkoiClass.getSyntaxAnalyzer().getRootAST());
        
        printStream.println("Syntax Trees:");
        for (int index = 0; index < roots.size(); index++) {
            final RootAST rootAST = roots.get(index);
            if (index == roots.size() - 1) {
                printStream.println("   │");
                printStream.println("   └── " + rootAST.getClass().getSimpleName());
                ArkoiASTPrinter.builder()
                        .printStream(printStream)
                        .indents("       ")
                        .build()
                        .visit(rootAST);
            } else {
                printStream.println("   │");
                printStream.println("   ├── " + rootAST.getClass().getSimpleName());
                ArkoiASTPrinter.builder()
                        .printStream(printStream)
                        .indents("   │   ")
                        .build()
                        .visit(rootAST);
            }
        }
    }
    
}
