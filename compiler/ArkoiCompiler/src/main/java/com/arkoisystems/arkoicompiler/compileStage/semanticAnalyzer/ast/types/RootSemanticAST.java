package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.doubles.DoubleSyntaxASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.VariableDefinitionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
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
    
    public RootSemanticAST(final RootSyntaxAST rootSyntaxAST) {
        super(null, rootSyntaxAST, ASTType.ROOT);
        
        this.variableStorage = new ArrayList<>();
        this.functionStorage = new ArrayList<>();
        this.importStorage = new ArrayList<>();
    }
    
    @Override
    public RootSemanticAST analyse(final SemanticAnalyzer semanticAnalyzer) {
        final HashMap<String, AbstractSyntaxAST> astNames = new HashMap<>();
        final List<AbstractSyntaxAST> abstractSyntaxASTs = new ArrayList<>();
        
        abstractSyntaxASTs.addAll(this.getSyntaxAST().getImportStorage());
        abstractSyntaxASTs.addAll(this.getSyntaxAST().getVariableStorage());
        abstractSyntaxASTs.sort(Comparator.comparingInt(AbstractSyntaxAST::getStart));
        
        for (final AbstractSyntaxAST abstractSyntaxAST : abstractSyntaxASTs) {
            final String name = this.getName(abstractSyntaxAST);
            if (name == null)
                continue;
            
            if (!astNames.containsKey(name)) {
                astNames.put(name, abstractSyntaxAST);
                
                if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST) {
                    final VariableDefinitionSemanticAST variableDefinitionSemanticAST =
                            new VariableDefinitionSemanticAST(this.getLastContainerAST(), (VariableDefinitionSyntaxAST) abstractSyntaxAST).analyse(semanticAnalyzer);
                    if (variableDefinitionSemanticAST == null)
                        return null;
                    else this.variableStorage.add(variableDefinitionSemanticAST);
                } else if (abstractSyntaxAST instanceof ImportDefinitionSyntaxAST) {
                    final ImportDefinitionSemanticAST importDefinitionSemanticAST =
                            new ImportDefinitionSemanticAST(this.getLastContainerAST(), (ImportDefinitionSyntaxAST) abstractSyntaxAST).analyse(semanticAnalyzer);
                    if (importDefinitionSemanticAST == null)
                        return null;
                    
                    this.importStorage.add(importDefinitionSemanticAST);
                } else {
                    semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(abstractSyntaxAST, "Couldn't analyse this AST because it isn't supported."));
                    return null;
                }
            } else {
                semanticAnalyzer.errorHandler().addError(new DoubleSyntaxASTError<>(astNames.get(name), abstractSyntaxAST, "Couldn't analyse the semantic of this AST because it already exists with this name."));
                return null;
            }
        }
        
        final HashMap<String, FunctionDefinitionSyntaxAST> functionDescriptions = new HashMap<>();
        for (final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST : this.getSyntaxAST().getFunctionStorage()) {
            final String functionDescription = this.getFunctionDescription(functionDefinitionSyntaxAST);
            
            if (!functionDescriptions.containsKey(functionDescription)) {
                functionDescriptions.put(functionDescription, functionDefinitionSyntaxAST);
                
                final FunctionDefinitionSemanticAST functionDefinitionSemanticAST =
                        new FunctionDefinitionSemanticAST(this.getLastContainerAST(), functionDefinitionSyntaxAST).analyse(semanticAnalyzer);
                if (functionDefinitionSemanticAST == null)
                    return null;
                
                this.functionStorage.add(functionDefinitionSemanticAST);
            } else {
                semanticAnalyzer.errorHandler().addError(new DoubleSyntaxASTError<>(functionDescriptions.get(functionDescription), functionDefinitionSyntaxAST, "Couldn't analyse the semantic of this function because there already exists a function with this description."));
                return null;
            }
        }
        return this;
    }
    
    private String getFunctionDescription(final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST) {
        StringBuilder functionDescription = new StringBuilder(functionDefinitionSyntaxAST.getFunctionNameToken().getTokenContent());
        for (final ArgumentDefinitionSyntaxAST argumentDefinitionSyntaxAST : functionDefinitionSyntaxAST.getFunctionArgumentASTs())
            functionDescription.append(argumentDefinitionSyntaxAST.getArgumentType().getTypeKind().getName());
        return functionDescription.toString().intern();
    }
    
    private String getName(final AbstractSyntaxAST abstractSyntaxAST) {
        if (abstractSyntaxAST instanceof ImportDefinitionSyntaxAST)
            return ((ImportDefinitionSyntaxAST) abstractSyntaxAST).getImportNameToken().getTokenContent();
        else if (abstractSyntaxAST instanceof VariableDefinitionSyntaxAST)
            return ((VariableDefinitionSyntaxAST) abstractSyntaxAST).getVariableNameToken().getTokenContent();
        return null;
    }
    
}
