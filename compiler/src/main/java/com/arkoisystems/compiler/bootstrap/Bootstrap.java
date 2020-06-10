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
package com.arkoisystems.compiler.bootstrap;

import com.arkoisystems.compiler.Compiler;
import com.arkoisystems.utils.general.FileUtils;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Bootstrap
{
    
    @SneakyThrows
    public static void main(@NotNull final String[] args) {
        final Options options = new Options();
        {
            final Option inputDirectory = new Option("ip", "inputPath", true, "target source directory or file");
            inputDirectory.setRequired(true);
            options.addOption(inputDirectory);
        
            final Option outputFile = new Option("of", "outputFile", true, "output path for the compilation");
            outputFile.setRequired(true);
            options.addOption(outputFile);
        }
        
        final CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (final ParseException ex) {
            System.out.println(ex.getMessage());
            new HelpFormatter().printHelp("arkoi-compiler", options);
            return;
        }
    
        compile(commandLine.getOptionValue("inputPath"), commandLine.getOptionValue("outputFile"));
    }
    
    @SneakyThrows
    public static boolean compile(@NotNull final String inputPath, @NotNull final String outputPath) {
        final File targetPath = new File(inputPath);
        if (!targetPath.exists())
            throw new NullPointerException("The given \"inputPath\" doesn't exists. Please correct the path to a valid file or directory.");
        
        final Compiler compiler = new Compiler(outputPath);
        if (targetPath.isDirectory()) {
            for (final File file : FileUtils.getAllFiles(targetPath, ".ark"))
                compiler.addFile(file);
        } else {
            if (!targetPath.getName().endsWith(".ark"))
                throw new NullPointerException("Couldn't compile this file because it doesn't has the Arkoi file extension \".ark\".");
            compiler.addFile(targetPath);
        }
        
        if (compiler.compile(System.out))
            return true;
        
        System.err.println("Couldn't compile the file. Please see the stacktrace for errors:");
        compiler.getErrorHandler().printStackTrace(System.err);
        return false;
    }
    
}
