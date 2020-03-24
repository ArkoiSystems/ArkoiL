/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 24, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.exceptions;

import org.jetbrains.annotations.NotNull;

public class CrashOnAccessException extends RuntimeException
{
    
    public CrashOnAccessException(@NotNull final String message) {
        super(message);
    }
    
}
