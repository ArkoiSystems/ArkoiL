/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.ParameterSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class IdentifierCallOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierCallOperableSyntaxAST, TypeKind>
{
    
    private AbstractSemanticAST<?> foundIdentifier;
    
    
    public IdentifierCallOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierCallOperableSyntaxAST, ASTType.IDENTIFIER_CALL_OPERABLE);
    }
    
    
    // TODO: Check for null safety.
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── access: " + this.getIdentifierAccess());
        printStream.println(indents + "└── identifier: ");
        this.getFoundIdentifier().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.getFoundIdentifier() == null)
            return null;
        
        final AbstractSemanticAST<?> foundIdentifier = this.getFoundIdentifier();
        if (foundIdentifier instanceof VariableDefinitionSemanticAST) {
            final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) foundIdentifier;
            if (variableDefinitionSemanticAST.getVariableExpression() == null)
                return null;
            return variableDefinitionSemanticAST.getVariableExpression().getOperableObject();
        } else if (foundIdentifier instanceof ParameterSemanticAST) {
            final ParameterSemanticAST parameterSemanticAST = (ParameterSemanticAST) foundIdentifier;
            if (parameterSemanticAST.getParameterName() == null)
                return null;
            if (parameterSemanticAST.getParameterType() == null)
                return null;
            return parameterSemanticAST.getParameterType().getTypeKind();
        } else {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    foundIdentifier,
                    SemanticErrorType.IDENTIFIER_CALL_AST_NOT_SUPPORTED
            );
            return null;
        }
    }
    
    
    public ASTAccess getIdentifierAccess() {
        return this.getSyntaxAST().getIdentifierAccess();
    }
    
    
    public AbstractSemanticAST<?> getFoundIdentifier() {
        if (this.foundIdentifier == null) {
            AbstractSemanticAST<?> abstractSemanticAST = null;
            if (this.getIdentifierAccess() != ASTAccess.THIS_ACCESS && this.getLastContainerAST() instanceof FunctionDefinitionSemanticAST) {
                final FunctionDefinitionSemanticAST functionDefinitionSemanticAST = (FunctionDefinitionSemanticAST) this.getLastContainerAST();
                abstractSemanticAST = functionDefinitionSemanticAST.findIdentifier(this.getSyntaxAST().getCalledIdentifier());
            }
            
            if (abstractSemanticAST == null)
                abstractSemanticAST = this.getSemanticAnalyzer().getRootSemanticAST().findIdentifier(this.getSyntaxAST().getCalledIdentifier());
            if (abstractSemanticAST == null) {
                this.addError(
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST(),
                        SemanticErrorType.IDENTIFIER_CALL_NO_SUCH_IDENTIFIER
                );
                return null;
            }
            return (this.foundIdentifier = abstractSemanticAST);
        }
        return this.foundIdentifier;
    }
    
}
