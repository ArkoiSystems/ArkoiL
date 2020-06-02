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
import com.arkoisystems.arkoicompiler.phases.parser.ast.enums.TypeKind;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.arkoicompiler.phases.parser.ast.types.operable.OperableNode;
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
import com.arkoisystems.llvm.Module;
import com.arkoisystems.llvm.*;
import lombok.Getter;
import lombok.Setter;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

@Getter
@Setter
public class CodeGenVisitor implements IVisitor<Object>
{
    
    @NotNull
    private Module module;
    
    @NotNull
    private Builder builder;
    
    @Override
    public LLVMTypeRef visit(final @NotNull TypeNode typeNode) {
        switch (typeNode.getTypeKind()) {
            case FLOAT:
                return LLVM.LLVMFloatType();
            case BOOLEAN:
                return LLVM.LLVMInt1Type();
            case BYTE:
                return LLVM.LLVMInt8Type();
            case CHAR:
                return LLVM.LLVMInt16Type();
            case DOUBLE:
                return LLVM.LLVMDoubleType();
            case INTEGER:
                return LLVM.LLVMInt32Type();
            case LONG:
                return LLVM.LLVMInt64Type();
            case VOID:
                return LLVM.LLVMVoidType();
            case STRING:
                return LLVM.LLVMPointerType(LLVM.LLVMInt8Type(), 0);
            default:
                return LLVM.LLVMVoidType();
            //                throw new NullPointerException("Unhandled type: " + typeNode.getTypeKind().name());
        }
    }
    
    @Override
    public Module visit(final @NotNull RootNode rootNode) {
        Objects.requireNonNull(rootNode.getParser(), "rootAST.parser must not be null.");
    
        final File file = new File(rootNode.getParser().getCompilerClass().getFilePath());
        this.setModule(Module.builder()
                .name(file.getName())
                .build());
        this.setBuilder(Builder.builder()
                .build());
    
        for (final ParserNode node : rootNode.getNodes())
            this.visit(node);
    
        final String error = module.verify(LLVM.LLVMReturnStatusAction);
    
        this.getModule().dump();
//        System.out.println(error);
    
        return this.getModule();
        //        return error.isEmpty() ? this.getModule() : null;
    }
    
    @Override
    public ParameterListNode visit(final @NotNull ParameterListNode parameterListNode) {
        for (final ParameterNode parameterNode : parameterListNode.getParameters())
            this.visit(parameterNode);
        return parameterListNode;
    }
    
    @Override
    public ParameterNode visit(final @NotNull ParameterNode parameter) {
        return parameter;
    }
    
    @Override
    public BlockNode visit(final @NotNull BlockNode blockNode) {
        for (final ParserNode node : blockNode.getNodes())
            this.visit(node);
        return blockNode;
    }
    
    @Override
    public FunctionNode visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getReturnType(), "functionNode.name must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
        
        Function.builder()
                .module(this.getModule())
                .name(functionNode.getName().getTokenContent())
                .parameters(functionNode.getParameters().getParameters().stream()
                        .filter(parameter -> parameter.getTypeKind() != TypeKind.VARIADIC)
                        .map(parameter -> Parameter.builder()
                                .name(Objects.requireNonNull(parameter.getName(), "parameter.name must not be null.").getTokenContent())
                                .typeRef(this.visit(Objects.requireNonNull(parameter.getTypeNode(), "parameter.typeNode must not be null.")))
                                .build())
                        .toArray(Parameter[]::new))
                .variadic(functionNode.getParameters().isVariadic())
                .returnType(this.visit(functionNode.getReturnType()))
                .foreignFunction(functionNode.getBlockNode() == null)
                .build();
        
        if (functionNode.getBlockNode() != null)
            this.visit(functionNode.getBlockNode());
        return functionNode;
    }
    
    @Override
    public ImportNode visit(final @NotNull ImportNode importNode) {
        return importNode;
    }
    
    @Override
    public ReturnNode visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null)
            this.visit(returnNode.getExpression());
        return returnNode;
    }
    
    @Override
    public VariableNode visit(final @NotNull VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getExpression(), "variableNode.expression must not be null.");
        this.visit(variableNode.getExpression());
        return variableNode;
    }
    
    @Override
    public StringNode visit(final @NotNull StringNode stringNode) {
        Objects.requireNonNull(stringNode.getStringToken(), "stringNode.stringToken must not be null.");
    
        GlobalVariable.builder()
                .module(this.getModule())
                .name("")
                .constant(ConstantString.builder()
                        .content(stringNode.getStringToken().getTokenContent())
                        .build())
                .isConstant(true)
                .linkage(LLVM.LLVMPrivateLinkage)
                .build();
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
        for (final OperableNode operableNode : expressionListNode.getExpressions())
            this.visit(operableNode);
        return expressionListNode;
    }
    
    @Override
    public AssignmentNode visit(final @NotNull AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentNode.leftHandSide must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentNode.rightHandSide must not be null.");
    
        this.visit(assignmentNode.getLeftHandSide());
        this.visit(assignmentNode.getRightHandSide());
        return assignmentNode;
    }
    
    @Override
    public BinaryNode visit(final @NotNull BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryNode.leftHandSide must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryNode.rightHandSide must not be null.");
    
        this.visit(binaryNode.getLeftHandSide());
        this.visit(binaryNode.getRightHandSide());
        return binaryNode;
    }
    
    @Override
    public ParenthesizedNode visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedNode.expression must not be null.");
        this.visit(parenthesizedNode.getExpression());
        return parenthesizedNode;
    }
    
    @Override
    public PostfixNode visit(final @NotNull PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixNode.leftHandSide must not be null.");
        this.visit(postfixNode.getLeftHandSide());
        return postfixNode;
    }
    
    @Override
    public PrefixNode visit(final @NotNull PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixNode.rightHandSide must not be null.");
        this.visit(prefixNode.getRightHandSide());
        return prefixNode;
    }
    
}
