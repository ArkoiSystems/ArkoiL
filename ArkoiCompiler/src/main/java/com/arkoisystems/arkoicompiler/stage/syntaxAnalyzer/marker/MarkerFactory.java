/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 29, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker;

import com.arkoisystems.arkoicompiler.api.ICompilerMarker;
import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class MarkerFactory<T extends ICompilerSyntaxAST, T1, T2>
{
    
    @NotNull
    private final List<MarkerFactory<? extends ICompilerSyntaxAST, ?, ?>> nextMarkerFactories = new ArrayList<>();
    
    
    @NotNull
    private final ICompilerMarker<T1, T2> currentMarker;
    
    
    @Nullable
    private final T syntaxAST;
    
    
    public void addFactory(final MarkerFactory<? extends ICompilerSyntaxAST, ?, ?> markerFactory) {
        this.nextMarkerFactories.add(markerFactory);
    }
    
    
    public void mark(final T1 start) {
        this.currentMarker.setStart(start);
    }
    
    
    public void error(final T1 start, final T2 end, final String message, final Object... arguments) {
        Objects.requireNonNull(this.getSyntaxAST());
        Objects.requireNonNull(this.getSyntaxAST().getSyntaxAnalyzer());
        
        this.currentMarker.setErrorMessage(message);
        this.currentMarker.setErrorArguments(arguments);
        
        this.mark(start);
        this.done(end);
    }
    
    
    public void done(final T2 end) {
        this.currentMarker.setEnd(end);
    }
    
}
