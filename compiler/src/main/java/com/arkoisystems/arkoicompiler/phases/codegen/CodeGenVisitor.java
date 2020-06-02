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
package com.arkoisystems.arkoicompiler.phases.codegen;

import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.phases.parser.ast.ParserNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.BlockType;
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.llvm4j.api.builder.Builder;
import com.arkoisystems.llvm4j.api.core.basicBlock.BasicBlock;
import com.arkoisystems.llvm4j.api.core.modules.Module;
import com.arkoisystems.llvm4j.api.core.types.Type;
import com.arkoisystems.llvm4j.api.core.types.modules.FloatingType;
import com.arkoisystems.llvm4j.api.core.types.modules.FunctionType;
import com.arkoisystems.llvm4j.api.core.types.modules.IntegerType;
import com.arkoisystems.llvm4j.api.core.types.modules.VoidType;
import com.arkoisystems.llvm4j.api.core.types.modules.sequential.PointerType;
import com.arkoisystems.llvm4j.api.core.values.Value;
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

@Getter
@Setter
public class CodeGenVisitor implements IVisitor<Object>
{
    
    @NotNull
    private Module module;
    
    @NotNull
    private Builder builder;
    
    @Nullable
    @Override
    public Type visit(final @NotNull TypeNode typeNode) {
        switch (typeNode.getTypeKind()) {
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
                throw new NullPointerException("Unhandled type: " + typeNode.getTypeKind().name());
        }
    }
    
    
    @Override
    public Module visit(final @NotNull RootNode rootNode) {
        Objects.requireNonNull(rootNode.getParser(), "rootAST.parser must not be null.");
        
        final File file = new File(rootNode.getParser().getCompilerClass().getFilePath());
        this.module = Module.createWithName(file.getName());
        this.builder = Builder.createBuilder();
        
        return rootNode.getNodes().stream()
                .anyMatch(node -> this.visit(node) == null) ? null : module;
    }
    
    @Nullable
    @Override
    public PointerArray<Type> visit(final @NotNull ParameterListNode parameterListNode) {
        final List<Type> parameterTypes = new ArrayList<>();
        for (final ParameterNode parameter : parameterListNode.getParameters()) {
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
    public Type visit(final @NotNull ParameterNode parameter) {
        Objects.requireNonNull(parameter.getTypeNode(), "parameterAST.parameterType must not be null.");
        return this.visit(parameter.getTypeNode());
    }
    
    @Override
    public BlockNode visit(final @NotNull BlockNode blockNode) {
        return blockNode;
    }
    
    @Override
    public Function visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParser(), "functionAST.parser must not be null.");
        Objects.requireNonNull(functionNode.getReturnTypeNode(), "functionAST.returnType must not be null.");
        Objects.requireNonNull(functionNode.getParameters(), "functionAST.parameters must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionAST.name must not be null.");
        Objects.requireNonNull(functionNode.getBlockNode(), "functionAST.block must not be null.");
        Objects.requireNonNull(this.getModule(), "module must not be null.");
        
        final Type returnType = this.visit(functionNode.getReturnTypeNode());
        if (returnType == null)
            return null;
        
        final PointerArray<Type> parameters = this.visit(functionNode.getParameters());
        if (parameters == null)
            return null;
        
        final boolean isVariadic = functionNode.getParameters().getParameters()
                .stream()
                .anyMatch(parameter -> parameter.getTypeKind() == TypeKind.VARIADIC);
        final Function function = this.getModule().addFunction(
                functionNode.getName().getTokenContent(),
                FunctionType.createFunctionType(
                        returnType,
                        parameters,
                        isVariadic
                )
        );
        
        if (functionNode.getBlockNode().getBlockType() != BlockType.NATIVE) {
            final BasicBlock basicBlock = function.appendBasicBlock("entry");
            this.getBuilder().setInsertPositionAtEnd(basicBlock);
            if (this.visit(functionNode.getBlockNode()) == null)
                return null;
        }
        return function;
    }
    
    @Override
    public ImportNode visit(final @NotNull ImportNode importNode) {
        return importNode;
    }
    
    @Override
    public ReturnNode visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null) {
            final Object object = this.visit(returnNode.getExpression());
            if (!(object instanceof Value))
                return null;
            this.getBuilder().buildReturn((Value) object);
        } else this.getBuilder().buildReturnVoid();
        return returnNode;
    }
    
    @Override
    public VariableNode visit(final @NotNull VariableNode variableNode) {
        return variableNode;
    }
    
    @Override
    public StringNode visit(final @NotNull StringNode stringNode) {
        return stringNode;
    }
    
    @Override
    public NumberNode visit(final @NotNull NumberNode numberNode) {
        return numberNode;
    }
    
    @Override
    public IdentifierNode visit(final @NotNull IdentifierNode identifierNode) {
        return identifierNode;
    }
    
    @Override
    public ExpressionListNode visit(final @NotNull ExpressionListNode expressionListNode) {
        return expressionListNode;
    }
    
    @Override
    public AssignmentNode visit(final @NotNull AssignmentNode assignmentNode) {
        return assignmentNode;
    }
    
    @Override
    public BinaryNode visit(final @NotNull BinaryNode binaryNode) {
        return binaryNode;
    }
    
    @Override
    public ParenthesizedNode visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        return parenthesizedNode;
    }
    
    @Override
    public PostfixNode visit(final @NotNull PostfixNode postfixNode) {
        return postfixNode;
    }
    
    @Override
    public PrefixNode visit(final @NotNull PrefixNode prefixNode) {
        return prefixNode;
    }
    
}
