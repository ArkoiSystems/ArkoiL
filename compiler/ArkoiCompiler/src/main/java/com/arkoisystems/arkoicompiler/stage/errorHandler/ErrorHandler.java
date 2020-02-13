package com.arkoisystems.arkoicompiler.stage.errorHandler;

import java.io.PrintStream;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
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
public abstract class ErrorHandler
{
    
    /**
     * This method will add an specified error "abstractError" to the current stage. Also
     * it can handle the error in every way it wants.
     *
     * @param abstractError
     *         The AbstractError is the given error which contains data why something
     *         doesn't work.
     */
    public abstract void addError(final AbstractError abstractError);
    
    /**
     * This method is used to print the stack trace of the error, which is useful to debug
     * the program if something went wrong.
     *
     * @param printStream
     *         The PrintStream in which the stack trace should be written to.
     */
    public abstract void printStackTrace(final PrintStream printStream);
    
}
