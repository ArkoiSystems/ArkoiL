/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage;

import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * The abstract version of a stage which has some pre-defined methods to overwrite. Every
 * stage need to have an {@link ErrorHandler} to report errors and the {@link
 * AbstractStage#processStage()} method which will execute the code of the current stage.
 */
public abstract class AbstractStage
{
    
    /**
     * Defines a flag if the stage failed to run or not.
     */
    @Getter
    private boolean failed = false;
    
    
    /**
     * Returns the {@link ErrorHandler} of the current stage. So every stage needs to
     * provide such a {@link ErrorHandler} which will make a lot of things easier.
     *
     * @return the {@link ErrorHandler} of the current stage.
     */
    @NotNull
    public abstract ErrorHandler errorHandler();
    
    
    /**
     * This is an essential method to provide the capability to run every stage with the
     * same method. Also it will return a flag ({@code boolean}), which will say if an
     * error occurred or not.
     *
     * @return the flag if an error occurred or not.
     */
    public abstract boolean processStage();
    
    
    public void failed() {
        this.failed = true;
    }
    
}
