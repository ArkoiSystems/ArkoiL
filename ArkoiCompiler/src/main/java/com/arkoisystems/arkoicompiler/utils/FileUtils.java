/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtils
{
    
    @NotNull
    public static List<File> getAllFiles(@NotNull final File directory) {
        if (!directory.isDirectory())
            throw new NullPointerException("Couldn't get all files out of this directory, because it isn't a directory.");
        
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
