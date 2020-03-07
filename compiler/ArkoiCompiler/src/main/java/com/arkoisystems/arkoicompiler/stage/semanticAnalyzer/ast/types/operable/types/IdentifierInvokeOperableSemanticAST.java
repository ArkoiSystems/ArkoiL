/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.ImportDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

import java.io.PrintStream;

public class IdentifierInvokeOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierInvokeOperableSyntaxAST, TypeKind>
{
    
    private AbstractSemanticAST<?> invokedIdentifier;
    
    
    private AbstractSemanticAST<?> invokePostStatement;
    
    
    public IdentifierInvokeOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierInvokeOperableSyntaxAST, ASTType.IDENTIFIER_INVOKE_OPERABLE);
    }
    
    
    // TODO: Check for null safety.
    @Override
    public void printSemanticAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── access: " + this.getIdentifierAccess());
        printStream.println(indents + "├── identifier: ");
        this.getInvokedIdentifier().printSemanticAST(printStream, indents + "│       ");
        printStream.println(indents + "└── statement:");
        printStream.println(indents + "    └── " + this.getInvokePostStatement().getClass().getSimpleName());
        this.getInvokePostStatement().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.getInvokePostStatement() == null)
            return null;
        
        final AbstractSemanticAST<?> invokedPostStatement = this.getInvokePostStatement();
        if (invokedPostStatement instanceof FunctionInvokeOperableSemanticAST) {
            final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST = (FunctionInvokeOperableSemanticAST) invokedPostStatement;
            return functionInvokeOperableSemanticAST.getOperableObject();
        } else return null;
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
                this.addError(
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST(),
                        SemanticErrorType.IDENTIFIER_INVOKE_NO_SUCH_IDENTIFIER
                );
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
                if (importDefinitionSemanticAST.getImportTargetClass() == null) {
                    this.addError(
                            this.getSemanticAnalyzer().getArkoiClass(), this.getSyntaxAST().getInvokePostStatement(), SemanticErrorType.IDENTIFIER_INVOKE_IMPORT_NOT_VALID
                    );
                    return null;
                }
    
                if (this.getSyntaxAST().getInvokePostStatement() instanceof FunctionInvokeOperableSyntaxAST) {
                    final FunctionInvokeOperableSyntaxAST functionInvokeOperableSyntaxAST = (FunctionInvokeOperableSyntaxAST) this.getSyntaxAST().getInvokePostStatement();
                    final FunctionInvokeOperableSemanticAST functionInvokeOperableSemanticAST
                            = new FunctionInvokeOperableSemanticAST(importDefinitionSemanticAST.getImportTargetClass().getSemanticAnalyzer(), this, this.getLastContainerAST(), functionInvokeOperableSyntaxAST);
        
                    functionInvokeOperableSemanticAST.getInvokedFunction();
                    functionInvokeOperableSemanticAST.getInvokedExpressions();
        
                    if (functionInvokeOperableSemanticAST.isFailed())
                        this.failed();
                    this.invokePostStatement = functionInvokeOperableSemanticAST;
                } else {
                    this.addError(
                            this.getSemanticAnalyzer().getArkoiClass(), this.getSyntaxAST().getInvokePostStatement(), SemanticErrorType.IDENTIFIER_INVOKE_STATEMENT_NOT_SUPPORTED
                    );
                    return null;
                }
            }
        }
        return this.invokePostStatement;
    }
    
}
