/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;

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
