/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;

import java.util.regex.Matcher;

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