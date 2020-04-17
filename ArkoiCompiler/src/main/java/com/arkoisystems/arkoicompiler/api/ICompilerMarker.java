/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 28, 2020
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

import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICompilerMarker<T1, T2>
{
    
    @Nullable
    T1 getStart();
    
    
    void setStart(T1 start);
    
    
    @Nullable
    T2 getEnd();
    
    
    void setEnd(T2 end);
    
    
    @Nullable
    Object[] getErrorArguments();
    
    
    void setErrorArguments(@NotNull Object[] errorArguments);
    
    
    @NotNull
    ASTType getAstType();
    
    
    @Nullable
    String getErrorMessage();
    
    
    void setErrorMessage(@NotNull String errorMessage);
    
}
