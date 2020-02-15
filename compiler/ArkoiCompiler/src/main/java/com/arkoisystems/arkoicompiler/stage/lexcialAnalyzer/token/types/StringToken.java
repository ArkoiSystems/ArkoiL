/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;

import java.util.regex.Matcher;

public class StringToken extends AbstractToken
{
    
    public StringToken(final String tokenContent, final int start, final int end) {
        super(TokenType.STRING_LITERAL, tokenContent, start, end);
    }
    
    @Override
    public AbstractToken parse(final Matcher matcher) {
        this.setTokenContent(this.getTokenContent().substring(1, this.getTokenContent().length() - 1));
        return this;
    }
    
}
