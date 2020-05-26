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
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.api.IFailed;
import com.arkoisystems.arkoicompiler.error.ArkoiError;
import com.arkoisystems.arkoicompiler.error.ErrorPosition;
import com.arkoisystems.arkoicompiler.stage.parser.ast.ArkoiNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.semantic.Semantic;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

public class TypeVisitor implements IVisitor<TypeKind>, IFailed
{
    
    @NotNull
    @Getter
    private final Semantic semantic;
    
    @Getter
    @Setter
    private boolean failed;
    
    public TypeVisitor(final @NotNull Semantic semantic) {
        this.semantic = semantic;
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull TypeNode typeAST) {
        return typeAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull RootNode rootAST) {
        for (final ArkoiNode astNode : rootAST.getNodes())
            this.visit(astNode);
        return rootAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParameterListNode parameterListAST) {
        for (final ParameterNode parameterAST : parameterListAST.getParameters())
            this.visit(parameterAST);
        return parameterListAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParameterNode parameterAST) {
        Objects.requireNonNull(parameterAST.getType(), "parameterAST.parameterType must not be null.");
    
        return this.visit(parameterAST.getType());
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull BlockNode blockAST) {
        for (final ArkoiNode astNode : blockAST.getNodes())
            this.visit(astNode);
        return blockAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ArgumentListNode argumentListAST) {
        for(final ArgumentNode argumentAST : argumentListAST.getArguments())
            this.visit(argumentAST);
        return argumentListAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ArgumentNode argumentAST) {
        Objects.requireNonNull(argumentAST.getExpression(), "argumentAST.argumentExpression must not be null.");
        
        return this.visit(argumentAST.getExpression());
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull FunctionNode functionAST) {
        Objects.requireNonNull(functionAST.getBlock(), "functionAST.block must not be null.");
        Objects.requireNonNull(functionAST.getReturnType(), "functionAST.returnType must not be null.");
        
        this.visit(functionAST.getBlock());
        return functionAST.getReturnType().getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ImportNode importAST) {
        return importAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ReturnNode returnAST) {
        Objects.requireNonNull(returnAST.getExpression(), "returnAST.returnExpression must not be null.");
        
        return this.visit(returnAST.getExpression());
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull VariableNode variableAST) {
        Objects.requireNonNull(variableAST.getExpression(), "variableAST.variableExpression must not be null.");
        
        return this.visit(variableAST.getExpression());
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull StringNode stringAST) {
        return stringAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull NumberNode numberAST) {
        return numberAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull IdentifierCallNode identifierCallAST) {
        Objects.requireNonNull(identifierCallAST.getParser(), "identifierCallAST.parser must not be null.");
    
        if (identifierCallAST.getFunctionPart() != null)
            this.visit(identifierCallAST.getFunctionPart());
    
        final ScopeVisitor scopeVisitor = new ScopeVisitor(identifierCallAST.getParser().getCompilerClass().getSemantic());
        scopeVisitor.visit(identifierCallAST.getParser().getRootAST());
        final ArkoiNode resultNode = scopeVisitor.visit(identifierCallAST);
        if (scopeVisitor.isFailed())
            this.setFailed(true);
        if (resultNode != null)
            return this.visit(resultNode);
        return identifierCallAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull FunctionCallPartNode functionCallPartAST) {
        for (final OperableNode operableAST : functionCallPartAST.getExpressions())
            this.visit(operableAST);
        return functionCallPartAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull CollectionNode collectionAST) {
        for (final OperableNode operableAST : collectionAST.getExpressions())
            this.visit(operableAST);
        return collectionAST.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull AssignmentExpressionNode assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftHandSide(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightHandSide(), "assignmentExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getParser(), "assignmentExpressionAST.parser must not be null.");
    
        final TypeKind
                leftSideType = this.visit(assignmentExpressionAST.getLeftHandSide()),
                rightSideType = this.visit(assignmentExpressionAST.getRightHandSide());
        if (rightSideType == leftSideType)
            return leftSideType;
    
        return this.addError(
                TypeKind.UNDEFINED,
                assignmentExpressionAST.getParser().getCompilerClass(),
                assignmentExpressionAST.getRightHandSide(),
                "Left type doesn't match the right one."
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull BinaryExpressionNode binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftHandSide(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightHandSide(), "binaryExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getOperatorType(), "binaryExpressionAST.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getParser(), "binaryExpressionAST.parser must not be null.");
    
        final TypeKind
                leftSideType = this.visit(binaryExpressionAST.getLeftHandSide()),
                rightSideType = this.visit(binaryExpressionAST.getRightHandSide());
    
        switch (binaryExpressionAST.getOperatorType()) {
            case MODULO:
                if (leftSideType == TypeKind.STRING && rightSideType == TypeKind.COLLECTION)
                    return TypeKind.STRING;
                break;
            case ADDITION:
            case DIVISION:
            case EXPONENTIAL:
            case SUBTRACTION:
            case MULTIPLICATION:
                break;
            default:
                return this.addError(
                        TypeKind.UNDEFINED,
        
                        binaryExpressionAST.getParser().getCompilerClass(),
                        binaryExpressionAST,
                        "Unsupported binary operation for the semantic analysis."
                );
        }
    
        if (leftSideType.isNumeric() && rightSideType.isNumeric())
            return leftSideType.getPrecision() > rightSideType.getPrecision() ? leftSideType : rightSideType;
    
        final String errorMessage;
        final ArkoiNode targetNode;
        if (!leftSideType.isNumeric() && !rightSideType.isNumeric()) {
            targetNode = binaryExpressionAST;
            errorMessage = "Both sides are not numeric.";
        } else if (!leftSideType.isNumeric()) {
            targetNode = binaryExpressionAST.getLeftHandSide();
            errorMessage = "Left side is not numeric.";
        } else {
            targetNode = binaryExpressionAST.getRightHandSide();
            errorMessage = "Right side is not numeric.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                binaryExpressionAST.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull EqualityExpressionNode equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftHandSide(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightHandSide(), "equalityExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getParser(), "equalityExpressionAST.parser must not be null.");
    
        final TypeKind
                leftSideType = this.visit(equalityExpressionAST.getLeftHandSide()),
                rightSideType = this.visit(equalityExpressionAST.getRightHandSide());
        if (leftSideType == TypeKind.BOOLEAN && rightSideType == TypeKind.BOOLEAN)
            return TypeKind.BOOLEAN;
    
        final String errorMessage;
        final ArkoiNode targetNode;
        if (leftSideType != TypeKind.BOOLEAN && rightSideType != TypeKind.BOOLEAN) {
            targetNode = equalityExpressionAST;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOLEAN) {
            targetNode = equalityExpressionAST.getLeftHandSide();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = equalityExpressionAST.getRightHandSide();
            errorMessage = "Right side is not a boolean.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                equalityExpressionAST.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull LogicalExpressionNode logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftHandSide(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightHandSide(), "logicalExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getParser(), "logicalExpressionAST.parser must not be null.");
    
        final TypeKind
                leftSideType = this.visit(logicalExpressionAST.getLeftHandSide()),
                rightSideType = this.visit(logicalExpressionAST.getRightHandSide());
        if (leftSideType == TypeKind.BOOLEAN && rightSideType == TypeKind.BOOLEAN)
            return TypeKind.BOOLEAN;
    
        final String errorMessage;
        final ArkoiNode targetNode;
        if (leftSideType != TypeKind.BOOLEAN && rightSideType != TypeKind.BOOLEAN) {
            targetNode = logicalExpressionAST;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOLEAN) {
            targetNode = logicalExpressionAST.getLeftHandSide();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = logicalExpressionAST.getRightHandSide();
            errorMessage = "Right side is not a boolean.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                logicalExpressionAST.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @NotNull
    @Override
    public TypeKind visit(final @NotNull ParenthesizedExpressionNode parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        return this.visit(parenthesizedExpressionAST.getExpression());
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull PostfixExpressionNode postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftHandSide(), "postfixExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(postfixExpressionAST.getParser(), "postfixExpressionAST.parser must not be null.");
    
        final TypeKind leftSideType = this.visit(postfixExpressionAST.getLeftHandSide());
        if (leftSideType.isNumeric())
            return leftSideType;
    
        return this.addError(
                TypeKind.UNDEFINED,
                postfixExpressionAST.getParser().getCompilerClass(),
                postfixExpressionAST,
                "Left side is not numeric."
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull PrefixExpressionNode prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightHandSide(), "prefixExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixExpressionAST.getOperatorType(), "prefixExpressionAST.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixExpressionAST.getParser(), "prefixExpressionAST.parser must not be null.");
    
        final TypeKind rightTSideType = this.visit(prefixExpressionAST.getRightHandSide());
        if (rightTSideType.isNumeric())
            return rightTSideType;
        
        return this.addError(
                TypeKind.UNDEFINED,
                prefixExpressionAST.getParser().getCompilerClass(),
                prefixExpressionAST,
                "Right side is not numeric."
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(final @NotNull RelationalExpressionNode relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftHandSide(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightHandSide(), "relationalExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getParser(), "relationalExpressionAST.parser must not be null.");
    
        final TypeKind
                leftSideType = this.visit(relationalExpressionAST.getLeftHandSide()),
                rightSideType = this.visit(relationalExpressionAST.getRightHandSide());
        if (leftSideType == TypeKind.BOOLEAN && rightSideType == TypeKind.BOOLEAN)
            return TypeKind.BOOLEAN;
    
        final String errorMessage;
        final ArkoiNode targetNode;
        if (leftSideType != TypeKind.BOOLEAN && rightSideType != TypeKind.BOOLEAN) {
            targetNode = relationalExpressionAST;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOLEAN) {
            targetNode = relationalExpressionAST.getLeftHandSide();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = relationalExpressionAST.getRightHandSide();
            errorMessage = "Right side is not a boolean.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                relationalExpressionAST.getParser().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, final @NotNull ArkoiClass compilerClass, final @NotNull ArkoiNode astNode, final @NotNull String message, final @NotNull Object... arguments) {
        compilerClass.getSemantic().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ErrorPosition.builder()
                        .lineRange(astNode.getLineRange())
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
