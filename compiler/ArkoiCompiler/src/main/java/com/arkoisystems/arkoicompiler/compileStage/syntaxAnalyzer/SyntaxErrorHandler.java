package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.ErrorHandler;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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
public class SyntaxErrorHandler extends ErrorHandler
{
    
    private final List<AbstractError> abstractErrors = new ArrayList<>();
    
    private final SyntaxAnalyzer syntaxAnalyzer;
    
    /**
     * This constructor is used to initialize this class with the SyntaxAnalyzer which is
     * getting used during the process of syntax analyzing.
     *
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which is currently used.
     */
    public SyntaxErrorHandler(final SyntaxAnalyzer syntaxAnalyzer) {
        this.syntaxAnalyzer = syntaxAnalyzer;
    }
    
    /**
     * This method will add an specified error "abstractError" to the current stage. It
     * will just add the error to a list.
     *
     * @param abstractError
     *         The AbstractError is the given error which contains data why something
     */
    @Override
    public void addError(final AbstractError abstractError) {
        this.abstractErrors.add(abstractError);
    }
    
    /**
     * This method is used to print the stack trace of the error, which is useful to debug
     * the program if something went wrong.
     *
     * @param printStream
     *         The PrintStream in which the stack trace should be written to.
     */
    @Override
    public void printStackTrace(final PrintStream printStream) {
        for (final AbstractError abstractError : this.abstractErrors)
            printStream.println(abstractError.toString());
    }
    
}
