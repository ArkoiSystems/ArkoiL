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
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.error.ICompilerError;
import com.arkoisystems.arkoicompiler.utils.Variables;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ArkoiError implements ICompilerError
{
    
    @Getter
    @Nullable
    private final List<ErrorPosition> positions;
    
    
    @Getter
    @NotNull
    private final ICompilerClass compilerClass;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @Nullable
    private final Object[] arguments;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @Nullable
    private final String message;
    
    
    @Override
    public @NotNull
    String getFinalError() {
        Objects.requireNonNull(this.getMessage(), "message must not be null.");
        Objects.requireNonNull(this.getPositions(), "positions must not be null.");
        Objects.requireNonNull(this.getCompilerClass(), "compilerClass must not be null.");
    
        final StringBuilder stringBuilder = new StringBuilder("[" + Variables.DATE_FORMAT.format(new Date()) + "/INFO] " + String.format(this.getMessage(), this.getArguments()));
        for (final ErrorPosition errorPosition : this.getPositions()) {
            Objects.requireNonNull(errorPosition.getLineRange(), "errorPosition.lineRange must not be null.");
            Objects.requireNonNull(errorPosition.getLineRange().getSourceCode(), "errorPosition.lineRange.sourceCode must not be null.");
            
            stringBuilder.append("\r\n");
    
            final int startLine = errorPosition.getLineRange().getStartLine(), endLine = errorPosition.getLineRange().getEndLine();
            
            for (int lineIndex = Math.max(startLine - 2, 0); lineIndex < startLine; lineIndex++)
                stringBuilder.append("  ").append(lineIndex).append(" │ ").append(ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex).getSourceCode());
            
            final String[] sourceCode = errorPosition.getLineRange().getSourceCode().split(System.getProperty("line.separator"));
            for(int lineIndex = startLine; lineIndex < startLine + sourceCode.length; lineIndex++)
                stringBuilder.append("> ").append(lineIndex).append(" │ ").append(ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex).getSourceCode());
            stringBuilder.append("    │ ");
            stringBuilder.append(" ".repeat(Math.max(0, errorPosition.getCharStart())));
            stringBuilder.append("^".repeat(Math.max(1, errorPosition.getCharEnd() - errorPosition.getCharStart())));
            stringBuilder.append("\r\n");
            
            for (int lineIndex = endLine + 1; lineIndex < endLine + 3; lineIndex++)
                stringBuilder.append("  ").append(lineIndex).append(" │ ").append(ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex).getSourceCode());
        }
        return stringBuilder.toString();
    }
    
    
    @Override
    public String toString() {
        return this.getFinalError();
    }
    
    
    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class ErrorPosition
    {
        
        @Nullable
        private final LineRange lineRange;
        
        
        private final int charStart, charEnd;
        
        
        @Getter
        @Setter
        @AllArgsConstructor
        @Builder
        public static class LineRange
        {
            
            @Nullable
            private final String sourceCode;
            
            
            private final int startLine, endLine;
            
            
            @NonNull
            public static LineRange make(final ICompilerClass compilerClass, final int startLine, final int endLine) {
                final String[] sourceSplit = new String(compilerClass.getContent()).split(System.getProperty("line.separator"));
                final StringBuilder sourceBuilder = new StringBuilder();
                for (int index = 0; index < sourceSplit.length; index++) {
                    if (index < startLine) continue;
                    if (index > endLine) break;
                    sourceBuilder.append(sourceSplit[index]).append(System.getProperty("line.separator"));
                }
                return LineRange.builder()
                        .sourceCode(sourceBuilder.toString())
                        .startLine(startLine)
                        .endLine(endLine)
                        .build();
            }
            
        }
        
    }
    
}
