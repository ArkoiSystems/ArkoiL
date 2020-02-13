package com.arkoisystems.arkoicompiler.stage.errorHandler;

import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public abstract class AbstractError
{
    
    @Expose
    private final String message;
    
    @Expose
    private final int start, end;
    
    public AbstractError(final int start, final int end, final String message, final Object... arguments) {
        this.start = start;
        this.end = end;
    
        this.message = String.format(message, arguments);
    }
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
