/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiASTPrinter;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootAST;
import com.arkoisystems.arkoicompiler.utils.FileUtils;
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
    
    
    public void addFile(@NotNull final File file) throws IOException {
        this.getArkoiClasses().add(new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath())));
    }
    
    
    public void addClass(@NotNull final ArkoiClass arkoiClass) {
        this.getArkoiClasses().add(arkoiClass);
    }
    
    
    public void printStackTrace(@NotNull final PrintStream errorStream) {
        for (final ICompilerClass arkoiClass : this.getArkoiClasses()) {
            arkoiClass.getLexicalAnalyzer().getErrorHandler().printStackTrace(errorStream, false);
            arkoiClass.getSyntaxAnalyzer().getErrorHandler().printStackTrace(errorStream, false);
            arkoiClass.getSemanticAnalyzer().getErrorHandler().printStackTrace(errorStream, false);
        }
    }
    
    
    public boolean compile() {
        final long compileStart = System.nanoTime();
        {
            final long lexicalStart = System.nanoTime();
            boolean failed = false;
            for (final ICompilerClass arkoiClass : this.getArkoiClasses()) {
                if (!arkoiClass.getLexicalAnalyzer().processStage())
                    failed = true;
            }
            if (failed)
                return false;
            System.out.printf("The lexical analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - lexicalStart) / 1_000_000D, this.arkoiClasses.size());
            
            final long syntaxStart = System.nanoTime();
            failed = false;
            for (final ICompilerClass arkoiClass : this.getArkoiClasses()) {
                if (!arkoiClass.getSyntaxAnalyzer().processStage())
                    failed = true;
            }
            if (failed)
                return false;
            System.out.printf("The syntax analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - syntaxStart) / 1_000_000D, this.getArkoiClasses().size());
            
            final long semanticStart = System.nanoTime();
            failed = false;
            for (final ICompilerClass arkoiClass : this.getArkoiClasses()) {
                if (!arkoiClass.getSemanticAnalyzer().processStage())
                    failed = true;
            }
            if (failed)
                return false;
            System.out.printf("The semantic analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - semanticStart) / 1_000_000D, this.getArkoiClasses().size());
        }
        System.out.printf("The compilation took %sms for all classes (%s in total)\n", (System.nanoTime() - compileStart) / 1_000_000D, this.getArkoiClasses().size());
        return true;
    }
    
    
    private void addNativeFiles() throws IOException {
        final File nativeDirectory = new File("./natives/");
        if (!nativeDirectory.exists())
            throw new NullPointerException("Couldn't find a native directory. Please try to fix the problem with reinstalling the compiler.");
        
        final List<File> files = FileUtils.getAllFiles(nativeDirectory);
        for (final File file : files) {
            final ArkoiClass arkoiClass = new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath()));
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
