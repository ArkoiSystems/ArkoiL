package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.compileStage.AbstractStage;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.RootSemanticAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
public class SemanticAnalyzer extends AbstractStage
{
    
    private final ArkoiClass arkoiClass;
    
    @Expose
    private final SemanticErrorHandler errorHandler;
    
    @Expose
    private RootSemanticAST rootSemanticAST;
    
    public SemanticAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new SemanticErrorHandler();
        this.rootSemanticAST = new RootSemanticAST(arkoiClass.getSyntaxAnalyzer().getRootSyntaxAST());
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        return this.rootSemanticAST.analyse(this) != null;
    }
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
}
