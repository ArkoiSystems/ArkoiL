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
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class SymbolToken extends ArkoiToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private SymbolType symbolType;
    
    @Builder
    public SymbolToken(
            @Nullable final LexicalAnalyzer lexicalAnalyzer,
            @NotNull final String tokenContent,
            @Nullable final SymbolType symbolType,
            final int startLine,
            final int charStart,
            final int endLine,
            final int charEnd
    ) {
        super(lexicalAnalyzer, TokenType.SYMBOL, tokenContent, startLine, charStart, endLine, charEnd);
        
        this.setSymbolType(symbolType);
    }
    
    @Override
    public @NotNull ArkoiToken parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer(), "lexicalAnalyzer must not be null.");
        
        this.setCharStart(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex());
        this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        this.setTokenContent(Objects.requireNonNull(this.getLineRange().getSourceCode(), "lineRange.sourceCode must not be null.").substring(
                this.getCharStart(),
                this.getCharEnd()
        ));
        
        final char currentChar = this.getLexicalAnalyzer().currentChar();
        
        final SymbolType symbolType = Arrays.stream(SymbolType.values())
                .filter(typeToCheck -> typeToCheck.getCharacter() == currentChar)
                .findAny()
                .orElse(null);
        if (symbolType == null)
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
                    
                    "Couldn't lex this symbol because it isn't supported."
            );
        
        this.getLexicalAnalyzer().next();
        this.setSymbolType(symbolType);
        return this;
    }
    
}
