/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.RootSemanticAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.SneakyThrows;

public class SemanticAnalyzer extends AbstractStage
{
    
    @Getter
    private final ArkoiClass arkoiClass;
    
    
    @Getter
    private final SemanticErrorHandler errorHandler;
    
    
    @Getter
    private final RootSemanticAST rootSemanticAST;
    
    
    public SemanticAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new SemanticErrorHandler();
        this.rootSemanticAST = new RootSemanticAST(this, arkoiClass.getSyntaxAnalyzer().getRootSyntaxAST());
    }
    
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        return this.getRootSemanticAST().initialize() != null;
    }
    
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
}
