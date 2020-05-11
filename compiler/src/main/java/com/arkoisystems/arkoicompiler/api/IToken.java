/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 06, 2020
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
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IToken extends IFailed
{
    
    @Nullable
    LexicalAnalyzer getLexicalAnalyzer();
    
    @NotNull
    TokenType getTokenType();
    
    @NotNull
    String getTokenContent();
    
    @EqualsAndHashCode.Include
    ArkoiError.ErrorPosition.LineRange getLineRange();
    
    int getCharStart();
    
    int getCharEnd();
    
    <E> E addError(@Nullable E errorSource, @NotNull final ICompilerClass compilerClass, final int charIndex, final int lineNumber, @NotNull final String message, final Object... arguments);
    
}
