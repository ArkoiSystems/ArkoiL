/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractToken
{
    
    @Expose
    private TokenType tokenType;
    
    @Expose
    private String tokenContent;
    
    @Expose
    private int start, end;
    
    public abstract AbstractToken parse(final LexicalAnalyzer lexicalAnalyzer);
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
