package com.arkoisystems.arkoicompiler.error;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ErrorPosition
{
    
    @EqualsAndHashCode.Include
    @NotNull
    @Getter
    private final LineRange lineRange;
    
    @EqualsAndHashCode.Include
    @Getter
    private final int charStart, charEnd;
    
}