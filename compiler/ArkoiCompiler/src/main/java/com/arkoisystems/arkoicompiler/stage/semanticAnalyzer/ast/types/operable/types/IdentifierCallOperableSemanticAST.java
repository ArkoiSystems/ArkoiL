/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.ArgumentDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

public class IdentifierCallOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierCallOperableSyntaxAST, TypeKind>
{
    
    private AbstractSemanticAST<?> foundIdentifier;
    
    
    public IdentifierCallOperableSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierCallOperableSyntaxAST, ASTType.IDENTIFIER_CALL_OPERABLE);
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
        } else if (foundIdentifier instanceof ArgumentDefinitionSemanticAST) {
            final ArgumentDefinitionSemanticAST argumentDefinitionSemanticAST = (ArgumentDefinitionSemanticAST) foundIdentifier;
            if (argumentDefinitionSemanticAST.getArgumentName() == null)
                return null;
            if(argumentDefinitionSemanticAST.getArgumentType() == null)
                return null;
            return argumentDefinitionSemanticAST.getArgumentType().getTypeKind();
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
        if(this.foundIdentifier == null) {
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
