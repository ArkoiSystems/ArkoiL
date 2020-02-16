/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import org.junit.Test;

import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

public class ArkoiCompilerTest
{
    
    public static String COMPILER_TEST = "../../examples/basic/";
    
    @Test
    public void runCompiler() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler(COMPILER_TEST);
        
        final File testDirectory = new File(COMPILER_TEST);
        if (testDirectory.listFiles() != null) {
            for (final File file : Objects.requireNonNull(testDirectory.listFiles()))
                if (file.getName().endsWith(".ark"))
                    arkoiCompiler.addFile(file);
        }
        
        if (!arkoiCompiler.compile()) {
            System.err.println("Couldn't compile the file. Please see the stacktrace for errors:");
            arkoiCompiler.printStackTrace(System.err);
        }
    
        try (final PrintStream printStream = new PrintStream(new File(COMPILER_TEST + "/output.result"))) {
            for (final ArkoiClass arkoiClass : arkoiCompiler.getArkoiClasses().values())
                printStream.print(arkoiClass);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
}