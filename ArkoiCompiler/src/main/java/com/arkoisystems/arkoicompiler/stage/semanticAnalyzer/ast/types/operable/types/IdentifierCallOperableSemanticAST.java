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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class IdentifierCallOperableSemanticAST extends AbstractOperableSemanticAST<IdentifierCallOperableSyntaxAST>
{
    
    @Nullable
    private AbstractSemanticAST<?> foundIdentifier;
    
    
    public IdentifierCallOperableSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NonNull final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierCallOperableSyntaxAST, ASTType.IDENTIFIER_CALL_OPERABLE);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── fileLocal: " + this.isFileLocal());
        printStream.println(indents + "└── identifier: " + (this.getFoundIdentifier() == null ? null : ""));
        if (this.getFoundIdentifier() != null)
            this.getFoundIdentifier().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Nullable
    @Override
    public TypeKind getTypeKind() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        if (this.getFoundIdentifier() == null)
            return null;
        
        final AbstractSemanticAST<?> foundIdentifier = this.getFoundIdentifier();
        if (foundIdentifier instanceof VariableDefinitionSemanticAST) {
            final VariableDefinitionSemanticAST variableDefinitionSemanticAST = (VariableDefinitionSemanticAST) foundIdentifier;
            if(variableDefinitionSemanticAST.getVariableExpression() == null)
                return null;
            return variableDefinitionSemanticAST.getVariableExpression().getTypeKind();
        } else if (foundIdentifier instanceof ParameterSemanticAST) {
            final ParameterSemanticAST parameterSemanticAST = (ParameterSemanticAST) foundIdentifier;
            if(parameterSemanticAST.getParameterType() == null)
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
    
    
    public boolean isFileLocal() {
        return this.getSyntaxAST().isFileLocal();
    }
    
    
    @Nullable
    public AbstractSemanticAST<?> getFoundIdentifier() {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        Objects.requireNonNull(this.getSemanticAnalyzer().getRootSemanticAST());
        
        if (this.foundIdentifier == null) {
            AbstractSemanticAST<?> abstractSemanticAST = null;
            if (this.isFileLocal() && this.getLastContainerAST() instanceof FunctionDefinitionSemanticAST) {
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
