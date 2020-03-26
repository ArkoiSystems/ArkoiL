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
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.CollectionOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.IdentifierCallOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.NumberOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.StringOperableSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.operable.types.expression.AbstractExpressionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.types.statements.VariableDefinitionSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.IdentifierCallOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.NumberOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.StringOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

public class AssignmentExpressionSemanticAST extends AbstractExpressionSemanticAST<AssignmentExpressionSyntaxAST>
{
    
    @Nullable
    private AbstractOperableSemanticAST<?> leftSideOperable, rightSideOperable;
    
    
    @Nullable
    private TypeKind expressionType;
    
    
    public AssignmentExpressionSemanticAST(@Nullable final SemanticAnalyzer semanticAnalyzer, @Nullable final AbstractSemanticAST<?> lastContainerAST, @NotNull final AssignmentExpressionSyntaxAST assignmentExpressionSyntaxAST) {
        super(semanticAnalyzer, lastContainerAST, assignmentExpressionSyntaxAST, ASTType.ASSIGNMENT_EXPRESSION);
    }
    
    
    @Override
    public void printSemanticAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSemanticAST(printStream, indents + "│        ");
        printStream.println(indents + "├── operator: " + this.getAssignmentOperatorType());
        printStream.println(indents + "└── right:");
        printStream.println(indents + "    └── " + (this.getRightSideOperable() != null ? this.getRightSideOperable().getClass().getSimpleName() : null));
        if (this.getRightSideOperable() != null)
            this.getRightSideOperable().printSemanticAST(printStream, indents + "        ");
    }
    
    
    @Nullable
    @Override
    public TypeKind getTypeKind() {
        if (this.expressionType == null) {
            if (this.getLeftSideOperable() == null) {
                this.getRightSideOperable();
                return null;
            } else if (this.getRightSideOperable() == null) {
                return null;
            } else if (this.getAssignmentOperatorType() == null)
                return null;
            
            final TypeKind typeKind;
            switch (this.getAssignmentOperatorType()) {
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
    
    
    @Nullable
    public AbstractOperableSemanticAST<?> getLeftSideOperable() {
        if (this.leftSideOperable == null)
            return (this.leftSideOperable = this.analyzeOperable(this.getSyntaxAST().getLeftSideOperable()));
        return this.leftSideOperable;
    }
    
    
    @Nullable
    public AssignmentOperatorType getAssignmentOperatorType() {
        return this.getSyntaxAST().getAssignmentOperatorType();
    }
    
    
    @Nullable
    public AbstractOperableSemanticAST<?> getRightSideOperable() {
        if (this.rightSideOperable == null)
            return (this.rightSideOperable = this.analyzeOperable(this.getSyntaxAST().getRightSideOperable()));
        return this.rightSideOperable;
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeOperable(@Nullable final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        if (abstractOperableSyntaxAST instanceof ParenthesizedExpressionSyntaxAST) {
            final ParenthesizedExpressionSyntaxAST parenthesizedExpressionSyntaxAST = (ParenthesizedExpressionSyntaxAST) abstractOperableSyntaxAST;
            final ParenthesizedExpressionSemanticAST parenthesizedExpressionSemanticAST
                    = new ParenthesizedExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), parenthesizedExpressionSyntaxAST);
            
            if (parenthesizedExpressionSemanticAST.getTypeKind() == null)
                return null;
            return parenthesizedExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof NumberOperableSyntaxAST) {
            final NumberOperableSyntaxAST numberOperableSyntaxAST = (NumberOperableSyntaxAST) abstractOperableSyntaxAST;
            return new NumberOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), numberOperableSyntaxAST);
        } else if (abstractOperableSyntaxAST instanceof StringOperableSyntaxAST) {
            final StringOperableSyntaxAST stringOperableSyntaxAST = (StringOperableSyntaxAST) abstractOperableSyntaxAST;
            return new StringOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), stringOperableSyntaxAST);
        } else if (abstractOperableSyntaxAST instanceof BinaryExpressionSyntaxAST) {
            final BinaryExpressionSyntaxAST binaryExpressionSyntaxAST = (BinaryExpressionSyntaxAST) abstractOperableSyntaxAST;
            final BinaryExpressionSemanticAST binaryExpressionSemanticAST
                    = new BinaryExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), binaryExpressionSyntaxAST);
            
            if (binaryExpressionSemanticAST.getTypeKind() == null)
                return null;
            return binaryExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PrefixExpressionSyntaxAST) {
            final PrefixExpressionSyntaxAST prefixExpressionSyntaxAST = (PrefixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PrefixExpressionSemanticAST prefixExpressionSemanticAST
                    = new PrefixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), prefixExpressionSyntaxAST);
            
            if (prefixExpressionSemanticAST.getTypeKind() == null)
                return null;
            return prefixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof EqualityExpressionSyntaxAST) {
            final EqualityExpressionSyntaxAST equalityExpressionSyntaxAST = (EqualityExpressionSyntaxAST) abstractOperableSyntaxAST;
            final EqualityExpressionSemanticAST equalityExpressionSemanticAST
                    = new EqualityExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), equalityExpressionSyntaxAST);
            
            if (equalityExpressionSemanticAST.getTypeKind() == null)
                return null;
            return equalityExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof LogicalExpressionSyntaxAST) {
            final LogicalExpressionSyntaxAST logicalExpressionSyntaxAST = (LogicalExpressionSyntaxAST) abstractOperableSyntaxAST;
            final LogicalExpressionSemanticAST logicalExpressionSemanticAST
                    = new LogicalExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), logicalExpressionSyntaxAST);
            
            if (logicalExpressionSemanticAST.getTypeKind() == null)
                return null;
            return logicalExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof PostfixExpressionSyntaxAST) {
            final PostfixExpressionSyntaxAST postfixExpressionSyntaxAST = (PostfixExpressionSyntaxAST) abstractOperableSyntaxAST;
            final PostfixExpressionSemanticAST postfixExpressionSemanticAST
                    = new PostfixExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), postfixExpressionSyntaxAST);
            
            if (postfixExpressionSemanticAST.getTypeKind() == null)
                return null;
            return postfixExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof RelationalExpressionSyntaxAST) {
            final RelationalExpressionSyntaxAST relationalExpressionSyntaxAST = (RelationalExpressionSyntaxAST) abstractOperableSyntaxAST;
            final RelationalExpressionSemanticAST relationalExpressionSemanticAST
                    = new RelationalExpressionSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), relationalExpressionSyntaxAST);
            
            if (relationalExpressionSemanticAST.getTypeKind() == null)
                return null;
            return relationalExpressionSemanticAST;
        } else if (abstractOperableSyntaxAST instanceof IdentifierCallOperableSyntaxAST) {
            final IdentifierCallOperableSyntaxAST identifierCallOperableSyntaxAST = (IdentifierCallOperableSyntaxAST) abstractOperableSyntaxAST;
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST
                    = new IdentifierCallOperableSemanticAST(this.getSemanticAnalyzer(), this.getLastContainerAST(), identifierCallOperableSyntaxAST);
            
            if (identifierCallOperableSemanticAST.getTypeKind() == null)
                return null;
            return identifierCallOperableSemanticAST;
        } else if (abstractOperableSyntaxAST != null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    abstractOperableSyntaxAST,
                    SemanticErrorType.ASSIGN_OPERABLE_NOT_SUPPORTED
            );
        }
        return null;
    }
    
    
    @Nullable
    @Override
    public TypeKind assign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if (leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Nullable
    @Override
    public TypeKind addAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if (leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_ADD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Nullable
    @Override
    public TypeKind subAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if (leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_SUB_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Nullable
    @Override
    public TypeKind mulAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            this.failed();
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if (leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_MUL_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Nullable
    @Override
    public TypeKind divAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if (leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_DIV_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Nullable
    @Override
    public TypeKind modAssign(@NotNull final AbstractOperableSemanticAST<?> leftSideOperable, @NotNull final AbstractOperableSemanticAST<?> rightSideOperable) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        final AbstractOperableSemanticAST<?> leftAssignedOperable = this.analyzeAssignedOperable(leftSideOperable);
        if (leftAssignedOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    leftSideOperable,
                    SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        final AbstractOperableSemanticAST<?> rightExpressionOperable = this.analyzeExpressionOperable(rightSideOperable);
        if (rightExpressionOperable == null) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightSideOperable,
                    SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            this.failed();
            return null;
        }
        final TypeKind leftTypeKind = TypeKind.getTypeKind(leftAssignedOperable), rightTypeKind = TypeKind.getTypeKind(rightExpressionOperable);
        if (leftTypeKind != rightTypeKind) {
            this.addError(
                    this.getSemanticAnalyzer().getArkoiClass(),
                    rightExpressionOperable,
                    SemanticErrorType.ASSIGN_MOD_ASSIGNMENT_OPERABLE_NOT_SUPPORTED
            );
            return null;
        }
        return leftTypeKind;
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeExpressionOperable(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof CollectionOperableSemanticAST)
            return null;
        return abstractOperableSemanticAST;
    }
    
    
    @Nullable
    private AbstractOperableSemanticAST<?> analyzeAssignedOperable(@NotNull final AbstractOperableSemanticAST<?> abstractOperableSemanticAST) {
        if (abstractOperableSemanticAST instanceof IdentifierCallOperableSemanticAST) {
            final IdentifierCallOperableSemanticAST identifierCallOperableSemanticAST = (IdentifierCallOperableSemanticAST) abstractOperableSemanticAST;
            final AbstractSemanticAST<?> foundIdentifier = identifierCallOperableSemanticAST.getFoundIdentifier();
            if (foundIdentifier instanceof VariableDefinitionSemanticAST)
                return abstractOperableSemanticAST;
        }
        return null;
    }
    
}
