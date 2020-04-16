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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArkoiCompilerTest
{
    
    @Test
    public void basicTest() throws Exception {
        this.runTest("../examples/basic/");
    }
    
    
    @Test
    public void conditionalReturnTest() throws Exception {
        this.runTest("../examples/conditional_return/");
    }
    
    
    @Test
    public void conditionalVariableDefinitionTest() throws Exception {
        this.runTest("../examples/conditional_variable_definition/");
    }
    
    
    private void runTest(final String inputDirectory) throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler();
        
        final File testDirectory = new File(inputDirectory);
        if (testDirectory.listFiles() != null) {
            for (final File file : Objects.requireNonNull(testDirectory.listFiles()))
                if (file.getName().endsWith(".ark"))
                    arkoiCompiler.addFile(file);
        }
        
        try (final PrintStream printStream = new PrintStream(new File(inputDirectory + "output.result"))) {
            assertTrue(arkoiCompiler.compile(), "\n" + this.getStackTraceAndPrintTree(arkoiCompiler, printStream));
            arkoiCompiler.printSyntaxTree(System.out);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    private String getStackTraceAndPrintTree(final ArkoiCompiler arkoiCompiler, final PrintStream treeStream) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        arkoiCompiler.printStackTrace(printStream);
        try {
            arkoiCompiler.printSyntaxTree(treeStream);
        } catch (final Exception ignored) {
        }
        return byteArrayOutputStream.toString();
    }
    
}