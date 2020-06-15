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
import com.arkoisystems.compiler.phases.parser.ast.types.operable.OperableNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.IdentifierNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.NumberNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.StringNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.ExpressionListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.expression.types.enums.PrefixOperators;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterListNode;
import com.arkoisystems.compiler.phases.parser.ast.types.parameter.ParameterNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ReturnNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.VariableNode;
import com.arkoisystems.compiler.visitor.IVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
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

@Getter
public class IRVisitor implements IVisitor<Object>
{
    
    @NonNull
    private final HashMap<ParserNode, Object> nodeRefs;
    
    @NotNull
    private final CompilerClass compilerClass;
    
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
        final FunctionNode targetFunction = parameter.getParent(FunctionNode.class);
        Objects.requireNonNull(targetFunction, "targetFunction must not be null.");
        
        Objects.requireNonNull(targetFunction.getBlockNode(), "targetFunction.blockNode must not be null.");
        Objects.requireNonNull(targetFunction.getParameterList(), "targetFunction.parameters must not be null.");
        Objects.requireNonNull(targetFunction.getName(), "targetFunction.name must not be null.");
        Objects.requireNonNull(parameter.getName(), "parameter.name must not be null.");
        
        final FunctionGen functionGen = this.visit(targetFunction);
        
        int index = 0;
        for (; index < targetFunction.getParameterList().getParameters().size(); index++) {
            final ParameterNode parameterNode = targetFunction.getParameterList().getParameters().get(index);
            Objects.requireNonNull(parameterNode.getName(), "parameterNode.name must not be null.");
            if (parameterNode.getName().getTokenContent().equals(parameter.getName().getTokenContent()))
                break;
        }
        
        return functionGen.getParameter(index);
    }
    
    @NotNull
    @Override
    public BuilderGen visit(@NotNull final BlockNode blockNode) {
        if (this.getNodeRefs().containsKey(blockNode)) {
            final Object object = this.getNodeRefs().get(blockNode);
            if (!(object instanceof BuilderGen))
                throw new NullPointerException();
            return (BuilderGen) object;
        }
    
        final FunctionNode functionNode = blockNode.getParent(FunctionNode.class);
        Objects.requireNonNull(functionNode, "functionNode must not be null.");
    
        final LLVMBasicBlockRef basicBlock = this.getContextGen().appendBasicBlock(
                this.visit(functionNode)
        );
    
        final BuilderGen builderGen = BuilderGen.builder()
                .contextGen(this.getContextGen())
                .build();
        builderGen.setPositionAtEnd(basicBlock);
        this.getNodeRefs().put(blockNode, builderGen);
    
        blockNode.getNodes().forEach(this::visit);
        return builderGen;
    }
    
    @NotNull
    @Override
    public FunctionGen visit(@NotNull final FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getParameterList(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
    
        if (this.getNodeRefs().containsKey(functionNode)) {
            final Object object = this.getNodeRefs().get(functionNode);
            if (!(object instanceof FunctionGen))
                throw new NullPointerException();
            return (FunctionGen) object;
        }
    
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
    
        if (foreign)
            return functionGen;
    
        if (functionNode.isBuiltin()) {
            final BIFunction foundFunction = BIManager.INSTANCE.getFunction(
                    functionNode.getName().getTokenContent()
            );
            Objects.requireNonNull(foundFunction, "foundFunction must no be null.");
        
            foundFunction.generateIR(this, functionNode);
        } else if (functionNode.getBlockNode() != null)
            this.visit(functionNode.getBlockNode());
    
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
        final BlockNode blockNode = returnNode.getParent(BlockNode.class);
        Objects.requireNonNull(blockNode, "blockNode must not be null.");
    
        final Object builderObject = this.getNodeRefs().getOrDefault(blockNode, null);
        Objects.requireNonNull(builderObject, "builderObject must not be null.");
    
        if (!(builderObject instanceof BuilderGen))
            throw new NullPointerException();
    
        final BuilderGen builderGen = (BuilderGen) builderObject;
        Objects.requireNonNull(builderGen, "builderGen must not be null.");
    
        if (returnNode.getExpression() != null) {
            final Object expressionObject = this.visit(returnNode.getExpression());
            if (!(expressionObject instanceof LLVMValueRef))
                throw new NullPointerException();
        
            builderGen.returnValue(((LLVMValueRef) expressionObject));
        } else builderGen.returnVoid();
        
        return returnNode;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getName(), "variableNode.name must not be null.");
    
        final BlockNode blockNode = variableNode.getParent(BlockNode.class);
        Objects.requireNonNull(blockNode, "blockNode must not be null.");
    
        final Object builderObject = this.getNodeRefs().getOrDefault(blockNode, null);
        Objects.requireNonNull(builderObject, "builderObject must not be null.");
    
        if (!(builderObject instanceof BuilderGen))
            throw new NullPointerException();
    
        final BuilderGen builderGen = (BuilderGen) builderObject;
    
        if (this.getNodeRefs().containsKey(variableNode)) {
            final Object object = this.getNodeRefs().get(variableNode);
            if (!(object instanceof LLVMValueRef))
                throw new NullPointerException();
            return builderGen.buildLoad((LLVMValueRef) object);
        }
    
        final LLVMValueRef variableRef;
        if (variableNode.isLocal()) {
            variableRef = builderGen.buildAlloca(
                    variableNode.getReturnType() == null ?
                            this.visit(variableNode.getTypeNode()) :
                            this.visit(variableNode.getReturnType())
            );
        
            this.getNodeRefs().put(variableNode, variableRef);
        
            if (variableNode.getExpression() != null) {
                final Object object = this.visit(variableNode.getExpression());
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
            
                LLVM.LLVMBuildStore(builderGen.getBuilderRef(), (LLVMValueRef) object, variableRef);
            }
        } else {
            variableRef = this.getModuleGen().buildGlobal(
                    variableNode.getReturnType() == null ?
                            this.visit(variableNode.getTypeNode()) :
                            this.visit(variableNode.getReturnType())
            );
        
            this.getNodeRefs().put(variableNode, variableRef);
        }
        
        return variableRef;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final StringNode stringNode) {
        Objects.requireNonNull(stringNode.getStringToken(), "stringNode.stringToken must not be null.");
    
        final BlockNode blockNode = stringNode.getParent(BlockNode.class);
        Objects.requireNonNull(blockNode, "blockNode must not be null.");
    
        final Object builderObject = this.getNodeRefs().getOrDefault(blockNode, null);
        Objects.requireNonNull(builderObject, "builderObject must not be null.");
    
        if (!(builderObject instanceof BuilderGen))
            throw new NullPointerException();
    
        final BuilderGen builderGen = (BuilderGen) builderObject;
        return builderGen.buildGlobalStringPtr(stringNode.getStringToken().getTokenContent());
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
        
        if (identifierNode.isFunctionCall()) {
            Objects.requireNonNull(identifierNode.getExpressionList(), "identifierNode.expressions must not be null.");
            Objects.requireNonNull(identifierNode.getExpressionList(), "identifierNode.expression must not be null.");
            Objects.requireNonNull(identifierNode.getParser(), "identifierNode.parser must not be null.");
    
            final BlockNode blockNode = identifierNode.getParent(BlockNode.class);
            Objects.requireNonNull(blockNode, "blockNode must not be null.");
    
            final Object builderObject = this.getNodeRefs().getOrDefault(blockNode, null);
            Objects.requireNonNull(builderObject, "builderObject must not be null.");
    
            if (!(builderObject instanceof BuilderGen))
                throw new NullPointerException();
    
            final BuilderGen builderGen = (BuilderGen) builderObject;
            final List<ParserNode> nodes = Objects.requireNonNullElse(
                    identifierNode.getParser()
                            .getCompilerClass()
                            .getRootScope()
                            .lookup(identifierNode.getIdentifier().getTokenContent()),
                    new ArrayList<>()
            );
    
            final List<FunctionNode> functions = nodes.stream()
                    .filter(node -> node instanceof FunctionNode)
                    .map(node -> (FunctionNode) node)
                    .filter(node -> node.equalsToIdentifier(identifierNode))
                    .collect(Collectors.toList());
            if (functions.size() != 1)
                throw new NullPointerException();
            
            final FunctionNode targetNode = functions.get(0);
            final LLVMValueRef functionRef = this.visit(targetNode).getFunctionRef();
            
            final int size = identifierNode.getExpressionList().getExpressions().size();
            final LLVMValueRef[] arguments = new LLVMValueRef[size];
            for (int index = 0; index < size; index++) {
                final OperableNode expression = identifierNode.getExpressionList().getExpressions().get(index);
                final Object object = this.visit(expression);
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
                
                arguments[index] = (LLVMValueRef) object;
            }
            
            return builderGen.buildFunctionCall(functionRef, arguments);
        } else {
            final List<ParserNode> nodes = Objects.requireNonNullElse(identifierNode.getCurrentScope().lookup(
                    identifierNode.getIdentifier().getTokenContent()
            ), new ArrayList<>());
            if (nodes.size() != 1)
                throw new NullPointerException();
            
            final ParserNode targetNode = nodes.get(0);
            if (targetNode instanceof ParameterNode) {
                final ParameterNode targetParameter = (ParameterNode) targetNode;
                return this.visit(targetParameter);
            } else if (targetNode instanceof VariableNode) {
                final VariableNode variableNode = (VariableNode) targetNode;
                return this.visit(variableNode);
            }
            
            throw new NullPointerException();
        }
    }
    
    @NotNull
    @Override
    public ExpressionListNode visit(@NotNull final ExpressionListNode expressionListNode) {
        expressionListNode.getExpressions().forEach(this::visit);
        return expressionListNode;
    }
    
    @NotNull
    @Override
    public AssignmentNode visit(@NotNull final AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentNode.leftHandSide must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentNode.rightHandSide must not be null.");
        
        this.visit(assignmentNode.getLeftHandSide());
        this.visit(assignmentNode.getRightHandSide());
        return assignmentNode;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final BinaryNode binaryNode) {
        Objects.requireNonNull(binaryNode.getLeftHandSide(), "binaryNode.leftHandSide must not be null.");
        Objects.requireNonNull(binaryNode.getRightHandSide(), "binaryNode.rightHandSide must not be null.");
    
        final BlockNode blockNode = binaryNode.getParent(BlockNode.class);
        Objects.requireNonNull(blockNode, "blockNode must not be null.");
    
        final Object builderObject = this.getNodeRefs().getOrDefault(blockNode, null);
        Objects.requireNonNull(builderObject, "builderObject must not be null.");
    
        if (!(builderObject instanceof BuilderGen))
            throw new NullPointerException();
    
        final BuilderGen builderGen = (BuilderGen) builderObject;
    
        final Object lhsObject = this.visit(binaryNode.getLeftHandSide());
        if (!(lhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final Object rhsObject = this.visit(binaryNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final boolean floating = binaryNode.getLeftHandSide().getTypeNode().getDataKind().isFloating() ||
                binaryNode.getRightHandSide().getTypeNode().getDataKind().isFloating();
        LLVMValueRef lhsValue = (LLVMValueRef) lhsObject, rhsValue = (LLVMValueRef) rhsObject;
        if (floating) {
            if (!binaryNode.getLeftHandSide().getTypeNode().getDataKind().isFloating())
                lhsValue = LLVM.LLVMBuildFPCast(builderGen.getBuilderRef(), lhsValue, this.visit(binaryNode.getLeftHandSide().getTypeNode()), "");
            if (!binaryNode.getRightHandSide().getTypeNode().getDataKind().isFloating())
                rhsValue = LLVM.LLVMBuildFPCast(builderGen.getBuilderRef(), rhsValue, this.visit(binaryNode.getLeftHandSide().getTypeNode()), "");
        }
        
        switch (binaryNode.getOperatorType()) {
            case ADD:
                if (floating)
                    return LLVM.LLVMBuildFAdd(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
                return LLVM.LLVMBuildAdd(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
            case MUL:
                if (floating)
                    return LLVM.LLVMBuildFMul(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
                return LLVM.LLVMBuildMul(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
            case DIV:
                if (floating)
                    return LLVM.LLVMBuildFDiv(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
                return LLVM.LLVMBuildUDiv(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
            case SUB:
                if (floating)
                    return LLVM.LLVMBuildFSub(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
                return LLVM.LLVMBuildSub(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
            case MOD:
                if (floating)
                    throw new NullPointerException();
                return LLVM.LLVMBuildURem(builderGen.getBuilderRef(), lhsValue, rhsValue, "");
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
    public LLVMValueRef visit(@NotNull final PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixNode.leftHandSide must not be null.");
        final Object object = this.visit(postfixNode.getLeftHandSide());
        if (!(object instanceof LLVMValueRef))
            throw new NullPointerException();
        return (LLVMValueRef) object;
    }
    
    @NotNull
    @Override
    public LLVMValueRef visit(@NotNull final PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixNode.rightHandSide must not be null.");
    
        final BlockNode blockNode = prefixNode.getParent(BlockNode.class);
        Objects.requireNonNull(blockNode, "blockNode must not be null.");
    
        final Object builderObject = this.getNodeRefs().getOrDefault(blockNode, null);
        Objects.requireNonNull(builderObject, "builderObject must not be null.");
    
        if (!(builderObject instanceof BuilderGen))
            throw new NullPointerException();
    
        final BuilderGen builderGen = (BuilderGen) builderObject;
    
        final Object rhsObject = this.visit(prefixNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef))
            throw new NullPointerException();
    
        final LLVMValueRef rhsValue = (LLVMValueRef) rhsObject;
        if (prefixNode.getOperatorType() == PrefixOperators.NEGATE)
            return builderGen.buildNeg(rhsValue);
    
        throw new NullPointerException();
    }
    
    @NotNull
    @Override
    public LLVMTypeRef visit(@NotNull final StructNode structNode) {
        Objects.requireNonNull(structNode.getName(), "structNode.name must not be null.");
    
        if (this.getNodeRefs().containsKey(structNode)) {
            final Object object = this.getNodeRefs().get(structNode);
            if (!(object instanceof LLVMTypeRef))
                throw new NullPointerException();
            return (LLVMTypeRef) object;
        }
    
        final LLVMTypeRef structRef = this.getContextGen().buildStruct();
        this.getNodeRefs().put(structNode, structRef);
    
        if (structNode.isBuiltin()) {
            final BIStructure foundStructure = BIManager.INSTANCE.getStructure(
                    structNode.getName().getTokenContent()
            );
            Objects.requireNonNull(foundStructure, "foundStructure must not be null.");
        
            foundStructure.generateIR(this, structNode);
        }
    
        return structRef;
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
