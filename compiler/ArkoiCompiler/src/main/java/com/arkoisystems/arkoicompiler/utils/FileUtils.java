/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is capable for all {@link File} related functions which are getting used
 * more frequently and so they don't need to be created every time.
 */
public class FileUtils
{
    
    /**
     * Gets every file inside a specified directory. It also includes sub-directories an
     * so on. If the {@link File[]} is null the method will throw an error.
     *
     * @param directory
     *         the start directory where all files and sub-directories are included.
     *
     * @return a list of all files without the directories.
     */
    public static List<File> getAllFiles(final File directory) {
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
