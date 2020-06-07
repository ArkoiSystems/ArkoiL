package com.arkoisystems.compiler.error;

import com.arkoisystems.compiler.CompilerClass;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Getter
public class LineRange
{
    
    @EqualsAndHashCode.Include
    @NonNull
    @NotNull
    private final String sourceCode;
    
    @EqualsAndHashCode.Include
    private final int startLine, endLine;
    
    @NotNull
    public static LineRange make(
            final @NotNull CompilerClass compilerClass,
            final int startLine,
            final int endLine
    ) {
        final String[] sourceSplit = compilerClass.getContent().split(System.getProperty("line.separator"));
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