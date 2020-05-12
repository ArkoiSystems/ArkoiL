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
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.FunctionAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ImportAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.ReturnAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.statement.types.VariableAST;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import com.arkoisystems.llvm4j.api.builder.Builder;
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
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

@Getter
@Setter
public class CodeGenVisitor implements IVisitor<Object>
{
    
    @NotNull
    private Builder builder;
    
    @NotNull
    private Module module;
    
    @Nullable
    @Override
    public Type visit(final @NotNull TypeAST typeAST) {
        switch (typeAST.getTypeKind()) {
            case FLOAT:
                return FloatingType.createFloatType();
            case BOOL:
                return IntegerType.createInt1Type();
            case BYTE:
                return IntegerType.createInt8Type();
            case CHAR:
                return IntegerType.createInt16Type();
            case DOUBLE:
                return FloatingType.createDoubleType();
            case INT:
                return IntegerType.createInt32Type();
            case LONG:
                return IntegerType.createInt64Type();
            case VOID:
                return VoidType.createVoidType();
            case STRING:
                // TODO: 5/11/20 Idk how this works
                return PointerType.createPointerType(IntegerType.createInt8Type(), 0);
            default:
                System.err.println("Unhandled type: " + typeAST.getTypeKind().name());
                return null;
        }
    }
    
    @Override
    public Module visit(final @NotNull RootAST rootAST) {
        Objects.requireNonNull(rootAST.getSyntaxAnalyzer(), "rootAST.syntaxAnalyzer must not be null.");
        
        final File file = new File(rootAST.getSyntaxAnalyzer().getCompilerClass().getFilePath());
        final Module module = Module.createWithName(file.getName());
        this.setBuilder(Builder.createBuilder());
        this.setModule(module);
        
        return rootAST.getAstNodes().stream()
                .anyMatch(node -> this.visit(node) == null) ? null : module;
    }
    
    @Nullable
    @Override
    public PointerArray<Type> visit(final @NotNull ParameterListAST parameterListAST) {
        final Type[] parameterTypes = new Type[parameterListAST.getParameters().size()];
        for (int index = 0; index < parameterListAST.getParameters().size(); index++) {
            final Type parameterType = this.visit(parameterListAST.getParameters().get(index));
            if(parameterType == null)
                return null;
            parameterTypes[index] = parameterType;
        }
        return new PointerArray<>(parameterTypes);
    }
    
    @Nullable
    @Override
    public Type visit(final @NotNull ParameterAST parameterAST) {
        Objects.requireNonNull(parameterAST.getParameterType(), "parameterAST.parameterType must not be null.");
        return this.visit(parameterAST.getParameterType());
    }
    
    @Override
    public Object visit(final @NotNull BlockAST blockAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ArgumentListAST argumentListAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ArgumentAST argumentAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull AnnotationAST annotationAST) {
        return null;
    }
    
    @Override
    public Function visit(final @NotNull FunctionAST functionAST) {
        Objects.requireNonNull(functionAST.getSyntaxAnalyzer(), "functionAST.syntaxAnalyzer must not be null.");
        Objects.requireNonNull(functionAST.getFunctionReturnType(), "functionAST.functionReturnType must not be null.");
        Objects.requireNonNull(functionAST.getFunctionParameters(), "functionAST.functionParameters must not be null.");
        Objects.requireNonNull(functionAST.getFunctionName(), "functionAST.functionName must not be null.");
        Objects.requireNonNull(this.getModule(), "module must not be null.");
        
        final Type returnType = this.visit(functionAST.getFunctionReturnType());
        if (returnType == null)
            return null;
        
        final PointerArray<Type> parameters = this.visit(functionAST.getFunctionParameters());
        if(parameters == null)
            return null;
        
        final Function function = this.getModule().addFunction(
                functionAST.getFunctionName().getData(),
                FunctionType.createFunctionType(
                        returnType,
                        parameters,
                        false
                )
        );
        this.getBuilder().setInsertPositionAtEnd(function.appendBasicBlock("entry"));
        
        return function;
    }
    
    @Override
    public TypeKind visit(final @NotNull ImportAST importAST) {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public Object visit(final @NotNull ReturnAST returnAST) {
        return null;
    }
    
    @Override
    public TypeKind visit(final @NotNull VariableAST variableAST) {
        return TypeKind.UNDEFINED;
    }
    
    @Override
    public Object visit(final @NotNull StringAST stringAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull NumberAST numberAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull IdentifierCallAST identifierCallAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull FunctionCallPartAST functionCallPartAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull CollectionAST collectionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull AssignmentExpressionAST assignmentExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull BinaryExpressionAST binaryExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull CastExpressionAST castExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull EqualityExpressionAST equalityExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull LogicalExpressionAST logicalExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull ParenthesizedExpressionAST parenthesizedExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull PostfixExpressionAST postfixExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull PrefixExpressionAST prefixExpressionAST) {
        return null;
    }
    
    @Override
    public Object visit(final @NotNull RelationalExpressionAST relationalExpressionAST) {
        return null;
    }
    
}
