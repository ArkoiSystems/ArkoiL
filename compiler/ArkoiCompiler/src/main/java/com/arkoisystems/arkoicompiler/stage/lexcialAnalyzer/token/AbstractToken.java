/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractToken
{
    
    /**
     * The {@link TokenType} is used to differentiate {@link AbstractToken}'s. Also it
     * helps when debugging with the {@link #toString()} method.
     */
    @Getter
    @Setter
    private TokenType tokenType;
    
    
    /**
     * The token content which got lexed. Used for later code generation or casting if
     * it's a {@link NumberToken}.
     */
    @Getter
    @Setter
    private String tokenContent;
    
    
    /**
     * The starting and ending position of this {@link AbstractToken}. This will help for
     * later debugging or syntax highlighting because we now the exact location.
     */
    @Getter
    @Setter
    private int start, end;
    
    
    /**
     * Lexes a new {@link AbstractToken} with the given {@link LexicalAnalyzer} which is
     * used to call methods like {@link LexicalAnalyzer#next(int)} or {@link
     * LexicalAnalyzer#peekChar(int)}.
     *
     * @param lexicalAnalyzer
     *         the {@link LexicalAnalyzer} which should get used for method calls like
     *         {@link LexicalAnalyzer#next(int)} or {@link LexicalAnalyzer#peekChar(int)}.
     *
     * @return {@code null} if an error occurred or itself/some other token if it lexed
     *         successfully.
     */
    public abstract AbstractToken lex(final LexicalAnalyzer lexicalAnalyzer);
    
    
    /**
     * Returns this class as a JSON based {@link String} with all exposed variables etc.
     * With this functionality you can better see problems or check each {@link
     * AbstractSyntaxAST} for it's correctness.
     *
     * @return this class as a JSON based {@link String}.
     */
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
