/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.RootSemanticAST;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SemanticAnalyzer extends AbstractStage
{
    
    @Getter
    @NotNull
    private final ArkoiClass arkoiClass;
    
    
    @Getter
    @NotNull
    private final SemanticErrorHandler errorHandler = new SemanticErrorHandler();
    
    
    @Getter
    @Nullable
    private RootSemanticAST rootSemanticAST;
    
    
    public SemanticAnalyzer(@NotNull final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
    }
    
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.rootSemanticAST = new RootSemanticAST(this, arkoiClass.getSyntaxAnalyzer().getRootSyntaxAST());
        this.rootSemanticAST.initialize();
        
        return !this.rootSemanticAST.isFailed();
    }
    
    
    @NotNull
    @Override
    public SemanticErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
}
