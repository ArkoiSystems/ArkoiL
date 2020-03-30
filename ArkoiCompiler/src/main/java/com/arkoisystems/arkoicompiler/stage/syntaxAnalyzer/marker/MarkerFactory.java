/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 29, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarkerFactory<T extends AbstractSyntaxAST>
{
    
    @Getter
    @NotNull
    private final List<MarkerFactory<? extends AbstractSyntaxAST>> nextMarkerFactories = new ArrayList<>();
    
    @Getter
    @NotNull
    private final ArkoiMarker currentMarker;
    
    
    @Getter
    @Nullable
    private final T abstractSyntaxAST;
    
    
    @Getter
    private boolean locked;
    
    public MarkerFactory(@Nullable final T abstractSyntaxAST, @NotNull final ArkoiMarker currentMarker) {
        this.abstractSyntaxAST = abstractSyntaxAST;
        this.currentMarker = currentMarker;
        
        this.locked = true;
    }
    
    
    public void addFactory(final MarkerFactory<? extends AbstractSyntaxAST> markerFactory) {
        this.nextMarkerFactories.add(markerFactory);
    }
    
    
    public void mark(final AbstractToken abstractToken) {
        this.currentMarker.setStartToken(abstractToken);
    }
    
    
    public void error(final String message, final Object... arguments) {
        Objects.requireNonNull(this.getAbstractSyntaxAST());
        Objects.requireNonNull(this.getAbstractSyntaxAST().getSyntaxAnalyzer());
        
        this.currentMarker.setErrorMessage(message);
        this.currentMarker.setErrorArguments(arguments);
        
        this.mark(this.getAbstractSyntaxAST().getSyntaxAnalyzer().currentToken());
        this.done(this.getAbstractSyntaxAST().getSyntaxAnalyzer().currentToken());
    }
    
    
    public boolean done(final AbstractToken abstractToken) {
        this.currentMarker.setEndToken(abstractToken);
        this.locked = true;
        
        for (final MarkerFactory<?> nextMarker : this.getNextMarkerFactories()) {
            if (nextMarker.isLocked())
                return false;
        }
        return true;
    }
    
}
