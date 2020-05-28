/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on April 17, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.semantic.routines;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.error.ArkoiError;
import com.arkoisystems.arkoicompiler.error.ErrorPosition;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Block;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Root;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Type;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.argument.Argument;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.argument.ArgumentList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.Operable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.CollectionOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.IdentifierOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.NumberOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.StringOperable;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.ExpressionList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.parameter.Parameter;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.parameter.ParameterList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnStatement;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableStatement;
import com.arkoisystems.arkoicompiler.stage.semantic.Semantic;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
public class TypeVisitor implements IVisitor<TypeKind>, IFailed
{
    
    @NotNull
    private final Semantic semantic;
    
    private boolean failed;
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Type type) {
        return type.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Root root) {
        for (final ArkoiNode astNode : root.getNodes())
            this.visit(astNode);
        return root.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParameterList parameterList) {
        for (final Parameter parameterAST : parameterList.getParameters())
            this.visit(parameterAST);
        return parameterList.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Parameter parameter) {
        return parameter.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Block block) {
        for (final ArkoiNode node : block.getNodes())
            this.visit(node);
        return block.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ArgumentList argumentList) {
        for (final Argument argumentAST : argumentList.getArguments())
            this.visit(argumentAST);
        return argumentList.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Argument argument) {
        Objects.requireNonNull(argument.getExpression(), "argumentNode.argumentExpression must not be null.");
        this.visit(argument.getExpression());
        return argument.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull FunctionStatement functionStatement) {
        Objects.requireNonNull(functionStatement.getParser(), "functionNode.parser must not be null.");
        Objects.requireNonNull(functionStatement.getBlock(), "functionNode.block must not be null.");
        Objects.requireNonNull(functionStatement.getReturnType(), "functionNode.returnType must not be null.");
        Objects.requireNonNull(functionStatement.getParameters(), "functionNode.parameters must not be null.");
        
        this.visit(functionStatement.getParameters());
        
        final TypeKind expectedType = this.visit(functionStatement.getReturnType());
        final TypeKind givenType = this.visit(functionStatement.getBlock());
        
        if (functionStatement.getBlock().getBlockType() != BlockType.NATIVE && expectedType != givenType)
            return this.addError(
                    TypeKind.UNDEFINED,
                    functionStatement.getParser().getCompilerClass(),
                    functionStatement.getBlock(),
                    "The block return type doesn't match that of the function."
            );
        
        return functionStatement.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ImportStatement importStatement) {
        return importStatement.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ReturnStatement returnStatement) {
        if (returnStatement.getExpression() != null)
            this.visit(returnStatement.getExpression());
        return returnStatement.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull VariableStatement variableStatement) {
        Objects.requireNonNull(variableStatement.getExpression(), "variableNode.variableExpression must not be null.");
        this.visit(variableStatement.getExpression());
        return variableStatement.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull StringOperable stringOperable) {
        return stringOperable.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull NumberOperable numberOperable) {
        return numberOperable.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull IdentifierOperable identifierOperable) {
        Objects.requireNonNull(identifierOperable.getParser(), "identifierCallNode.parser must not be null.");
        
        if (identifierOperable.isFunctionCall()) {
            Objects.requireNonNull(identifierOperable.getExpressionList(), "identifierOperable.expressionList must not be null.");
            this.visit(identifierOperable.getExpressionList());
        }
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(identifierOperable.getParser().getCompilerClass().getSemantic());
        scopeVisitor.visit(identifierOperable.getParser().getRootAST());
        final ArkoiNode resultNode = scopeVisitor.visit(identifierOperable);
        if (scopeVisitor.isFailed())
            this.setFailed(true);
        if (resultNode != null)
            this.visit(resultNode);
        
        return identifierOperable.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull CollectionOperable collectionOperable) {
        for (final Operable operable : collectionOperable.getExpressions())
            this.visit(operable);
        return collectionOperable.getTypeKind();
    }
    
    @Override
    public TypeKind visit(final @NotNull ExpressionList expressionList) {
        for (final Operable operable : expressionList.getExpressions())
            this.visit(operable);
        return expressionList.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull AssignmentExpression assignmentExpressionNode) {
        Objects.requireNonNull(assignmentExpressionNode.getLeftHandSide(), "assignmentExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionNode.getRightHandSide(), "assignmentExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionNode.getParser(), "assignmentExpressionNode.parser must not be null.");
        
        final TypeKind leftHandSide = this.visit(assignmentExpressionNode.getLeftHandSide());
        final TypeKind rightHandSide = this.visit(assignmentExpressionNode.getRightHandSide());
        if (rightHandSide != leftHandSide)
            return this.addError(
                    TypeKind.UNDEFINED,
                    assignmentExpressionNode.getParser().getCompilerClass(),
                    assignmentExpressionNode.getRightHandSide(),
                    "Left type doesn't match the right one."
            );
        
        return assignmentExpressionNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull BinaryExpression binaryExpressionNode) {
        Objects.requireNonNull(binaryExpressionNode.getLeftHandSide(), "binaryExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionNode.getRightHandSide(), "binaryExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionNode.getOperatorType(), "binaryExpressionNode.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryExpressionNode.getParser(), "binaryExpressionNode.parser must not be null.");
        
        final TypeKind leftSideType = this.visit(binaryExpressionNode.getLeftHandSide());
        final TypeKind rightSideType = this.visit(binaryExpressionNode.getRightHandSide());
        if (leftSideType.isNumeric() && rightSideType.isNumeric())
            return binaryExpressionNode.getTypeKind();
        
        final java.lang.String errorMessage;
        final ArkoiNode targetNode;
        if (!leftSideType.isNumeric() && !rightSideType.isNumeric()) {
            targetNode = binaryExpressionNode;
            errorMessage = "Both sides are not numeric.";
        } else if (!leftSideType.isNumeric()) {
            targetNode = binaryExpressionNode.getLeftHandSide();
            errorMessage = "Left side is not numeric.";
        } else {
            targetNode = binaryExpressionNode.getRightHandSide();
            errorMessage = "Right side is not numeric.";
        }
        
        return this.addError(
                TypeKind.UNDEFINED,
                binaryExpressionNode.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull EqualityExpression equalityExpressionNode) {
        Objects.requireNonNull(equalityExpressionNode.getLeftHandSide(), "equalityExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionNode.getRightHandSide(), "equalityExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionNode.getParser(), "equalityExpressionNode.parser must not be null.");
        
        final TypeKind leftSideType = this.visit(equalityExpressionNode.getLeftHandSide());
        final TypeKind rightSideType = this.visit(equalityExpressionNode.getRightHandSide());
        if (leftSideType == TypeKind.BOOLEAN && rightSideType == TypeKind.BOOLEAN)
            return equalityExpressionNode.getTypeKind();
        
        final java.lang.String errorMessage;
        final ArkoiNode targetNode;
        if (leftSideType != TypeKind.BOOLEAN && rightSideType != TypeKind.BOOLEAN) {
            targetNode = equalityExpressionNode;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOLEAN) {
            targetNode = equalityExpressionNode.getLeftHandSide();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = equalityExpressionNode.getRightHandSide();
            errorMessage = "Right side is not a boolean.";
        }
        
        return this.addError(
                TypeKind.UNDEFINED,
                equalityExpressionNode.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull LogicalExpression logicalExpressionNode) {
        Objects.requireNonNull(logicalExpressionNode.getLeftHandSide(), "logicalExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionNode.getRightHandSide(), "logicalExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionNode.getParser(), "logicalExpressionNode.parser must not be null.");
        
        final TypeKind leftSideType = this.visit(logicalExpressionNode.getLeftHandSide());
        final TypeKind rightSideType = this.visit(logicalExpressionNode.getRightHandSide());
        if (leftSideType == TypeKind.BOOLEAN && rightSideType == TypeKind.BOOLEAN)
            return logicalExpressionNode.getTypeKind();
        
        final java.lang.String errorMessage;
        final ArkoiNode targetNode;
        if (leftSideType != TypeKind.BOOLEAN && rightSideType != TypeKind.BOOLEAN) {
            targetNode = logicalExpressionNode;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOLEAN) {
            targetNode = logicalExpressionNode.getLeftHandSide();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = logicalExpressionNode.getRightHandSide();
            errorMessage = "Right side is not a boolean.";
        }
        
        return this.addError(
                TypeKind.UNDEFINED,
                logicalExpressionNode.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParenthesizedExpression parenthesizedExpressionNode) {
        Objects.requireNonNull(parenthesizedExpressionNode.getExpression(), "parenthesizedExpressionNode.expression must not be null.");
        this.visit(parenthesizedExpressionNode.getExpression());
        return parenthesizedExpressionNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull PostfixExpression postfixExpressionNode) {
        Objects.requireNonNull(postfixExpressionNode.getLeftHandSide(), "postfixExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(postfixExpressionNode.getParser(), "postfixExpressionNode.parser must not be null.");
        
        final TypeKind leftSideType = this.visit(postfixExpressionNode.getLeftHandSide());
        if (!leftSideType.isNumeric())
            return this.addError(
                    TypeKind.UNDEFINED,
                    postfixExpressionNode.getParser().getCompilerClass(),
                    postfixExpressionNode,
                    "Left side is not numeric."
            );
        
        return postfixExpressionNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull PrefixExpression prefixExpressionNode) {
        Objects.requireNonNull(prefixExpressionNode.getRightHandSide(), "prefixExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixExpressionNode.getOperatorType(), "prefixExpressionNode.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixExpressionNode.getParser(), "prefixExpressionNode.parser must not be null.");
        
        final TypeKind rightHandSide = this.visit(prefixExpressionNode.getRightHandSide());
        if (!rightHandSide.isNumeric())
            return this.addError(
                    TypeKind.UNDEFINED,
                    prefixExpressionNode.getParser().getCompilerClass(),
                    prefixExpressionNode,
                    "Right side is not numeric."
            );
        
        return prefixExpressionNode.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull RelationalExpression relationalExpressionNode) {
        Objects.requireNonNull(relationalExpressionNode.getLeftHandSide(), "relationalExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionNode.getRightHandSide(), "relationalExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionNode.getParser(), "relationalExpressionNode.parser must not be null.");
        
        final TypeKind leftSideType = this.visit(relationalExpressionNode.getLeftHandSide());
        final TypeKind rightSideType = this.visit(relationalExpressionNode.getRightHandSide());
        if (leftSideType == TypeKind.BOOLEAN && rightSideType == TypeKind.BOOLEAN)
            return TypeKind.BOOLEAN;
        
        final java.lang.String errorMessage;
        final ArkoiNode targetNode;
        if (leftSideType != TypeKind.BOOLEAN && rightSideType != TypeKind.BOOLEAN) {
            targetNode = relationalExpressionNode;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOLEAN) {
            targetNode = relationalExpressionNode.getLeftHandSide();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = relationalExpressionNode.getRightHandSide();
            errorMessage = "Right side is not a boolean.";
        }
        
        return this.addError(
                TypeKind.UNDEFINED,
                relationalExpressionNode.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiNode astNode, final @NotNull java.lang.String message, final @NotNull Object... arguments) {
        compilerClass.getSemantic().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(Objects.requireNonNull(astNode.getLineRange(), "astNode.lineRange must not be null."))
                        .charStart(Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart())
                        .charEnd(Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getCharEnd())
                        .build()))
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.setFailed(true);
        return errorSource;
    }
    
}
