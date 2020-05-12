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
package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.visitors;

import com.arkoisystems.arkoicompiler.ArkoiError;
import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.api.utils.IFailed;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.SemanticAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableAST;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

public class TypeVisitor implements IVisitor<TypeKind>, IFailed
{
    
    @NotNull
    @Getter
    private final SemanticAnalyzer semanticAnalyzer;
    
    @Getter
    private boolean failed;
    
    public TypeVisitor(@NotNull final SemanticAnalyzer semanticAnalyzer) {
        this.semanticAnalyzer = semanticAnalyzer;
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final TypeAST typeAST) {
        return typeAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final RootAST rootAST) {
        for (final IASTNode astNode : rootAST.getAstNodes())
            this.visit(astNode);
        return rootAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ParameterListAST parameterListAST) {
        for (final ParameterAST parameterAST : parameterListAST.getParameters())
            this.visit(parameterAST);
        return parameterListAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ParameterAST parameterAST) {
        Objects.requireNonNull(parameterAST.getParameterType(), "parameterAST.parameterType must not be null.");
    
        return this.visit(parameterAST.getParameterType());
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final BlockAST blockAST) {
        for (final IASTNode astNode : blockAST.getAstNodes())
            this.visit(astNode);
        return blockAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ArgumentListAST argumentListAST) {
        for(final ArgumentAST argumentAST : argumentListAST.getArguments())
            this.visit(argumentAST);
        return argumentListAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ArgumentAST argumentAST) {
        Objects.requireNonNull(argumentAST.getArgumentExpression(), "argumentAST.argumentExpression must not be null.");
        
        return this.visit(argumentAST.getArgumentExpression());
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final AnnotationAST annotationAST) {
        Objects.requireNonNull(annotationAST.getAnnotationArguments(), "annotationAST.annotationArguments must not be null.");
        
        return this.visit(annotationAST.getAnnotationArguments());
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final FunctionAST functionAST) {
        Objects.requireNonNull(functionAST.getFunctionBlock(), "functionAST.functionBlock must not be null.");
        Objects.requireNonNull(functionAST.getFunctionReturnType(), "functionAST.functionReturnType must not be null.");
        
        this.visit(functionAST.getFunctionBlock());
        return functionAST.getFunctionReturnType().getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ImportAST importAST) {
        return importAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ReturnAST returnAST) {
        Objects.requireNonNull(returnAST.getReturnExpression(), "returnAST.returnExpression must not be null.");
        
        return this.visit(returnAST.getReturnExpression());
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final VariableAST variableAST) {
        Objects.requireNonNull(variableAST.getVariableExpression(), "variableAST.variableExpression must not be null.");
        
        return this.visit(variableAST.getVariableExpression());
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final StringAST stringAST) {
        return stringAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final NumberAST numberAST) {
        return numberAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final IdentifierCallAST identifierCallAST) {
        Objects.requireNonNull(identifierCallAST.getSyntaxAnalyzer(), "identifierCallAST.syntaxAnalyzer must not be null.");
        
        if (identifierCallAST.getCalledFunctionPart() != null)
            this.visit(identifierCallAST.getCalledFunctionPart());
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(identifierCallAST.getSyntaxAnalyzer().getCompilerClass().getSemanticAnalyzer());
        scopeVisitor.visit(identifierCallAST.getSyntaxAnalyzer().getRootAST());
        final IASTNode resultNode = scopeVisitor.visit(identifierCallAST);
        if(scopeVisitor.isFailed())
            this.failed();
        if (resultNode != null)
            return this.visit(resultNode);
        return identifierCallAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final FunctionCallPartAST functionCallPartAST) {
        for (final OperableAST operableAST : functionCallPartAST.getCalledExpressions())
            this.visit(operableAST);
        return functionCallPartAST.getTypeKind();
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final CollectionAST collectionAST) {
        for (final OperableAST operableAST : collectionAST.getCollectionExpressions())
            this.visit(operableAST);
        return collectionAST.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final AssignmentExpressionAST assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftSideOperable(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightSideOperable(), "assignmentExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getSyntaxAnalyzer(), "assignmentExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind
                leftSideType = this.visit(assignmentExpressionAST.getLeftSideOperable()),
                rightSideType = this.visit(assignmentExpressionAST.getRightSideOperable());
        if (rightSideType == leftSideType)
            return leftSideType;
//        if (!this.isDetailed() && (leftSideType == TypeKind.UNDEFINED || rightSideType == TypeKind.UNDEFINED))
//            return TypeKind.UNDEFINED;
    
        return this.addError(
                TypeKind.UNDEFINED,
                assignmentExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                assignmentExpressionAST.getRightSideOperable(),
                "Left type doesn't match the right one."
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final BinaryExpressionAST binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftSideOperable(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightSideOperable(), "binaryExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getBinaryOperatorType(), "binaryExpressionAST.binaryOperatorType must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getSyntaxAnalyzer(), "binaryExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind
                leftSideType = this.visit(binaryExpressionAST.getLeftSideOperable()),
                rightSideType = this.visit(binaryExpressionAST.getRightSideOperable());
    
        switch (binaryExpressionAST.getBinaryOperatorType()) {
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
        
                        binaryExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                        binaryExpressionAST,
                        "Unsupported binary operation for the semantic analysis."
                );
        }
    
        if (leftSideType.isNumeric() && rightSideType.isNumeric())
            return leftSideType.getPrecision() > rightSideType.getPrecision() ? leftSideType : rightSideType;
//        if (!this.isDetailed() && (leftSideType == TypeKind.UNDEFINED || rightSideType == TypeKind.UNDEFINED))
//            return TypeKind.UNDEFINED;
    
        final String errorMessage;
        final IASTNode targetNode;
        if (!leftSideType.isNumeric() && !rightSideType.isNumeric()) {
            targetNode = binaryExpressionAST;
            errorMessage = "Both sides are not numeric.";
        } else if (!leftSideType.isNumeric()) {
            targetNode = binaryExpressionAST.getLeftSideOperable();
            errorMessage = "Left side is not numeric.";
        } else {
            targetNode = binaryExpressionAST.getRightSideOperable();
            errorMessage = "Right side is not numeric.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                binaryExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final CastExpressionAST castExpressionAST) {
        Objects.requireNonNull(castExpressionAST.getLeftSideOperable(), "castExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(castExpressionAST.getSyntaxAnalyzer(), "castExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind leftSideType = this.visit(castExpressionAST.getLeftSideOperable());
//        if (!this.isDetailed() && leftSideType == TypeKind.UNDEFINED)
//            return TypeKind.UNDEFINED;
        if (!leftSideType.isNumeric())
            return this.addError(
                    TypeKind.UNDEFINED,
        
                    castExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                    castExpressionAST.getLeftSideOperable(),
                    "Left side is not numeric."
            );
    
        return castExpressionAST.getTypeKind();
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final EqualityExpressionAST equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftSideOperable(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightSideOperable(), "equalityExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getSyntaxAnalyzer(), "equalityExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind
                leftSideType = this.visit(equalityExpressionAST.getLeftSideOperable()),
                rightSideType = this.visit(equalityExpressionAST.getRightSideOperable());
        if (leftSideType == TypeKind.BOOL && rightSideType == TypeKind.BOOL)
            return TypeKind.BOOL;
//        if (!this.isDetailed() && (leftSideType == TypeKind.UNDEFINED || rightSideType == TypeKind.UNDEFINED))
//            return TypeKind.UNDEFINED;
    
        final String errorMessage;
        final IASTNode targetNode;
        if (leftSideType != TypeKind.BOOL && rightSideType != TypeKind.BOOL) {
            targetNode = equalityExpressionAST;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOL) {
            targetNode = equalityExpressionAST.getLeftSideOperable();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = equalityExpressionAST.getRightSideOperable();
            errorMessage = "Right side is not a boolean.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                equalityExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final LogicalExpressionAST logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftSideOperable(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightSideOperable(), "logicalExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getSyntaxAnalyzer(), "logicalExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind
                leftSideType = this.visit(logicalExpressionAST.getLeftSideOperable()),
                rightSideType = this.visit(logicalExpressionAST.getRightSideOperable());
        if (leftSideType == TypeKind.BOOL && rightSideType == TypeKind.BOOL)
            return TypeKind.BOOL;
//        if (!this.isDetailed() && (leftSideType == TypeKind.UNDEFINED || rightSideType == TypeKind.UNDEFINED))
//            return TypeKind.UNDEFINED;
    
        final String errorMessage;
        final IASTNode targetNode;
        if (leftSideType != TypeKind.BOOL && rightSideType != TypeKind.BOOL) {
            targetNode = logicalExpressionAST;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOL) {
            targetNode = logicalExpressionAST.getLeftSideOperable();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = logicalExpressionAST.getRightSideOperable();
            errorMessage = "Right side is not a boolean.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                logicalExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @NotNull
    @Override
    public TypeKind visit(@NotNull final ParenthesizedExpressionAST parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getParenthesizedExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        return this.visit(parenthesizedExpressionAST.getParenthesizedExpression());
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final PostfixExpressionAST postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftSideOperable(), "postfixExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(postfixExpressionAST.getSyntaxAnalyzer(), "postfixExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind leftSideType = this.visit(postfixExpressionAST.getLeftSideOperable());
//        if (!this.isDetailed() && leftSideType == TypeKind.UNDEFINED)
//            return TypeKind.UNDEFINED;
        if (leftSideType.isNumeric())
            return leftSideType;
    
        return this.addError(
                TypeKind.UNDEFINED,
                postfixExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                postfixExpressionAST,
                "Left side is not numeric."
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final PrefixExpressionAST prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightSideOperable(), "prefixExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(prefixExpressionAST.getPrefixOperatorType(), "prefixExpressionAST.prefixOperatorType must not be null.");
        Objects.requireNonNull(prefixExpressionAST.getSyntaxAnalyzer(), "prefixExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind rightTSideType = this.visit(prefixExpressionAST.getRightSideOperable());
//        if (!this.isDetailed() && rightTSideType == TypeKind.UNDEFINED)
//            return TypeKind.UNDEFINED;
        if (rightTSideType.isNumeric())
            return rightTSideType;
        
        return this.addError(
                TypeKind.UNDEFINED,
                prefixExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                prefixExpressionAST,
                "Right side is not numeric."
        );
    }
    
    @Nullable
    @Override
    public TypeKind visit(@NotNull final RelationalExpressionAST relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftSideOperable(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightSideOperable(), "relationalExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getSyntaxAnalyzer(), "relationalExpressionAST.syntaxAnalyzer must not be null.");
    
        final TypeKind
                leftSideType = this.visit(relationalExpressionAST.getLeftSideOperable()),
                rightSideType = this.visit(relationalExpressionAST.getRightSideOperable());
        if (leftSideType == TypeKind.BOOL && rightSideType == TypeKind.BOOL)
            return TypeKind.BOOL;
//        if (!this.isDetailed() && (leftSideType == TypeKind.UNDEFINED || rightSideType == TypeKind.UNDEFINED))
//            return TypeKind.UNDEFINED;
    
        final String errorMessage;
        final IASTNode targetNode;
        if (leftSideType != TypeKind.BOOL && rightSideType != TypeKind.BOOL) {
            targetNode = relationalExpressionAST;
            errorMessage = "Both sides are not a boolean.";
        } else if (leftSideType != TypeKind.BOOL) {
            targetNode = relationalExpressionAST.getLeftSideOperable();
            errorMessage = "Left side is not a boolean.";
        } else {
            targetNode = relationalExpressionAST.getRightSideOperable();
            errorMessage = "Right side is not a boolean.";
        }
    
        return this.addError(
                TypeKind.UNDEFINED,
                relationalExpressionAST.getSyntaxAnalyzer().getCompilerClass(),
                targetNode,
                errorMessage
        );
    }
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    @Nullable
    public <E> E addError(@Nullable E errorSource, @NotNull final ICompilerClass compilerClass, @NotNull final IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments) {
        compilerClass.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(Collections.singletonList(ArkoiError.ErrorPosition.builder()
                        .lineRange(astNode.getLineRange())
                        .charStart(Objects.requireNonNull(astNode.getStartToken(), "astNode.startToken must not be null.").getCharStart())
                        .charEnd(Objects.requireNonNull(astNode.getEndToken(), "astNode.endToken must not be null.").getCharEnd())
                        .build())
                )
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
        return errorSource;
    }
    
}
