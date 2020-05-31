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
package com.arkoisystems.arkoicompiler.error;

import com.arkoisystems.arkoicompiler.CompilerClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ArkoiError
{
    
    @EqualsAndHashCode.Include
    @Getter
    @Nullable
    private final List<ErrorPosition> positions;
    
    @Getter
    @NotNull
    private final CompilerClass compilerClass;
    
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
    
        final StringBuilder stringBuilder = new StringBuilder(String.format(
                "%s:%s:%s: %s",
                compilerClass.getFilePath(),
                this.getPositions().get(0).getLineRange().getStartLine() + 1,
                this.getPositions().get(0).getCharStart() + 1,
                String.format(this.getMessage(),
                        this.getArguments())
        ));
    
        for (final ErrorPosition errorPosition : this.getPositions()) {
            stringBuilder.append("\r\n");
        
            final int startLine = errorPosition.getLineRange().getStartLine(), endLine = errorPosition.getLineRange().getEndLine();
            final String[] sourceLines = errorPosition.getLineRange().getSourceCode().split(System.getProperty("line.separator"));
            final int biggestNumber = String.valueOf(endLine + 1).length();
            for (int lineIndex = startLine; lineIndex < startLine + sourceLines.length; lineIndex++) {
                final LineRange lineRange = LineRange.make(this.getCompilerClass(), lineIndex, lineIndex);
                final String sourceCode = lineRange.getSourceCode().replace("\n", "");
            
                final int leadingSpaces = sourceCode.length() - sourceCode.replaceAll("^\\s+", "").length();
                final int trailingSpaces = sourceCode.length() - sourceCode.replaceAll("\\s+$", "").length();
            
                final String numberReplacement = " ".repeat(String.valueOf(endLine + 1).length());
                final String whitespacePrefix = " ".repeat(biggestNumber - String.valueOf(lineIndex + 1).length());
    
                int repeats = sourceCode.length() - leadingSpaces;
                if(repeats <= 0)
                    continue;
                stringBuilder.append("> ")
                        .append(whitespacePrefix)
                        .append(lineIndex + 1)
                        .append(" │ ")
                        .append(sourceCode)
                        .append("\r\n");
                if (lineIndex == startLine) {
                    repeats = startLine == endLine ? errorPosition.getCharEnd() - errorPosition.getCharStart() : (sourceCode.length() - errorPosition.getCharStart()) - trailingSpaces;
                    stringBuilder.append(whitespacePrefix)
                            .append(numberReplacement)
                            .append("   │ ")
                            .append(" ".repeat(errorPosition.getCharStart()))
                            .append("^")
                            .append("~".repeat(repeats - 1))
                            .append("\r\n");
                } else if (lineIndex == endLine) {
                    stringBuilder.append(whitespacePrefix)
                            .append(numberReplacement)
                            .append("   │ ")
                            .append(" ".repeat(leadingSpaces))
                            .append("^")
                            .append("~".repeat(errorPosition.getCharEnd() - leadingSpaces - 1))
                            .append("\r\n");
                } else {
                    stringBuilder.append(whitespacePrefix)
                            .append(numberReplacement)
                            .append("   │ ")
                            .append(" ".repeat(leadingSpaces))
                            .append("^")
                            .append("~".repeat(repeats - 1))
                            .append("\r\n");
                }
            }
        }
        return stringBuilder.toString();
    }
    
}
