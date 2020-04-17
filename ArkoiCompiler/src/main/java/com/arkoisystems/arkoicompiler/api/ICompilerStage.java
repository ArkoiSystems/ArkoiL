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
package com.arkoisystems.arkoicompiler.api;

import com.arkoisystems.arkoicompiler.api.error.IErrorHandler;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.api.utils.IResettable;
import org.jetbrains.annotations.NotNull;

public interface ICompilerStage extends IFailed, IResettable
{
    
    @NotNull
    IErrorHandler getErrorHandler();
    
    
    boolean processStage();
    
}
