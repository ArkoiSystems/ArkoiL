/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.bootstrap;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.utils.FileUtils;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@link Bootstrap} class is used for the main method. It's the first method which is
 * getting called. It will check the correctness of the input arguments and also will
 * throw an error if the input {@link File} doesn't exists etc.
 */
public class Bootstrap
{
    
    /**
     * This method will get called at first when executing the program. It will check for
     * correct arguments and if the input {@link File} exists.
     *
     * @param args
     *         the arguments which are given from the Java program.
     */
    @SneakyThrows
    public static void main(final String[] args) {
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
        
        final File targetPath = new File(commandLine.getOptionValue("inputPath"));
        if (targetPath.exists())
            throw new NullPointerException("The given \"inputPath\" doesn't exists. Please correct the path to a valid file or directory.");
        
        final ArkoiCompiler arkoiCompiler;
        if (targetPath.isDirectory()) {
            arkoiCompiler = new ArkoiCompiler(commandLine.getOptionValue("inputDirectory"));
            for (final File file : FileUtils.getAllFiles(targetPath)) {
                if (!file.getName().endsWith(".ark"))
                    continue;
                arkoiCompiler.addFile(file);
            }
        } else {
            if (!targetPath.getName().endsWith(".ark"))
                throw new NullPointerException("Couldn't compile this file because it doesn't has the Arkoi file extension \".ark\".");
            
            final File inputDirectory = targetPath.getParentFile();
            arkoiCompiler = new ArkoiCompiler(inputDirectory.getCanonicalPath());
            arkoiCompiler.addFile(targetPath);
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
    
}
