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

import com.arkoisystems.alt.api.IError;
import com.arkoisystems.alt.api.ILexer;
import com.arkoisystems.alt.api.IToken;
import com.arkoisystems.alt.api.annotations.Token;
import com.arkoisystems.alt.lexer.LexerToken;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.KeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.OperatorToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.TypeToken;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArkoiLexer implements ICompilerStage, ILexer
{
    
    @NotNull
    private final List<IToken> tokens;
    
    @NonNull
    private final String lexerEBNF;
    
    @NotNull
    private ICompilerClass compilerClass;
    
    @NotNull
    private LexerErrorHandler errorHandler;
    
    private int line, startChar;
    
    private boolean failed;
    
    @NotNull
    private String input;
    
    @SneakyThrows
    public ArkoiLexer() {
        this.tokens = new ArrayList<>();
        this.lexerEBNF = new String(ArkoiLexer.class.getResourceAsStream("/grammar/lexer.ebnf")
                .readAllBytes());
        this.reset();
    }
    
    @Override
    public boolean processStage() {
        this.reset();
        this.getTokens().addAll(this.getCompilerClass().getLanguageTools().getTokens(
                this.getCompilerClass().getContent()
        ));
        return !this.isFailed();
    }
    
    @Override
    public void reset() {
        this.setErrorHandler(new LexerErrorHandler());
        this.getTokens().clear();
        this.setFailed(false);
        this.setStartChar(0);
        this.setLine(0);
    }
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    @Token(tokens = { "NEWLINE", "OTHERS" })
    @NotNull
    public ArkoiToken onWhiteSpace(@NotNull final LexerToken token) {
        if (token.getType().equals("NEWLINE")) {
            this.startChar = token.getCharStart();
            this.line++;
        }
        return new ArkoiToken(this, TokenType.WHITESPACE, token);
    }
    
    @Token(tokens = "COMMENT")
    @NotNull
    public ArkoiToken onComment(@NotNull final LexerToken token) {
        return new ArkoiToken(this, TokenType.COMMENT, token);
    }
    
    @Token(tokens = {
            "EQUALS",
            "PLUS_EQUALS", "PLUS_PLUS", "PLUS",
            "MINUS_EQUALS", "MINUS_MINUS", "MINUS",
            "ASTERISK_EQUALS", "ASTERISK_ASTERISK", "ASTERISK",
            "SLASH_EQUALS", "SLASH",
            "PERCENT_EQUALS", "PERCENT"
    })
    @NotNull
    public ArkoiToken onOperator(@NotNull final LexerToken token) {
        return new OperatorToken(this, TokenType.OPERATOR, token);
    }
    
    @Token(tokens = {
            "AT_SIGN",
            "CARET",
            "COLON",
            "OPENING_BRACE", "CLOSING_BRACE",
            "OPENING_PARENTHESIS", "CLOSING_PARENTHESIS",
            "OPENING_BRACKET", "CLOSING_BRACKET",
            "COMMA", "PERIOD",
            "OPENING_ARROW", "CLOSING_ARROW"
    })
    @NotNull
    public ArkoiToken onSymbol(@NotNull final LexerToken token) {
        return new SymbolToken(this, TokenType.SYMBOL, token);
    }
    
    @Token(tokens = {
            "THIS",
            "VAR",
            "RETURN",
            "FUN",
            "AS",
            "IMPORT"
    })
    @NotNull
    public ArkoiToken onKeyword(@NotNull final LexerToken token) {
        return new KeywordToken(this, TokenType.KEYWORD, token);
    }
    
    @Token(tokens = {
            "STRING", "CHAR",
            "BOOL",
            "INT", "BYTE", "LONG", "SHORT",
            "FLOAT", "DOUBLE"
    })
    @NotNull
    public ArkoiToken onToken(@NotNull final LexerToken token) {
        return new TypeToken(this, TokenType.TYPE, token);
    }
    
    @Token(tokens = {
            "NUMBER_LITERAL",
            "STRING_LITERAL",
            "CHARACTER_LITERAL"
    })
    @NonNull
    public ArkoiToken onLiteral(@NotNull final LexerToken token) {
        if (token.getType().equals("NUMBER_LITERAL")) {
            return new ArkoiToken(this, TokenType.NUMBER_LITERAL, token);
        } else if (token.getType().equals("STRING_LITERAL")) {
            if (token.getData().endsWith("\"") && token.getData().endsWith("\\\"")) {
                // TODO: 5/12/20 Handle error here
                //                    this.onError(ArkoiError.builder()
                //                            .message("The string has an invalid ending.")
                //                            .compilerClass(this.getCompilerClass())
                //                            .positions(List.of(ArkoiError.ErrorPosition.builder()
                //                                    .lineRange(ArkoiError.ErrorPosition.LineRange.builder()
                //                                            .sourceCode("Lelek")
                //                                            .startLine(0)
                //                                            .endLine(0)
                //                                            .build())
                //                                    .charStart(token.getCharStart())
                //                                    .charEnd(token.getCharEnd())
                //                                    .build()))
                //                            .build());
                // System.out.println(token.getData());
            }
            
            token.setData(token.getData().substring(1, token.getData().length() - 1));
            return new ArkoiToken(this, TokenType.STRING_LITERAL, token);
        } else throw new NullPointerException("Not implemented.");
    }
    
    @Token(tokens = "IDENTIFIER")
    @NotNull
    public ArkoiToken onIdentifier(@NotNull final LexerToken token) {
        return new ArkoiToken(this, TokenType.IDENTIFIER, token);
    }
    
    @Override
    public void onError(@NonNull final IError error) {
        // TODO: 5/13/20 Add to error list
        System.out.println(error);
        this.failed();
    }
    
}
