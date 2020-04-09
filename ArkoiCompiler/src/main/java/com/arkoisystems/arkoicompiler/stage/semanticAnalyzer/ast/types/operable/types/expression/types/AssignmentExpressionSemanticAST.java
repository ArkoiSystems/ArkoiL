/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.OperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.ExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.AssignmentExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Objects;

public class AssignmentExpressionSemanticAST extends ExpressionSemanticAST<AssignmentExpressionSyntaxAST>
{
    
    @Getter
    @NotNull
    private final AssignmentOperatorType assignmentOperatorType = this.checkAssignmentOperatorType();
    @Getter
    @Nullable
    private final OperableSemanticAST<?> rightSideOperable = this.checkRightSideOperable();
    @Getter
    @Nullable
    private final OperableSemanticAST<?> leftSideOperable = this.checkLeftSideOperable();
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public AssignmentExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, assignmentExpressionSyntaxAST, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.printf("%s├── left:%n", indents);
        printStream.printf("%s│   └── %s%n", indents, this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null);
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSemanticAST(printStream, indents + "│        ");
        printStream.printf("%s├── operator: %s%n", indents, this.getAssignmentOperatorType());
        printStream.printf("%s└── right:%n", indents);
        printStream.printf("%s    └── %s%n", indents, this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null);
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @NotNull
    private TypeKind checkTypeKind() {
        if (this.getLeftSideOperable() == null)
            return TypeKind.UNDEFINED;
        return this.getLeftSideOperable().getTypeKind();
    }
    
    
    @Nullable
    private OperableSemanticAST<?> checkLeftSideOperable() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getLeftSideOperable(), this.getFailedSupplier("syntaxAST.leftSideOperable must not be null."));
        
        final OperableSemanticAST<?> operableSemanticAST = this.getSpecifiedOperable(this.getSyntaxAST().getLeftSideOperable(), Collections.singletonList(
                IdentifierCallSyntaxAST.class
        ));
        if (operableSemanticAST == null)
            return this.addError(
                    null,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getLeftSideOperable(),
                    SemanticErrorType.ASSIGN_OPERABLE_NOT_SUPPORTED
            );
        
        if (this.getRightSideOperable() == null)
            return operableSemanticAST;
        
        switch (this.getAssignmentOperatorType()) {
            case ASSIGN:
                if (operableSemanticAST.getTypeKind() != this.getRightSideOperable().getTypeKind())
                    return this.addError(
                            null,
                            this.getSemanticAnalyzer().getArkoiClass(),
                            this.getSyntaxAST().getLeftSideOperable(),
                            "17"
                    );
                break;
            case ADD_ASSIGN:
            case DIV_ASSIGN:
            case MOD_ASSIGN:
            case MUL_ASSIGN:
            case SUB_ASSIGN:
                if (!this.getRightSideOperable().getTypeKind().isNumeric())
                    return this.addError(
                            null,
                            this.getSemanticAnalyzer().getArkoiClass(),
                            this.getSyntaxAST().getLeftSideOperable(),
                            "18"
                    );
                break;
            default:
                return this.addError(
                        null,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST().getLeftSideOperable(),
                        "17"
                );
        }
        return operableSemanticAST;
    }
    
    
    @NotNull
    private AssignmentOperatorType checkAssignmentOperatorType() {
        Objects.requireNonNull(this.getSyntaxAST().getAssignmentOperatorType(), this.getFailedSupplier("syntaxAST.assignmentOperatorType must not be null."));
        return this.getSyntaxAST().getAssignmentOperatorType();
    }
    
    
    @Nullable
    private OperableSemanticAST<?> checkRightSideOperable() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getRightSideOperable(), this.getFailedSupplier("syntaxAST.rightSideOperable must not be null."));
        
        final OperableSemanticAST<?> operableSemanticAST = this.getSpecifiedOperable(this.getSyntaxAST().getRightSideOperable(), null);
        if (operableSemanticAST == null)
            return this.addError(
                    null,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getRightSideOperable(),
                    SemanticErrorType.ASSIGN_OPERABLE_NOT_SUPPORTED
            );
        
        switch (this.getAssignmentOperatorType()) {
            case ASSIGN:
                break;
            case ADD_ASSIGN:
            case DIV_ASSIGN:
            case MOD_ASSIGN:
            case MUL_ASSIGN:
            case SUB_ASSIGN:
                if (!operableSemanticAST.getTypeKind().isNumeric())
                    return this.addError(
                            null,
                            this.getSemanticAnalyzer().getArkoiClass(),
                            this.getSyntaxAST().getRightSideOperable(),
                            "20"
                    );
                break;
            default:
                return this.addError(
                        null,
                        this.getSemanticAnalyzer().getArkoiClass(),
                        this.getSyntaxAST().getLeftSideOperable(),
                        "21"
                );
        }
        return operableSemanticAST;
    }
    
}
