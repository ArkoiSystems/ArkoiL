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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.BinaryExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class BinaryExpressionSemanticAST extends ExpressionSemanticAST<BinaryExpressionSyntaxAST>
{
    
    @Getter
    @Nullable
    private final OperableSemanticAST<?> leftSideOperable = this.checkLeftSideOperable();
    
    
    @Getter
    @NotNull
    private final BinaryOperatorType binaryOperatorType = this.checkBinaryOperatorType();
    
    
    @Getter
    @Nullable
    private final OperableSemanticAST<?> rightSideOperable = this.checkRightSideOperable();
    
    
    @Getter
    @NotNull
    private final TypeKind typeKind = this.checkTypeKind();
    
    
    public BinaryExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, binaryExpressionSyntaxAST, ASTType.BINARY_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSemanticAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getBinaryOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @NotNull
    private TypeKind checkTypeKind() {
        if (this.getLeftSideOperable() == null || this.getRightSideOperable() == null)
            return TypeKind.UNDEFINED;
        return this.getLeftSideOperable().getTypeKind().getPrecision() > this.getRightSideOperable().getTypeKind().getPrecision()
                ? this.getLeftSideOperable().getTypeKind()
                : this.getRightSideOperable().getTypeKind();
    }
    
    @Nullable
    private OperableSemanticAST<?> checkLeftSideOperable() {
        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
        Objects.requireNonNull(this.getSyntaxAST().getLeftSideOperable(), this.getFailedSupplier("syntaxAST.leftSideOperable must not be null."));
        
        final OperableSemanticAST<?> operableSemanticAST = this.getSpecifiedOperable(this.getSyntaxAST().getLeftSideOperable(), null);
        if (operableSemanticAST == null)
            return this.addError(
                    null,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getLeftSideOperable(),
                    SemanticErrorType.BINARY_OPERABLE_NOT_SUPPORTED
            );
        
        if (!operableSemanticAST.getTypeKind().isNumeric())
            return this.addError(
                    operableSemanticAST,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getLeftSideOperable(),
                    "22"
            );
        return operableSemanticAST;
    }
    
    
    @NotNull
    private BinaryOperatorType checkBinaryOperatorType() {
        Objects.requireNonNull(this.getSyntaxAST().getBinaryOperatorType(), this.getFailedSupplier("syntaxAST.binaryOperatorType must not be null."));
        return this.getSyntaxAST().getBinaryOperatorType();
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
                    SemanticErrorType.BINARY_OPERABLE_NOT_SUPPORTED
            );
        
        if (!operableSemanticAST.getTypeKind().isNumeric())
            return this.addError(
                    operableSemanticAST,
                    this.getSemanticAnalyzer().getArkoiClass(),
                    this.getSyntaxAST().getRightSideOperable(),
                    "23"
            );
        return operableSemanticAST;
    }
    
}