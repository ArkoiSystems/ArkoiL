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
package com.arkoisystems.compiler.phases.lexer.token.types;

import com.arkoisystems.compiler.error.CompilerError;
import com.arkoisystems.compiler.error.ErrorPosition;
import com.arkoisystems.compiler.phases.lexer.Lexer;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import lombok.Builder;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class StringToken extends LexerToken
{
    
    @Builder
    public StringToken(
            @NonNull
            @NotNull final Lexer lexer,
            final int startLine,
            final int endLine,
            final int charStart,
            final int charEnd
    ) {
        super(lexer, TokenType.STRING, startLine, endLine, charStart, charEnd);
    
        if(this.getTokenContent().length() < 2 || this.getTokenContent().endsWith("\\\"") || !this.getTokenContent().endsWith("\"")) {
            this.getLexer().setFailed(true);
            this.getLexer().getCompilerClass().getCompiler().getErrorHandler().addError(CompilerError.builder()
                    .causePosition(ErrorPosition.builder()
                            .compilerClass(this.getLexer().getCompilerClass())
                            .lineRange(this.getLineRange())
                            .charStart(this.getCharStart())
                            .charEnd(this.getCharEnd())
                            .build())
                    .causeMessage("A string must be terminated correctly.")
                    .build());
            return;
        }
    
        this.setTokenContent(StringToken.unescapeString(
                this.getTokenContent().substring(1, this.getTokenContent().length() - 1)
        ));
    }
    
    @NotNull
    public static String unescapeString(@NotNull final String input) {
        final StringBuilder resultBuilder = new StringBuilder();
        for (int index = 0; index < input.length(); index++) {
            char currentChar = input.charAt(index);
            if (currentChar == '\\') {
                final char nextChar = input.length() - 1 == index ? '\\' : input.charAt(index + 1);
                if (nextChar >= '0' && nextChar <= '7') {
                    final StringBuilder octalBuilder = new StringBuilder();
                    
                    int octalIndex = 0;
                    for (; octalIndex < 3; octalIndex++) {
                        final int charIndex = octalIndex + index + 1;
                        if (charIndex > input.length() - 1)
                            break;
                        
                        final char octalChar = input.charAt(index + 1 + octalIndex);
                        octalBuilder.append(octalChar);
                    }
                    index += octalIndex;
                    
                    resultBuilder.append((char) Integer.parseInt(octalBuilder.toString(), 8));
                    continue;
                } else if (nextChar == 'u') {
                    if (index <= input.length() - 5) {
                        final int code = Integer.parseInt("" +
                                input.charAt(index + 2) +
                                input.charAt(index + 3) +
                                input.charAt(index + 4) +
                                input.charAt(index + 5), 16);
                        resultBuilder.append(Character.toChars(code));
                        index += 5;
                        continue;
                    }
                }
                
                switch (nextChar) {
                    case '\\':
                        currentChar = '\\';
                        break;
                    case 'b':
                        currentChar = '\b';
                        break;
                    case 'n':
                        currentChar = '\n';
                        break;
                    case 'f':
                        currentChar = '\f';
                        break;
                    case 'r':
                        currentChar = '\r';
                        break;
                    case 't':
                        currentChar = '\t';
                        break;
                    case '"':
                        currentChar = '"';
                        break;
                    case '\'':
                        currentChar = '\'';
                        break;
                }
                
                index++;
            }
            
            resultBuilder.append(currentChar);
        }
        return resultBuilder.toString();
    }
    
    /**
     * Unescapes a string that contains standard Java escape sequences.
     * <ul>
     * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
     * BS, FF, NL, CR, TAB, double and single quote.</li>
     * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
     * specification (0 - 377, 0x00 - 0xFF).</li>
     * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode character.</li>
     * </ul>
     *
     * @param st
     *         A string optionally containing standard java escape sequences.
     *
     * @return The translated string.
     */
    public static String unescapeJavaString(String st) {
        
        StringBuilder sb = new StringBuilder(st.length());
        
        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                        + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
    
}
