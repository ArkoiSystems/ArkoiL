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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ArkoiError implements ICompilerError
{
    
    @EqualsAndHashCode.Include
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
    public String toString() {
        Objects.requireNonNull(this.getMessage(), "message must not be null.");
        Objects.requireNonNull(this.getPositions(), "positions must not be null.");
        Objects.requireNonNull(this.getCompilerClass(), "compilerClass must not be null.");
    
        final StringBuilder stringBuilder = new StringBuilder("[" + Variables.DATE_FORMAT.format(new Date()) + "/INFO] " + String.format(this.getMessage(), this.getArguments()));
        for (final ErrorPosition errorPosition : this.getPositions()) {
            stringBuilder.append("\r\n");
    
            Objects.requireNonNull(errorPosition.getLineRange(), "errorPosition.lineRange must not be null.");
            Objects.requireNonNull(errorPosition.getLineRange().getSourceCode(), "errorPosition.lineRange.sourceLines must not be null.");
    
            final int startLine = errorPosition.getLineRange().getStartLine(), endLine = errorPosition.getLineRange().getEndLine();
            final int biggestNumber = String.valueOf(endLine + 2).length();
            for (int lineIndex = Math.max(startLine - 2, 0); lineIndex < startLine; lineIndex++) {
                final ErrorPosition.LineRange lineRange = ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex);
                Objects.requireNonNull(lineRange.getSourceCode(), "lineRange.sourceCode must not be null.");
                stringBuilder.append("  ")
                        .append(" ".repeat(biggestNumber - String.valueOf(lineIndex).length()))
                        .append(lineIndex)
                        .append(" │ ")
                        .append(lineRange.getSourceCode().replace("\n", ""))
                        .append("\r\n");
            }
    
            final String[] sourceLines = errorPosition.getLineRange().getSourceCode().split(System.getProperty("line.separator"));
            for (int lineIndex = startLine; lineIndex < startLine + sourceLines.length; lineIndex++) {
                final ErrorPosition.LineRange lineRange = ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex);
                Objects.requireNonNull(lineRange.getSourceCode(), "lineRange.sourceCode must not be null.");
                final String sourceCode = lineRange.getSourceCode().replace("\n", "");
        
                final int leadingSpaces = sourceCode.length() - sourceCode.replaceAll("^\\s+", "").length();
                final int trailingSpaces = sourceCode.length() - sourceCode.replaceAll("\\s+$", "").length();
                final int maxLength = Math.max(0, sourceCode.length() - (leadingSpaces + trailingSpaces));
                if (maxLength == 0)
                    continue;
            
                final String numberReplacement = " ".repeat(String.valueOf(lineIndex).length());
                final String whitespacePrefix = " ".repeat(biggestNumber - String.valueOf(lineIndex).length());
        
                stringBuilder.append("> ")
                        .append(whitespacePrefix)
                        .append(lineIndex)
                        .append(" │ ")
                        .append(sourceCode)
                        .append("\r\n");
                if (lineIndex == startLine) {
                    stringBuilder.append(whitespacePrefix)
                            .append(numberReplacement)
                            .append("   │ ")
                            .append(" ".repeat(errorPosition.getCharStart()))
                            .append("^".repeat(startLine == endLine ? errorPosition.getCharEnd() - errorPosition.getCharStart() : (sourceCode.length() - errorPosition.getCharStart()) - trailingSpaces))
                            .append("\r\n");
                } else if (lineIndex == endLine) {
                    stringBuilder.append(whitespacePrefix)
                            .append(numberReplacement)
                            .append("   │ ")
                            .append(" ".repeat(leadingSpaces))
                            .append("^".repeat(errorPosition.getCharEnd()))
                            .append("\r\n");
                } else {
                    stringBuilder.append(whitespacePrefix)
                            .append(numberReplacement)
                            .append("   │ ")
                            .append(" ".repeat(leadingSpaces))
                            .append("^".repeat(maxLength))
                            .append("\r\n");
                }
            }
        
            for (int lineIndex = endLine + 1; lineIndex < Math.max(endLine + 3, sourceLines.length - 1); lineIndex++) {
                final ErrorPosition.LineRange lineRange = ErrorPosition.LineRange.make(this.getCompilerClass(), lineIndex, lineIndex);
                Objects.requireNonNull(lineRange.getSourceCode(), "lineRange.sourceCode must not be null.");
                stringBuilder.append("  ")
                        .append(" ".repeat(biggestNumber - String.valueOf(lineIndex).length()))
                        .append(lineIndex)
                        .append(" │ ")
                        .append(lineRange.getSourceCode().replace("\n", ""))
                        .append("\r\n");
            }
        }
        return stringBuilder.toString();
    }
    
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    public static class ErrorPosition
    {
        
        @EqualsAndHashCode.Include
        @Nullable
        @Getter
        private final LineRange lineRange;
        
        @EqualsAndHashCode.Include
        @Getter
        private final int charStart, charEnd;
        
        @EqualsAndHashCode(onlyExplicitlyIncluded = true)
        @Builder
        public static class LineRange
        {
            
            @EqualsAndHashCode.Include
            @Nullable
            @Getter
            private final String sourceCode;
            
            @EqualsAndHashCode.Include
            @Getter
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
