package com.arkoisystems.arkoicompiler;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

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
public class ArkoiCompilerTest
{
    
    @Test
    public void runCompiler() throws IOException {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler();
        arkoiCompiler.addFile(new File("../../examples/basic/main.ark"));
        
        if (!arkoiCompiler.compile()) {
            System.err.println("Couldn't compile the file. Please see the stacktrace for errors:");
            arkoiCompiler.printStackTrace(System.err);
        } else {
            try (final PrintStream printStream = new PrintStream(new File("../../examples/basic/output.result"))) {
                for (final ArkoiClass arkoiClass : arkoiCompiler.getArkoiClasses())
                    printStream.print(arkoiClass);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
}