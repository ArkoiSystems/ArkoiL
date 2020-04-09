/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.ParameterSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.OperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.FunctionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.FunctionCallPartSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class IdentifierCallSemanticAST extends OperableSemanticAST<IdentifierCallSyntaxAST>
{
    
    
    @Getter
    private final boolean isFileLocal = this.checkFileLocal();
    
    
    @Getter
    @Nullable
    private final ArkoiSemanticAST<?> calledIdentifier = this.checkCalledIdentifier();
    
    
    @Getter
    @Nullable
    private final FunctionCallPartSyntaxAST calledFunctionPart = this.checkCalledFunctionPart();
    
    
    @Getter
    @Nullable
    private final IdentifierCallSyntaxAST nextIdentifierCall = this.checkNextIdentifierCall();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public IdentifierCallSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NonNull final IdentifierCallSyntaxAST identifierCallSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, identifierCallSyntaxAST, ASTType.IDENTIFIER_CALL);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── fileLocal: %s%n", indents, this.isFileLocal());
        printStream.printf("%s└── identifier: %s%n", indents, this.getCalledIdentifier() != null ? "" : null);
        if (this.getCalledIdentifier() != null)
            this.getCalledIdentifier().printSemanticAST(printStream, indents + "        ");
    }
    
    
    private boolean checkFileLocal() {
        return this.getSyntaxAST().isFileLocal();
    }
    
    
    // TODO: Replace with a scope check
    @Nullable
    private ArkoiSemanticAST<?> checkCalledIdentifier() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSemanticAnalyzer().getRootSemanticAST(), this.getFailedSupplier("semanticAnalyzer.rootSemanticAST must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getCalledIdentifier(), this.getFailedSupplier("syntaxAST.calledIdentifier must not be null."));
        
        ArkoiSemanticAST<?> arkoiSemanticAST = null;
        if (this.isFileLocal() && this.getLastContainerAST() instanceof FunctionSemanticAST) {
            final FunctionSemanticAST functionSemanticAST = (FunctionSemanticAST) this.getLastContainerAST();
            arkoiSemanticAST = functionSemanticAST.findIdentifier(this.getSyntaxAST().getCalledIdentifier());
        }
        
        if (arkoiSemanticAST == null)
            arkoiSemanticAST = this.getSemanticAnalyzer().getRootSemanticAST().findIdentifier(this.getSyntaxAST().getCalledIdentifier());
        if (arkoiSemanticAST != null)
            return arkoiSemanticAST;
        
        return this.addError(
                null,
                this.getSemanticAnalyzer().getArkoiClass(),
                this.getSyntaxAST(),
                SemanticErrorType.IDENTIFIER_CALL_NO_SUCH_IDENTIFIER
        );
    }
    
    
    @Nullable
    private FunctionCallPartSyntaxAST checkCalledFunctionPart() {
        return null;
    }
    
    
    @Nullable
    private IdentifierCallSyntaxAST checkNextIdentifierCall() {
        return null;
    }
    
    
    @NotNull
    private TypeKind checkTypeKind() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        if (this.getCalledIdentifier() == null)
            return TypeKind.UNDEFINED;
        
        final ArkoiSemanticAST<?> foundIdentifier = this.getCalledIdentifier();
        if (foundIdentifier instanceof VariableSemanticAST) {
            final VariableSemanticAST variableSemanticAST = (VariableSemanticAST) foundIdentifier;
            return variableSemanticAST.getVariableExpression().getTypeKind();
        } else if (foundIdentifier instanceof ParameterSemanticAST) {
            final ParameterSemanticAST parameterSemanticAST = (ParameterSemanticAST) foundIdentifier;
            return parameterSemanticAST.getParameterType().getTypeKind();
        }
        
        return this.addError(
                TypeKind.UNDEFINED,
                this.getSemanticAnalyzer().getArkoiClass(),
                foundIdentifier,
                SemanticErrorType.IDENTIFIER_CALL_AST_NOT_SUPPORTED
        );
    }
    
}
