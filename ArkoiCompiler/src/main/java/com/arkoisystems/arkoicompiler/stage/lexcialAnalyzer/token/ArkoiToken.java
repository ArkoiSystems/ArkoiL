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
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.IToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class ArkoiToken implements IToken
{
    
    @Getter
    @Nullable
    private final LexicalAnalyzer lexicalAnalyzer;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @NotNull
    private final TokenType tokenType;
    
    
    @Getter
    private boolean failed;
    
    
    @EqualsAndHashCode.Include
    @Setter
    @Getter
    @NotNull
    private String tokenContent;
    
    
    @EqualsAndHashCode.Include
    @Getter
    private ArkoiError.ErrorPosition.LineRange lineRange;
    
    
    @EqualsAndHashCode.Include
    @Getter
    private int charStart, charEnd;
    
    
    @Getter
    private int startLine;
    
    
    public ArkoiToken(
            @Nullable final LexicalAnalyzer lexicalAnalyzer,
            @NotNull final TokenType tokenType,
            @Nullable final String tokenContent,
            final int startLine,
            final int charStart,
            final int endLine,
            final int charEnd
    ) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.tokenType = tokenType;
        
        this.setTokenContent(tokenContent == null ? "" : tokenContent);
        this.setCharStart(startLine, charStart);
        this.setCharEnd(endLine, charEnd);
    }
    
    
    public abstract @Nullable ArkoiToken parseToken();
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    @Override
    public <E> E addError(@Nullable E errorSource, @NotNull final ICompilerClass compilerClass, final int charIndex, final int lineNumber, @NotNull final String message, final Object... arguments) {
        compilerClass.getLexicalAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ArkoiError.ErrorPosition.builder()
                        .lineRange(ArkoiError.ErrorPosition.LineRange.make(compilerClass, lineNumber, lineNumber))
                        .charStart(charIndex)
                        .charEnd(charIndex + 1)
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
    
    public void setCharStart(final int startLine, final int charStart) {
        this.startLine = startLine;
        this.charStart = charStart;
    }
    
    
    public void setCharEnd(final int endLine, final int charEnd) {
        Objects.requireNonNull(this.getLexicalAnalyzer(), "lexicalAnalyzer must not be null.");
        
        this.charEnd = charEnd;
        this.lineRange = ArkoiError.ErrorPosition.LineRange.make(this.getLexicalAnalyzer().getCompilerClass(), this.startLine, endLine);
        
        Objects.requireNonNull(this.getLineRange(), "lineRange must not be null.");
        Objects.requireNonNull(this.getLineRange().getSourceCode(), "lineRange.sourceCode must not be null.");
        
        this.tokenContent = this.getLineRange().getSourceCode().substring(
                this.getCharStart(),
                this.getCharEnd()
        );
    }
    
}
