/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.RootSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.utils.FileUtils;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link ArkoiCompiler} class is used for compiling multiple files. It also contains
 * some useful methods for debugging etc. To start the compiling process you just need to
 * call the {@link ArkoiCompiler#compile()} method and so every file is getting compiled.
 */
public class ArkoiCompiler
{
    
    /**
     * This {@link HashMap} contains every {@link ArkoiClass} with the canonical path as a
     * key. You can use it to search a specified file or getting all {@link ArkoiClass}'s
     * inside it.
     */
    @Getter
    private final HashMap<String, ArkoiClass> arkoiClasses;
    
    
    /**
     * The working directory is specified with a {@link String} and is used to declare the
     * workstation of the compiler.
     */
    @Getter
    private final String workingDirectory;
    
    
    /**
     * Constructs an {@link ArkoiCompiler} with the given parameters. It will set the
     * workstation for the compiler {@link ArkoiCompiler#workingDirectory} and also
     * initializes itself with all natives files found ({@link ArkoiCompiler#addNativeFiles()}).
     *
     * @param workingDirectory
     *         the workstation for the compiler which is declared through a {@link
     *         String}.
     *
     * @throws IOException
     *         if something with the natives went wrong.
     */
    public ArkoiCompiler(@NonNull final String workingDirectory) throws IOException {
        this.workingDirectory = workingDirectory;
        this.arkoiClasses = new HashMap<>();
    
        this.addNativeFiles();
    }
    
    
    /**
     * Adds the specified file to the {@link ArkoiCompiler#arkoiClasses} map and creates a
     * new {@link ArkoiClass}.
     *
     * @param file
     *         the file which is used to get the canonical path and the content of it.
     *
     * @throws IOException
     *         if something went wrong during the reading of all bytes inside the file.
     */
    public void addFile(@NonNull final File file) throws IOException {
        this.getArkoiClasses().put(file.getCanonicalPath(), new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath())));
    }
    
    
    /**
     * Prints a StackTrace of the current errors and writes it to the given {@link
     * PrintStream}. This method is used if an error occurred or something went wrong.
     *
     * @param errorStream
     *         the {@link PrintStream} which is used to print the StackTrace.
     */
    public void printStackTrace(@NonNull final PrintStream errorStream) {
        for (final Map.Entry<String, ArkoiClass> classEntry : this.getArkoiClasses().entrySet()) {
            errorStream.println(classEntry.getKey() + ":");
            if (classEntry.getValue().getLexicalAnalyzer() != null)
                classEntry.getValue().getLexicalAnalyzer().getErrorHandler().printStackTrace(errorStream);
            if (classEntry.getValue().getSyntaxAnalyzer() != null)
                classEntry.getValue().getSyntaxAnalyzer().getErrorHandler().printStackTrace(errorStream);
            if (classEntry.getValue().getSemanticAnalyzer() != null)
                classEntry.getValue().getSemanticAnalyzer().getErrorHandler().printStackTrace(errorStream);
        }
    }
    
    /**
     * The main method of this whole class. It will initialize every stage of the given
     * {@link ArkoiClass}'s and tries to proceed them. If something doesn't work the
     * {@link AbstractStage#processStage()} method will return false and so the {@link
     * ArkoiCompiler#compile()} method too. This will indicate that an error occurred.
     *
     * @return true if everything worked correctly or false if an error occurred.
     */
    public boolean compile() {
        final long compileStart = System.nanoTime();
        final long lexicalStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values())
            arkoiClass.initializeLexical();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values()) {
            if (!arkoiClass.getLexicalAnalyzer().processStage())
                return false;
        }
        System.out.printf("The lexical analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - lexicalStart) / 1_000_000D, this.arkoiClasses.size());
        
        final long syntaxStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values())
            arkoiClass.initializeSyntax();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values()) {
            if (!arkoiClass.getSyntaxAnalyzer().processStage())
                return false;
        }
        System.out.printf("The syntax analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - syntaxStart) / 1_000_000D, this.getArkoiClasses().size());
        
        final long semanticStart = System.nanoTime();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values())
            arkoiClass.initializeSemantic();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values()) {
            if (!arkoiClass.getSemanticAnalyzer().processStage())
                return false;
        }
        System.out.printf("The semantic analysis took %sms for all classes (%s in total)\n", (System.nanoTime() - semanticStart) / 1_000_000D, this.getArkoiClasses().size());
        
        System.out.printf("The compilation took %sms for all classes (%s in total)\n", (System.nanoTime() - compileStart) / 1_000_000D, this.getArkoiClasses().size());
        return true;
    }
    
    
    /**
     * Adds every native file to the compilation task which are inside the native
     * directory. It will throw an error if the native directory doesn't exits or an error
     * occurred during the process of reading the file.
     *
     * @throws IOException
     *         if the native directory doesn't exists or an error occurred during the
     *         reading of the file.
     */
    private void addNativeFiles() throws IOException {
        final File nativeDirectory = new File("natives");
        if (!nativeDirectory.exists())
            throw new NullPointerException("Couldn't find a native directory. Please try to fix the problem with reinstalling the compiler.");
        
        final List<File> files = FileUtils.getAllFiles(nativeDirectory);
        for (final File file : files)
            this.getArkoiClasses().put(file.getCanonicalPath(), new ArkoiClass(this, file.getCanonicalPath(), Files.readAllBytes(file.toPath()), true));
    }
    
    
    /**
     * Finds a native {@link FunctionDefinitionSemanticAST} by the given function
     * description. It will skip every non-native {@link ArkoiClass} and will return null,
     * if no {@link FunctionDefinitionSemanticAST} was found.
     *
     * @param functionDescription
     *         the function description which is used to find the {@link
     *         FunctionDefinitionSemanticAST}.
     *
     * @return the found {@link FunctionDefinitionSemanticAST} or null if there doesn't
     *         exists any native functions with the same description.
     */
    public FunctionDefinitionSemanticAST findNativeSemanticFunction(@NonNull final String functionDescription) {
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values()) {
            if (!arkoiClass.isNativeClass())
                continue;
    
            for (final FunctionDefinitionSemanticAST functionDefinitionSemanticAST : arkoiClass.getSemanticAnalyzer().getRootSemanticAST().getFunctionStorage()) {
                if (functionDefinitionSemanticAST.getFunctionDescription().equals(functionDescription) && functionDefinitionSemanticAST.getSyntaxAST().hasAnnotation("native"))
                    return functionDefinitionSemanticAST;
            }
        }
        return null;
    }
    
    public void printSyntaxTree(final PrintStream printStream) {
        final List<RootSyntaxAST> roots = new ArrayList<>();
        for (final ArkoiClass arkoiClass : this.getArkoiClasses().values())
            roots.add(arkoiClass.getSyntaxAnalyzer().getRootSyntaxAST());
        
        printStream.println("Syntax Trees:");
        for (int index = 0; index < roots.size(); index++) {
            final AbstractSyntaxAST abstractSyntaxAST = roots.get(index);
            if (index == roots.size() - 1) {
                printStream.println("   │");
                printStream.println("   └── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, "       ");
            } else {
                printStream.println("   │");
                printStream.println("   ├── " + abstractSyntaxAST.getClass().getSimpleName());
                abstractSyntaxAST.printSyntaxAST(printStream, "   │   ");
            }
        }
    }
    
}
