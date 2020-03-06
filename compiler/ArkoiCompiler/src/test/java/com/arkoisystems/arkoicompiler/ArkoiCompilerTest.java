/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

public class ArkoiCompilerTest
{
    
    @Test
    void basicTest() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("../../examples/basic/");
        
        final File testDirectory = new File("../../examples/basic/");
        if (testDirectory.listFiles() != null) {
            for (final File file : Objects.requireNonNull(testDirectory.listFiles()))
                if (file.getName().endsWith(".ark"))
                    arkoiCompiler.addFile(file);
        }
        
        if (!arkoiCompiler.compile()) {
            System.err.println("Couldn't compile the file. Please see the stacktrace for errors:");
            arkoiCompiler.printStackTrace(System.err);
        }
        
        try (final PrintStream printStream = new PrintStream(new File("../../examples/basic/output.result"))) {
            arkoiCompiler.printSyntaxTree(printStream);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
}