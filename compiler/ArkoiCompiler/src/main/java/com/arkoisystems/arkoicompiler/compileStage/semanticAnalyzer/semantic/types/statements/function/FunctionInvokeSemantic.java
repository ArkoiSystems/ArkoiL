package com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.statements.function;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.function.FunctionInvokeAST;

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
public class FunctionInvokeSemantic extends AbstractSemantic<FunctionInvokeAST>
{
    
    public FunctionInvokeSemantic(final AbstractSemantic<?> abstractSemantic, final FunctionInvokeAST functionInvokeAST) {
        super(abstractSemantic, functionInvokeAST);
    }
    
    @Override
    public boolean analyse(final SemanticAnalyzer semanticAnalyzer) {
        final AbstractAST<?> foundFunction = this.searchRootAST(semanticAnalyzer);
        if(foundFunction == null) {
            semanticAnalyzer.errorHandler().addError(new ASTError<>(this.getAbstractAST(), "Couldn't find any function in this class which matches the function name and arguments."));
            return false;
        }
        return true;
    }
    
    private AbstractAST<?> searchRootAST(final SemanticAnalyzer semanticAnalyzer) {
        for (final FunctionDefinitionAST functionDefinitionAST : semanticAnalyzer.getRootSemantic().getAbstractAST().getFunctionStorage()) {
            if (functionDefinitionAST.getFunctionNameToken().getTokenContent().equals(this.getAbstractAST().getInvokedFunctionNameToken().getTokenContent()) &&
                    functionDefinitionAST.getFunctionArgumentASTs().size() == this.getAbstractAST().getInvokedArguments().size()) // TODO: Add a argument type comparison
                return functionDefinitionAST;
        }
        return null;
    }
    
}
