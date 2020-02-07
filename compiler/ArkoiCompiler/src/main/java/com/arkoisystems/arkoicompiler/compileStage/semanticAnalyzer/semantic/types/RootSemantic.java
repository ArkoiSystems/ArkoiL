package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.DuplicateASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ImportDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable.VariableDefinitionAST;
import lombok.Getter;
import lombok.SneakyThrows;

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
public class RootSemantic extends AbstractSemantic<RootAST>
{
    
    public RootSemantic(final AbstractSemantic<?> abstractSemantic, final RootAST rootAST) {
        super(abstractSemantic, rootAST);
    }
    
    @SneakyThrows
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        final List<AbstractAST<?>> importsAndVariables = new ArrayList<>();
        importsAndVariables.addAll(this.getAbstractAST().getImportStorage());
        importsAndVariables.addAll(this.getAbstractAST().getVariableStorage());
        importsAndVariables.sort(Comparator.comparingInt(AbstractAST::getStart));
        
        final HashMap<String, AbstractAST<?>> names = new HashMap<>();
        for (final AbstractAST<?> abstractAST : importsAndVariables) {
            String name = "";
            if (abstractAST instanceof ImportDefinitionAST)
                name = ((ImportDefinitionAST) abstractAST).getImportNameToken().getTokenContent();
            else if (abstractAST instanceof VariableDefinitionAST)
                name = ((VariableDefinitionAST) abstractAST).getVariableNameToken().getTokenContent();
            
            if (!names.containsKey(name)) {
                names.put(name, abstractAST);
                
                if (abstractAST instanceof VariableDefinitionAST)
                    if (!semanticAnalyzer.analyseSemanticClass(this, abstractAST))
                        return false;
            } else {
                final AbstractAST<?> alreadyExist = names.get(name);
                if (alreadyExist instanceof ImportDefinitionAST)
                    semanticAnalyzer.errorHandler().addError(new DuplicateASTError<>(alreadyExist, abstractAST, "There already exists an import with the same name."));
                else if (alreadyExist instanceof VariableDefinitionAST)
                    semanticAnalyzer.errorHandler().addError(new DuplicateASTError<>(alreadyExist, abstractAST, "There already exists a variable with the same name."));
                return false;
            }
        }
        
        final HashMap<String, FunctionDefinitionAST> functionDescriptions = new HashMap<>();
        for (final FunctionDefinitionAST functionDefinitionAST : this.getAbstractAST().getFunctionStorage()) {
            StringBuilder functionDescription = new StringBuilder(functionDefinitionAST.getFunctionNameToken().getTokenContent());
            for (final ArgumentDefinitionAST argumentDefinitionAST : functionDefinitionAST.getFunctionArgumentASTs())
                functionDescription.append(argumentDefinitionAST.getArgumentType().getTypeKind().getName());
            
            if (!functionDescriptions.containsKey(functionDescription.toString())) {
                functionDescriptions.put(functionDescription.toString(), functionDefinitionAST);
                
                if (!semanticAnalyzer.analyseSemanticClass(this, functionDefinitionAST))
                    return false;
            } else {
                semanticAnalyzer.errorHandler().addError(new DuplicateASTError<>(functionDescriptions.get(functionDescription.toString()), functionDefinitionAST, "There already exists a function with the same name and arguments."));
                return false;
            }
        }
        return true;
    }
    
}
