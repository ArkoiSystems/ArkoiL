/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class SemanticAnalyzer implements ICompilerStage
{
    
    @Getter
    @NotNull
    private final ICompilerClass compilerClass;
    
    
    @Getter
    @NotNull
    private SemanticErrorHandler errorHandler = new SemanticErrorHandler();
    
    
    @Getter
    private boolean failed;
    
    
    public SemanticAnalyzer(@NotNull final ICompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.reset();
        final ASTScope astScope = new ASTScope(this);
        astScope.visit(this.getCompilerClass().getSyntaxAnalyzer().getRootAST());
        return !astScope.isFailed();
    }
    
    
    @Override
    public void reset() {
        this.errorHandler = new SemanticErrorHandler();
        this.failed = false;
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
}
