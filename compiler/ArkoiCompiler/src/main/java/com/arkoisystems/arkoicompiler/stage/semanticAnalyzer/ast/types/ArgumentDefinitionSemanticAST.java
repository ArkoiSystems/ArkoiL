package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.ArgumentDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
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
@Setter
public class ArgumentDefinitionSemanticAST extends AbstractSemanticAST<ArgumentDefinitionSyntaxAST>
{
    
    @Expose
    private IdentifierToken argumentName;
    
    @Expose
    private TypeSemanticAST argumentType;
    
    public ArgumentDefinitionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final ArgumentDefinitionSyntaxAST argumentDefinitionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, argumentDefinitionSyntaxAST, ASTType.ARGUMENT_DEFINITION);
    }
    
    public IdentifierToken getArgumentName() {
        if(this.argumentName == null)
            return (this.argumentName = this.getSyntaxAST().getArgumentName());
        return this.argumentName;
    }
    
    public TypeSemanticAST getArgumentType() {
        if(this.argumentType == null)
            return (this.argumentType = new TypeSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), this.getSyntaxAST().getArgumentType()));
        return this.argumentType;
    }
    
}
