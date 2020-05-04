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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.api.error.ICompilerError;
import com.arkoisystems.arkoicompiler.api.error.IErrorHandler;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashSet;

public class SyntaxErrorHandler implements IErrorHandler
{
    
    @Getter
    @NotNull
    private final HashSet<ICompilerError> compilerErrors = new HashSet<>();
    
    
    @Override
    public void addError(@NotNull final ICompilerError compilerError) {
        this.compilerErrors.add(compilerError);
    }
    
    
    @Override
    public void printStackTrace(@NotNull final PrintStream printStream, boolean testing) {
        for (final ICompilerError compilerError : this.compilerErrors)
            printStream.println(testing ? compilerError.toString().substring(compilerError.toString().indexOf(' ') + 1) : compilerError.toString());
    }
    
}
