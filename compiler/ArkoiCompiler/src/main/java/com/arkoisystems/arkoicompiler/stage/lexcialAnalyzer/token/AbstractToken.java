/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractToken
{
    
    @Expose
    private TokenType tokenType;
    
    @Expose
    private String tokenContent;
    
    @Expose
    private final int start, end;
    
    public abstract AbstractToken parse(final Matcher matcher) throws Exception;
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
