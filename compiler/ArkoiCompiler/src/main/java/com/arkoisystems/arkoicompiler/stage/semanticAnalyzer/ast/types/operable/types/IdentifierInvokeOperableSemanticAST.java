/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SemanticASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.google.gson.annotations.Expose;
import lombok.Setter;

public class IdentifierInvokeOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierInvokeOperableSyntaxAST, TypeKind>
{
    
    private AbstractSemanticAST<?> invokedIdentifier;
    
    
    private AbstractSemanticAST<?> invokePostStatement;
    
    
    public IdentifierInvokeOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierInvokeOperableSyntaxAST, ASTType.IDENTIFIER_INVOKE_OPERABLE);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.getInvokePostStatement() == null)
            return null;
        
        final AbstractSemanticAST<?> invokedPostStatement = this.getInvokePostStatement();
        if (invokedPostStatement instanceof FunctionInvokeOperableSemanticAST) {
            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST = (FunctionInvokeOperableSemanticAST) invokedPostStatement;
            return functionInvokeOperableSemanticAST.getOperableObject();
        } else if (invokedPostStatement instanceof IdentifierInvokeOperableSemanticAST) {
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST = (IdentifierInvokeOperableSemanticAST) invokedPostStatement;
            return identifierInvokeOperableSemanticAST.getOperableObject();
        } else if (invokedPostStatement instanceof IdentifierCallOperableSemanticAST) {
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) invokedPostStatement;
            return identifierCallOperableSemanticAST.getOperableObject();
        } else {
            this.getSemanticAnalyzer().errorHandler().addError(new SemanticASTError<>(invokedPostStatement, "Couldn't analyze this identifier invoke because the followed statement isn't supported."));
            return null;
        }
    }
    
    
    public ASTAccess getIdentifierAccess() {
        return this.getSyntaxAST().getIdentifierAccess();
    }
    
    
    public AbstractSemanticAST<?> getInvokedIdentifier() {
        if (this.invokedIdentifier == null) {
            AbstractSemanticAST<?> abstractSemanticAST = null;
            if (this.getIdentifierAccess() != ASTAccess.THIS_ACCESS && this.getLastFunctionDefinition(this.getLastContainerAST()) instanceof FunctionDefinitionSemanticAST) {
                final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = (FunctionDefinitionSemanticAST) this.getLastContainerAST();
                abstractSemanticAST = functionDefinitionSemanticAST.findIdentifier(this.getSyntaxAST().getInvokedIdentifier());
            }
            
            if (abstractSemanticAST == null)
                abstractSemanticAST = this.getSemanticAnalyzer().getRootSemanticAST().findIdentifier(this.getSyntaxAST().getInvokedIdentifier());
            if (abstractSemanticAST == null) {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this identifier invoke because there is no existing identifier with this name."));
                return null;
            }
            return (this.invokedIdentifier = abstractSemanticAST);
        }
        return this.invokedIdentifier;
    }
    
    
    private FunctionDefinitionSemanticAST getLastFunctionDefinition(final AbstractSemanticAST<?> abstractSemanticAST) {
        if (abstractSemanticAST.getClass() == FunctionDefinitionSemanticAST.class)
            return (FunctionDefinitionSemanticAST) abstractSemanticAST;
        else if (abstractSemanticAST.getLastContainerAST() == null)
            return null;
        else if (abstractSemanticAST.getLastContainerAST().getClass() == FunctionDefinitionSemanticAST.class)
            return (FunctionDefinitionSemanticAST) abstractSemanticAST.getLastContainerAST();
        else
            return this.getLastFunctionDefinition(abstractSemanticAST.getLastContainerAST());
    }
    
    
    public AbstractSemanticAST<?> getInvokePostStatement() {
        if (this.invokePostStatement == null) {
            final AbstractSemanticAST<?> invokedIdentifier = this.getInvokedIdentifier();
            if (invokedIdentifier == null)
                return null;
            
            if (invokedIdentifier instanceof ImportDefinitionSemanticAST) {
                final ImportDefinitionSemanticAST importDefinitionSemanticAST = (ImportDefinitionSemanticAST) invokedIdentifier;
                
                if (this.getSyntaxAST().getInvokePostStatement() instanceof FunctionInvokeOperableSyntaxAST) {
                    final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) this.getSyntaxAST().getInvokePostStatement();
                    final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                            = new FunctionInvokeOperableSemanticAST(importDefinitionSemanticAST.getImportTargetClass().getSemanticAnalyzer(), this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
                    
                    if (functionInvokeOperableSemanticAST.getInvokedFunction() == null)
                        return null;
                    if (functionInvokeOperableSyntaxAST.getInvokedExpressions() == null)
                        return null;
                    this.invokePostStatement = functionInvokeOperableSemanticAST;
                } else {
                    this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST().getInvokePostStatement(), "Couldn't analyze this AST because it isn't supported by the identifier invoke operable."));
                    return null;
                }
            }
        }
        return this.invokePostStatement;
    }
    
}
