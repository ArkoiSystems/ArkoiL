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
import com.arkoisystems.llvm.Builder;
import com.arkoisystems.llvm.Function;
import com.arkoisystems.llvm.Module;
import com.arkoisystems.llvm.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

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
        return this.getTypeRef(typeNode.getTypeKind(), typeNode.isArray());
    }
    
    @SneakyThrows
    @Override
    public Module visit(final @NotNull RootNode rootNode) {
        Objects.requireNonNull(rootNode.getCurrentScope(), "rootAST.currentScope must not be null.");
        Objects.requireNonNull(rootNode.getParser(), "rootAST.parser must not be null.");
        
        final File file = new File(rootNode.getParser().getCompilerClass().getFilePath());
        this.setModule(Module.builder()
                .name(file.getName())
                .build());
        
        rootNode.getCurrentScope().getSymbolTable().values().stream()
                .flatMap(Collection::stream)
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .forEach(node -> this.createFunction(node, !rootNode.getNodes().contains(node)));
        
        this.setBuilder(Builder.builder()
                .build());
        
        rootNode.getNodes().forEach(this::visit);
        
        final String error = module.verify(LLVM.LLVMReturnStatusAction);
        
        if (error.isEmpty()) {
            final BytePointer errorPointer = new BytePointer();
            
            LLVM.LLVMInitializeNativeTarget();
            LLVM.LLVMInitializeAllTargets();
            LLVM.LLVMInitializeAllTargetMCs();
            LLVM.LLVMInitializeAllAsmPrinters();
            LLVM.LLVMInitializeAllAsmParsers();
            
            LLVMPassRegistryRef passRegistryRef = LLVM.LLVMGetGlobalPassRegistry();
            LLVM.LLVMInitializeCore(passRegistryRef);
            LLVM.LLVMInitializeCodeGen(passRegistryRef);
            LLVM.LLVMInitializeScalarOpts(passRegistryRef);
            LLVM.LLVMInitializeVectorization(passRegistryRef);
            
            final String targetTriple = LLVM.LLVMGetDefaultTargetTriple().getString();
            
            final LLVMTargetRef targetRef = new LLVMTargetRef();
            LLVM.LLVMGetTargetFromTriple(new BytePointer(targetTriple), targetRef, errorPointer);
            if (!Pointer.isNull(errorPointer))
                throw new NullPointerException(errorPointer.getString());
            
            final LLVMTargetMachineRef targetMachineRef = LLVM.LLVMCreateTargetMachine(
                    targetRef,
                    targetTriple,
                    LLVM.LLVMGetHostCPUName().getString(),
                    LLVM.LLVMGetHostCPUFeatures().getString(),
                    LLVM.LLVMCodeGenLevelNone,
                    LLVM.LLVMRelocDefault,
                    LLVM.LLVMCodeModelDefault
            );
            
            final LLVMTargetDataRef targetDataRef = LLVM.LLVMCreateTargetDataLayout(targetMachineRef);
            LLVM.LLVMSetModuleDataLayout(this.getModule().getModuleRef(), targetDataRef);
            
            final String name = new File(rootNode.getParser().getCompilerClass().getFilePath()).getName();
            final File objectFile = new File(String.format(
                    "%s/obj/%s.o",
                    rootNode.getParser().getCompilerClass().getCompiler().getOutputPath(),
                    name.substring(0, name.length() - 4)
            ));
            
            if (!objectFile.getParentFile().exists())
                objectFile.getParentFile().mkdirs();
            
            LLVM.LLVMTargetMachineEmitToFile(
                    targetMachineRef,
                    this.getModule().getModuleRef(),
                    new BytePointer(objectFile.getCanonicalPath()),
                    LLVM.LLVMObjectFile,
                    errorPointer
            );
        }
        
        return error.isEmpty() ? this.getModule() : null;
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
    
    public void createFunction(
            final @NotNull FunctionNode functionNode,
            final boolean foreign
    ) {
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
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
                // TODO: 6/3/20 Remake the typekind system
                .returnType(this.getTypeRef(functionNode.getTypeKind(), false))
                .foreignFunction(foreign || functionNode.getBlockNode() == null)
                .build();
    }
    
    @Override
    public FunctionNode visit(final @NotNull FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
        
        final LLVMValueRef functionValue = LLVM.LLVMGetNamedFunction(
                this.getModule().getModuleRef(),
                functionNode.getName().getTokenContent()
        );
        if (functionValue == null)
            throw new NullPointerException("2");
        
        if (functionNode.getBlockNode() != null) {
            final LLVMBasicBlockRef basicBlock = LLVM.LLVMGetFirstBasicBlock(functionValue);
            if (basicBlock == null)
                throw new NullPointerException("3");
            this.getBuilder().setPositionAtEnd(basicBlock);
            
            this.visit(functionNode.getBlockNode());
        }
        return functionNode;
    }
    
    @Override
    public ImportNode visit(final @NotNull ImportNode importNode) {
        return importNode;
    }
    
    @Override
    public ReturnNode visit(final @NotNull ReturnNode returnNode) {
        if (returnNode.getExpression() != null) {
            final Object object = this.visit(returnNode.getExpression());
            if (!(object instanceof LLVMValueRef))
                return returnNode;
        
            this.getBuilder().returnValue(((LLVMValueRef) object));
        } else this.getBuilder().returnVoid();
        return returnNode;
    }
    
    @Override
    public LLVMValueRef visit(final @NotNull VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getName(), "variableNode.name must not be null.");
        
        if (variableNode.isLocal()) {
            if (variableNode.getReturnType() != null) {
                return LLVM.LLVMBuildAlloca(
                        this.getBuilder().getBuilderRef(),
                        this.visit(variableNode.getReturnType()),
                        variableNode.getName().getTokenContent()
                );
            } else {
                return LLVM.LLVMBuildAlloca(
                        this.getBuilder().getBuilderRef(),
                        this.getTypeRef(variableNode.getTypeKind(), false),
                        variableNode.getName().getTokenContent()
                );
            }
        } else {
            if (variableNode.getReturnType() != null) {
                return LLVM.LLVMAddGlobal(
                        this.getModule().getModuleRef(),
                        this.visit(variableNode.getReturnType()),
                        variableNode.getName().getTokenContent()
                );
            } else {
                return LLVM.LLVMAddGlobal(
                        this.getModule().getModuleRef(),
                        this.getTypeRef(variableNode.getTypeKind(), false),
                        variableNode.getName().getTokenContent()
                );
            }
        }
    }
    
    @Override
    public LLVMValueRef visit(final @NotNull StringNode stringNode) {
        Objects.requireNonNull(stringNode.getStringToken(), "stringNode.stringToken must not be null.");
        return LLVM.LLVMBuildGlobalStringPtr(
                this.getBuilder().getBuilderRef(),
                stringNode.getStringToken().getTokenContent(),
                ""
        );
    }
    
    @Override
    public LLVMValueRef visit(final @NotNull NumberNode numberNode) {
        Objects.requireNonNull(numberNode.getNumberToken(), "numberNode.numberToken must not be null.");
        
        if (numberNode.getTypeKind() == TypeKind.INTEGER) {
            // TODO: 6/4/20 Signed integers
            final int value = Integer.parseInt(numberNode.getNumberToken().getTokenContent());
            return LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), value, 0);
        }
        
        return null;
    }
    
    @Override
    public LLVMValueRef visit(final @NotNull IdentifierNode identifierNode) {
        Objects.requireNonNull(identifierNode.getIdentifier(), "identifierNode.identifier must not be null.");
        
        if (identifierNode.isFunctionCall()) {
            Objects.requireNonNull(identifierNode.getExpressions(), "identifierNode.expression must not be null.");
            
            final LLVMValueRef functionValue = LLVM.LLVMGetNamedFunction(
                    this.getModule().getModuleRef(),
                    identifierNode.getIdentifier().getTokenContent()
            );
            if (functionValue == null) {
                this.getModule().dump();
                throw new NullPointerException(identifierNode.getIdentifier().getTokenContent());
            }
            
            final List<LLVMValueRef> arguments = new ArrayList<>();
            for (final OperableNode expression : identifierNode.getExpressions().getExpressions()) {
                final Object object = this.visit(expression);
                if (!(object instanceof LLVMValueRef)) {
                    // TODO: 6/4/20 Throw error
                    continue;
                }
                
                arguments.add((LLVMValueRef) object);
            }
            
            return LLVM.LLVMBuildCall(
                    this.getBuilder().getBuilderRef(),
                    functionValue,
                    new PointerPointer<>(arguments.toArray(LLVMValueRef[]::new)),
                    arguments.size(),
                    ""
            );
        }
        
        return null;
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
    public LLVMValueRef visit(final @NotNull BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryNode.leftHandSide must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryNode.rightHandSide must not be null.");
        
        final Object lhsObject = this.visit(binaryNode.getLeftHandSide());
        if (!(lhsObject instanceof LLVMValueRef)) {
            // TODO: 6/4/20 Throw error
            return null;
        }
        
        final Object rhsObject = this.visit(binaryNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef)) {
            // TODO: 6/4/20 Throw error
            return null;
        }
        
        final LLVMValueRef lhsValue = (LLVMValueRef) lhsObject, rhsValue = (LLVMValueRef) rhsObject;
        switch (binaryNode.getOperatorType()) {
            case ADD:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildAdd(this.getBuilder().getBuilderRef(), lhsValue, rhsValue, "");
            case MUL:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildMul(this.getBuilder().getBuilderRef(), lhsValue, rhsValue, "");
            case DIV:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildUDiv(this.getBuilder().getBuilderRef(), lhsValue, rhsValue, "");
            case SUB:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildSub(this.getBuilder().getBuilderRef(), lhsValue, rhsValue, "");
            case EXP:
                throw new NullPointerException("6");
            case MOD:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildURem(this.getBuilder().getBuilderRef(), lhsValue, rhsValue, "");
        }
        
        return null;
    }
    
    @Override
    public LLVMValueRef visit(final @NotNull ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedNode.expression must not be null.");
        final Object object = this.visit(parenthesizedNode.getExpression());
        if (!(object instanceof LLVMValueRef)) {
            // TODO: 6/4/20 Throw error
            return null;
        }
        return (LLVMValueRef) object;
    }
    
    @Override
    public PostfixNode visit(final @NotNull PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixNode.leftHandSide must not be null.");
        this.visit(postfixNode.getLeftHandSide());
        return postfixNode;
    }
    
    @Override
    public LLVMValueRef visit(final @NotNull PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixNode.rightHandSide must not be null.");
        
        final Object rhsObject = this.visit(prefixNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef)) {
            // TODO: 6/4/20 Throw error
            return null;
        }
        
        final LLVMValueRef rhsValue = (LLVMValueRef) rhsObject;
        switch (prefixNode.getOperatorType()) {
            case NEGATE:
                return LLVM.LLVMBuildNeg(this.getBuilder().getBuilderRef(), rhsValue, "");
        }
        
        return null;
    }
    
    @NotNull
    private LLVMTypeRef getTypeRef(final @NotNull TypeKind typeKind, final boolean array) {
        final LLVMTypeRef typeRef;
        switch (typeKind) {
            case FLOAT:
                typeRef = LLVM.LLVMFloatType();
                break;
            case BOOLEAN:
                typeRef = LLVM.LLVMInt1Type();
                break;
            case BYTE:
                typeRef = LLVM.LLVMInt8Type();
                break;
            case CHAR:
                typeRef = LLVM.LLVMInt16Type();
                break;
            case DOUBLE:
                typeRef = LLVM.LLVMDoubleType();
                break;
            case INTEGER:
                typeRef = LLVM.LLVMInt32Type();
                break;
            case LONG:
                typeRef = LLVM.LLVMInt64Type();
                break;
            case VOID:
                typeRef = LLVM.LLVMVoidType();
                break;
            case STRING:
                typeRef = LLVM.LLVMPointerType(LLVM.LLVMInt8Type(), 0);
                break;
            default:
                throw new NullPointerException("Unhandled type: " + typeKind.name());
        }
        
        return !array ? typeRef : LLVM.LLVMPointerType(typeRef, 0);
    }
    
}
