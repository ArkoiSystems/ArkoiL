package com.arkoisystems.compiler.error;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
public class ErrorPosition
{
    
    @EqualsAndHashCode.Include
    @NotNull
    private final LineRange lineRange;
    
    @EqualsAndHashCode.Include
    @NotNull
    private final String sourceCode, filePath;
    
    @EqualsAndHashCode.Include
    private final int charStart, charEnd;
    
    public void toString(
            @NotNull final StringBuilder stringBuilder,
            @NotNull final String indent
    ) {
        final int startLine = this.getLineRange().getStartLine(), endLine = this.getLineRange().getEndLine();
        final String[] sourceLines = this.getLineRange().getSourceCode().split(System.getProperty("line.separator"));
        final int biggestNumber = String.valueOf(endLine + 1).length();
        for (int lineIndex = startLine; lineIndex < startLine + sourceLines.length; lineIndex++) {
            final LineRange lineRange = LineRange.make(this.getSourceCode(), lineIndex, lineIndex);
            final String sourceCode = lineRange.getSourceCode().replace("\n", "");
            
            final int leadingSpaces = sourceCode.length() - sourceCode.replaceAll("^\\s+", "").length();
            final int trailingSpaces = sourceCode.length() - sourceCode.replaceAll("\\s+$", "").length();
            
            final String numberReplacement = " ".repeat(biggestNumber);
            final String whitespacePrefix = " ".repeat(biggestNumber - String.valueOf(lineIndex + 1).length());
            
            int repeats = sourceCode.length() - leadingSpaces;
            if (repeats <= 0)
                continue;
    
            stringBuilder.append(indent)
                    .append("> ")
                    .append(whitespacePrefix)
                    .append(lineIndex + 1)
                    .append(" │ ")
                    .append(sourceCode)
                    .append("\r\n");
            if (lineIndex == startLine) {
                repeats = startLine == endLine ? this.getCharEnd() - this.getCharStart() : (sourceCode.length() - this.getCharStart()) - trailingSpaces;
                stringBuilder.append(indent)
                        .append(numberReplacement)
                        .append("   │ ")
                        .append(" ".repeat(this.getCharStart()))
                        .append("^")
                        .append("~".repeat(repeats - 1))
                        .append("\r\n");
            } else if (lineIndex == endLine) {
                stringBuilder.append(indent)
                        .append(numberReplacement)
                        .append("   │ ")
                        .append(" ".repeat(leadingSpaces))
                        .append("^")
                        .append("~".repeat(this.getCharEnd() - leadingSpaces - 1))
                        .append("\r\n");
            } else {
                stringBuilder.append(indent)
                        .append(numberReplacement)
                        .append("   │ ")
                        .append(" ".repeat(leadingSpaces))
                        .append("^")
                        .append("~".repeat(repeats - 1))
                        .append("\r\n");
            }
        }
    }
    
}