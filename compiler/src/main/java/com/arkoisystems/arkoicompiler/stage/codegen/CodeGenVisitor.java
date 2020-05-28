/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 11, 2020
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
package com.arkoisystems.arkoicompiler.stage.codegen;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.argument.Argument;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.argument.ArgumentList;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
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
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.enums.TypeKind;
import com.arkoisystems.llvm4j.api.core.modules.Module;
import com.arkoisystems.llvm4j.api.core.types.modules.FloatingType;
import com.arkoisystems.llvm4j.api.core.types.modules.FunctionType;
import com.arkoisystems.llvm4j.api.core.types.modules.IntegerType;
import com.arkoisystems.llvm4j.api.core.types.modules.VoidType;
import com.arkoisystems.llvm4j.api.core.types.modules.sequential.PointerType;
import com.arkoisystems.llvm4j.utils.PointerArray;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CodeGenVisitor implements IVisitor<Object>
{
    
    @Getter
    @Setter
    private Module module;
    
    @Nullable
    @Override
    public com.arkoisystems.llvm4j.api.core.types.Type visit(final @NotNull Type typeAST) {
        switch (typeAST.getTypeKind()) {
            case FLOAT:
                return FloatingType.createFloatType();
            case BOOLEAN:
                return IntegerType.createInt1Type();
            case BYTE:
                return IntegerType.createInt8Type();
            case CHAR:
                return IntegerType.createInt16Type();
            case DOUBLE:
                return FloatingType.createDoubleType();
            case INTEGER:
                return IntegerType.createInt32Type();
            case LONG:
                return IntegerType.createInt64Type();
            case VOID:
                return VoidType.createVoidType();
            case STRING:
                return PointerType.createPointerType(IntegerType.createInt8Type(), 0);
            default:
                System.err.println("Unhandled type: " + typeAST.getTypeKind().name());
                return null;
        }
    }
    
    
    @Override
    public Module visit(final @NotNull Root rootAST) {
        Objects.requireNonNull(rootAST.getParser(), "rootAST.parser must not be null.");
        
        final File file = new File(rootAST.getParser().getCompilerClass().getFilePath());
        final Module module = Module.createWithName(file.getName());
        this.setModule(module);
        
        return rootAST.getNodes().stream()
                .anyMatch(node -> this.visit(node) == null) ? null : module;
    }
    
    @Nullable
    @Override
    public PointerArray<com.arkoisystems.llvm4j.api.core.types.Type> visit(final @NotNull ParameterList parameterListAST) {
        final List<com.arkoisystems.llvm4j.api.core.types.Type> parameterTypes = new ArrayList<>();
        for (final Parameter parameter : parameterListAST.getParameters()) {
            if (parameter.getTypeKind() == TypeKind.VARIADIC)
                continue;
            final com.arkoisystems.llvm4j.api.core.types.Type parameterType = this.visit(parameter);
            if (parameterType == null)
                return null;
            parameterTypes.add(parameterType);
        }
        return new PointerArray<>(parameterTypes.toArray(com.arkoisystems.llvm4j.api.core.types.Type[]::new));
    }
    
    @Nullable
    @Override
    public com.arkoisystems.llvm4j.api.core.types.Type visit(final @NotNull Parameter parameterAST) {
        Objects.requireNonNull(parameterAST.getType(), "parameterAST.parameterType must not be null.");
        return this.visit(parameterAST.getType());
    }
    
    @Override
    public Object visit(final @NotNull Block blockAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ArgumentList argumentListAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull Argument argumentAST) {
        return null;
    }
    
    @Override
    public com.arkoisystems.llvm4j.api.core.values.constants.function.Function visit(final @NotNull FunctionStatement functionStatement) {
        Objects.requireNonNull(functionStatement.getParser(), "functionAST.parser must not be null.");
        Objects.requireNonNull(functionStatement.getReturnType(), "functionAST.returnType must not be null.");
        Objects.requireNonNull(functionStatement.getParameters(), "functionAST.parameters must not be null.");
        Objects.requireNonNull(functionStatement.getName(), "functionAST.name must not be null.");
        Objects.requireNonNull(functionStatement.getBlock(), "functionAST.block must not be null.");
        Objects.requireNonNull(this.getModule(), "module must not be null.");
    
        final com.arkoisystems.llvm4j.api.core.types.Type returnType = this.visit(functionStatement.getReturnType());
        if (returnType == null)
            return null;
    
        final PointerArray<com.arkoisystems.llvm4j.api.core.types.Type> parameters = this.visit(functionStatement.getParameters());
        if (parameters == null)
            return null;
    
        final boolean isVariadic = functionStatement.getParameters().getParameters()
                .stream()
                .anyMatch(parameter -> parameter.getTypeKind() == TypeKind.VARIADIC);
    
        final com.arkoisystems.llvm4j.api.core.values.constants.function.Function function = this.getModule().addFunction(
                functionStatement.getName().getTokenContent(),
                FunctionType.createFunctionType(
                        returnType,
                        parameters,
                        isVariadic
                )
        );
    
        if (functionStatement.getBlock().getBlockType() != BlockType.NATIVE)
            function.appendBasicBlock("entry");
        return function;
    }
    
    @Override
    public TypeKind visit(final @NotNull ImportStatement importStatement) {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public Object visit(final @NotNull ReturnStatement returnStatement) {
        return null;
    }
    
    @Override
    public TypeKind visit(final @NotNull VariableStatement variableStatement) {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public Object visit(final @NotNull StringOperable stringOperable) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull NumberOperable numberOperable) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull IdentifierOperable identifierOperable) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull CollectionOperable collectionOperable) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ExpressionList expressionList) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull AssignmentExpression assignmentExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull BinaryExpression binaryExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull EqualityExpression equalityExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull LogicalExpression logicalExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ParenthesizedExpression parenthesizedExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull PostfixExpression postfixExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull PrefixExpression prefixExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull RelationalExpression relationalExpressionAST) {
        return null;
    }
    
}
