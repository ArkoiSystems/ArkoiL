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
    
    @NotNull
    private final ScopeVisitor scopeVisitor;
    
    private boolean failed;
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Type type) {
        return type.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Root root) {
        TypeKind typeKind = root.getTypeKind();
        for (final ArkoiNode astNode : root.getNodes()) {
            if (this.visit(astNode) == TypeKind.ERROR)
                typeKind = TypeKind.ERROR;
        }
        return typeKind;
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParameterList parameterList) {
        TypeKind typeKind = parameterList.getTypeKind();
        for (final Parameter parameter : parameterList.getParameters()) {
            if (this.visit(parameter) == TypeKind.ERROR)
                typeKind = TypeKind.ERROR;
        }
        return typeKind;
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Parameter parameter) {
        return parameter.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Block block) {
        TypeKind typeKind = block.getTypeKind();
        for (final ArkoiNode node : block.getNodes()) {
            if (this.visit(node) == TypeKind.ERROR)
                typeKind = TypeKind.ERROR;
        }
        return typeKind;
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ArgumentList argumentList) {
        TypeKind typeKind = argumentList.getTypeKind();
        for (final Argument argument : argumentList.getArguments()) {
            if (this.visit(argument) == TypeKind.ERROR)
                typeKind = TypeKind.ERROR;
        }
        return typeKind;
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull Argument argument) {
        Objects.requireNonNull(argument.getExpression(), "argumentNode.argumentExpression must not be null.");
        if (this.visit(argument.getExpression()) == TypeKind.ERROR)
            return TypeKind.ERROR;
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
        if (expectedType == TypeKind.ERROR || givenType == TypeKind.ERROR)
            return TypeKind.ERROR;
    
        if ((expectedType != TypeKind.AUTO && functionStatement.getBlock().getBlockType() != BlockType.NATIVE) &&
                expectedType != givenType)
            return this.addError(
                    TypeKind.ERROR,
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
        if (returnStatement.getExpression() != null && this.visit(returnStatement.getExpression()) == TypeKind.ERROR)
            return TypeKind.ERROR;
        return returnStatement.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull VariableStatement variableStatement) {
        Objects.requireNonNull(variableStatement.getExpression(), "variableNode.variableExpression must not be null.");
        if (this.visit(variableStatement.getExpression()) == TypeKind.ERROR)
            return TypeKind.ERROR;
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
        if (identifierOperable.isFunctionCall()) {
            Objects.requireNonNull(identifierOperable.getExpressionList(), "identifierOperable.expressionList must not be null.");
            this.visit(identifierOperable.getExpressionList());
        }
    
        return identifierOperable.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull CollectionOperable collectionOperable) {
        TypeKind typeKind = collectionOperable.getTypeKind();
        for (final Operable operable : collectionOperable.getExpressions()) {
            if (this.visit(operable) == TypeKind.ERROR)
                typeKind = TypeKind.ERROR;
        }
        return typeKind;
    }
    
    @Override
    public TypeKind visit(final @NotNull ExpressionList expressionList) {
        TypeKind typeKind = expressionList.getTypeKind();
        for (final Operable operable : expressionList.getExpressions()) {
            if (this.visit(operable) == TypeKind.ERROR)
                typeKind = TypeKind.ERROR;
        }
        return typeKind;
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull AssignmentExpression assignmentExpression) {
        Objects.requireNonNull(assignmentExpression.getLeftHandSide(), "assignmentExpression.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpression.getRightHandSide(), "assignmentExpression.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpression.getParser(), "assignmentExpression.parser must not be null.");
        
        if (!(assignmentExpression.getLeftHandSide() instanceof IdentifierOperable) ||
                ((IdentifierOperable) assignmentExpression.getLeftHandSide()).isFunctionCall()) {
            // TODO: 5/29/20 Check if its mutable or not
            return this.addError(
                    TypeKind.ERROR,
                    assignmentExpression.getParser().getCompilerClass(),
                    assignmentExpression.getLeftHandSide(),
                    "Left hand side it not an identifier operable which can be re-assigned."
            );
        }
        
        final TypeKind leftHandSide = this.visit(assignmentExpression.getLeftHandSide());
        final TypeKind rightHandSide = this.visit(assignmentExpression.getRightHandSide());
        if (leftHandSide == TypeKind.ERROR || rightHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
        
        if (rightHandSide != leftHandSide)
            return this.addError(
                    TypeKind.ERROR,
                    assignmentExpression.getParser().getCompilerClass(),
                    assignmentExpression.getRightHandSide(),
                    "Left type doesn't match the right one."
            );
    
        return assignmentExpression.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull BinaryExpression binaryExpression) {
        Objects.requireNonNull(binaryExpression.getLeftHandSide(), "binaryExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpression.getRightHandSide(), "binaryExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryExpression.getOperatorType(), "binaryExpressionNode.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryExpression.getParser(), "binaryExpressionNode.parser must not be null.");
    
        final TypeKind leftHandSide = this.visit(binaryExpression.getLeftHandSide());
        final TypeKind rightHandSide = this.visit(binaryExpression.getRightHandSide());
        if (leftHandSide == TypeKind.ERROR || rightHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
    
        if (!leftHandSide.isNumeric() && !rightHandSide.isNumeric())
            return this.addError(
                    TypeKind.ERROR,
                    binaryExpression.getParser().getCompilerClass(),
                    binaryExpression,
                    "Both sides are not numeric."
            );
    
        if (!leftHandSide.isNumeric())
            return this.addError(
                    TypeKind.ERROR,
                    binaryExpression.getParser().getCompilerClass(),
                    binaryExpression.getLeftHandSide(),
                    "Left side is not numeric."
            );
    
        if (!rightHandSide.isNumeric())
            return this.addError(
                    TypeKind.ERROR,
                    binaryExpression.getParser().getCompilerClass(),
                    binaryExpression.getRightHandSide(),
                    "Right side is not numeric."
            );
    
        return binaryExpression.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull EqualityExpression equalityExpression) {
        Objects.requireNonNull(equalityExpression.getLeftHandSide(), "equalityExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpression.getRightHandSide(), "equalityExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(equalityExpression.getParser(), "equalityExpressionNode.parser must not be null.");
    
        final TypeKind leftHandSide = this.visit(equalityExpression.getLeftHandSide());
        final TypeKind rightHandSide = this.visit(equalityExpression.getRightHandSide());
        if (leftHandSide == TypeKind.ERROR || rightHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
    
        if (leftHandSide != TypeKind.BOOLEAN && rightHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    equalityExpression.getParser().getCompilerClass(),
                    equalityExpression,
                    "Both sides are not a boolean."
            );
    
        if (leftHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    equalityExpression.getParser().getCompilerClass(),
                    equalityExpression.getLeftHandSide(),
                    "Left side is not a boolean."
            );
    
        if (rightHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    equalityExpression.getParser().getCompilerClass(),
                    equalityExpression.getRightHandSide(),
                    "Right side is not a boolean."
            );
    
        return equalityExpression.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull LogicalExpression logicalExpression) {
        Objects.requireNonNull(logicalExpression.getLeftHandSide(), "logicalExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpression.getRightHandSide(), "logicalExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(logicalExpression.getParser(), "logicalExpressionNode.parser must not be null.");
    
        final TypeKind leftHandSide = this.visit(logicalExpression.getLeftHandSide());
        final TypeKind rightHandSide = this.visit(logicalExpression.getRightHandSide());
        if (leftHandSide == TypeKind.ERROR || rightHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
    
        if (leftHandSide != TypeKind.BOOLEAN && rightHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    logicalExpression.getParser().getCompilerClass(),
                    logicalExpression,
                    "Both sides are not a boolean."
            );
    
        if (leftHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    logicalExpression.getParser().getCompilerClass(),
                    logicalExpression.getLeftHandSide(),
                    "Left side is not a boolean."
            );
    
        if (rightHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    logicalExpression.getParser().getCompilerClass(),
                    logicalExpression.getRightHandSide(),
                    "Right side is not a boolean."
            );
    
        return logicalExpression.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParenthesizedExpression parenthesizedExpression) {
        Objects.requireNonNull(parenthesizedExpression.getExpression(), "parenthesizedExpressionNode.expression must not be null.");
        if (this.visit(parenthesizedExpression.getExpression()) == TypeKind.ERROR)
            return TypeKind.ERROR;
        return parenthesizedExpression.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull PostfixExpression postfixExpression) {
        Objects.requireNonNull(postfixExpression.getLeftHandSide(), "postfixExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(postfixExpression.getParser(), "postfixExpressionNode.parser must not be null.");
        
        final TypeKind leftHandSide = this.visit(postfixExpression.getLeftHandSide());
        if (leftHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
        
        if (!leftHandSide.isNumeric())
            return this.addError(
                    TypeKind.ERROR,
                    postfixExpression.getParser().getCompilerClass(),
                    postfixExpression,
                    "Left side is not numeric."
            );
        
        return postfixExpression.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull PrefixExpression prefixExpression) {
        Objects.requireNonNull(prefixExpression.getRightHandSide(), "prefixExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixExpression.getOperatorType(), "prefixExpressionNode.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixExpression.getParser(), "prefixExpressionNode.parser must not be null.");
        
        final TypeKind rightHandSide = this.visit(prefixExpression.getRightHandSide());
        if (rightHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
        
        if (!rightHandSide.isNumeric())
            return this.addError(
                    TypeKind.ERROR,
                    prefixExpression.getParser().getCompilerClass(),
                    prefixExpression,
                    "Right side is not numeric."
            );
        
        return prefixExpression.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull RelationalExpression relationalExpression) {
        Objects.requireNonNull(relationalExpression.getLeftHandSide(), "relationalExpressionNode.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpression.getRightHandSide(), "relationalExpressionNode.rightSideOperable must not be null.");
        Objects.requireNonNull(relationalExpression.getParser(), "relationalExpressionNode.parser must not be null.");
    
        final TypeKind leftHandSide = this.visit(relationalExpression.getLeftHandSide());
        final TypeKind rightHandSide = this.visit(relationalExpression.getRightHandSide());
        if (leftHandSide == TypeKind.ERROR || rightHandSide == TypeKind.ERROR)
            return TypeKind.ERROR;
    
        if (leftHandSide != TypeKind.BOOLEAN && rightHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    relationalExpression.getParser().getCompilerClass(),
                    relationalExpression,
                    "Both sides are not a boolean."
            );
    
        if (leftHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    relationalExpression.getParser().getCompilerClass(),
                    relationalExpression.getLeftHandSide(),
                    "Left side is not a boolean."
            );
    
        if (rightHandSide != TypeKind.BOOLEAN)
            return this.addError(
                    TypeKind.ERROR,
                    relationalExpression.getParser().getCompilerClass(),
                    relationalExpression.getRightHandSide(),
                    "Right side is not a boolean."
            );
    
        return relationalExpression.getTypeKind();
    }
    
    public <E> E addError(final @Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
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
