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
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CommentToken extends ArkoiToken
{
    
    @Builder
    public CommentToken(
            @Nullable final LexicalAnalyzer lexicalAnalyzer,
            @Nullable final String tokenContent,
            final int startLine,
            final int charStart,
            final int endLine,
            final int charEnd
    ) {
        super(lexicalAnalyzer, TokenType.COMMENT, tokenContent, startLine, charStart, endLine, charEnd);
    }
    
    @Override
    public @NotNull ArkoiToken parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer(), "lexicalAnalyzer must not be null.");
        
        if (this.getLexicalAnalyzer().currentChar() != '#')
            return this.addError(
                    BadToken.builder()
                            .lexicalAnalyzer(this.getLexicalAnalyzer())
                            .startLine(this.getLexicalAnalyzer().getLineIndex())
                            .charStart(this.getLexicalAnalyzer().getCharIndex())
                            .endLine(this.getLexicalAnalyzer().getLineIndex())
                            .charEnd(this.getLexicalAnalyzer().getCharIndex() + 1)
                            .build()
                            .parseToken(),
                    
                    this.getLexicalAnalyzer().getCompilerClass(),
                    
                    this.getLexicalAnalyzer().getCharIndex(),
                    this.getLexicalAnalyzer().getLineIndex(),
                    
                    "Couldn't lex this comment because it doesn't start with an \"#\"."
            );
        
        this.setCharStart(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex());
        while (this.getLexicalAnalyzer().getPosition() < this.getLexicalAnalyzer().getCompilerClass().getContent().length) {
            this.getLexicalAnalyzer().next();
            if (this.getLexicalAnalyzer().currentChar() == 0x0a)
                break;
        }
        this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex());
        return this;
    }
    
}