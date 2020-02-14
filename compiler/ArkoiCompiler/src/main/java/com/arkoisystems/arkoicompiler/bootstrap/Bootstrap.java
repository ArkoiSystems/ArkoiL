package com.arkoisystems.arkoicompiler.bootstrap;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
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
public class Bootstrap
{
    
    @SneakyThrows
    public static void main(String[] args) {
        final Options options = new Options();
        
        {
            final Option inputDirectory = new Option("id", "inputDirectory", true, "target source directory");
            inputDirectory.setRequired(true);
            options.addOption(inputDirectory);
            
            final Option outputFile = new Option("of", "outputFile", true, "output path for the compilation");
            outputFile.setRequired(true);
            options.addOption(outputFile);
        }
        
        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine commandLine;
        
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (final ParseException ex) {
            System.out.println(ex.getMessage());
            helpFormatter.printHelp("arkoi-compiler", options);
            return;
        }
        
        final File inputDirectory = new File(commandLine.getOptionValue("inputDirectory"));
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler(commandLine.getOptionValue("inputDirectory"));
        
        for(final File file : getAllFiles(inputDirectory)) {
            if(!file.getName().endsWith(".ark"))
                continue;
            arkoiCompiler.addFile(file);
        }
        
        if (!arkoiCompiler.compile()) {
            System.err.println("Couldn't compile the file. Please see the stacktrace for errors:");
            arkoiCompiler.printStackTrace(System.err);
        }
        
        try (final PrintStream printStream = new PrintStream(new File(commandLine.getOptionValue("outputFile")))) {
            for (final ArkoiClass arkoiClass : arkoiCompiler.getArkoiClasses().values())
                printStream.print(arkoiClass);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static List<File> getAllFiles(final File directory) {
        final List<File> files = new ArrayList<>();
        for (final File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory())
                files.addAll(getAllFiles(file));
            else
                files.add(file);
        }
        return files;
    }
    
}
