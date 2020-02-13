package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Setter;

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
public class IdentifierInvokeOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierInvokeOperableSyntaxAST, TypeSyntaxAST.TypeKind>
{
    
    @Expose
    private AbstractSemanticAST<?> invokedIdentifier;
    
    @Expose
    private AbstractSemanticAST<?> invokePostStatement;
    
    public IdentifierInvokeOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierInvokeOperableSyntaxAST, ASTType.IDENTIFIER_INVOKE_OPERABLE);
    }
    
    public ASTAccess getIdentifierAccess() {
        return this.getSyntaxAST().getIdentifierAccess();
    }
    
    public AbstractSemanticAST<?> getInvokedIdentifier() {
        if (this.invokedIdentifier == null) {
            AbstractSemanticAST<?> abstractSemanticAST = null;
            if (this.getIdentifierAccess() != ASTAccess.THIS_ACCESS && this.getLastContainerAST() instanceof FunctionDefinitionSemanticAST) {
                final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = (FunctionDefinitionSemanticAST) this.getLastContainerAST();
                abstractSemanticAST = functionDefinitionSemanticAST.findIdentifier(this.getSyntaxAST().getInvokedIdentifier());
            }
            
            if (abstractSemanticAST == null)
                abstractSemanticAST = this.getSemanticAnalyzer().getRootSemanticAST().findIdentifier(this.getSyntaxAST().getInvokedIdentifier());
            if (abstractSemanticAST == null) {
                this.getSemanticAnalyzer().errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST(), "Couldn't analyze this identifier invoke because there is no existing identifier with this name."));
                return null;
            }
            
            this.invokedIdentifier = abstractSemanticAST;
        }
        return this.invokedIdentifier;
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
    
    //    @Override
    //    public IdentifierInvokeOperableSemanticAST analyseAST(final SemanticAnalyzer semanticAnalyzer) {
    //        this.identifierAccess = this.getSyntaxAST().getIdentifierAccess();
    //
    //        AbstractSemanticAST<?> abstractSemanticAST = null;
    //        if (this.identifierAccess != ASTAccess.THIS_ACCESS && this.getLastContainerAST() instanceof FunctionDefinitionSemanticAST) {
    //            final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = (FunctionDefinitionSemanticAST) this.getLastContainerAST();
    //            abstractSemanticAST = functionDefinitionSemanticAST.findSemanticAST(this.getSyntaxAST().getInvokedIdentifierNameToken());
    //        }
    //
    //        if (abstractSemanticAST == null)
    //            abstractSemanticAST = semanticAnalyzer.getRootSemanticAST().findSemanticAST(this.getSyntaxAST().getInvokedIdentifierNameToken());
    //
    //        if (abstractSemanticAST instanceof ImportDefinitionSemanticAST) {
    //            final ImportDefinitionSemanticAST importDefinitionSemanticAST = (ImportDefinitionSemanticAST) abstractSemanticAST;
    //
    //            if (this.getSyntaxAST().getInvokedIdentifierStatementAST() instanceof FunctionInvokeOperableSyntaxAST) {
    //                final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) this.getSyntaxAST().getInvokedIdentifierStatementAST();
    //                final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
    //                        = new FunctionInvokeOperableSemanticAST(null, functionInvokeOperableSyntaxAST).analyseAST(importDefinitionSemanticAST.getImportTargetClass().getSemanticAnalyzer());
    //                if (functionInvokeOperableSemanticAST == null)
    //                    return null;
    //            } else {
    //                semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST().getInvokedIdentifierStatementAST(), "Couldn't analyze this AST because it isn't supported by the identifier invoke AST."));
    //                return null;
    //            }
    //
    //            return this;
    //        } else if (abstractSemanticAST == null) {
    //            semanticAnalyzer.errorHandler().addError(new SyntaxASTError<>(this.getSyntaxAST().getInvokedIdentifierStatementAST(), "Couldn't analyze this AST because there was no target identifier found."));
    //            return null;
    //        }
    //
    //        semanticAnalyzer.errorHandler().addError(new SemanticASTError<>(abstractSemanticAST, "Couldn't analyze this AST because the target identifier isn't supported."));
    //        return null;
    //    }
    
}
