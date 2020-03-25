/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AbstractExpressionSemanticAST<T extends AbstractSyntaxAST> extends AbstractOperableSemanticAST<T>
{
    
    public AbstractExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final T syntaxAST, @NonNull final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    
    @Nullable
    public TypeKind binAdd(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.BINARY_ADDITION_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind binSub(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.BINARY_SUBTRACTION_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind binMul(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.BINARY_MULTIPLICATION_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind binDiv(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.BINARY_DIVISION_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind binMod(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.BINARY_MODULO_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind binExp(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.BINARY_EXPONENTIAL_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind assign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.ASSIGN_ASSIGNMENT_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind addAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind subAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind mulAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind divAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind modAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind equal(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.EQUAL_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind notEqual(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.NOT_EQUAL_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind logicalOr(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.LOGICAL_OR_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind logicalAnd(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.LOGICAL_AND_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind postfixAdd(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.POSTFIX_ADD_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind postfixSub(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.POSTFIX_SUB_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind prefixAdd(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.PREFIX_ADD_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind prefixSub(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.PREFIX_SUB_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind prefixNegate(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.PREFIX_NEGATE_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind prefixAffirm(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST, 
                SemanticErrorType.PREFIX_AFFIRM_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind relationalLessThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.RELATIONAL_LESS_THAN_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind relationalGreaterThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.RELATIONAL_GREATER_THAN_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind relationalLessEqualThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.RELATIONAL_LESS_EQUAL_THAN_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind relationalGreaterEqualThan(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.RELATIONAL_GREATER_EQUAL_THAN_NOT_SUPPORTED
        );
        return null;
    }
    
    
    @Nullable
    public TypeKind relationalIs(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
    
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                new AbstractSemanticAST[] {
                        leftSideOperable,
                        rightSideOperable
                },
                SemanticErrorType.RELATIONAL_IS_NOT_SUPPORTED
        );
        return null;
    }
    
}
