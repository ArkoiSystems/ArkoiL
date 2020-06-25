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
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
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
    
    @NonNull
    private final HashMap<ParserNode, Object> nodeRefs;
    
    @NotNull
    private final CompilerClass compilerClass;
    
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
        
        this.nodeRefs = new HashMap<>();
    }
    
    @NotNull
    @Override
    public LLVMTypeRef visit(@NotNull final TypeNode typeNode) {
        Objects.requireNonNull(typeNode.getDataKind(), "typeNode.dataKind must not be null.");
    
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
                Objects.requireNonNull(typeNode.getTargetNode(), "typeNode.targetNode must not be null.");
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
        Objects.requireNonNull(rootNode.getCurrentScope(), "rootAST.currentScope must not be null.");
        Objects.requireNonNull(rootNode.getParser(), "rootAST.parser must not be null.");
    
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
        Objects.requireNonNull(parameter.getName(), "parameter.name must not be null.");
    
        final FunctionNode targetFunction = parameter.getParent(FunctionNode.class);
        Objects.requireNonNull(targetFunction, "targetFunction must not be null.");
    
        Objects.requireNonNull(targetFunction.getBlockNode(), "targetFunction.blockNode must not be null.");
        Objects.requireNonNull(targetFunction.getParameterList(), "targetFunction.parameters must not be null.");
        Objects.requireNonNull(targetFunction.getName(), "targetFunction.name must not be null.");
    
        final FunctionGen functionGen = this.visit(targetFunction);
        for (int index = 0; index < targetFunction.getParameterList().getParameters().size(); index++) {
            final ParameterNode parameterNode = targetFunction.getParameterList().getParameters().get(index);
            Objects.requireNonNull(parameterNode.getName(), "parameterNode.name must not be null.");
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
        Objects.requireNonNull(argumentNode.getExpression(), "argumentNode.expression must not be null.");
        
        final Object expressionObject = this.visit(argumentNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
        return (LLVMValueRef) expressionObject;
    }
    
    @NotNull
    @Override
    public LLVMBasicBlockRef visit(@NotNull final BlockNode blockNode) {
        if (this.getNodeRefs().containsKey(blockNode)) {
            final Object object = this.getNodeRefs().get(blockNode);
            if (!(object instanceof LLVMBasicBlockRef))
                throw new NullPointerException();
            return (LLVMBasicBlockRef) object;
        }
        
        final FunctionNode functionNode = blockNode.getParent(FunctionNode.class);
        Objects.requireNonNull(functionNode, "functionNode must not be null.");
        
        final LLVMBasicBlockRef currentBlock = this.getBuilderGen().getCurrentBlock();
        final LLVMBasicBlockRef basicBlockRef = this.getContextGen().appendBasicBlock(
                this.visit(functionNode)
        );
        
        this.getBuilderGen().setPositionAtEnd(basicBlockRef);
        this.getNodeRefs().put(blockNode, basicBlockRef);
        blockNode.getNodes().forEach(this::visit);
        if (currentBlock != null)
            this.getBuilderGen().setPositionAtEnd(currentBlock);
        
        return basicBlockRef;
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
    
        Objects.requireNonNull(functionNode.getParameterList(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
    
        final boolean foreign = !this.getCompilerClass().getParser().getRootNode().getNodes().contains(functionNode);
        final FunctionGen functionGen = FunctionGen.builder()
                .moduleGen(this.getModuleGen())
                .name(functionNode.getName().getTokenContent())
                .parameters(functionNode.getParameterList().getParameters().stream()
                        .map(parameter -> ParameterGen.builder()
                                .name(Objects.requireNonNull(parameter.getName(), "parameter.name must not be null.").getTokenContent())
                                .typeRef(this.visit(Objects.requireNonNull(parameter.getTypeNode(), "parameter.typeNode must not be null.")))
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
            } else if (functionNode.getBlockNode() != null) {
                this.visit(functionNode.getBlockNode());
        
                if (functionNode.getBlockNode().getTypeNode().getDataKind() == DataKind.VOID) {
                    final List<ReturnNode> returns = functionNode.getBlockNode().getNodes().stream()
                            .filter(node -> node instanceof ReturnNode)
                            .map(node -> (ReturnNode) node)
                            .collect(Collectors.toList());
                    if (returns.size() == 0)
                        this.getBuilderGen().returnVoid();
                }
            }
        }
    
        return functionGen;
    }
    
    @NotNull
    @Override
    public ImportNode visit(@NotNull final ImportNode importNode) {
        return importNode;
    }
    
    @NotNull
    @Override
    public ReturnNode visit(@NotNull final ReturnNode returnNode) {
        if (returnNode.getExpression() != null) {
            final Object expressionObject = this.visit(returnNode.getExpression());
            if (!(expressionObject instanceof LLVMValueRef))
                throw new NullPointerException();
        
            this.getBuilderGen().returnValue(((LLVMValueRef) expressionObject));
        } else this.getBuilderGen().returnVoid();
        
        return returnNode;
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
    
        Objects.requireNonNull(variableNode.getName(), "variableNode.name must not be null.");
    
        if (variableNode.isLocal()) {
            final LLVMValueRef variableRef = this.getBuilderGen().buildAlloca(this.visit(variableNode.getTypeNode()));
            this.getNodeRefs().put(variableNode, variableRef);
    
            if (variableNode.getExpression() != null) {
                final Object object = this.visit(variableNode.getExpression());
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
    
                LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), (LLVMValueRef) object, variableRef);
            }
    
            return variableRef;
        } else {
            final LLVMValueRef variableRef = this.getModuleGen().buildGlobal(this.visit(variableNode.getTypeNode()));
            this.getNodeRefs().put(variableNode, variableRef);
    
            if (variableNode.getExpression() != null) {
                final Object object = this.visit(variableNode.getExpression());
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
        
                LLVM.LLVMSetInitializer(variableRef, (LLVMValueRef) object);
            }
        
            return variableRef;
        }
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final StringNode stringNode) {
        Objects.requireNonNull(stringNode.getStringToken(), "stringNode.stringToken must not be null.");
        final LLVMValueRef variableRef = LLVM.LLVMAddGlobal(
                this.getModuleGen().getModuleRef(),
                LLVM.LLVMArrayType(
                        this.getContextGen().makeIntType(8),
                        stringNode.getStringToken().getTokenContent().length()
                ),
                ""
        );
    
        LLVM.LLVMSetLinkage(variableRef, LLVM.LLVMPrivateLinkage);
        LLVM.LLVMSetUnnamedAddress(variableRef, LLVM.LLVMGlobalUnnamedAddr);
        LLVM.LLVMSetGlobalConstant(variableRef, 1);
        LLVM.LLVMSetInitializer(variableRef, LLVM.LLVMConstString(
                stringNode.getStringToken().getTokenContent(),
                stringNode.getStringToken().getTokenContent().length(),
                1
        ));
    
        return LLVM.LLVMConstInBoundsGEP(variableRef, new PointerPointer<>(
                LLVM.LLVMConstInt(this.getContextGen().makeIntType(32), 0, 1),
                LLVM.LLVMConstInt(this.getContextGen().makeIntType(32), 0, 1)
        ), 2);
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final NumberNode numberNode) {
        Objects.requireNonNull(numberNode.getNumberToken(), "numberNode.numberToken must not be null.");
        
        if (numberNode.getTypeNode().getDataKind() == DataKind.INTEGER) {
            final int value = Integer.parseInt(numberNode.getNumberToken().getTokenContent());
            return LLVM.LLVMConstInt(this.getContextGen().makeIntType(numberNode.getNumberToken().getBits()), value, 1);
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
        Objects.requireNonNull(identifierNode.getCurrentScope(), "identifierNode.currentScope must not be null.");
        Objects.requireNonNull(identifierNode.getIdentifier(), "identifierNode.identifier must not be null.");
    
        ParserNode targetNode = identifierNode.getTargetNode();
        StructNode targetStruct = null;
    
        final LLVMValueRef valueTarget;
        if (targetNode instanceof ParameterNode) {
            final ParameterNode parameterNode = (ParameterNode) targetNode;
            valueTarget = this.visit(parameterNode);
        
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
    
        final int instOp = LLVM.LLVMGetInstructionOpcode(currentValue);
        if (!(identifierNode instanceof AssignNode) &&
                (instOp == LLVM.LLVMGetElementPtr || instOp == LLVM.LLVMAlloca))
            return this.getBuilderGen().buildLoad(currentValue);
    
        return currentValue;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final FunctionCallNode functionCallNode) {
        Objects.requireNonNull(functionCallNode.getExpressionList(), "functionCallNode.expressions must not be null.");
        Objects.requireNonNull(functionCallNode.getIdentifier(), "functionCallNode.identifier must not be null.");
        Objects.requireNonNull(functionCallNode.getParser(), "functionCallNode.parser must not be null.");
    
        final List<ParserNode> nodes = functionCallNode.getParser().getCompilerClass()
                .getRootScope()
                .lookup(functionCallNode.getIdentifier().getTokenContent());
    
        final List<FunctionNode> functions = nodes.stream()
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .filter(node -> node.equalsToCall(functionCallNode))
                .collect(Collectors.toList());
        if (functions.size() != 1)
            throw new NullPointerException();
    
        final FunctionNode targetNode = functions.get(0);
        final LLVMValueRef functionRef = this.visit(targetNode).getFunctionRef();
        
        Objects.requireNonNull(targetNode.getParameterList(), "targetNode.parameterList must not be null.");
        
        final int size = functionCallNode.getExpressionList().getExpressions().size();
        final LLVMValueRef[] arguments = new LLVMValueRef[size];
        for (int index = 0; index < size; index++) {
            final OperableNode expression = functionCallNode.getExpressionList().getExpressions().get(index);
            final Object object = this.visit(expression);
            if (!(object instanceof LLVMValueRef))
                throw new NullPointerException();
    
            LLVMValueRef valueRef = (LLVMValueRef) object;
            if (index > targetNode.getParameterList().getParameters().size() - 1)
                valueRef = this.promoteValue(
                        this.getBuilderGen(),
                        expression.getTypeNode(),
                        valueRef
                );
            arguments[index] = valueRef;
        }
    
        return this.getBuilderGen().buildFunctionCall(functionRef, arguments);
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final AssignNode assignNode) {
        Objects.requireNonNull(assignNode.getExpression(), "structCreationNode.expression must not be null.");
        Objects.requireNonNull(assignNode.getParser(), "structCreationNode.parser must not be null.");
    
        final LLVMValueRef valueRef = this.visit((IdentifierNode) assignNode);
    
        final Object expressionObject = this.visit(assignNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), (LLVMValueRef) expressionObject, valueRef);
        return (LLVMValueRef) expressionObject;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final StructCreateNode structCreateNode) {
        Objects.requireNonNull(structCreateNode.getArgumentList(), "structCreationNode.argumentList must not be null.");
        Objects.requireNonNull(structCreateNode.getIdentifier(), "structCreationNode.identifier must not be null.");
        Objects.requireNonNull(structCreateNode.getParser(), "structCreationNode.parser must not be null.");
    
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
    
        final StructNode targetStruct = structNodes.get(0);
        final LLVMTypeRef structRef = this.visit(targetStruct);
    
        final LLVMValueRef[] arguments = new LLVMValueRef[targetStruct.getVariables().size()];
        for (int argumentIndex = 0; argumentIndex < structCreateNode.getArgumentList().getArguments().size(); argumentIndex++) {
            final ArgumentNode argumentNode = structCreateNode.getArgumentList().getArguments().get(argumentIndex);
            Objects.requireNonNull(argumentNode.getName(), "argumentNode.name must not be null.");
            
            for (int variableIndex = 0; variableIndex < targetStruct.getVariables().size(); variableIndex++) {
                final VariableNode variableNode = targetStruct.getVariables().get(variableIndex);
                Objects.requireNonNull(variableNode.getName(), "variableNode.name must not be null.");
                
                if (variableNode.getName().getTokenContent().equals(argumentNode.getName().getTokenContent()))
                    arguments[variableIndex] = this.visit(argumentNode);
            }
        }
        
        for (int index = 0; index < targetStruct.getVariables().size(); index++) {
            if (arguments[index] != null) continue;
    
            final VariableNode variableNode = targetStruct.getVariables().get(index);
            if (variableNode.getExpression() == null)
                continue;
    
            final Object object = this.visit(variableNode.getExpression());
            if (!(object instanceof LLVMValueRef))
                throw new NullPointerException();
            arguments[index] = (LLVMValueRef) object;
        }
        
        return LLVM.LLVMConstNamedStruct(structRef, new PointerPointer<>(arguments), arguments.length);
    }
    
    @NotNull
    @Override
    public ExpressionListNode visit(@NotNull final ExpressionListNode expressionListNode) {
        expressionListNode.getExpressions().forEach(this::visit);
        return expressionListNode;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryNode.rightHandSide must not be null.");
        Objects.requireNonNull(binaryNode.getOperatorType(), "binaryNode.operatorType must not be null.");
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryNode.leftHandSide must not be null.");
    
        final Object lhsObject = this.visit(binaryNode.getLeftHandSide());
        if (!(lhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final Object rhsObject = this.visit(binaryNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final TypeNode rhsType = binaryNode.getRightHandSide().getTypeNode();
        final TypeNode lhsType = binaryNode.getLeftHandSide().getTypeNode();
    
        Objects.requireNonNull(rhsType.getDataKind(), "rhsType.dataKind must not be null.");
        Objects.requireNonNull(lhsType.getDataKind(), "lhsType.dataKind must not be null.");
    
        final boolean floatingPoint = lhsType.getDataKind().isFloating() || rhsType.getDataKind().isFloating();
        final boolean signed = lhsType.isSigned() || rhsType.isSigned();
        LLVMValueRef lhsValue = (LLVMValueRef) lhsObject, rhsValue = (LLVMValueRef) rhsObject;
        if (floatingPoint) {
            if (binaryNode.getRightHandSide().getTypeNode().getDataKind() == DataKind.INTEGER) {
                if (binaryNode.getRightHandSide().getTypeNode().isSigned()) {
                    rhsValue = LLVM.LLVMBuildSIToFP(
                            this.getBuilderGen().getBuilderRef(),
                            rhsValue,
                            this.visit(lhsType),
                            ""
                    );
                } else {
                    rhsValue = LLVM.LLVMBuildUIToFP(
                            this.getBuilderGen().getBuilderRef(),
                            rhsValue,
                            this.visit(lhsType),
                            ""
                    );
                }
            } else if (binaryNode.getLeftHandSide().getTypeNode().getDataKind() == DataKind.INTEGER) {
                if (binaryNode.getLeftHandSide().getTypeNode().isSigned()) {
                    lhsValue = LLVM.LLVMBuildSIToFP(
                            this.getBuilderGen().getBuilderRef(),
                            lhsValue,
                            this.visit(rhsType),
                            ""
                    );
                } else {
                    lhsValue = LLVM.LLVMBuildUIToFP(
                            this.getBuilderGen().getBuilderRef(),
                            lhsValue,
                            this.visit(rhsType),
                            ""
                    );
                }
            }
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
            default:
                throw new NullPointerException();
        }
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedNode.expression must not be null.");
        final Object object = this.visit(parenthesizedNode.getExpression());
        if (!(object instanceof LLVMValueRef))
            throw new NullPointerException();
        return (LLVMValueRef) object;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final UnaryNode unaryNode) {
        Objects.requireNonNull(unaryNode.getRightHandSide(), "prefixNode.rightHandSide must not be null.");
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
    
        Objects.requireNonNull(structNode.getName(), "structNode.name must not be null.");
    
        final LLVMTypeRef structRef = this.getContextGen().buildStruct();
        this.getNodeRefs().put(structNode, structRef);
    
        if (structNode.isBuiltin()) {
            final BIStructure foundStructure = BIManager.INSTANCE.getStructure(
                    structNode.getName().getTokenContent()
            );
            Objects.requireNonNull(foundStructure, "foundStructure must not be null.");
        
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
        
        Objects.requireNonNull(ifNode.getExpression(), "ifNode.expression must not be null.");
        Objects.requireNonNull(ifNode.getBlock(), "ifNode.block must not be null.");
        
        final FunctionNode functionNode = ifNode.getParent(FunctionNode.class);
        if (functionNode == null)
            throw new NullPointerException();
        
        final Object expressionObject = this.visit(ifNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
        
        final LLVMValueRef expressionValue = (LLVMValueRef) expressionObject;
        final FunctionGen functionGen = this.visit(functionNode);
        
        final LLVMBasicBlockRef recoveryBranch = this.getContextGen().appendBasicBlock(functionGen);
        this.getNodeRefs().put(ifNode, recoveryBranch);
        
        final LLVMBasicBlockRef startBranch = this.getBuilderGen().getCurrentBlock();
        Objects.requireNonNull(startBranch, "startBranch must not be null.");
        
        final LLVMBasicBlockRef thenBranch = this.visit(ifNode.getBlock());
        this.getBuilderGen().setPositionAtEnd(thenBranch);
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
        
        LLVMBasicBlockRef lastBranch = null;
        if (ifNode.getNextBranch() != null) {
            ElseNode currentElse = ifNode.getNextBranch();
            while (currentElse != null) {
                if (currentElse.getNextBranch() == null) {
                    lastBranch = this.visit(currentElse);
                    break;
                }
                
                currentElse = currentElse.getNextBranch();
            }
        } else lastBranch = thenBranch;
        
        if (lastBranch != null)
            LLVM.LLVMMoveBasicBlockAfter(recoveryBranch, lastBranch);
        return recoveryBranch;
    }
    
    @Override
    public LLVMBasicBlockRef visit(@NotNull final ElseNode elseNode) {
        if (this.getNodeRefs().containsKey(elseNode)) {
            final Object recoveryObject = this.getNodeRefs().get(elseNode);
            if (!(recoveryObject instanceof LLVMBasicBlockRef))
                throw new NullPointerException();
            return (LLVMBasicBlockRef) recoveryObject;
        }
        
        Objects.requireNonNull(elseNode.getExpression(), "elseNode.expression must not be null.");
        Objects.requireNonNull(elseNode.getBlock(), "elseNode.block must not be null.");
        
        final IfNode ifNode = elseNode.getParent(IfNode.class);
        if (ifNode == null)
            throw new NullPointerException();
        
        final FunctionNode functionNode = elseNode.getParent(FunctionNode.class);
        if (functionNode == null)
            throw new NullPointerException();
        
        final FunctionGen functionGen = this.visit(functionNode);
        
        final LLVMBasicBlockRef startBranch = this.getContextGen().appendBasicBlock(functionGen);
        this.getBuilderGen().setPositionAtEnd(startBranch);
        
        final Object expressionObject = this.visit(elseNode.getExpression());
        if (!(expressionObject instanceof LLVMValueRef))
            throw new NullPointerException();
        
        final LLVMValueRef expressionValue = (LLVMValueRef) expressionObject;
        final LLVMBasicBlockRef thenBranch = this.visit(elseNode.getBlock());
        final LLVMBasicBlockRef recoveryBranch = this.visit(ifNode);
        
        this.getNodeRefs().put(elseNode, thenBranch);
        this.getBuilderGen().setPositionAtEnd(thenBranch);
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
        
        return startBranch;
    }
    
    // TODO: 6/16/20 Make a conversion system (c++ standard is pretty good)
    // https://en.cppreference.com/w/c/language/conversion
    @NotNull
    private LLVMValueRef promoteValue(
            @NotNull final BuilderGen builderGen,
            @NotNull final TypeNode typeNode,
            @NotNull final LLVMValueRef valueRef
    ) {
        if (typeNode.getPointers() > 0)
            return valueRef;
    
        if (typeNode.getDataKind() == DataKind.FLOAT)
            return LLVM.LLVMBuildFPExt(builderGen.getBuilderRef(), valueRef, this.getContextGen().makeDoubleType(), "");
        else if (typeNode.getDataKind() == DataKind.INTEGER && typeNode.getBits() != 32) {
            if (typeNode.isSigned())
                return LLVM.LLVMBuildSExt(builderGen.getBuilderRef(), valueRef, this.getContextGen().makeIntType(32), "");
            else
                return LLVM.LLVMBuildZExt(builderGen.getBuilderRef(), valueRef, this.getContextGen().makeIntType(32), "");
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
