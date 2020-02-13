package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

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
@Setter
public abstract class AbstractSemanticAST<T extends AbstractSyntaxAST>
{
    
    private final AbstractSemanticAST<?> lastContainerAST;
    
    private final SemanticAnalyzer semanticAnalyzer;
    
    private final T syntaxAST;
    
    @Expose
    private final ASTType astType;
    
    @Expose
    private int start, end;
    
    public AbstractSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final T syntaxAST, final ASTType astType) {
        this.lastContainerAST = lastContainerAST;
        this.semanticAnalyzer = semanticAnalyzer;
        this.syntaxAST = syntaxAST;
        this.astType = astType;
    }
    
    public AbstractSemanticAST<?> initialize() {
        return null;
    }
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this.getSyntaxAST());
    }
    
}
