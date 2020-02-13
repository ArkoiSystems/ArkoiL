package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;

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
public class LexicalErrorHandler extends ErrorHandler
{
    
    private final List<AbstractError> abstractErrors = new ArrayList<>();
    
    @Override
    public void addError(final AbstractError abstractError) {
        this.abstractErrors.add(abstractError);
    }
    
    @Override
    public void printStackTrace(final PrintStream printStream) {
        for(final AbstractError abstractError : this.abstractErrors)
            printStream.println(abstractError.toString());
    }
    
}
