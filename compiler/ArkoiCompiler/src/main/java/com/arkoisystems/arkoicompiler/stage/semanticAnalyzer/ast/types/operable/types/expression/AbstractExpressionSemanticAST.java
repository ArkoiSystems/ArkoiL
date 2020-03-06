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

public class AbstractExpressionSemanticAST<T extends AbstractSyntaxAST> extends AbstractOperableSemanticAST<T, TypeKind>
{
    
    public AbstractExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final T syntaxAST, final ASTType astType) {
        super(semanticAnalyzer, lastContainerAST, syntaxAST, astType);
    }
    
    
    public TypeKind binAdd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind binSub(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind binMul(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind binDiv(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind binMod(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind binExp(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind assign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind addAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind subAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind mulAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind divAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind modAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind equal(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind notEqual(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind logicalOr(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind logicalAnd(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind postfixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.POSTFIX_ADD_NOT_SUPPORTED
        );
        return null;
    }
    
    
    public TypeKind postfixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.POSTFIX_SUB_NOT_SUPPORTED
        );
        return null;
    }
    
    
    public TypeKind prefixAdd(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(), abstractOperableSemanticAST, SemanticErrorType.PREFIX_ADD_NOT_SUPPORTED
        );
        return null;
    }
    
    
    public TypeKind prefixSub(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
               abstractOperableSemanticAST,
                SemanticErrorType.PREFIX_SUB_NOT_SUPPORTED
        );
        return null;
    }
    
    
    public TypeKind prefixNegate(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(),
                abstractOperableSemanticAST,
                SemanticErrorType.PREFIX_NEGATE_NOT_SUPPORTED
        );
        return null;
    }
    
    
    public TypeKind prefixAffirm(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        this.addError(
                this.getSemanticAnalyzer().getArkoiClass(), abstractOperableSemanticAST, SemanticErrorType.PREFIX_AFFIRM_NOT_SUPPORTED
        );
        return null;
    }
    
    
    public TypeKind relationalLessThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind relationalGreaterThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind relationalLessEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind relationalGreaterEqualThan(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
    
    
    public TypeKind relationalIs(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
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
