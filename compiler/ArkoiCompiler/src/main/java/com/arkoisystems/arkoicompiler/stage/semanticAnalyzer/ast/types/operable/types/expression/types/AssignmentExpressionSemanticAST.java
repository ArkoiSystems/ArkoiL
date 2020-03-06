/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticErrorType;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.AbstractOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierInvokeOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;

public class AssignmentExpressionSemanticAST extends AbstractExpressionSemanticAST<AssignmentExpressionSyntaxAST>
{
    
    private AbstractOperableSemanticAST<?, ?> leftSideOperable, rightSideOperable;
    
    
    private TypeKind expressionType;
    
    
    public AssignmentExpressionSemanticAST(final SemanticAnalyzer semanticAnalyzer, final AbstractSemanticAST<?> lastContainerAST, final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, assignmentExpressionSyntaxAST, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    @Override
    public TypeKind getOperableObject() {
        if (this.expressionType == null) {
            if (this.getAssignmentOperator() == null)
                return null;
            if (this.getLeftSideOperable() == null)
                return null;
            if (this.getRightSideOperable() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getAssignmentOperator()) {
                case ASSIGN:
                    typeKind = this.assign(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case ADD_ASSIGN:
                    typeKind = this.addAssign(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case SUB_ASSIGN:
                    typeKind = this.subAssign(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case MUL_ASSIGN:
                    typeKind = this.mulAssign(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case DIV_ASSIGN:
                    typeKind = this.divAssign(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                case MOD_ASSIGN:
                    typeKind = this.modAssign(this.getLeftSideOperable(), this.getRightSideOperable());
                    break;
                default:
                    typeKind = null;
                    break;
            }
            return (this.expressionType = typeKind);
        }
        return this.expressionType;
    }
    
    
    public AbstractOperableSemanticAST<?, ?> getLeftSideOperable() {
        if (this.leftSideOperable == null)
            return (this.leftSideOperable = this.analyzeOperable(this.getSyntaxAST().getLeftSideOperable()));
        return this.leftSideOperable;
    }
    
    
    public AssignmentOperatorType getAssignmentOperator() {
        return this.getSyntaxAST().getAssignmentOperatorType();
    }
    
    
    public AbstractOperableSemanticAST<?, ?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    
    private AbstractOperableSemanticAST<?, ?> analyzeOperable(final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        if (abstractOperableSyntaxAST instanceof ParenthesizedExpressionSyntaxAST) {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) abstractOperableSyntaxAST;
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST
                    = new ParenthesizedExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), parenthesizedExpressionSyntaxAST);
            
            if (parenthesizedExpressionSemanticAST.getOperableObject() == null)
                return null;
            return parenthesizedExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            final NumberOperableSemanticAST numberOperableSemanticAST
                    = new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
            
            if (numberOperableSemanticAST.getOperableObject() == null)
                return null;
            return numberOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof StringOperableSyntaxAST) {
            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) abstractOperableSyntaxAST;
            final StringOperableSemanticAST stringOperableSemanticAST
                    = new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
            
            if (stringOperableSemanticAST.getOperableObject() == null)
                return null;
            return stringOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof BinaryExpressionSyntaxAST) {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) abstractOperableSyntaxAST;
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                    = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
            
            if (binaryExpressionSemanticAST.getOperableObject() == null)
                return null;
            return binaryExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
            
            if (prefixExpressionSemanticAST.getOperableObject() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof EqualityExpressionSyntaxAST) {
            final EqualityExpressionSyntaxAST equalityExpressionSyntaxAST = (EqualityExpressionSyntaxAST) abstractOperableSyntaxAST;
            final EqualityExpressionSemanticAST equalityExpressionSemanticAST
                    = new EqualityExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), equalityExpressionSyntaxAST);
            
            if (equalityExpressionSemanticAST.getOperableObject() == null)
                return null;
            return equalityExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof LogicalExpressionSyntaxAST) {
            final LogicalExpressionSyntaxAST logicalExpressionSyntaxAST = (LogicalExpressionSyntaxAST) abstractOperableSyntaxAST;
            final LogicalExpressionSemanticAST logicalExpressionSemanticAST
                    = new LogicalExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), logicalExpressionSyntaxAST);
            
            if (logicalExpressionSemanticAST.getOperableObject() == null)
                return null;
            return logicalExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PostfixExpressionSyntaxAST) {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST
                    = new PostfixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), postfixExpressionSyntaxAST);
            
            if (postfixExpressionSemanticAST.getOperableObject() == null)
                return null;
            return postfixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof RelationalExpressionSyntaxAST) {
            final RelationalExpressionSyntaxAST relationalExpressionSyntaxAST = (RelationalExpressionSyntaxAST) abstractOperableSyntaxAST;
            final RelationalExpressionSemanticAST relationalExpressionSemanticAST
                    = new RelationalExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), relationalExpressionSyntaxAST);
            
            if (relationalExpressionSemanticAST.getOperableObject() == null)
                return null;
            return relationalExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
    
            if (identifierCallOperableSemanticAST.getOperableObject() == null)
                return null;
            return identifierCallOperableSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierInvokeOperableSyntaxAST) {
            final IdentifierInvokeOperableSyntaxAST identifierInvokeOperableSyntaxAST = (IdentifierInvokeOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST
                    = new IdentifierInvokeOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierInvokeOperableSyntaxAST);
    
            if (identifierInvokeOperableSemanticAST.getOperableObject() == null)
                return null;
            return identifierInvokeOperableSemanticAST;
        } else {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.ASSIGN_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
    }
    
    
    @Override
    public TypeKind assign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if(rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if(leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Override
    public TypeKind addAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if(rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if(leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Override
    public TypeKind subAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if(rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if(leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Override
    public TypeKind mulAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if(rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            this.setFailed(true);
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if(leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Override
    public TypeKind divAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if(rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if(leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Override
    public TypeKind modAssign(final AbstractOperableSemanticAST<?, ?> leftSideOperable, final AbstractOperableSemanticAST<?, ?> rightSideOperable) {
        final AbstractOperableSemanticAST<?, ?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?, ?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if(rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            this.setFailed(true);
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if(leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    private AbstractOperableSemanticAST<?, ?> analyzeExpressionOperable(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        if(abstractOperableSemanticAST instanceof CollectionOperableSemanticAST)
            return null;
        return abstractOperableSemanticAST;
    }
    
    
    private AbstractOperableSemanticAST<?, ?> analyzeAssignedOperable(final AbstractOperableSemanticAST<?, ?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof IdentifierCallOperableSemanticAST) {
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) abstractOperableSemanticAST;
            final AbstractSemanticAST<?> foundIdentifier = identifierCallOperableSemanticAST.getFoundIdentifier();
            if (foundIdentifier instanceof VariableDefinitionSemanticAST)
                return abstractOperableSemanticAST;
        } else if (abstractOperableSemanticAST instanceof IdentifierInvokeOperableSemanticAST) {
            final IdentifierInvokeOperableSemanticAST identifierInvokeOperableSemanticAST = (IdentifierInvokeOperableSemanticAST) abstractOperableSemanticAST;
            final AbstractSemanticAST<?> invokePostStatement = identifierInvokeOperableSemanticAST.getInvokePostStatement();
            if (invokePostStatement instanceof AbstractOperableSemanticAST)
                return this.analyzeAssignedOperable((AbstractOperableSemanticAST<?, ?>) invokePostStatement);
        }
        return null;
    }
    
}
