/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

import java.util.ArrayList;
import java.util.List;

public class FunctionInvokeOperableSemanticAST extends AbstractOperableSemanticAST<FunctionInvokeOperableSyntaxAST, TypeKind>
{
    
    private FunctionDefinitionSemanticAST invokedFunction;
    
    
    private List<ExpressionSemanticAST> invokedExpressions;
    
    
    public FunctionInvokeOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, functionInvokeOperableSyntaxAST, ASTType.FUNCTION_INVOKE_OPERABLE);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.getInvokedFunction() == null)
            return null;
        
        final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = this.getInvokedFunction();
        return functionDefinitionSemanticAST.getFunctionReturnType().getTypeKind();
    }
    
    
    public ASTAccess getFunctionAccess() {
        return this.getSyntaxAST().getFunctionAccess();
    }
    
    
    public FunctionDefinitionSemanticAST getInvokedFunction() {
        if (this.invokedFunction == null) {
            final String functionDescription = this.getFunctionDescription();
            if (functionDescription == null)
                return null;
            if (this.getFunctionAccess() == null)
                return null;
            
            FunctionDefinitionSemanticAST functionDefinitionSemanticAST = null;
            if (this.getFunctionAccess() != ASTAccess.THIS_ACCESS)
                functionDefinitionSemanticAST = this.getSemanticAnalyzer().getArkoiClass().getArkoiCompiler().findNativeSemanticFunction(functionDescription);
            if (functionDefinitionSemanticAST == null)
                functionDefinitionSemanticAST = this.getSemanticAnalyzer().getRootSemanticAST().findFunction(functionDescription);
            if (functionDefinitionSemanticAST == null) {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSemanticAnalyzer().getArkoiClass(), this.getSyntaxAST(), "Couldn't analyze this function invoke because there exists no function with this description."));
                return null;
            }
            return (this.invokedFunction = functionDefinitionSemanticAST);
        }
        return this.invokedFunction;
    }
    
    
    public List<ExpressionSemanticAST> getInvokedExpressions() {
        if(this.invokedExpressions == null) {
            this.invokedExpressions = new ArrayList<>();
            for(final ExpressionSyntaxAST expressionSyntaxAST : this.getSyntaxAST().getInvokedExpressions()) {
                final ExpressionSemanticAST expressionSemanticAST
                        = new ExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), expressionSyntaxAST);
    
                if (expressionSemanticAST.getOperableObject() == null)
                    return null;
                this.invokedExpressions.add(expressionSemanticAST);
            }
        }
        return this.invokedExpressions;
    }
    
    
    private String getFunctionDescription() {
        final StringBuilder descriptionBuilder = new StringBuilder(this.getSyntaxAST().getInvokedFunctionName().getTokenContent());
        if(this.getInvokedExpressions() == null)
            return null;
        
        for(final ExpressionSemanticAST expressionSemanticAST : this.getInvokedExpressions()) {
            final TypeKind typeKind = expressionSemanticAST.getOperableObject();
            if(typeKind == null)
                return null;
            descriptionBuilder.append(typeKind.name());
        }
        return descriptionBuilder.toString();
    }
    
}
