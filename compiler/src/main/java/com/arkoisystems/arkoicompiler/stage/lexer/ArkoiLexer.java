/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.lexer;

import com.arkoisystems.alt.api.ILexer;
import com.arkoisystems.alt.api.IToken;
import com.arkoisystems.alt.api.annotations.Token;
import com.arkoisystems.alt.lexer.LexerToken;
import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.KeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.OperatorToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.TypeToken;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArkoiLexer implements ICompilerStage, ILexer
{
    
    @NotNull
    private ICompilerClass compilerClass;
    
    @NotNull
    private LexerErrorHandler errorHandler;
    
    private int line, startChar;
    
    @NotNull
    private List<IToken> tokens;
    
    private boolean failed;
    
    @NotNull
    private String input;
    
    public ArkoiLexer() {
        this.reset();
    }
    
    @Override
    public boolean processStage() {
        this.reset();
        this.setTokens(this.getCompilerClass().getLanguageTools().getLexerEvaluator().getTokens(
                this.getCompilerClass().getContent()
        ));
        if(!this.getCompilerClass().getLanguageTools().getLexerEvaluator().getErrors().isEmpty()) {
            System.out.println(this.getCompilerClass().getLanguageTools().getLexerEvaluator().getErrors());
            // TODO: 5/12/20 Add errors to the list.
            this.failed();
        }
        return !this.isFailed();
    }
    
    @Override
    public void reset() {
        this.setErrorHandler(new LexerErrorHandler());
        this.setTokens(new ArrayList<>());
        this.setFailed(false);
        this.setStartChar(0);
        this.setLine(0);
    }
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    @Token(ebnf = "NEWLINE = `\\n`;" +
            "      OTHERS  = `\\s`;",
            precedence = 7
    )
    @NotNull
    public ArkoiToken checkWhiteSpace(@NotNull final LexerToken token) {
        if (token.getType().equals("NEWLINE")) {
            this.startChar = token.getCharStart();
            this.line++;
        }
        return new ArkoiToken(this, TokenType.WHITESPACE, token);
    }
    
    @Token(ebnf = "COMMENT = `#[^\\r\\n]+`;", precedence = 6)
    @NotNull
    public ArkoiToken checkComment(@NotNull final LexerToken token) {
        return new ArkoiToken(this, TokenType.COMMENT, token);
    }
    
    @Token(ebnf = "EQUALS            = `=`;" +
            "      PLUS_EQUALS       = `\\+=`;" +
            "      PLUS_PLUS         = `\\+\\+`;" +
            "      PLUS              = `\\+`;" +
            "      MINUS_EQUALS      = `-=`;" +
            "      MINUS_MINUS       = `--`;" +
            "      MINUS             = `-`;" +
            "      ASTERISK_EQUALS   = `\\*=`;" +
            "      ASTERISK_ASTERISK = `\\*\\*`;" +
            "      ASTERISK          = `\\*`;" +
            "      SLASH_EQUALS      = `/=`;" +
            "      SLASH             = `=`;" +
            "      PERCENT_EQUALS    = `%=`;" +
            "      PERCENT           = `%`;",
            precedence = 5
    )
    @NotNull
    public ArkoiToken checkOperator(@NotNull final LexerToken token) {
        return new OperatorToken(this, TokenType.OPERATOR, token);
    }
    
    @Token(ebnf = "AT_SIGN             = `@`;" +
            "      CARET               = `\\^`;" +
            "      COLON               = `:`;" +
            "      OPENING_BRACE       = `\\{`;" +
            "      CLOSING_BRACE       = `\\}`;" +
            "      OPENING_PARENTHESIS = `\\(`;" +
            "      CLOSING_PARENTHESIS = `\\)`;" +
            "      OPENING_BRACKET     = `\\[`;" +
            "      CLOSING_BRACKET     = `\\]`;" +
            "      COMMA               = `,`;" +
            "      PERIOD              = `\\.`;" +
            "      OPENING_ARROW       = `<`;" +
            "      CLOSING_ARROW       = `>`;",
            precedence = 4
    )
    @NotNull
    public ArkoiToken checkSymbol(@NotNull final LexerToken token) {
        return new SymbolToken(this, TokenType.SYMBOL, token);
    }
    
    @Token(ebnf = "THIS   = `this`;" +
            "      VAR    = `var`;" +
            "      RETURN = `return`;" +
            "      FUN    = `fun`;" +
            "      AS     = `as`;" +
            "      IMPORT = `import`;",
            precedence = 3
    )
    @NotNull
    public ArkoiToken checkKeyword(@NotNull final LexerToken token) {
        return new KeywordToken(this, TokenType.KEYWORD, token);
    }
    
    @Token(ebnf = "CHAR   = `char`;" +
            "      BOOL   = `bool`;" +
            "      BYTE   = `byte`;" +
            "      INT    = `int`;" +
            "      LONG   = `long`;" +
            "      SHORT  = `short`;" +
            "      STRING = `string`;",
            precedence = 2
    )
    @NotNull
    public ArkoiToken checkToken(@NotNull final LexerToken token) {
        return new TypeToken(this, TokenType.TYPE, token);
    }
    
    @Token(ebnf = "NUMBER_LITERAL    = `(?:(?<hex>0[xX][a-zA-Z0-9]+)|(?:(?<fp>\\d\\.\\d*)|(?<int>\\d+))(?<sn>E[-+]\\d+)?)`;" + // Return different types
            "      STRING_LITERAL    = `\"(?:\"|[^\"]*[^\\\\]\"?)`;" +
            "      CHARACTER_LITERAL = `'(?:'|[^']*[^\\\\]')`;", // Check that just one character is inside
            precedence = 1
    )
    @NotNull
    public ArkoiToken checkLiteral(@NotNull final LexerToken token) {
        switch (token.getType()) {
            case "NUMBER_LITERAL":
                return new ArkoiToken(this, TokenType.NUMBER_LITERAL, token);
            case "STRING_LITERAL":
                if(token.getData().endsWith("\"") && token.getData().endsWith("\\\"")) {
                    // TODO: 5/12/20 Handle error here
                    this.failed();
                    System.out.println(token.getData());
                }
                token.setData(token.getData().substring(1, token.getData().length() - 1));
                return new ArkoiToken(this, TokenType.STRING_LITERAL, token);
            default:
                throw new NullPointerException("Not implemented.");
        }
    }
    
    @Token(ebnf = "IDENTIFIER = `[a-zA-Z][a-zA-Z0-9_]+`;")
    @NotNull
    public ArkoiToken checkIdentifier(@NotNull final LexerToken token) {
        return new ArkoiToken(this, TokenType.IDENTIFIER, token);
    }
    
}
