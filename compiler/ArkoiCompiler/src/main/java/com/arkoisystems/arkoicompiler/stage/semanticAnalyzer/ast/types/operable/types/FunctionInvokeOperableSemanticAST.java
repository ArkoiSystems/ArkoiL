package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

import java.util.ArrayList;
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
@Setter
public class FunctionInvokeOperableSemanticAST extends AbstractOperableSemanticAST<FunctionInvokeOperableSyntaxAST, TypeSyntaxAST.TypeKind>
{
    
    @Expose
    private FunctionDefinitionSemanticAST invokedFunction;
    
    @Expose
    private List<ExpressionSemanticAST> invokedExpressions;
    
    public FunctionInvokeOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, functionInvokeOperableSyntaxAST, ASTType.FUNCTION_INVOKE_OPERABLE);
    }
    
    public FunctionDefinitionSemanticAST getInvokedFunction() {
        if (this.invokedFunction == null) {
            final String functionDescription = this.getFunctionDescription();
            if(functionDescription == null)
                return null;
            
            final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = this.getSemanticAnalyzer().getRootSemanticAST().findFunction(functionDescription);
            if (functionDefinitionSemanticAST == null) {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this function invoke because there exists no function with this description."));
                return null;
            } else return (this.invokedFunction = functionDefinitionSemanticAST);
        }
        return this.invokedFunction;
    }
    
    public List<ExpressionSemanticAST> getInvokedExpressions() {
        if(this.invokedExpressions == null) {
            final List<ExpressionSemanticAST> invokedExpressions = new ArrayList<>();
            for(final ExpressionSyntaxAST expressionSyntaxAST : this.getSyntaxAST().getInvokedExpressions()) {
                final ExpressionSemanticAST expressionSemanticAST
                        = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
                
                if(expressionSemanticAST.getExpressionOperable() == null)
                    return null;
                invokedExpressions.add(expressionSemanticAST);
            }
            return (this.invokedExpressions = invokedExpressions);
        }
        return this.invokedExpressions;
    }
    
    private String getFunctionDescription() {
        final StringBuilder descriptionBuilder = new StringBuilder(this.getSyntaxAST().getInvokedFunctionName().getTokenContent());
        if(this.getInvokedExpressions() == null)
            return null;
        
        for(final ExpressionSemanticAST expressionSemanticAST : this.getInvokedExpressions()) {
            final TypeSyntaxAST.TypeKind typeKind = expressionSemanticAST.getOperableObject();
            if(typeKind == null)
                return null;
            descriptionBuilder.append(typeKind.name());
        }
        return descriptionBuilder.toString();
    }
    
    //    @Override
    //    public FunctionInvokeOperableSemanticAST analyseAST(final SemanticAnalyzer semanticAnalyzer) {
    //        if (this.getSyntaxAST().getFunctionInvocation() != FunctionInvokeOperableSyntaxAST.FunctionInvocation.EXPRESSION_INVOCATION) {
    //            semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this AST because it wasn't invoked inside an expression."));
    //            return null;
    //        }
    //
    //        for (final ExpressionSyntaxAST expressionSyntaxAST : this.getSyntaxAST().getInvokedArguments()) {
    //            final ExpressionSemanticAST expressionSemanticAST
    //                    = new ExpressionSemanticAST(this.getLastContainerAST(), expressionSyntaxAST).analyseAST(semanticAnalyzer);
    //            if (expressionSemanticAST == null)
    //                return null;
    //
    //            this.invokedExpressions.add(expressionSemanticAST);
    //        }
    //
    //        final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = semanticAnalyzer.getRootSemanticAST().findFunction(this.getFunctionDescription());
    //        if (functionDefinitionSemanticAST == null) {
    //            semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this AST because no function exists with this description."));
    //            return null;
    //        } else this.invokedFunction = functionDefinitionSemanticAST;
    //        return this;
    //    }
    //
    //    private String getFunctionDescription() {
    //        final StringBuilder functionDescription = new StringBuilder(this.getSyntaxAST().getInvokedFunctionNameToken().getTokenContent());
    //        for (final ExpressionSemanticAST expressionSemanticAST : this.getInvokedExpressions())
    //            functionDescription.append(expressionSemanticAST.getOperableObject().getName());
    //        return functionDescription.toString().intern();
    //    }
    
}
