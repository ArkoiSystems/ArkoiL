package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.statements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.doubles.DoubleSyntaxASTError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.ArgumentDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.ast.types.BlockSemanticAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionSyntaxAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.FunctionDefinitionSyntaxAST;
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
public class FunctionDefinitionSemanticAST extends AbstractSemanticAST<FunctionDefinitionSyntaxAST>
{
    
    @Expose
    private final List<ArgumentDefinitionSemanticAST> functionArguments;
    
    public FunctionDefinitionSemanticAST(final AbstractSemanticAST<?> lastContainerAST, final FunctionDefinitionSyntaxAST functionDefinitionSyntaxAST) {
        super(lastContainerAST, functionDefinitionSyntaxAST, ASTType.FUNCTION_DEFINITION);
        
        this.functionArguments = new ArrayList<>();
    }
    
    @Override
    public FunctionDefinitionSemanticAST analyse(final SemanticAnalyzer semanticAnalyzer) {
        final HashMap<String, ArgumentDefinitionSyntaxAST> argumentNames = new HashMap<>();
        for (final ArgumentDefinitionSyntaxAST functionArgumentAST : this.getSyntaxAST().getFunctionArgumentASTs()) {
            final String name = functionArgumentAST.getArgumentNameIdentifier().getTokenContent();
            
            if (!argumentNames.containsKey(name)) {
                argumentNames.put(name, functionArgumentAST);
                
                final ArgumentDefinitionSemanticAST argumentDefinitionAST =
                        new ArgumentDefinitionSemanticAST(this.getLastContainerAST(), functionArgumentAST).analyse(semanticAnalyzer);
                if (argumentDefinitionAST == null)
                    return null;
                
                this.functionArguments.add(argumentDefinitionAST);
            } else {
                semanticAnalyzer.errorHandler().addError(new DoubleSyntaxASTError<>(argumentNames.get(name), functionArgumentAST, "Couldn't analyze this argument because there already exists another argument with the same name."));
                return null;
            }
        }
        
        final BlockSemanticAST blockSemanticAST
                = new BlockSemanticAST(this.getLastContainerAST(), this.getSyntaxAST().getBlockSyntaxAST()).analyse(semanticAnalyzer);
        if(blockSemanticAST == null)
            return null;
        
        
        return this;
    }
    
}
