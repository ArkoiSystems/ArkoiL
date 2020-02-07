package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.function;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.DuplicateASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variable.VariableDefinitionAST;
import lombok.SneakyThrows;

import java.util.HashMap;

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
public class FunctionDefinitionSemantic extends AbstractSemantic<FunctionDefinitionAST>
{
    
    public FunctionDefinitionSemantic(final AbstractSemantic<?> abstractSemantic, final FunctionDefinitionAST functionDefinitionAST) {
        super(abstractSemantic, functionDefinitionAST);
    }
    
    @SneakyThrows
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        final HashMap<String, ArgumentDefinitionAST> argumentNames = new HashMap<>();
        for (final ArgumentDefinitionAST argumentDefinitionAST : this.getAbstractAST().getFunctionArgumentASTs()) {
            final String argumentName = argumentDefinitionAST.getArgumentNameIdentifier().getTokenContent();
            if (!argumentNames.containsKey(argumentName)) {
                argumentNames.put(argumentName, argumentDefinitionAST);
            } else {
                semanticAnalyzer.errorHandler().addError(new DuplicateASTError<>(argumentNames.get(argumentName), argumentDefinitionAST, "There already exists an argument with the same name in this function."));
                return false;
            }
        }
        
        return this.searchBlock(new HashMap<>(), this.getAbstractAST().getBlockAST(), semanticAnalyzer);
    }
    
    @SneakyThrows
    private boolean searchBlock(final HashMap<String, AbstractAST<?>> variableNames, final BlockAST blockAST, final SemanticAnalyzer semanticAnalyzer) {
        for (final AbstractAST<?> abstractAST : blockAST.getBlockStorage()) {
            if (abstractAST instanceof BlockAST) {
                if (!this.searchBlock(variableNames, ((BlockAST) abstractAST), semanticAnalyzer))
                    return false;
            } else if (abstractAST instanceof VariableDefinitionAST) {
                final VariableDefinitionAST variableDefinitionAST = (VariableDefinitionAST) abstractAST;
                final String name = variableDefinitionAST.getVariableNameToken().getTokenContent();
                if (!variableNames.containsKey(name)) {
                    variableNames.put(name, variableDefinitionAST);
                    
                    if (!semanticAnalyzer.analyseSemanticClass(this, variableDefinitionAST))
                        return false;
                } else {
                    semanticAnalyzer.errorHandler().addError(new DuplicateASTError<>(variableNames.get(name), variableDefinitionAST, "There already exists a variable with the same name inside the function block."));
                    return false;
                }
            } else if (!semanticAnalyzer.analyseSemanticClass(this, abstractAST))
                return false;
        }
        return true;
    }
    
}
