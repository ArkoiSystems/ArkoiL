/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 25, 2020
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
package com.arkoisystems.arkoicompiler.stage.lexer;

import com.arkoisystems.arkoicompiler.api.IErrorHandler;
import com.arkoisystems.arkoicompiler.error.ArkoiError;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashSet;

public class LexerErrorHandler implements IErrorHandler
{
    
    @Getter
    @NotNull
    private final HashSet<ArkoiError> compilerErrors = new HashSet<>();
    
    @Override
    public void addError(final @NotNull ArkoiError compilerError) {
        this.compilerErrors.add(compilerError);
    }
    
    @Override
    public void printStackTrace(final @NotNull PrintStream printStream) {
        for (final ArkoiError error : this.compilerErrors)
            printStream.println(error.toString());
    }
    
}
