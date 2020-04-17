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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types.VariableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
    
    
    @Override
    public TypeKind visit(@NotNull final TypeAST typeAST) {
        return typeAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final RootAST rootAST) {
        for (final IASTNode astNode : rootAST.getAstNodes())
            this.visit(astNode);
        return rootAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ParameterListAST parameterListAST) {
        for (final ParameterAST parameterAST : parameterListAST.getParameters())
            this.visit(parameterAST);
        return parameterListAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ParameterAST parameterAST) {
        Objects.requireNonNull(parameterAST.getParameterType(), "parameterAST.parameterType must not be null.");
    
        return this.visit(parameterAST.getParameterType());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final BlockAST blockAST) {
        for (final IASTNode astNode : blockAST.getAstNodes())
            this.visit(astNode);
        return blockAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ArgumentListAST argumentListAST) {
        for(final ArgumentAST argumentAST : argumentListAST.getArguments())
            this.visit(argumentAST);
        return argumentListAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ArgumentAST argumentAST) {
        Objects.requireNonNull(argumentAST.getArgumentExpression(), "argumentAST.argumentExpression must not be null.");
        
        return this.visit(argumentAST.getArgumentExpression());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final AnnotationAST annotationAST) {
        Objects.requireNonNull(annotationAST.getAnnotationArguments(), "annotationAST.annotationArguments must not be null.");
        
        return this.visit(annotationAST.getAnnotationArguments());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final FunctionAST functionAST) {
        Objects.requireNonNull(functionAST.getFunctionBlock(), "functionAST.functionBlock must not be null.");
        Objects.requireNonNull(functionAST.getFunctionReturnType(), "functionAST.functionReturnType must not be null.");
        
        this.visit(functionAST.getFunctionBlock());
        return functionAST.getFunctionReturnType().getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ImportAST importAST) {
        return importAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ReturnAST returnAST) {
        Objects.requireNonNull(returnAST.getReturnExpression(), "returnAST.returnExpression must not be null.");
        
        return this.visit(returnAST.getReturnExpression());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final VariableAST variableAST) {
        Objects.requireNonNull(variableAST.getVariableExpression(), "variableAST.variableExpression must not be null.");
        
        return this.visit(variableAST.getVariableExpression());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final StringAST stringAST) {
        return stringAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final NumberAST numberAST) {
        return numberAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final IdentifierCallAST identifierCallAST) {
        Objects.requireNonNull(identifierCallAST.getSyntaxAnalyzer(), "identifierCallAST.syntaxAnalyzer must not be null.");
        
        if (identifierCallAST.getCalledFunctionPart() != null)
            this.visit(identifierCallAST.getCalledFunctionPart());
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(identifierCallAST.getSyntaxAnalyzer().getCompilerClass().getSemanticAnalyzer());
        scopeVisitor.visit(identifierCallAST.getSyntaxAnalyzer().getRootAST());
        final IASTNode resultNode = scopeVisitor.visit(identifierCallAST);
        if (resultNode != null)
            return this.visit(resultNode);
        return identifierCallAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final FunctionCallPartAST functionCallPartAST) {
        for (final OperableAST operableAST : functionCallPartAST.getCalledExpressions())
            this.visit(operableAST);
        return functionCallPartAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final CollectionAST collectionAST) {
        for (final OperableAST operableAST : collectionAST.getCollectionExpressions())
            this.visit(operableAST);
        return collectionAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final AssignmentExpressionAST assignmentExpressionAST) {
        Objects.requireNonNull(assignmentExpressionAST.getLeftSideOperable(), "assignmentExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getRightSideOperable(), "assignmentExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(assignmentExpressionAST.getSyntaxAnalyzer(), "assignmentExpressionAST.syntaxAnalyzer must not be null.");
        
        // TODO: Type checking
        
        this.visit(assignmentExpressionAST.getRightSideOperable());
        return this.visit(assignmentExpressionAST.getLeftSideOperable());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final BinaryExpressionAST binaryExpressionAST) {
        Objects.requireNonNull(binaryExpressionAST.getLeftSideOperable(), "binaryExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getRightSideOperable(), "binaryExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(binaryExpressionAST.getSyntaxAnalyzer(), "binaryExpressionAST.syntaxAnalyzer must not be null.");
        
        final TypeKind leftSideType = this.visit(binaryExpressionAST.getLeftSideOperable()),
                rightSideType = this.visit(binaryExpressionAST.getRightSideOperable());
        
        // TODO: Type checking (if its a valid operation like String & Collection with the % operator or Number & Number with the + operator)
        
        if (leftSideType.isNumeric() && rightSideType.isNumeric()) {
            if (leftSideType.getPrecision() > rightSideType.getPrecision())
                return leftSideType;
            else return rightSideType;
        } else return TypeKind.UNDEFINED;
    }
    
    
    @Override
    public TypeKind visit(@NotNull final CastExpressionAST castExpressionAST) {
        Objects.requireNonNull(castExpressionAST.getLeftSideOperable(), "castExpressionAST.leftSideOperable must not be null.");
        
        this.visit(castExpressionAST.getLeftSideOperable());
        return castExpressionAST.getTypeKind();
    }
    
    
    @Override
    public TypeKind visit(@NotNull final EqualityExpressionAST equalityExpressionAST) {
        Objects.requireNonNull(equalityExpressionAST.getLeftSideOperable(), "equalityExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getRightSideOperable(), "equalityExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(equalityExpressionAST.getSyntaxAnalyzer(), "equalityExpressionAST.syntaxAnalyzer must not be null.");
        
        // TODO: Type checking
        
        this.visit(equalityExpressionAST.getRightSideOperable());
        this.visit(equalityExpressionAST.getLeftSideOperable());
        return TypeKind.BOOLEAN;
    }
    
    
    @Override
    public TypeKind visit(@NotNull final LogicalExpressionAST logicalExpressionAST) {
        Objects.requireNonNull(logicalExpressionAST.getLeftSideOperable(), "logicalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getRightSideOperable(), "logicalExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(logicalExpressionAST.getSyntaxAnalyzer(), "logicalExpressionAST.syntaxAnalyzer must not be null.");
        
        // TODO: Type checking
        
        this.visit(logicalExpressionAST.getRightSideOperable());
        this.visit(logicalExpressionAST.getLeftSideOperable());
        return TypeKind.BOOLEAN;
    }
    
    
    @Override
    public TypeKind visit(@NotNull final ParenthesizedExpressionAST parenthesizedExpressionAST) {
        Objects.requireNonNull(parenthesizedExpressionAST.getParenthesizedExpression(), "parenthesizedExpressionAST.parenthesizedExpression must not be null.");
        
        return this.visit(parenthesizedExpressionAST.getParenthesizedExpression());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final PostfixExpressionAST postfixExpressionAST) {
        Objects.requireNonNull(postfixExpressionAST.getLeftSideOperable(), "postfixExpressionAST.leftSideOperable must not be null.");
        
        // TODO: Type checking
        
        return this.visit(postfixExpressionAST.getLeftSideOperable());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final PrefixExpressionAST prefixExpressionAST) {
        Objects.requireNonNull(prefixExpressionAST.getRightSideOperable(), "prefixExpressionAST.rightSideOperable must not be null.");
        
        // TODO: Type checking
        
        return this.visit(prefixExpressionAST.getRightSideOperable());
    }
    
    
    @Override
    public TypeKind visit(@NotNull final RelationalExpressionAST relationalExpressionAST) {
        Objects.requireNonNull(relationalExpressionAST.getLeftSideOperable(), "relationalExpressionAST.leftSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getRightSideOperable(), "relationalExpressionAST.rightSideOperable must not be null.");
        Objects.requireNonNull(relationalExpressionAST.getSyntaxAnalyzer(), "relationalExpressionAST.syntaxAnalyzer must not be null.");
        
        // TODO: Type checking
        
        this.visit(relationalExpressionAST.getRightSideOperable());
        this.visit(relationalExpressionAST.getLeftSideOperable());
        return TypeKind.BOOLEAN;
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    public void addError(@NotNull final ICompilerClass compilerClass, @NotNull final IASTNode astNode, @NotNull final String message, @NotNull final Object... arguments) {
        Objects.requireNonNull(this.getSemanticAnalyzer());
        
        this.getSemanticAnalyzer().getErrorHandler().addError(ArkoiError.builder()
                .compilerClass(compilerClass)
                .positions(new int[][] { {
                        Objects.requireNonNull(astNode.getStartToken()).getStart(),
                        Objects.requireNonNull(astNode.getEndToken()).getEnd()
                } })
                .message(message)
                .arguments(arguments)
                .build()
        );
        
        this.failed();
    }
    
}
