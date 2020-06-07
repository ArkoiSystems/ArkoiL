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
package com.arkoisystems.compiler.phases.lexer;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.api.IStage;
import com.arkoisystems.compiler.phases.lexer.token.LexerToken;
import com.arkoisystems.compiler.phases.lexer.token.enums.TokenType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Lexer implements IStage
{
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @NotNull
    private final List<LexerToken> tokens;
    
    private int lineIndex, charIndex;
    
    @Setter
    private boolean failed;
    
    public Lexer(final @NotNull CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
        
        this.tokens = new ArrayList<>();
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        final StringBuilder patternBuffer = new StringBuilder();
        for (final TokenType tokenType : TokenType.values())
            patternBuffer.append(String.format("|(?<%s>%s)", tokenType.name().replace("_", ""), tokenType.getRegex()));
        final Pattern tokenPatterns = Pattern.compile(patternBuffer.substring(1));
        
        final Matcher matcher = tokenPatterns.matcher(this.getCompilerClass().getContent());
        int lastPosition = 0;
        while (matcher.find()) {
            for (final TokenType tokenType : TokenType.values()) {
                if (matcher.group(tokenType.name().replace("_", "")) == null) continue;
                
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
    
}
