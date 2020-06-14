/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 25, 2020
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
package com.arkoisystems.compiler.phases.lexer.token.enums;

import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.types.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public enum TokenType
{
    
    NEWLINE("\\n", NewlineToken.class),
    WHITESPACE("\\s", WhitespaceToken.class),
    COMMENT("#[^\\r\\n]*", CommentToken.class),
    KEYWORD("(?:this|var|return|struct|fun|as|import|const)", KeywordToken.class),
    TYPE("(?:bool|(?<i>[ui]\\d+)|float|double|void|\\.\\.\\.)", TypeToken.class),
    OPERATOR("(?:=|\\+=|\\+\\+|\\+|-=|--|-|\\*=|\\*|/=|\\/|%=|%)", OperatorToken.class),
    SYMBOL("(?:@|^|:|\\{|\\}|\\(|\\)|\\[|\\]|,|\\.|<|>)", SymbolToken.class),
    STRING("\"(?:\\\\\"|[^\\n\"])*\\\"?", StringToken.class),
    NUMBER("(?:(?<hex>0[xX][a-zA-Z0-9]*)|(?:(?<fp>(?:\\d_|\\d)*\\.\\d*)|(?<int>(?:\\d_|\\d)+))(?<sn>E[-+]\\d+)?)", NumberToken.class),
    IDENTIFIER("[a-zA-Z][a-zA-Z0-9_]*", IdentifierToken.class),
    UNDEFINED(".", UndefinedToken.class);
    
    @NotNull
    private final String regex;
    
    private final Class<? extends LexerToken> tokenClass;
    
}
