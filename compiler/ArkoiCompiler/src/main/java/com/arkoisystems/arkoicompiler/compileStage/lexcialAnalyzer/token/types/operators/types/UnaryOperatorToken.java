package com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.AbstractOperatorToken;
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
public class UnaryOperatorToken extends AbstractOperatorToken
{
    
    @Expose
    private UnaryOperatorType unaryOperatorType;
    
    public UnaryOperatorToken(final String tokenContent, final int start, final int end) {
        super(TokenType.UNARY_OPERATOR, tokenContent, start, end);
    }
    
    @Override
    public AbstractToken parse(final Matcher matcher) {
        switch (this.getTokenContent()) {
            case "++":
                this.setUnaryOperatorType(UnaryOperatorType.INCREMENT);
                break;
            case "--":
                this.setUnaryOperatorType(UnaryOperatorType.DECREMENT);
                break;
            case "!":
                this.setUnaryOperatorType(UnaryOperatorType.LOGICAL_NEGATION);
                break;
        }
        return this;
    }
    
    public enum UnaryOperatorType
    {
        
        INCREMENT,
        DECREMENT,
        LOGICAL_NEGATION,
        
    }
    
}
