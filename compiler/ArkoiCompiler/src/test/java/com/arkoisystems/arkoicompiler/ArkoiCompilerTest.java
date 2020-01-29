package com.arkoisystems.arkoicompiler;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

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

    public static String OS_NAME = System.getProperty("os.name").toLowerCase();
    
    @Test
    public void runCompiler() throws IOException {
        if(OS_NAME.contains("win")) {
            final ArkoiCompiler arkoiCompiler = new ArkoiCompiler();
            arkoiCompiler.addFile(new File("C:/dev/projects/ArkoiL/examples/main.ark"));

            if (!arkoiCompiler.compile())
                System.err.println("Couldn't compile the file. Please see the stacktrace for errors.");
        } else if(OS_NAME.contains("nix") || OS_NAME.contains("nux") || OS_NAME.contains("aix")) {
            final ArkoiCompiler arkoiCompiler = new ArkoiCompiler();
            arkoiCompiler.addFile(new File("/media/timo/Boot/dev/projects/ArkoiL/examples/main.ark"));

            if (!arkoiCompiler.compile())
                System.err.println("Couldn't compile the file. Please see the stacktrace for errors.");
        }
    }
    
}