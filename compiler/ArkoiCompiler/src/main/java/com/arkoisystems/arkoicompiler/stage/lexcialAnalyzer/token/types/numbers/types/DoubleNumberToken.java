/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import lombok.Getter;

@Getter
public class DoubleNumberToken extends AbstractNumberToken
{
    
    public DoubleNumberToken(String tokenContent, final int start, final int end) throws NumberFormatException {
        super(tokenContent, start, end);
    
        this.setNumberType(NumberType.DOUBLE);
    }
    
}
