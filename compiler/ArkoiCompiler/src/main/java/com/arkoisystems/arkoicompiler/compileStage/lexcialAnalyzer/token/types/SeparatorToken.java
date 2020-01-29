package com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
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
public class SeparatorToken extends AbstractToken
{
    
    @Expose
    private SeparatorType separatorType;
    
    public SeparatorToken(final String tokenContent, final int start, final int end) {
        super(TokenType.SEPARATOR, tokenContent, start, end);
    }
    
    @Override
    public AbstractToken parse(final Matcher matcher) {
        switch (this.getTokenContent().charAt(0)) {
            case '@':
                this.setSeparatorType(SeparatorType.AT_SIGN);
                break;
            case ':':
                this.setSeparatorType(SeparatorType.COLON);
                break;
            case ';':
                this.setSeparatorType(SeparatorType.SEMICOLON);
                break;
            case '{':
                this.setSeparatorType(SeparatorType.OPENING_BRACE);
                break;
            case '}':
                this.setSeparatorType(SeparatorType.CLOSING_BRACE);
                break;
            case '(':
                this.setSeparatorType(SeparatorType.OPENING_PARENTHESIS);
                break;
            case ')':
                this.setSeparatorType(SeparatorType.CLOSING_PARENTHESIS);
                break;
            case '[':
                this.setSeparatorType(SeparatorType.OPENING_BRACKET);
                break;
            case ']':
                this.setSeparatorType(SeparatorType.CLOSING_BRACKET);
                break;
            case ',':
                this.setSeparatorType(SeparatorType.COMMA);
                break;
            case '.':
                this.setSeparatorType(SeparatorType.PERIOD);
                break;
            case '>':
                this.setSeparatorType(SeparatorType.GREATER_THAN_SIGN);
                break;
            case '<':
                this.setSeparatorType(SeparatorType.LESS_THAN_SIGN);
                break;
        }
        return this;
    }
    
    public enum SeparatorType
    {
        
        AT_SIGN,
    
        COLON,
        SEMICOLON,
        
        OPENING_BRACE,
        CLOSING_BRACE,
        
        OPENING_PARENTHESIS,
        CLOSING_PARENTHESIS,
        
        OPENING_BRACKET,
        CLOSING_BRACKET,
        
        COMMA,
        PERIOD,
        
        LESS_THAN_SIGN,
        GREATER_THAN_SIGN
        
    
    }
    
}
