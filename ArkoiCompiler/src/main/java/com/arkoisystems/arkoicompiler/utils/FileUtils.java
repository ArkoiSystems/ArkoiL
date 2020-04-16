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
