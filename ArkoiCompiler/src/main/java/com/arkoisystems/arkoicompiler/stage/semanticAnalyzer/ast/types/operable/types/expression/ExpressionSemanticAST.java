/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.api.ICompilerSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.ArkoiSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.OperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;

public class ExpressionSemanticAST<T extends OperableSyntaxAST> extends OperableSemanticAST<T>
{
    
    
    protected ExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final T syntaxAST, @NotNull final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    
    public ExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final ICompilerSemanticAST<?> lastContainerAST, @NotNull final T syntaxAST) {
        this(semanticAnalyzer, lastContainerAST, syntaxAST, ASTType.EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
    
    //    @NotNull
    //    public TypeKind binAdd(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null."));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.BINARY_ADDITION_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind binSub(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.BINARY_SUBTRACTION_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind binMul(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.BINARY_MULTIPLICATION_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind binDiv(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.BINARY_DIVISION_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind binMod(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.BINARY_MODULO_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind binExp(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.BINARY_EXPONENTIAL_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind assign(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.ASSIGN_ASSIGNMENT_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind addAssign(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind subAssign(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind mulAssign(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind divAssign(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind modAssign(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind equal(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.EQUAL_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind notEqual(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.NOT_EQUAL_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind logicalOr(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.LOGICAL_OR_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind logicalAnd(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.LOGICAL_AND_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind postfixAdd(@NotNull final OperableSemanticAST<?> leftSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                leftSideOperable,
    //                SemanticErrorType.POSTFIX_ADD_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind postfixSub(@NotNull final OperableSemanticAST<?> leftSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                leftSideOperable,
    //                SemanticErrorType.POSTFIX_SUB_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind prefixAdd(@NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                rightSideOperable,
    //                SemanticErrorType.PREFIX_ADD_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind prefixSub(@NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                rightSideOperable,
    //                SemanticErrorType.PREFIX_SUB_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind prefixNegate(@NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                rightSideOperable,
    //                SemanticErrorType.PREFIX_NEGATE_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind prefixAffirm(@NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                rightSideOperable,
    //                SemanticErrorType.PREFIX_AFFIRM_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind relationalLessThan(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.RELATIONAL_LESS_THAN_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind relationalGreaterThan(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.RELATIONAL_GREATER_THAN_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind relationalLessEqualThan(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.RELATIONAL_LESS_EQUAL_THAN_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind relationalGreaterEqualThan(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.RELATIONAL_GREATER_EQUAL_THAN_NOT_SUPPORTED
    //        );
    //    }
    //
    //
    //    @NotNull
    //    public TypeKind relationalIs(@NotNull final OperableSemanticAST<?> leftSideOperable, @NotNull final OperableSemanticAST<?> rightSideOperable) {
    //        Objects.requireNonNull(this.getSemanticAnalyzer(), this.getFailedSupplier("semanticAnalyzer must not be null"));
    //        return this.addError(
    //                TypeKind.UNDEFINED,
    //                this.getSemanticAnalyzer().getArkoiClass(),
    //                new AbstractSemanticAST[] {
    //                        leftSideOperable,
    //                        rightSideOperable
    //                },
    //                SemanticErrorType.RELATIONAL_IS_NOT_SUPPORTED
    //        );
    //    }
    
}
