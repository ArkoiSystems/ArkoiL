package com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.numbers;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.numbers.types.*;
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
public class AbstractNumberToken extends AbstractToken
{
    
    @Expose
    private NumberType numberType;
    
    public AbstractNumberToken(final String tokenContent, final int start, final int end) {
        super(TokenType.NUMBER_LITERAL, tokenContent, start, end);
    }
    
    @Override
    public AbstractToken parse(final Matcher matcher) throws Exception {
        NumberType numberType = null;
        
        numberType_loop:
        for (final NumberType type : NumberType.values()) {
            if (type.getPrefix() != null) {
                for (final String prefix : type.getPrefix())
                    if (this.getTokenContent().startsWith(prefix)) {
                        numberType = type;
                        break numberType_loop;
                    }
            }
            
            if (type.getSuffix() != null) {
                for (final String suffix : type.getSuffix())
                    if (this.getTokenContent().endsWith(suffix)) {
                        numberType = type;
                        break numberType_loop;
                    }
            }
        }
        
        if(numberType == null) {
            if(this.getTokenContent().contains(".")) numberType = NumberType.FLOAT;
            else numberType = NumberType.INTEGER;
        }
        
        return numberType.getNumberCreator()
                .getDeclaredConstructor(String.class, int.class, int.class)
                .newInstance(this.getTokenContent(), matcher.start(), matcher.end());
    }
    
    @Getter
    public enum NumberType
    {
        
        HEXADECIMAL(HexadecimalToken.class, "(?:0x[0-9A-Fa-f]{1,8})", new String[] { "0x" }, null),
        INTEGER(IntegerNumberToken.class, null, null, new String[] { "i", "I" }),
        FLOAT(FloatNumberToken.class, null, null, new String[] { "f", "F" }),
        DOUBLE(DoubleNumberToken.class, null, null, new String[] { "d", "D" }),
        SHORT(ShortNumberToken.class, null, null, new String[] { "s", "S" }),
        BYTE(ByteNumberToken.class, null, null, new String[] { "b", "B" });
        
        
        public static String NUMBER_PATTERN;
        
        
        private final Class<? extends AbstractNumberToken> numberCreator;
        
        private final String pattern;
        
        private final String[] prefix, suffix;
        
        
        NumberType(final Class<? extends AbstractNumberToken> numberCreator, final String pattern, final String[] prefix, final String[] suffix) {
            this.numberCreator = numberCreator;
            this.pattern = pattern;
            this.prefix = prefix;
            this.suffix = suffix;
        }
        
        static {
            final StringBuilder numberSuffixes = new StringBuilder();
            final StringBuilder numberPattern = new StringBuilder("(?:");
            
            for (final NumberType numberType : values()) {
                if (numberType.getPattern() == null && numberType.getSuffix() != null) {
                    for (final String suffix : numberType.getSuffix())
                        numberSuffixes.append(suffix);
                } else if (numberType.getPattern() != null)
                    numberPattern.append(numberType.getPattern()).append("|");
            }
    
            numberPattern
                    .append("(?:[0-9]+\\.[0-9]*[")
                    .append(numberSuffixes.toString())
                    .append("]?)|(?:[0-9]*\\.[0-9]+[")
                    .append(numberSuffixes.toString())
                    .append("]?)|(?:[0-9]+[")
                    .append(numberSuffixes.toString())
                    .append("]?)")
                    .append(")");
    
            //            numberPattern
            //                    .append("(?:-?[0-9]+\\.[0-9]*[")
            //                    .append(numberSuffixes.toString())
            //                    .append("]?)|(?:-?[0-9]*\\.[0-9]+[")
            //                    .append(numberSuffixes.toString())
            //                    .append("]?)|(?:-?[0-9]+[")
            //                    .append(numberSuffixes.toString())
            //                    .append("]?)")
            //                    .append(")");
    
            NUMBER_PATTERN = numberPattern.toString();
        }
        
    }
    
}