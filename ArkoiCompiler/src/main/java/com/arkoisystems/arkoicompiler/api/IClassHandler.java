/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 31, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IClassHandler
{
    
    @Nullable ArkoiClass getArkoiFile(@NotNull final String filePath);
    
}
