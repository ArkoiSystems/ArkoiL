package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;

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
public abstract class AbstractToken
{
    
    @Expose
    private TokenType tokenType;
    
    @Expose
    private String tokenContent;
    
    @Expose
    private final int start, end;
    
    public AbstractToken(final TokenType tokenType, final String tokenContent, final int start, final int end) {
        this.tokenContent = tokenContent;
        this.tokenType = tokenType;
        this.start = start;
        this.end = end;
    }
    
    public abstract AbstractToken parse(final Matcher matcher) throws Exception;
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
