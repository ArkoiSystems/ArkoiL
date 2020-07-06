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
package com.arkoisystems.compiler.phases.irgen;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.error.CompilerError;
import com.arkoisystems.compiler.error.ErrorPosition;
import com.arkoisystems.compiler.error.LineRange;
import com.arkoisystems.compiler.phases.irgen.builtin.BIManager;
import com.arkoisystems.compiler.phases.irgen.builtin.function.BIFunction;
import com.arkoisystems.compiler.phases.irgen.builtin.structure.BIStructure;
import com.arkoisystems.compiler.phases.irgen.llvm.*;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import com.arkoisystems.compiler.phases.parser.ast.types.TypeNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.argument.ArgumentNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.BinaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.ParenthesizedNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.UnaryNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.PrefixOperators;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.AssignNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.StructCreateNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.*;
import com.arkoisystems.compiler.visitor.IVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: 6/16/20 Add promotion and demotion of data types when calling etc
@Getter
public class IRVisitor implements IVisitor<Object>
{
    
    @NotNull
    private final HashMap<StructCreateNode, List<ParserNode>> structCreateNodes;
    
    @NonNull
    private final HashMap<ParserNode, Object> nodeRefs;
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @Setter
    @Nullable
    private StructCreateNode currentStructCreate;
    
    @Setter
    @NotNull
    private BuilderGen builderGen;
    
    @Setter
    @NotNull
    private ContextGen contextGen;
    
    @Setter
    @NotNull
    private ModuleGen moduleGen;
    
    @Setter
    private boolean failed;
    
    public IRVisitor(@NotNull final CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    
        this.structCreateNodes = new HashMap<>();
        this.nodeRefs = new HashMap<>();
    }
    
    @NotNull
    @Override
    public LLVMTypeRef visit(@NotNull final TypeNode typeNode) {
        Objects.requireNonNull(typeNode.getDataKind());
    
        LLVMTypeRef typeRef;
        switch (typeNode.getDataKind()) {
            case FLOAT:
                typeRef = this.getContextGen().makeFloatType();
                break;
            case BOOLEAN:
                typeRef = this.getContextGen().makeIntType(1);
                break;
            case DOUBLE:
                typeRef = this.getContextGen().makeDoubleType();
                break;
            case INTEGER:
                typeRef = this.getContextGen().makeIntType(typeNode.getBits());
                break;
            case VOID:
                typeRef = this.getContextGen().makeVoidType();
                break;
            case STRUCT:
                Objects.requireNonNull(typeNode.getTargetNode());
                final Object object = this.visit(typeNode.getTargetNode());
                if (!(object instanceof LLVMTypeRef))
                    throw new NullPointerException();
                
                typeRef = (LLVMTypeRef) object;
                break;
            default:
                throw new NullPointerException("Unhandled type: " + typeNode.getDataKind().name());
        }
        
        for (int index = 0; index < typeNode.getPointers(); index++)
            typeRef = LLVM.LLVMPointerType(typeRef, 0);
        return typeRef;
    }
    
    @NotNull
    @SneakyThrows
    @Override
    public ModuleGen visit(@NotNull final RootNode rootNode) {
        Objects.requireNonNull(rootNode.getCurrentScope());
        Objects.requireNonNull(rootNode.getParser());
    
        // TODO: 6/11/20 Check why its just working with the global context
        this.setContextGen(ContextGen.builder()
                .usingGlobal(true)
                .build());
        this.setBuilderGen(BuilderGen.builder()
                .contextGen(this.getContextGen())
                .build());
        this.setModuleGen(ModuleGen.builder()
                .contextGen(this.getContextGen())
                .name(rootNode.getParser().getCompilerClass().getName())
                .build());
    
        rootNode.getNodes().forEach(this::visit);
    
        final String error = moduleGen.verify();
        if (!error.isEmpty() || this.isFailed()) {
            final File file = new File(String.format(
                    "%s/error/%s.ll",
                    this.getCompilerClass().getCompiler().getOutputPath(),
                    this.getCompilerClass().getName()
            ));
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
    
            Files.write(file.toPath(), LLVM.LLVMPrintModuleToString(
                    moduleGen.getModuleRef()
            ).getStringBytes());
    
            this.getCompilerClass().getCompiler().getErrorHandler().getCompilerErrors().addAll(
                    this.getLLVMErrors(error, file.getCanonicalPath())
            );
            this.setFailed(true);
        }
    
        this.getModuleGen().dump();
        return moduleGen;
    }
    
    @NotNull
    @Override
    public ParameterListNode visit(@NotNull final ParameterListNode parameterListNode) {
        parameterListNode.getParameters().forEach(this::visit);
        return parameterListNode;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final ParameterNode parameter) {
        Objects.requireNonNull(parameter.getName());
    
        final FunctionNode targetFunction = parameter.getParent(FunctionNode.class);
        Objects.requireNonNull(targetFunction, "targetFunction must not be null.");
    
        Objects.requireNonNull(targetFunction.getBlockNode());
        Objects.requireNonNull(targetFunction.getParameterList());
        Objects.requireNonNull(targetFunction.getName());
    
        final FunctionGen functionGen = this.visit(targetFunction);
        for (int index = 0; index < targetFunction.getParameterList().getParameters().size(); index++) {
            final ParameterNode parameterNode = targetFunction.getParameterList().getParameters().get(index);
            Objects.requireNonNull(parameterNode.getName());
            if (parameterNode.getName().getTokenContent().equals(parameter.getName().getTokenContent()))
                return functionGen.getParameter(index);
        }
    
        throw new NullPointerException();
    }
    
    @Override
    public ArgumentListNode visit(@NotNull final ArgumentListNode argumentListNode) {
        argumentListNode.getArguments().forEach(this::visit);
        return argumentListNode;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final ArgumentNode argumentNode) {
        Objects.requireNonNull(argumentNode.getExpression());
        
        final Object expressionObject = this.visit(argumentNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
        return (LLVMValueRef) expressionObject;
    }
    
    @NotNull
    @Override
    public FunctionGen visit(@NotNull final FunctionNode functionNode) {
        if (this.getNodeRefs().containsKey(functionNode)) {
            final Object object = this.getNodeRefs().get(functionNode);
            if (!(object instanceof FunctionGen))
                throw new NullPointerException();
            return (FunctionGen) object;
        }
    
        Objects.requireNonNull(functionNode.getParameterList());
        Objects.requireNonNull(functionNode.getName());
    
        final boolean foreign = !this.getCompilerClass().getParser().getRootNode().getNodes().contains(functionNode);
        final FunctionGen functionGen = FunctionGen.builder()
                .moduleGen(this.getModuleGen())
                .name(functionNode.getName().getTokenContent())
                .parameters(functionNode.getParameterList().getParameters().stream()
                        .map(parameter -> ParameterGen.builder()
                                .name(Objects.requireNonNull(parameter.getName()).getTokenContent())
                                .typeRef(this.visit(Objects.requireNonNull(parameter.getTypeNode())))
                                .build())
                        .toArray(ParameterGen[]::new))
                .variadic(functionNode.getParameterList().isVariadic())
                .returnType(this.visit(functionNode.getTypeNode()))
                .build();
        this.getNodeRefs().put(functionNode, functionGen);
    
        if (!foreign) {
            if (functionNode.isBuiltin()) {
                final BIFunction foundFunction = BIManager.INSTANCE.getFunction(
                        functionNode.getName().getTokenContent()
                );
                Objects.requireNonNull(foundFunction, "foundFunction must no be null.");
    
                foundFunction.generateIR(this, functionNode);
            } else if (functionNode.getBlockNode() != null)
                this.visit(functionNode.getBlockNode());
        }
    
        return functionGen;
    }
    
    @NotNull
    @Override
    public LLVMBasicBlockRef visit(@NotNull final BlockNode blockNode) {
        if (this.getNodeRefs().containsKey(blockNode)) {
            final Object object = this.getNodeRefs().get(blockNode);
            if (!(object instanceof List<?>))
                throw new NullPointerException();
            
            final List<?> blockData = (List<?>) object;
            if (blockData.size() == 0)
                throw new NullPointerException();
            
            return (LLVMBasicBlockRef) blockData.get(0);
        }
        
        final FunctionNode functionNode = blockNode.getParent(FunctionNode.class);
        Objects.requireNonNull(functionNode, "functionNode must not be null.");
        
        final FunctionGen functionGen = this.visit(functionNode);
    
        final LLVMBasicBlockRef currentBlock = this.getBuilderGen().getCurrentBlock();
        final LLVMBasicBlockRef startBlock = this.getContextGen().appendBasicBlock(functionGen.getFunctionRef());
        
        final boolean hasReturn = blockNode.getNodes().stream()
                .anyMatch(node -> node instanceof ReturnNode);
        final boolean isFunctionBlock = functionNode == blockNode.getParentNode();
        final List<Object> returnData = new ArrayList<>();
        
        returnData.add(startBlock);
        this.getBuilderGen().setPositionAtEnd(startBlock);
        this.getNodeRefs().put(blockNode, returnData);
        
        final LLVMBasicBlockRef returnBlock;
        if (isFunctionBlock) {
            final LLVMValueRef returnVariable;
            if (blockNode.getTypeNode().getDataKind() != DataKind.VOID) {
                returnVariable = this.getBuilderGen().buildAlloca(
                        this.visit(blockNode.getTypeNode())
                );
                returnData.add(returnVariable);
            } else returnVariable = null;
    
            returnBlock = this.getContextGen().appendBasicBlock(functionGen.getFunctionRef());
            this.getBuilderGen().setPositionAtEnd(returnBlock);
            returnData.add(returnBlock);
    
            if (blockNode.getTypeNode().getDataKind() != DataKind.VOID) {
                final LLVMValueRef returnValue = this.getBuilderGen().buildLoad(returnVariable);
                this.getBuilderGen().returnValue(returnValue);
            } else this.getBuilderGen().returnVoid();
    
            this.getBuilderGen().setPositionAtEnd(startBlock);
        } else returnBlock = null;
    
        blockNode.getNodes().forEach(this::visit);
    
        if (!hasReturn && isFunctionBlock)
            LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), returnBlock);
    
        if (isFunctionBlock) {
            LLVM.LLVMMoveBasicBlockAfter(returnBlock, LLVM.LLVMGetLastBasicBlock(
                    functionGen.getFunctionRef()
            ));
        }
    
        if (currentBlock != null)
            this.getBuilderGen().setPositionAtEnd(currentBlock);
        
        return startBlock;
    }
    
    @NotNull
    @Override
    public ReturnNode visit(@NotNull final ReturnNode returnNode) {
        final FunctionNode functionNode = returnNode.getParent(FunctionNode.class);
        Objects.requireNonNull(functionNode, "functionNode must not be null.");
    
        final Object object = this.getNodeRefs().get(functionNode.getBlockNode());
        if (!(object instanceof List<?>))
            throw new NullPointerException();
    
        final List<?> blockData = (List<?>) object;
        if (functionNode.getTypeNode().getDataKind() == DataKind.VOID) {
            final LLVMBasicBlockRef returnBlock = (LLVMBasicBlockRef) blockData.get(1);
            LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), returnBlock);
        } else {
            Objects.requireNonNull(returnNode.getExpression());
    
            final Object expressionObject = this.visit(returnNode.getExpression());
            if (!(expressionObject instanceof LLVMValueRef))
                throw new NullPointerException();
    
            final LLVMValueRef expressionValue = (LLVMValueRef) expressionObject;
            final LLVMValueRef variableRef = (LLVMValueRef) blockData.get(1);
    
            LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), expressionValue, variableRef);
    
            final LLVMBasicBlockRef returnBlock = (LLVMBasicBlockRef) blockData.get(2);
            LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), returnBlock);
        }
    
        return returnNode;
    }
    
    @NotNull
    @Override
    public ImportNode visit(@NotNull final ImportNode importNode) {
        return importNode;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final VariableNode variableNode) {
        if (this.getNodeRefs().containsKey(variableNode)) {
            final Object object = this.getNodeRefs().get(variableNode);
            if (!(object instanceof LLVMValueRef))
                throw new NullPointerException();
            return (LLVMValueRef) object;
        }
    
        if (this.getCurrentStructCreate() != null) {
            final List<ParserNode> nodes = this.getStructCreateNodes().get(this.getCurrentStructCreate());
            nodes.add(variableNode);
        }
    
        Objects.requireNonNull(variableNode.getName());
    
        final LLVMValueRef variableRef;
        if (variableNode.isLocal() || variableNode.getParentNode() instanceof StructNode) {
            variableRef = this.getBuilderGen().buildAlloca(this.visit(variableNode.getTypeNode()));
            this.getNodeRefs().put(variableNode, variableRef);
    
            if (variableNode.getExpression() != null) {
                final Object object = this.visit(variableNode.getExpression());
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
        
                LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), (LLVMValueRef) object, variableRef);
            }
        } else {
            variableRef = this.getModuleGen().buildGlobal(this.visit(variableNode.getTypeNode()));
            this.getNodeRefs().put(variableNode, variableRef);
        
            if (variableNode.getExpression() != null) {
                final Object object = this.visit(variableNode.getExpression());
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
            
                LLVM.LLVMSetInitializer(variableRef, (LLVMValueRef) object);
            }
        }
    
        return variableRef;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final StringNode stringNode) {
        Objects.requireNonNull(stringNode.getStringToken());
        final LLVMValueRef stringConstant = LLVM.LLVMConstString(
                stringNode.getStringToken().getTokenContent(),
                stringNode.getStringToken().getTokenContent().length(),
                0
        );
    
        final LLVMValueRef variableRef = LLVM.LLVMAddGlobal(
                this.getModuleGen().getModuleRef(),
                LLVM.LLVMTypeOf(stringConstant),
                ""
        );
    
        LLVM.LLVMSetLinkage(variableRef, LLVM.LLVMPrivateLinkage);
        LLVM.LLVMSetUnnamedAddress(variableRef, LLVM.LLVMGlobalUnnamedAddr);
        LLVM.LLVMSetGlobalConstant(variableRef, 1);
        LLVM.LLVMSetInitializer(variableRef, stringConstant);
        LLVM.LLVMSetAlignment(variableRef, 1);
    
        return LLVM.LLVMConstInBoundsGEP(variableRef, new PointerPointer<>(
                LLVM.LLVMConstInt(this.getContextGen().makeIntType(32), 0, 1),
                LLVM.LLVMConstInt(this.getContextGen().makeIntType(32), 0, 1)
        ), 2);
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final NumberNode numberNode) {
        Objects.requireNonNull(numberNode.getNumberToken());
        
        if (numberNode.getTypeNode().getDataKind() == DataKind.INTEGER) {
            final int value = Integer.parseInt(numberNode.getNumberToken().getTokenContent());
            final LLVMValueRef numberRef = LLVM.LLVMConstInt(
                    this.getContextGen().makeIntType(numberNode.getNumberToken().getBits()),
                    value,
                    numberNode.getTypeNode().isSigned() ? 1 : 0
            );
            LLVM.LLVMSetAlignment(numberRef, numberNode.getTypeNode().getBits() / 8);
            return numberRef;
        } else if (numberNode.getTypeNode().getDataKind() == DataKind.FLOAT) {
            final float value = Float.parseFloat(numberNode.getNumberToken().getTokenContent());
            return LLVM.LLVMConstReal(LLVM.LLVMFloatType(), value);
        } else if (numberNode.getTypeNode().getDataKind() == DataKind.DOUBLE) {
            final double value = Double.parseDouble(numberNode.getNumberToken().getTokenContent());
            return LLVM.LLVMConstReal(LLVM.LLVMDoubleType(), value);
        }
    
        throw new NullPointerException();
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final IdentifierNode identifierNode) {
        Objects.requireNonNull(identifierNode.getCurrentScope());
        Objects.requireNonNull(identifierNode.getIdentifier());
    
        ParserNode targetNode = identifierNode.getTargetNode();
        StructNode targetStruct = null;
    
        LLVMValueRef valueTarget;
        if (targetNode instanceof ParameterNode) {
            final ParameterNode parameterNode = (ParameterNode) targetNode;
            final LLVMValueRef parameterRef = this.visit(parameterNode);
    
            if (this.getNodeRefs().containsKey(parameterNode)) {
                final Object object = this.getNodeRefs().get(parameterNode);
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
                valueTarget = (LLVMValueRef) object;
            } else {
                valueTarget = this.getBuilderGen().buildAlloca(
                        this.visit(parameterNode.getTypeNode())
                );
                this.getNodeRefs().put(parameterNode, valueTarget);
                LLVM.LLVMBuildStore(
                        this.getBuilderGen().getBuilderRef(),
                        parameterRef,
                        valueTarget
                );
            }
    
            if (parameterNode.getTypeNode().getDataKind() == DataKind.STRUCT)
                targetStruct = (StructNode) parameterNode.getTypeNode().getTargetNode();
        } else if (targetNode instanceof VariableNode) {
            final VariableNode variableNode = (VariableNode) targetNode;
            valueTarget = this.visit(variableNode);
    
            if (variableNode.getTypeNode().getDataKind() == DataKind.STRUCT)
                targetStruct = (StructNode) variableNode.getTypeNode().getTargetNode();
        } else throw new NullPointerException();
    
        if (targetStruct == null && identifierNode.getNextIdentifier() != null)
            throw new NullPointerException();
    
        valueTarget = identifierNode.isDereference()
                ? this.getBuilderGen().buildLoad(valueTarget)
                : valueTarget;
    
        IdentifierNode currentIdentifier = identifierNode;
        LLVMValueRef currentValue = valueTarget;
        while (currentIdentifier.getNextIdentifier() != null) {
            Objects.requireNonNull(targetStruct, "targetStruct must not be null.");
        
            final IdentifierNode nextIdentifier = currentIdentifier.getNextIdentifier();
            if (nextIdentifier instanceof FunctionCallNode) {
                currentValue = this.visit((FunctionCallNode) nextIdentifier);
            } else {
                targetNode = nextIdentifier.getTargetNode();
                if (!(targetNode instanceof VariableNode))
                    throw new NullPointerException();
    
                final VariableNode variableNode = (VariableNode) targetNode;
                final int index = targetStruct.getVariables().indexOf(variableNode);
            
                currentValue = LLVM.LLVMBuildStructGEP(this.getBuilderGen().getBuilderRef(), currentValue, index, "");
            
                if (variableNode.getTypeNode().getDataKind() == DataKind.STRUCT)
                    targetStruct = (StructNode) variableNode.getTypeNode().getTargetNode();
            }
        
            currentIdentifier = currentIdentifier.getNextIdentifier();
        }
    
        if (!(identifierNode instanceof AssignNode) && !identifierNode.isPointer())
            return this.getBuilderGen().buildLoad(currentValue);
    
        return currentValue;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final FunctionCallNode functionCallNode) {
        Objects.requireNonNull(functionCallNode.getArgumentList());
        Objects.requireNonNull(functionCallNode.getIdentifier());
        Objects.requireNonNull(functionCallNode.getParser());
    
        final List<FunctionNode> functions = functionCallNode.getParser().getCompilerClass()
                .getRootScope()
                .lookup(
                        functionCallNode.getIdentifier().getTokenContent(),
                        node -> {
                            if (!(node instanceof FunctionNode))
                                return false;
                    
                            final FunctionNode functionNode = (FunctionNode) node;
                            return functionNode.equalsToCall(functionCallNode);
                        }
                ).stream()
                .map(node -> (FunctionNode) node)
                .collect(Collectors.toList());
        if (functions.size() != 1)
            throw new NullPointerException();
    
        final FunctionNode targetNode = functions.get(0);
        final LLVMValueRef functionRef = this.visit(targetNode).getFunctionRef();
    
        Objects.requireNonNull(targetNode.getParameterList());
    
        final List<ArgumentNode> sortedArguments = functionCallNode.getSortedArguments(targetNode);
        final LLVMValueRef[] functionArguments = new LLVMValueRef[sortedArguments.size()];
        for (int index = 0; index < functionArguments.length; index++) {
            final ArgumentNode argumentNode = sortedArguments.get(index);
            Objects.requireNonNull(argumentNode.getExpression());
        
            final Object object = this.visit(argumentNode.getExpression());
            if (!(object instanceof LLVMValueRef))
                throw new NullPointerException();
        
            LLVMValueRef expression = (LLVMValueRef) object;
            if (index >= targetNode.getParameterList().getParameters().size())
                expression = this.promoteValue(
                        argumentNode.getExpression().getTypeNode(),
                        expression
                );
        
            functionArguments[index] = expression;
        }
    
        return this.getBuilderGen().buildFunctionCall(functionRef, functionArguments);
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final AssignNode assignNode) {
        Objects.requireNonNull(assignNode.getExpression());
        Objects.requireNonNull(assignNode.getParser());
    
        final LLVMValueRef valueRef = this.visit((IdentifierNode) assignNode);
    
        final Object expressionObject = this.visit(assignNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), (LLVMValueRef) expressionObject, valueRef);
        return (LLVMValueRef) expressionObject;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final StructCreateNode structCreateNode) {
        Objects.requireNonNull(structCreateNode.getArgumentList());
        Objects.requireNonNull(structCreateNode.getIdentifier());
        Objects.requireNonNull(structCreateNode.getParser());
    
        final List<StructNode> structNodes = structCreateNode.getParser().getCompilerClass()
                .getRootScope()
                .lookup(
                        structCreateNode.getIdentifier().getTokenContent(),
                        parserNode -> parserNode instanceof StructNode
                ).stream()
                .map(node -> (StructNode) node)
                .collect(Collectors.toList());
        if (structNodes.size() != 1)
            throw new NullPointerException();
    
        final StructCreateNode oldStructCreate = this.getCurrentStructCreate();
        final List<ParserNode> nodes = new ArrayList<>();
        this.getStructCreateNodes().put(structCreateNode, nodes);
        this.setCurrentStructCreate(structCreateNode);
    
        final StructNode targetStruct = structNodes.get(0);
        final LLVMTypeRef structRef = this.visit(targetStruct);
    
        final List<OperableNode> oldExpressions = new ArrayList<>();
        targetStruct.getVariables().forEach(variableNode -> oldExpressions.add(variableNode.getExpression()));
    
        for (int variableIndex = 0; variableIndex < targetStruct.getVariables().size(); variableIndex++) {
            final VariableNode variableNode = targetStruct.getVariables().get(variableIndex);
            Objects.requireNonNull(variableNode.getName());
        
            OperableNode expressionNode = null;
            for (int index = 0; index < structCreateNode.getArgumentList().getArguments().size(); index++) {
                final ArgumentNode argumentNode = structCreateNode.getArgumentList().getArguments().get(index);
                Objects.requireNonNull(argumentNode.getName());
            
                if (variableNode.getName().getTokenContent().equals(argumentNode.getName().getTokenContent())) {
                    expressionNode = argumentNode.getExpression();
                    break;
                }
            }
        
            if (expressionNode == null && variableNode.getExpression() != null)
                expressionNode = variableNode.getExpression();
        
            variableNode.setExpression(expressionNode);
        }
    
        final LLVMValueRef structVariable = this.getBuilderGen().buildAlloca(structRef);
        for (int index = 0; index < targetStruct.getVariables().size(); index++) {
            final VariableNode variableNode = targetStruct.getVariables().get(index);
            Objects.requireNonNull(variableNode.getExpression());
        
            final LLVMValueRef variableGEP = LLVM.LLVMBuildStructGEP(
                    this.getBuilderGen().getBuilderRef(),
                    structVariable,
                    index,
                    ""
            );
        
            final Object object = this.visit(variableNode.getExpression());
            if (!(object instanceof LLVMValueRef))
                throw new NullPointerException();
        
            LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), (LLVMValueRef) object, variableGEP);
        }
    
        for (int index = 0; index < targetStruct.getVariables().size(); index++) {
            final VariableNode variableNode = targetStruct.getVariables().get(index);
            variableNode.setExpression(oldExpressions.get(index));
        }
    
        nodes.forEach(node -> this.getNodeRefs().remove(node));
        this.setCurrentStructCreate(oldStructCreate);
    
        return this.getBuilderGen().buildLoad(structVariable);
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getRightHandSide());
        Objects.requireNonNull(binaryNode.getOperatorType());
        Objects.requireNonNull(binaryNode.getLeftHandSide());
    
        final Object lhsObject = this.visit(binaryNode.getLeftHandSide());
        if (!(lhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final Object rhsObject = this.visit(binaryNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final TypeNode rhsType = binaryNode.getRightHandSide().getTypeNode();
        final TypeNode lhsType = binaryNode.getLeftHandSide().getTypeNode();
    
        Objects.requireNonNull(rhsType.getDataKind());
        Objects.requireNonNull(lhsType.getDataKind());
    
        final boolean floatingPoint = lhsType.getDataKind().isFloating() || rhsType.getDataKind().isFloating();
        final boolean signed = lhsType.isSigned() || rhsType.isSigned();
        LLVMValueRef lhsValue = (LLVMValueRef) lhsObject, rhsValue = (LLVMValueRef) rhsObject;
        if (floatingPoint && rhsType.getDataKind() == DataKind.INTEGER) {
            rhsValue = this.getBuilderGen().buildIntToFP(
                    rhsValue,
                    this.visit(lhsType),
                    rhsType.isSigned()
            );
        } else if (floatingPoint && lhsType.getDataKind() == DataKind.INTEGER) {
            lhsValue = this.getBuilderGen().buildIntToFP(
                    lhsValue,
                    this.visit(rhsType),
                    lhsType.isSigned()
            );
        }
    
        switch (binaryNode.getOperatorType()) {
            case ADDITION:
                return this.getBuilderGen().buildAdd(floatingPoint, lhsValue, rhsValue);
            case MULTIPLICATION:
                return this.getBuilderGen().buildMul(floatingPoint, lhsValue, rhsValue);
            case DIVISION:
                return this.getBuilderGen().buildDiv(floatingPoint, signed, lhsValue, rhsValue);
            case SUBTRACTION:
                return this.getBuilderGen().buildSub(floatingPoint, lhsValue, rhsValue);
            case REMAINING:
                if (floatingPoint)
                    throw new NullPointerException();
                return this.getBuilderGen().buildRem(signed, lhsValue, rhsValue);
            case LESS_THAN:
                return this.getBuilderGen().buildLT(floatingPoint, signed, lhsValue, rhsValue);
            case GREATER_THAN:
                return this.getBuilderGen().buildGT(floatingPoint, signed, lhsValue, rhsValue);
            case LESS_EQUAL_THAN:
                return this.getBuilderGen().buildLE(floatingPoint, signed, lhsValue, rhsValue);
            case GREATER_EQUAL_THAN:
                return this.getBuilderGen().buildGE(floatingPoint, signed, lhsValue, rhsValue);
            case EQUAL:
                return this.getBuilderGen().buildEQ(floatingPoint, lhsValue, rhsValue);
            case NOT_EQUAL:
                return this.getBuilderGen().buildNE(floatingPoint, lhsValue, rhsValue);
            default:
                throw new NullPointerException();
        }
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression());
        final Object object = this.visit(parenthesizedNode.getExpression());
        if (!(object instanceof LLVMValueRef))
            throw new NullPointerException();
    
        return (LLVMValueRef) object;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final UnaryNode unaryNode) {
        Objects.requireNonNull(unaryNode.getRightHandSide());
        final Object rhsObject = this.visit(unaryNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
        
        final LLVMValueRef rhsValue = (LLVMValueRef) rhsObject;
        if (unaryNode.getOperatorType() == PrefixOperators.NEGATE)
            return builderGen.buildNeg(rhsValue);
        
        throw new NullPointerException();
    }
    
    @NotNull
    @Override
    public LLVMTypeRef visit(@NotNull final StructNode structNode) {
        if (this.getNodeRefs().containsKey(structNode)) {
            final Object object = this.getNodeRefs().get(structNode);
            if (!(object instanceof LLVMTypeRef))
                throw new NullPointerException();
            return (LLVMTypeRef) object;
        }
    
        Objects.requireNonNull(structNode.getName());
    
        final LLVMTypeRef structRef = this.getContextGen().buildStruct();
        this.getNodeRefs().put(structNode, structRef);
    
        if (structNode.isBuiltin()) {
            final BIStructure foundStructure = BIManager.INSTANCE.getStructure(
                    structNode.getName().getTokenContent()
            );
            Objects.requireNonNull(foundStructure);
        
            foundStructure.generateIR(this, structNode);
        } else {
            final LLVMTypeRef[] types = structNode.getVariables().stream()
                    .map(variableNode -> this.visit(variableNode.getTypeNode()))
                    .toArray(LLVMTypeRef[]::new);
            LLVM.LLVMStructSetBody(structRef, new PointerPointer<>(types), types.length, 0);
        }
    
        return structRef;
    }
    
    @Override
    public LLVMBasicBlockRef visit(@NotNull final IfNode ifNode) {
        if (this.getNodeRefs().containsKey(ifNode)) {
            final Object recoveryObject = this.getNodeRefs().get(ifNode);
            if (!(recoveryObject instanceof LLVMBasicBlockRef))
                throw new NullPointerException();
            return (LLVMBasicBlockRef) recoveryObject;
        }
    
        Objects.requireNonNull(ifNode.getExpression());
        Objects.requireNonNull(ifNode.getBlock());
    
        final FunctionNode functionNode = ifNode.getParent(FunctionNode.class);
        if (functionNode == null)
            throw new NullPointerException();
    
        final Object expressionObject = this.visit(ifNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final LLVMValueRef expressionValue = (LLVMValueRef) expressionObject;
        final FunctionGen functionGen = this.visit(functionNode);
    
        final LLVMBasicBlockRef recoveryBranch = this.getContextGen().appendBasicBlock(functionGen.getFunctionRef());
        this.getNodeRefs().put(ifNode, recoveryBranch);
    
        final LLVMBasicBlockRef startBranch = this.getBuilderGen().getCurrentBlock();
        Objects.requireNonNull(startBranch, "startBranch must not be null.");
    
        final LLVMBasicBlockRef thenBranch = this.visit(ifNode.getBlock());
        this.getBuilderGen().setPositionAtEnd(thenBranch);
    
        final LLVMValueRef lastInstruction = LLVM.LLVMGetLastInstruction(thenBranch);
        if (lastInstruction == null || LLVM.LLVMGetInstructionOpcode(lastInstruction) != LLVM.LLVMBr)
            LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), recoveryBranch);
    
        final LLVMBasicBlockRef elseBranch;
        if (ifNode.getNextBranch() != null)
            elseBranch = this.visit(ifNode.getNextBranch());
        else elseBranch = recoveryBranch;
    
        this.getBuilderGen().setPositionAtEnd(startBranch);
        LLVM.LLVMBuildCondBr(
                this.getBuilderGen().getBuilderRef(),
                expressionValue,
                thenBranch,
                elseBranch
        );
    
        this.getBuilderGen().setPositionAtEnd(recoveryBranch);
    
        final LLVMBasicBlockRef lastBranch = ifNode.getNextBranch() != null
                ? this.getLastBlock(ifNode.getNextBranch())
                : thenBranch;
        if (lastBranch != null)
            LLVM.LLVMMoveBasicBlockAfter(recoveryBranch, lastBranch);
    
        final LLVMBasicBlockRef currentBlock = this.getBuilderGen().getCurrentBlock();
        final List<IfNode> childNodes = ifNode.getBlock().getNodes().stream()
                .filter(node -> node instanceof IfNode)
                .map(node -> (IfNode) node)
                .collect(Collectors.toList());
    
        for (final IfNode childNode : childNodes) {
            final LLVMBasicBlockRef childRecovery = this.visit(childNode);
            this.getBuilderGen().setPositionAtEnd(childRecovery);
            LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), recoveryBranch);
        }
    
        if (currentBlock != null)
            this.getBuilderGen().setCurrentBlock(currentBlock);
    
        return recoveryBranch;
    }
    
    @Nullable
    private LLVMBasicBlockRef getLastBlock(@NotNull final ElseNode elseNode) {
        LLVMBasicBlockRef lastBlock = null;
        ElseNode currentElse = elseNode;
        while (currentElse != null) {
            if (currentElse.getNextBranch() == null) {
                Objects.requireNonNull(currentElse.getBlock());
                
                final List<IfNode> childNodes = currentElse.getBlock().getNodes().stream()
                        .filter(node -> node instanceof IfNode)
                        .map(node -> (IfNode) node)
                        .collect(Collectors.toList());
                if (childNodes.size() == 0) {
                    lastBlock = this.visit(currentElse);
                    break;
                }
                
                lastBlock = this.visit(childNodes.get(childNodes.size() - 1));
                break;
            }
            
            currentElse = currentElse.getNextBranch();
        }
        
        return lastBlock;
    }
    
    @Override
    public LLVMBasicBlockRef visit(@NotNull final ElseNode elseNode) {
        if (this.getNodeRefs().containsKey(elseNode)) {
            final Object recoveryObject = this.getNodeRefs().get(elseNode);
            if (!(recoveryObject instanceof LLVMBasicBlockRef))
                throw new NullPointerException();
            return (LLVMBasicBlockRef) recoveryObject;
        }
    
        Objects.requireNonNull(elseNode.getBlock());
        
        final IfNode parentNode = elseNode.getParent(IfNode.class);
        if (parentNode == null)
            throw new NullPointerException();
        
        final FunctionNode functionNode = elseNode.getParent(FunctionNode.class);
        if (functionNode == null)
            throw new NullPointerException();
        
        final LLVMBasicBlockRef recoveryBranch = this.visit(parentNode);
        final FunctionGen functionGen = this.visit(functionNode);
        
        if (elseNode.getExpression() != null) {
            final LLVMBasicBlockRef startBranch = this.getContextGen().appendBasicBlock(functionGen.getFunctionRef());
            this.getBuilderGen().setPositionAtEnd(startBranch);
    
            final Object expressionObject = this.visit(elseNode.getExpression());
            if (!(expressionObject instanceof LLVMValueRef))
                throw new NullPointerException();
    
            final LLVMValueRef expressionValue = (LLVMValueRef) expressionObject;
    
            final LLVMBasicBlockRef thenBranch = this.visit(elseNode.getBlock());
            this.getBuilderGen().setPositionAtEnd(thenBranch);
            this.getNodeRefs().put(elseNode, thenBranch);
    
            final LLVMValueRef lastInstruction = LLVM.LLVMGetLastInstruction(thenBranch);
            if (lastInstruction == null || LLVM.LLVMGetInstructionOpcode(lastInstruction) != LLVM.LLVMBr)
                LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), recoveryBranch);
    
            final LLVMBasicBlockRef elseBranch;
            if (elseNode.getNextBranch() != null)
                elseBranch = this.visit(elseNode.getNextBranch());
            else elseBranch = recoveryBranch;
    
            this.getBuilderGen().setPositionAtEnd(startBranch);
            LLVM.LLVMBuildCondBr(
                    this.getBuilderGen().getBuilderRef(),
                    expressionValue,
                    thenBranch,
                    elseBranch
            );
            
            final List<IfNode> childNodes = elseNode.getBlock().getNodes().stream()
                    .filter(node -> node instanceof IfNode)
                    .map(node -> (IfNode) node)
                    .collect(Collectors.toList());
            
            final LLVMBasicBlockRef currentBlock = this.getBuilderGen().getCurrentBlock();
            for (final IfNode childNode : childNodes) {
                final LLVMBasicBlockRef childRecovery = this.visit(childNode);
                this.getBuilderGen().setPositionAtEnd(childRecovery);
                LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), recoveryBranch);
            }
            
            if (currentBlock != null)
                this.getBuilderGen().setCurrentBlock(currentBlock);
            
            return startBranch;
        } else {
            final LLVMBasicBlockRef thenBranch = this.visit(elseNode.getBlock());
            this.getBuilderGen().setPositionAtEnd(thenBranch);
            this.getNodeRefs().put(elseNode, thenBranch);
    
            final LLVMValueRef lastInstruction = LLVM.LLVMGetLastInstruction(thenBranch);
            if (lastInstruction == null || LLVM.LLVMGetInstructionOpcode(lastInstruction) != LLVM.LLVMBr)
                LLVM.LLVMBuildBr(this.getBuilderGen().getBuilderRef(), recoveryBranch);
    
            return thenBranch;
        }
    }
    
    // TODO: 6/16/20 Make a conversion system (c++ standard is pretty good)
    // https://en.cppreference.com/w/c/language/conversion
    @NotNull
    private LLVMValueRef promoteValue(
            @NotNull final TypeNode typeNode,
            @NotNull final LLVMValueRef valueRef
    ) {
        if (typeNode.getPointers() > 0)
            return valueRef;
    
        if (typeNode.getDataKind() == DataKind.FLOAT) {
            return LLVM.LLVMBuildFPExt(this.getBuilderGen().getBuilderRef(), valueRef, this.getContextGen().makeDoubleType(), "");
        } else if (typeNode.getDataKind() == DataKind.INTEGER && typeNode.getBits() != 32) {
            if (typeNode.isSigned())
                return LLVM.LLVMBuildSExt(this.getBuilderGen().getBuilderRef(), valueRef, this.getContextGen().makeIntType(32), "");
            return LLVM.LLVMBuildZExt(this.getBuilderGen().getBuilderRef(), valueRef, this.getContextGen().makeIntType(32), "");
        }
    
        return valueRef;
    }
    
    @NotNull
    private List<CompilerError> getLLVMErrors(
            @NotNull final String error,
            @NotNull final String filePath
    ) {
        final List<CompilerError> errors = new ArrayList<>();
    
        final String source = LLVM.LLVMPrintModuleToString(moduleGen.getModuleRef()).getString();
        final String[] errorSplit = error.split(System.getProperty("line.separator"));
    
        for (int index = 0; index < errorSplit.length; ) {
            final String errorMessage = errorSplit[index];
        
            int errorIndex = index + 1;
            for (; errorIndex < errorSplit.length; errorIndex++) {
                if (errorSplit[index].startsWith("\\s"))
                    break;
            }
            
            final int indexOfCause = source.indexOf(errorSplit[index + 1]);
            final ErrorPosition errorPosition = this.getErrorPosition(
                    source,
                    filePath,
                    indexOfCause
            );
            
            errors.add(CompilerError.builder()
                    .causeMessage(errorMessage)
                    .causePosition(errorPosition)
                    .build());
            index += errorIndex;
        }
        
        return errors;
    }
    
    @NotNull
    private ErrorPosition getErrorPosition(
            @NotNull final String sourceCode,
            @NotNull final String filePath,
            final int causeIndex
    ) {
        int charStart = 0, charEnd = 0, lineStart = 0, lineIndex = 0, charIndex = 0;
        for (int index = 0; index < sourceCode.length(); index++) {
            final char currentChar = sourceCode.charAt(index);
            
            if (index == causeIndex) {
                lineStart = lineIndex;
                charStart = charIndex;
            } else if (index > causeIndex && currentChar == '\n' || currentChar == '\r') {
                charEnd = charIndex;
                break;
            }
            
            charIndex++;
            
            if (currentChar == '\n' || currentChar == '\r') {
                charIndex = 0;
                lineIndex++;
            }
        }
        
        return ErrorPosition.builder()
                .sourceCode(sourceCode)
                .filePath(filePath)
                .lineRange(LineRange.make(sourceCode, lineStart, lineStart))
                .charStart(charStart)
                .charEnd(charEnd)
                .build();
    }
    
}