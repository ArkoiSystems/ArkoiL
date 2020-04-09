/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.error.ICompilerError;
import com.arkoisystems.arkoicompiler.utils.Variables;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ArkoiError implements ICompilerError
{
    
    @Getter
    @NotNull
    private final ICompilerClass compilerClass;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @Nullable
    private final Object[] arguments;
    
    
    @Getter
    @Nullable
    private final int[][] positions;
    
    
    @EqualsAndHashCode.Include
    @Getter
    @Nullable
    private final String message;
    
    
    @Override
    public @NotNull
    String getFinalError() {
        Objects.requireNonNull(this.getMessage());
        Objects.requireNonNull(this.getPositions());
        Objects.requireNonNull(this.getCompilerClass());
        
        final StringBuilder stringBuilder = new StringBuilder("[" + Variables.DATE_FORMAT.format(new Date()) + "/INFO] " + String.format(this.getMessage(), this.getArguments()) + "\n");
        for (int index = 0; index < this.getPositions().length; index++) {
            final int[] position = this.getPositions()[index];
            if (position.length != 2)
                continue;
            
            stringBuilder.append(" >>> ");
            int startPosition = position[0], endPosition = position[1];
            for (; startPosition > 0; startPosition--) {
                if (this.getCompilerClass().getContent()[startPosition] != 0x0A)
                    continue;
                startPosition++;
                break;
            }
            for (; endPosition < this.getCompilerClass().getContent().length; endPosition++) {
                if (this.getCompilerClass().getContent()[endPosition] != 0x0A)
                    continue;
                break;
            }
            
            final String realLine = new String(Arrays.copyOfRange(this.getCompilerClass().getContent(), startPosition, endPosition));
            final String line = realLine.replaceAll("\n", "");
            final int difference = realLine.length() - line.length();
            
            stringBuilder.append(line).append("\n");
            stringBuilder.append(" ".repeat(Math.max(0, 5 + (position[0] - startPosition) - difference)));
            stringBuilder.append("^".repeat(Math.max(1, (position[1] - position[0]))));
            
            if (index != this.getPositions().length - 1)
                stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    
    
    @Override
    public String toString() {
        return this.getFinalError();
    }
    
}
