/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.*;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public enum TokenType
{
    
    WHITESPACE(WhiteSpaceToken.class, "\\s"),
    COMMENT(CommentToken.class, "#.*"),
    
    SYMBOL(SymbolToken.class, "(@|:|;|\\{|\\}|\\(|\\)|\\[|\\]|,|\\.|<|>|\\+|-|\\*|/|%|!|=|&)"),
    
    STRING_LITERAL(StringToken.class, "\"(?:.|(?:\"))*\""),
    NUMBER_LITERAL(AbstractNumberToken.class, AbstractNumberToken.NumberType.NUMBER_PATTERN),
    
    // TODO: Add BOOLEAN_LITERAL and CHAR_LITERAL
    IDENTIFIER(IdentifierToken.class, "[a-zA-Z]+[a-zA-Z_0-9]*"),
    
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
