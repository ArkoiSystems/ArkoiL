package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.RootSemantic;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.function.FunctionDefinitionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierCallAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable.VariableDefinitionAST;

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
public class IdentifierCallSemantic extends AbstractSemantic<IdentifierCallAST>
{
    
    public IdentifierCallSemantic(final AbstractSemantic<?> abstractSemantic, final IdentifierCallAST identifierCallAST) {
        super(abstractSemantic, identifierCallAST);
    }
    
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        AbstractAST<?> foundVariable = null;
        if (this.getLastContainerSemantic() instanceof FunctionDefinitionSemantic) {
            if (this.getAbstractAST().getIdentifierAccess() != IdentifierCallAST.IdentifierAccess.THIS_ACCESS) {
                // TODO: Search function for variable
            }
            
            foundVariable = this.searchRootAST(semanticAnalyzer);
        } else if (this.getLastContainerSemantic() instanceof RootSemantic)
            foundVariable = this.searchRootAST(semanticAnalyzer);
        
        if (foundVariable == null) {
            semanticAnalyzer.errorHandler().addError(new ASTError<>(this.getAbstractAST(), "Couldn't find any variable with the name \"%s\".", this.getAbstractAST().getCalledIdentifierToken().getTokenContent()));
            return false;
        }
        return true;
    }
    
    private AbstractAST<?> searchRootAST(final SemanticAnalyzer semanticAnalyzer) {
        for (final VariableDefinitionAST variableDefinitionAST : semanticAnalyzer.getRootSemantic().getAbstractAST().getVariableStorage()) {
            if (variableDefinitionAST.getVariableNameToken().getTokenContent().equals(this.getAbstractAST().getCalledIdentifierToken().getTokenContent()))
                return variableDefinitionAST;
        }
        return null;
    }
    
}
