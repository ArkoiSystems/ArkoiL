package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.RootSemantic;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.function.FunctionDefinitionSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable.VariableDefinitionAST;
import lombok.SneakyThrows;

import java.io.File;

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
public class IdentifierInvokeSemantic extends AbstractSemantic<IdentifierInvokeAST>
{
    
    public IdentifierInvokeSemantic(final AbstractSemantic<?> abstractSemantic, final IdentifierInvokeAST identifierInvokeAST) {
        super(abstractSemantic, identifierInvokeAST);
    }
    
    @SneakyThrows
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        AbstractAST<?> foundIdentifier = null;
        if (this.getLastContainerSemantic() instanceof FunctionDefinitionSemantic) {
            if (this.getAbstractAST().getIdentifierAccess() != IdentifierInvokeAST.IdentifierAccess.THIS_ACCESS) {
                // TODO: Search in the function for the identifier (variables)
            }
            
            foundIdentifier = this.searchRootAST(semanticAnalyzer);
        } else if (this.getLastContainerSemantic() instanceof RootSemantic)
            foundIdentifier = this.searchRootAST(semanticAnalyzer);
        
        if (foundIdentifier == null) {
            semanticAnalyzer.errorHandler().addError(new ASTError<>(this.getAbstractAST(), "Couldn't find any variable or import with the name \"%s\".", this.getAbstractAST().getInvokedIdentifierNameToken().getTokenContent()));
            return false;
        }
        
        if (foundIdentifier instanceof ImportDefinitionAST) {
            final String filePath = new File(semanticAnalyzer.getArkoiClass().getArkoiCompiler().getWorkingDirectory() + File.separator + ((ImportDefinitionAST) foundIdentifier).getImportFilePathToken().getTokenContent() + ".ark").getAbsolutePath();
            
            final ArkoiClass arkoiClass = semanticAnalyzer.getArkoiClass().getArkoiCompiler().getArkoiClasses().get(filePath);
            if (arkoiClass == null) {
                semanticAnalyzer.errorHandler().addError(new ASTError<>(foundIdentifier, "Couldn't find the according ArkoiClass for this path \"%s\".", filePath));
                return false;
            }
    
            return semanticAnalyzer.analyseSemanticClass(arkoiClass.getSemanticAnalyzer().getRootSemantic(), this.getAbstractAST().getInvokedIdentifierStatement(), arkoiClass.getSemanticAnalyzer());
        } else if(foundIdentifier instanceof VariableDefinitionAST) {
            // TODO: Search for the result file of the variable
        }
        
        semanticAnalyzer.errorHandler().addError(new ASTError<>(this.getAbstractAST(), "Couldn't find the right last container for this AST."));
        return false;
    }
    
    private AbstractAST<?> searchRootAST(final SemanticAnalyzer semanticAnalyzer) {
        for (final ImportDefinitionAST importDefinitionAST : semanticAnalyzer.getRootSemantic().getAbstractAST().getImportStorage()) {
            if (importDefinitionAST.getImportNameToken().getTokenContent().equals(this.getAbstractAST().getInvokedIdentifierNameToken().getTokenContent()))
                return importDefinitionAST;
        }
        
        for (final VariableDefinitionAST variableDefinitionAST : semanticAnalyzer.getRootSemantic().getAbstractAST().getVariableStorage()) {
            if (variableDefinitionAST.getVariableNameToken().getTokenContent().equals(this.getAbstractAST().getInvokedIdentifierNameToken().getTokenContent()))
                return variableDefinitionAST;
        }
        return null;
    }
    
}
