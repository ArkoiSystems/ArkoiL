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
package com.arkoisystems.arkoicompiler.api.error;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ICompilerError
{
    
    /**
     * This method is used to get the {@link ICompilerClass} <b>everywhere</b> where the
     * {@link ICompilerError} is used.
     *
     * @return the {@link ICompilerClass} in which the {@link ICompilerError} occurred.
     */
    @NotNull
    ICompilerClass getCompilerClass();
    
    
    /**
     * This method returns a {@link List} of {@link ArkoiError.ErrorPosition}s which can
     * be used to display the problematic sections.
     *
     * @return a {@link List} of {@link ArkoiError.ErrorPosition}s.
     */
    @NotNull
    List<ArkoiError.ErrorPosition> getPositions();
    
}
