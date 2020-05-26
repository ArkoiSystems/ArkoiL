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
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.BlockType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.TypeKind;
import com.arkoisystems.llvm4j.api.core.modules.Module;
import com.arkoisystems.llvm4j.api.core.types.Type;
import com.arkoisystems.llvm4j.api.core.types.modules.FloatingType;
import com.arkoisystems.llvm4j.api.core.types.modules.FunctionType;
import com.arkoisystems.llvm4j.api.core.types.modules.IntegerType;
import com.arkoisystems.llvm4j.api.core.types.modules.VoidType;
import com.arkoisystems.llvm4j.api.core.types.modules.sequential.PointerType;
import com.arkoisystems.llvm4j.api.core.values.constants.function.Function;
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
    public Type visit(final @NotNull TypeNode typeAST) {
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
    public Module visit(final @NotNull RootNode rootAST) {
        Objects.requireNonNull(rootAST.getParser(), "rootAST.parser must not be null.");
        
        final File file = new File(rootAST.getParser().getCompilerClass().getFilePath());
        final Module module = Module.createWithName(file.getName());
        this.setModule(module);
        
        return rootAST.getNodes().stream()
                .anyMatch(node -> this.visit(node) == null) ? null : module;
    }
    
    @Nullable
    @Override
    public PointerArray<Type> visit(final @NotNull ParameterListNode parameterListAST) {
        final List<Type> parameterTypes = new ArrayList<>();
        for (final ParameterNode parameter : parameterListAST.getParameters()) {
            if (parameter.getTypeKind() == TypeKind.VARIADIC)
                continue;
            final Type parameterType = this.visit(parameter);
            if (parameterType == null)
                return null;
            parameterTypes.add(parameterType);
        }
        return new PointerArray<>(parameterTypes.toArray(Type[]::new));
    }
    
    @Nullable
    @Override
    public Type visit(final @NotNull ParameterNode parameterAST) {
        Objects.requireNonNull(parameterAST.getType(), "parameterAST.parameterType must not be null.");
        return this.visit(parameterAST.getType());
    }
    
    @Override
    public Object visit(final @NotNull BlockNode blockAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ArgumentListNode argumentListAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ArgumentNode argumentAST) {
        return null;
    }
    
    @Override
    public Function visit(final @NotNull FunctionNode functionAST) {
        Objects.requireNonNull(functionAST.getParser(), "functionAST.parser must not be null.");
        Objects.requireNonNull(functionAST.getReturnType(), "functionAST.returnType must not be null.");
        Objects.requireNonNull(functionAST.getParameters(), "functionAST.parameters must not be null.");
        Objects.requireNonNull(functionAST.getName(), "functionAST.name must not be null.");
        Objects.requireNonNull(functionAST.getBlock(), "functionAST.block must not be null.");
        Objects.requireNonNull(this.getModule(), "module must not be null.");
    
        final Type returnType = this.visit(functionAST.getReturnType());
        if (returnType == null)
            return null;
    
        final PointerArray<Type> parameters = this.visit(functionAST.getParameters());
        if (parameters == null)
            return null;
    
        final boolean isVariadic = functionAST.getParameters().getParameters()
                .stream()
                .anyMatch(parameter -> parameter.getTypeKind() == TypeKind.VARIADIC);
    
        final Function function = this.getModule().addFunction(
                functionAST.getName().getTokenContent(),
                FunctionType.createFunctionType(
                        returnType,
                        parameters,
                        isVariadic
                )
        );
    
        if (functionAST.getBlock().getBlockType() != BlockType.NATIVE)
            function.appendBasicBlock("entry");
        return function;
    }
    
    @Override
    public TypeKind visit(final @NotNull ImportNode importAST) {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public Object visit(final @NotNull ReturnNode returnAST) {
        return null;
    }
    
    @Override
    public TypeKind visit(final @NotNull VariableNode variableAST) {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public Object visit(final @NotNull StringNode stringAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull NumberNode numberAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull IdentifierCallNode identifierCallAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull FunctionCallPartNode functionCallPartAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull CollectionNode collectionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull AssignmentExpressionNode assignmentExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull BinaryExpressionNode binaryExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull EqualityExpressionNode equalityExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull LogicalExpressionNode logicalExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ParenthesizedExpressionNode parenthesizedExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull PostfixExpressionNode postfixExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull PrefixExpressionNode prefixExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull RelationalExpressionNode relationalExpressionAST) {
        return null;
    }
    
}
