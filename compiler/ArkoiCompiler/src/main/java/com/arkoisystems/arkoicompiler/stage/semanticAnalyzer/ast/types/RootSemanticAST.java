package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles.DoubleSyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class RootSemanticAST extends AbstractSemanticAST<RootSyntaxAST>
{
    
    @Expose
    private final List<ImportDefinitionSemanticAST> importStorage;
    
    @Expose
    private final List<VariableDefinitionSemanticAST> variableStorage;
    
    @Expose
    private final List<FunctionDefinitionSemanticAST> functionStorage;
    
    public RootSemanticAST(final SemanticAnalyzer semanticAnalyzer, final RootSyntaxAST rootSyntaxAST) {
        super(semanticAnalyzer, null, rootSyntaxAST, ASTType.ROOT);
        
        this.importStorage = new ArrayList<>();
        for (final ImportDefinitionSyntaxAST importDefinitionSyntaxAST : rootSyntaxAST.getImportStorage()) {
            final ImportDefinitionSemanticAST importDefinitionSemanticAST
                    = new ImportDefinitionSemanticAST(semanticAnalyzer, this, importDefinitionSyntaxAST);
            this.importStorage.add(importDefinitionSemanticAST);
        }
        
        this.variableStorage = new ArrayList<>();
        for (final VariableDefinitionSyntaxAST variableDefinitionSyntaxAST : rootSyntaxAST.getVariableStorage()) {
            final VariableDefinitionSemanticAST variableDefinitionSemanticAST
                    = new VariableDefinitionSemanticAST(semanticAnalyzer, this, variableDefinitionSyntaxAST);
            this.variableStorage.add(variableDefinitionSemanticAST);
        }
        
        this.functionStorage = new ArrayList<>();
        for (final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST : rootSyntaxAST.getFunctionStorage()) {
            final FunctionDefinitionSemanticAST functionDefinitionSemanticAST
                    = new FunctionDefinitionSemanticAST(semanticAnalyzer, null, functionDefinitionSyntaxAST);
            this.functionStorage.add(functionDefinitionSemanticAST);
        }
    }
    
    @Override
    public RootSemanticAST initialize() {
        final HashMap<String, AbstractSemanticAST<?>> names = new HashMap<>();
        for (final ImportDefinitionSemanticAST importDefinitionSemanticAST : this.getImportStorage()) {
            final IdentifierToken importName = importDefinitionSemanticAST.getImportName();
            if (importName == null)
                return null;
            
            if (names.containsKey(importName.getTokenContent())) {
                final AbstractSemanticAST<?> alreadyExistAST = names.get(importName.getTokenContent());
                this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(importDefinitionSemanticAST.getSyntaxAST(), alreadyExistAST.getSyntaxAST(), "Couldn't analyze this import because there already exists another AST with the same name."));
                return null;
            } else names.put(importName.getTokenContent(), importDefinitionSemanticAST);
            
            if (importDefinitionSemanticAST.getImportTargetClass() == null)
                return null;
        }
        
        for (final VariableDefinitionSemanticAST variableDefinitionSemanticAST : this.getVariableStorage()) {
            if (variableDefinitionSemanticAST.getVariableAnnotations() == null)
                return null;
            
            final IdentifierToken variableName = variableDefinitionSemanticAST.getVariableName();
            if (variableName == null)
                return null;
            
            if (names.containsKey(variableName.getTokenContent())) {
                final AbstractSemanticAST<?> alreadyExistAST = names.get(variableName.getTokenContent());
                this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(variableDefinitionSemanticAST.getSyntaxAST(), alreadyExistAST.getSyntaxAST(), "Couldn't analyze this variable because there already exists another AST with the same name."));
                return null;
            } else
                names.put(variableName.getTokenContent(), variableDefinitionSemanticAST);
            
            if (variableDefinitionSemanticAST.getVariableExpression() == null)
                return null;
        }
        
        for (final FunctionDefinitionSemanticAST functionDefinitionSemanticAST : this.getFunctionStorage()) {
            if (functionDefinitionSemanticAST.getFunctionAnnotations() == null)
                return null;
            
            final String functionDescription = functionDefinitionSemanticAST.getFunctionDescription();
            if (functionDescription == null)
                return null;
            
            if (names.containsKey(functionDescription)) {
                final AbstractSemanticAST<?> alreadyExistAST = names.get(functionDescription);
                this.getSemanticAnalyzer().errorHandler().addError(new DoubleSyntaxASTError<>(functionDefinitionSemanticAST.getSyntaxAST(), alreadyExistAST.getSyntaxAST(), "Couldn't analyze this function because there already exists another one with the same description."));
                return null;
            } else names.put(functionDescription, functionDefinitionSemanticAST);
            
            if(functionDefinitionSemanticAST.getFunctionBlock() == null)
                return null;
        }
        return this;
    }
    
    public AbstractSemanticAST<?> findIdentifier(final IdentifierToken invokedIdentifier) {
        for(final ImportDefinitionSemanticAST importDefinitionSemanticAST : this.getImportStorage()) {
            if(importDefinitionSemanticAST.getImportName().getTokenContent().equals(invokedIdentifier.getTokenContent()))
                return importDefinitionSemanticAST;
        }
        
        for(final VariableDefinitionSemanticAST variableDefinitionSemanticAST : this.getVariableStorage()) {
            if(variableDefinitionSemanticAST.getVariableName().getTokenContent().equals(invokedIdentifier.getTokenContent()))
                return variableDefinitionSemanticAST;
        }
        return null;
    }
    
    public FunctionDefinitionSemanticAST findFunction(final String functionDescription) {
        for(final FunctionDefinitionSemanticAST functionDefinitionSemanticAST : this.getFunctionStorage()) {
            if(functionDefinitionSemanticAST.getFunctionDescription().equals(functionDescription))
                return functionDefinitionSemanticAST;
        }
        return null;
    }
    
}
