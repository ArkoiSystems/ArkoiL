package com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.*;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public enum TokenType
{
    
    WHITESPACE(WhiteSpaceToken.class, " \t\n"),
    COMMENT(CommentToken.class, "#.*"),
    
    STRING_LITERAL(StringToken.class, "\"(?:.|(?:\"))*\""),
    NUMBER_LITERAL(AbstractNumberToken.class, AbstractNumberToken.NumberType.NUMBER_PATTERN),
    
    // TODO: Add BOOLEAN_LITERAL and CHAR_LITERAL
    IDENTIFIER(IdentifierToken.class, "[a-zA-Z]+[a-zA-Z_0-9]*"),
    
    SYMBOL(SymbolToken.class, "(@|:|;|\\{|\\}|\\(|\\)|\\[|\\]|,|\\.|<|>|\\+|-|\\*|/|%|!|=|&)"),
    
    END_OF_FILE(EndOfFileToken.class, null);
    
    
    public static final Pattern PATTERN_ENGINE;
    
    
    public final Class<? extends AbstractToken> tokenCreator;
    
    private final String pattern;
    
    
    TokenType(final Class<? extends AbstractToken> tokenCreator, final String pattern) {
        this.tokenCreator = tokenCreator;
        this.pattern = pattern;
    }
    
    public String getGroupName() {
        return this.name().replace("_", "");
    }
    
    public static AbstractToken createToken(final Matcher matcher) throws Exception {
        String tokenContent;
        for (final TokenType tokenType : TokenType.values()) {
            if(tokenType.getTokenCreator() == null || tokenType.getPattern() == null)
                continue;
            
            if ((tokenContent = matcher.group(tokenType.getGroupName())) != null)
                return tokenType.getTokenCreator()
                        .getDeclaredConstructor(String.class, int.class, int.class)
                        .newInstance(tokenContent, matcher.start(), matcher.end())
                        .parse(matcher);
        }
        return null;
    }
    
    static {
        final StringBuilder tokenPatterns = new StringBuilder();
        for (final TokenType tokenType : TokenType.values())
            tokenPatterns.append(String.format("|(?<%s>%s)", tokenType.getGroupName(), tokenType.getPattern()));
        PATTERN_ENGINE = Pattern.compile(tokenPatterns.toString().substring(1));
    }
    
}
