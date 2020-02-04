package com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.google.gson.annotations.Expose;

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
public class CommentToken extends AbstractToken
{
    
    public CommentToken(final String tokenContent, final int start, final int end) {
        super(TokenType.COMMENT, tokenContent, start, end);
    }
    
    @Override
    public AbstractToken parse(final Matcher matcher) {
        this.setTokenContent(matcher.group(0));
        return this;
    }
    
}