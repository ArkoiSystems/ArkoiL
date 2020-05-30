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
package com.arkoisystems.arkoicompiler.stage.lexer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.IStage;
import com.arkoisystems.arkoicompiler.error.ArkoiError;
import com.arkoisystems.arkoicompiler.error.ErrorPosition;
import com.arkoisystems.arkoicompiler.error.LineRange;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class Lexer implements IStage
{
    
    @Getter
    @NotNull
    private final ArkoiClass compilerClass;
    
    @NotNull
    private LexerErrorHandler errorHandler = new LexerErrorHandler();
    
    private int lineIndex, charIndex;
    
    @NotNull
    private List<ArkoiToken> tokens;
    
    private boolean failed;
    
    public Lexer(final @NotNull ArkoiClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.reset();
        
        final StringBuilder patternBuffer = new StringBuilder();
        for (final TokenType tokenType : TokenType.values())
            patternBuffer.append(String.format("|(?<%s>%s)", tokenType.name().replace("_", ""), tokenType.getRegex()));
        final Pattern tokenPatterns = Pattern.compile(patternBuffer.substring(1));
        
        final Matcher matcher = tokenPatterns.matcher(this.getCompilerClass().getContent());
        int lastPosition = 0;
        while (matcher.find()) {
            for (final TokenType tokenType : TokenType.values()) {
                if (matcher.group(tokenType.name().replace("_", "")) == null) continue;
    
                if (tokenType == TokenType.UNDEFINED) {
                    this.getErrorHandler().addError(ArkoiError.builder()
                            .compilerClass(compilerClass)
                            .positions(Collections.singletonList(ErrorPosition.builder()
                                    .lineRange(LineRange.make(this.getCompilerClass(), this.getLineIndex(), this.getLineIndex()))
                                    .charStart(this.charIndex + matcher.start() - lastPosition)
                                    .charEnd(this.charIndex + matcher.end() - lastPosition)
                                    .build()))
                            .message("This character is unknown to the lexer: %s")
                            .arguments(new Object[] {
                                    this.getCompilerClass().getContent().substring(
                                            matcher.start(),
                                            matcher.end()
                                    ).trim()
                            })
                            .build()
                    );
                    lastPosition = matcher.end();
                    break;
                }
                
                this.getTokens().add(tokenType.getTokenClass().getConstructor(
                        Lexer.class, int.class, int.class, int.class, int.class
                ).newInstance(
                        this,
                        this.getLineIndex(),
                        this.getLineIndex(),
                        this.charIndex + matcher.start() - lastPosition,
                        this.charIndex + matcher.end() - lastPosition
                ));
                lastPosition = matcher.end();
                
                if (tokenType == TokenType.NEWLINE) {
                    this.charIndex = 0;
                    this.lineIndex++;
                } else this.charIndex += matcher.end() - matcher.start();
            }
        }
        return !this.isFailed();
    }
    
    @Override
    public void reset() {
        this.setErrorHandler(new LexerErrorHandler());
        this.setTokens(new ArrayList<>());
        this.setFailed(false);
        
        this.setCharIndex(0);
        this.setLineIndex(0);
    }
    
}
