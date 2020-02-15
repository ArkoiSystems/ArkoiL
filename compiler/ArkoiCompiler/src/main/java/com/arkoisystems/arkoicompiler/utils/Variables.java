/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Just a simple class with static variables which are used more frequently and so don't
 * need to be created every time.
 */
public class Variables
{
    
    /**
     * This variable is used to get a {@link Gson} instance without creating a new one
     * every time needed. It also enables to exclude fields without the {@link Expose}
     * annotation and disables HTML escaping.
     */
    public static Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
    
}
