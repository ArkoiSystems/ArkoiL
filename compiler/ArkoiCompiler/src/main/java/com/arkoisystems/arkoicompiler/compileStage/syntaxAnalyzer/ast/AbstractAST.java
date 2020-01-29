package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast;

import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
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
public abstract class AbstractAST
{
    
    @Expose
    private ASTType astType;
    
    @Expose
    private int start, end;
    
    public AbstractAST(final ASTType astType) {
        this.astType = astType;
    }
    
    public abstract AbstractAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer);
    
    public abstract <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer);
    
    @Override
    public String toString() {
        return Variables.GSON.toJson(this);
    }
    
}
