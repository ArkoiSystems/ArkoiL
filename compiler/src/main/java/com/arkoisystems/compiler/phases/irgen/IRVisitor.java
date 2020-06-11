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
import com.arkoisystems.compiler.phases.irgen.llvm.BuilderGen;
import com.arkoisystems.compiler.phases.irgen.llvm.FunctionGen;
import com.arkoisystems.compiler.phases.irgen.llvm.ModuleGen;
import com.arkoisystems.compiler.phases.irgen.llvm.ParameterGen;
import com.arkoisystems.compiler.phases.parser.ast.DataKind;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.BlockNode;
import com.arkoisystems.compiler.phases.parser.ast.types.RootNode;
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
import lombok.Setter;
import lombok.SneakyThrows;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class IRVisitor implements IVisitor<Object>
{
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @Setter
    @NotNull
    private BuilderGen builderGen;
    
    @Setter
    @NotNull
    private ModuleGen moduleGen;
    
    @Setter
    private boolean failed;
    
    public IRVisitor(@NotNull final CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @Override
    public LLVMTypeRef visit(@NotNull final TypeNode typeNode) {
        LLVMTypeRef typeRef;
        switch (typeNode.getDataKind()) {
            case FLOAT:
                typeRef = LLVM.LLVMFloatType();
                break;
            case BOOLEAN:
                typeRef = LLVM.LLVMInt1Type();
                break;
            case BYTE:
            case CHAR:
                typeRef = LLVM.LLVMInt8Type();
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
            default:
                throw new NullPointerException("Unhandled type: " + typeNode.getDataKind().name());
        }
        
        for (int index = 0; index < typeNode.getPointers(); index++)
            typeRef = LLVM.LLVMPointerType(typeRef, 0);
        return typeRef;
    }
    
    @SneakyThrows
    @Override
    public ModuleGen visit(@NotNull final RootNode rootNode) {
        Objects.requireNonNull(rootNode.getCurrentScope(), "rootAST.currentScope must not be null.");
        Objects.requireNonNull(rootNode.getParser(), "rootAST.parser must not be null.");
        
        this.setBuilderGen(BuilderGen.builder()
                .build());
        this.setModuleGen(ModuleGen.builder()
                .name(rootNode.getParser().getCompilerClass().getName())
                .build());
        
        rootNode.getCurrentScope().getSymbolTable().values().stream()
                .flatMap(Collection::stream)
                .filter(node -> node instanceof FunctionNode)
                .map(node -> (FunctionNode) node)
                .forEach(node -> this.createFunction(node, !rootNode.getNodes().contains(node)));
        rootNode.getNodes().forEach(this::visit);
        
        final String error = moduleGen.verify();
        if (!error.isEmpty()) {
            final File file = new File(String.format(
                    "%s/error/%s.ll",
                    this.getCompilerClass().getCompiler().getOutputPath(),
                    LLVM.LLVMGetModuleIdentifier(moduleGen.getModuleRef(), new SizeTPointer(0)).getString()
            ));
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            
            Files.write(file.toPath(), LLVM.LLVMPrintModuleToString(
                    moduleGen.getModuleRef()).getStringBytes()
            );
            
            this.getCompilerClass().getCompiler().getErrorHandler().getCompilerErrors().addAll(
                    this.getLLVMErrors(error, file.getCanonicalPath())
            );
            this.setFailed(true);
        }
        return moduleGen;
    }
    
    @Override
    public ParameterListNode visit(@NotNull final ParameterListNode parameterListNode) {
        for (final ParameterNode parameterNode : parameterListNode.getParameters())
            this.visit(parameterNode);
        return parameterListNode;
    }
    
    @Override
    public ParameterNode visit(@NotNull final ParameterNode parameter) {
        return parameter;
    }
    
    @Override
    public BlockNode visit(@NotNull final BlockNode blockNode) {
        for (final ParserNode node : blockNode.getNodes())
            this.visit(node);
        return blockNode;
    }
    
    public void createFunction(
            @NotNull final FunctionNode functionNode,
            final boolean foreign
    ) {
        Objects.requireNonNull(functionNode.getParameters(), "functionNode.parameters must not be null.");
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
    
        FunctionGen.builder()
                .moduleGen(this.getModuleGen())
                .name(functionNode.getName().getTokenContent())
                .parameters(functionNode.getParameters().getParameters().stream()
                        .filter(parameter -> parameter.getTypeNode().getDataKind() != DataKind.VARIADIC)
                        .map(parameter -> ParameterGen.builder()
                                .name(Objects.requireNonNull(parameter.getName(), "parameter.name must not be null.").getTokenContent())
                                .typeRef(this.visit(Objects.requireNonNull(parameter.getTypeNode(), "parameter.typeNode must not be null.")))
                                .build())
                        .toArray(ParameterGen[]::new))
                .variadic(functionNode.getParameters().isVariadic())
                .returnType(this.visit(functionNode.getTypeNode()))
                .foreignFunction(foreign || functionNode.getBlockNode() == null)
                .build();
    }
    
    @Override
    public FunctionNode visit(@NotNull final FunctionNode functionNode) {
        Objects.requireNonNull(functionNode.getName(), "functionNode.name must not be null.");
        
        final LLVMValueRef functionValue = LLVM.LLVMGetNamedFunction(
                this.getModuleGen().getModuleRef(),
                functionNode.getName().getTokenContent()
        );
        if (functionValue == null) {
            this.setFailed(true);
            return null;
        }
        
        if (functionNode.getBlockNode() != null) {
            final LLVMBasicBlockRef basicBlock = LLVM.LLVMGetFirstBasicBlock(functionValue);
            if (basicBlock == null) {
                this.setFailed(true);
                return null;
            }
    
            this.getBuilderGen().setPositionAtEnd(basicBlock);
            this.visit(functionNode.getBlockNode());
        }
        return functionNode;
    }
    
    @Override
    public ImportNode visit(@NotNull final ImportNode importNode) {
        return importNode;
    }
    
    @Override
    public ReturnNode visit(@NotNull final ReturnNode returnNode) {
        if (returnNode.getExpression() != null) {
            final Object object = this.visit(returnNode.getExpression());
            if (!(object instanceof LLVMValueRef)) {
                this.setFailed(true);
                return null;
            }
        
            this.getBuilderGen().returnValue(((LLVMValueRef) object));
        } else this.getBuilderGen().returnVoid();
        return returnNode;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final VariableNode variableNode) {
        Objects.requireNonNull(variableNode.getName(), "variableNode.name must not be null.");
    
        if (variableNode.isLocal()) {
            final LLVMValueRef variableRef;
            if (variableNode.getReturnType() != null) {
                variableRef = LLVM.LLVMBuildAlloca(
                        this.getBuilderGen().getBuilderRef(),
                        this.visit(variableNode.getReturnType()),
                        ""
                );
            } else {
                variableRef = LLVM.LLVMBuildAlloca(
                        this.getBuilderGen().getBuilderRef(),
                        this.visit(variableNode.getTypeNode()),
                        ""
                );
            }
        
            variableNode.setVariableRef(variableRef);
        
            if (variableNode.getExpression() != null) {
                final Object object = this.visit(variableNode.getExpression());
                if (!(object instanceof LLVMValueRef))
                    throw new NullPointerException();
            
                LLVM.LLVMBuildStore(this.getBuilderGen().getBuilderRef(), (LLVMValueRef) object, variableRef);
            }
        
            return variableRef;
        } else {
            final LLVMValueRef variableRef;
            if (variableNode.getReturnType() != null) {
                variableRef = LLVM.LLVMAddGlobal(
                        this.getModuleGen().getModuleRef(),
                        this.visit(variableNode.getReturnType()),
                        ""
                );
            } else {
                variableRef = LLVM.LLVMAddGlobal(
                        this.getModuleGen().getModuleRef(),
                        this.visit(variableNode.getTypeNode()),
                        ""
                );
            }
        
            variableNode.setVariableRef(variableRef);
            return variableRef;
        }
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final StringNode stringNode) {
        Objects.requireNonNull(stringNode.getStringToken(), "stringNode.stringToken must not be null.");
        return LLVM.LLVMBuildGlobalStringPtr(
                this.getBuilderGen().getBuilderRef(),
                stringNode.getStringToken().getTokenContent(),
                ""
        );
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final NumberNode numberNode) {
        Objects.requireNonNull(numberNode.getNumberToken(), "numberNode.numberToken must not be null.");
        
        if (numberNode.getTypeNode().getDataKind() == DataKind.INTEGER) {
            // TODO: 6/4/20 Signed integers
            final int value = Integer.parseInt(numberNode.getNumberToken().getTokenContent());
            return LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), value, 1);
        }
        
        return null;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final IdentifierNode identifierNode) {
        Objects.requireNonNull(identifierNode.getIdentifier(), "identifierNode.identifier must not be null.");
        
        if (identifierNode.isFunctionCall()) {
            Objects.requireNonNull(identifierNode.getExpressions(), "identifierNode.expression must not be null.");
            
            final LLVMValueRef functionValue = LLVM.LLVMGetNamedFunction(
                    this.getModuleGen().getModuleRef(),
                    identifierNode.getIdentifier().getTokenContent()
            );
            if (functionValue == null) {
                this.setFailed(true);
                return null;
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
                    this.getBuilderGen().getBuilderRef(),
                    functionValue,
                    new PointerPointer<>(arguments.toArray(LLVMValueRef[]::new)),
                    arguments.size(),
                    ""
            );
        } else {
            Objects.requireNonNull(identifierNode.getCurrentScope(), "identifierNode.currentScope must not be null.");
            
            final List<ParserNode> nodes = identifierNode.getCurrentScope().lookup(
                    identifierNode.getIdentifier().getTokenContent()
            );
            if (nodes == null || nodes.size() != 1) {
                this.setFailed(true);
                return null;
            }
            
            final ParserNode targetNode = nodes.get(0);
            if (targetNode instanceof ParameterNode) {
                final FunctionNode targetFunction = targetNode.getParent(FunctionNode.class);
                if (targetFunction == null) {
                    this.setFailed(true);
                    return null;
                }
        
                final ParameterNode targetParameter = (ParameterNode) targetNode;
        
                Objects.requireNonNull(targetFunction.getName(), "targetFunction.name must not be null.");
                Objects.requireNonNull(targetParameter.getName(), "targetParameter.name must not be null.");
        
                final LLVMValueRef functionValue = LLVM.LLVMGetNamedFunction(
                        this.getModuleGen().getModuleRef(),
                        targetFunction.getName().getTokenContent()
                );
                if (functionValue == null) {
                    this.setFailed(true);
                    return null;
                }
        
                Objects.requireNonNull(targetFunction.getParameters(), "targetFunction.parameters must not be null.");
                int index = 0;
                for (; index < targetFunction.getParameters().getParameters().size(); index++) {
                    final ParameterNode parameterNode = targetFunction.getParameters().getParameters().get(index);
                    Objects.requireNonNull(parameterNode.getName(), "parameterNode.name must not be null.");
                    if (parameterNode.getName().getTokenContent().equals(targetParameter.getName().getTokenContent()))
                        break;
                }
        
                // TODO: 6/11/20 VARIADIC doesnt work correctly
                if (targetParameter.getTypeNode().getDataKind() == DataKind.VARIADIC) {
                    final LLVMTypeRef struct = LLVM.LLVMStructCreateNamed(LLVM.LLVMGetGlobalContext(), "struct.va_list");
                    LLVM.LLVMStructSetBody(struct, new PointerPointer<>(
                            LLVM.LLVMInt32Type(),
                            LLVM.LLVMInt32Type(),
                            LLVM.LLVMPointerType(LLVM.LLVMInt8Type(), 0),
                            LLVM.LLVMPointerType(LLVM.LLVMInt8Type(), 0)
                    ), 4, 0);
            
                    final LLVMValueRef va_list = LLVM.LLVMBuildAlloca(this.getBuilderGen().getBuilderRef(), LLVM.LLVMArrayType(struct, 1), "");
            
                    final FunctionGen va_start = FunctionGen.builder()
                            .parameters(new ParameterGen[] {
                                    ParameterGen.builder()
                                            .typeRef(LLVM.LLVMPointerType(LLVM.LLVMInt8Type(), 0))
                                            .name("")
                                            .build()
                            })
                            .foreignFunction(true)
                            .moduleGen(this.getModuleGen())
                            .name("llvm.va_start")
                            .returnType(LLVM.LLVMVoidType())
                            .build();
            
                    final LLVMValueRef structGEP1 = LLVM.LLVMBuildInBoundsGEP(this.getBuilderGen().getBuilderRef(), va_list, new PointerPointer<>(
                            LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 0, 1),
                            LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 0, 1)
                    ), 2, "");
            
                    final LLVMValueRef gepBitcast = LLVM.LLVMBuildBitCast(
                            this.getBuilderGen().getBuilderRef(),
                            structGEP1,
                            LLVM.LLVMPointerType(LLVM.LLVMInt8Type(), 0)
                            ,
                            ""
                    );
            
                    LLVM.LLVMBuildCall(this.getBuilderGen().getBuilderRef(), va_start.getFunctionRef(), new PointerPointer<>(new LLVMValueRef[] {
                            gepBitcast
                    }), 1, "");
            
                    return LLVM.LLVMBuildInBoundsGEP(this.getBuilderGen().getBuilderRef(), va_list, new PointerPointer<>(
                            LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 0, 1),
                            LLVM.LLVMConstInt(LLVM.LLVMInt32Type(), 0, 1)
                    ), 2, "");
                }
        
                return LLVM.LLVMGetParam(functionValue, index);
            } else if (targetNode instanceof VariableNode) {
                final VariableNode variableNode = (VariableNode) targetNode;
                final LLVMValueRef valueRef = variableNode.getVariableRef();
                if (valueRef == null)
                    throw new NullPointerException();
        
                return LLVM.LLVMBuildLoad(this.getBuilderGen().getBuilderRef(), valueRef, "");
            }
        }
        
        return null;
    }
    
    @Override
    public ExpressionListNode visit(@NotNull final ExpressionListNode expressionListNode) {
        for (final OperableNode operableNode : expressionListNode.getExpressions())
            this.visit(operableNode);
        return expressionListNode;
    }
    
    @Override
    public AssignmentNode visit(@NotNull final AssignmentNode assignmentNode) {
        Objects.requireNonNull(assignmentNode.getLeftHandSide(), "assignmentNode.leftHandSide must not be null.");
        Objects.requireNonNull(assignmentNode.getRightHandSide(), "assignmentNode.rightHandSide must not be null.");
        
        this.visit(assignmentNode.getLeftHandSide());
        this.visit(assignmentNode.getRightHandSide());
        return assignmentNode;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final BinaryNode binaryNode) {
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
                return LLVM.LLVMBuildAdd(this.getBuilderGen().getBuilderRef(), lhsValue, rhsValue, "");
            case MUL:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildMul(this.getBuilderGen().getBuilderRef(), lhsValue, rhsValue, "");
            case DIV:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildUDiv(this.getBuilderGen().getBuilderRef(), lhsValue, rhsValue, "");
            case SUB:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildSub(this.getBuilderGen().getBuilderRef(), lhsValue, rhsValue, "");
            case EXP:
                throw new NullPointerException("6");
            case MOD:
                // TODO: 6/4/20 Check if its floating point/signed or not.
                return LLVM.LLVMBuildURem(this.getBuilderGen().getBuilderRef(), lhsValue, rhsValue, "");
        }
        
        return null;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final ParenthesizedNode parenthesizedNode) {
        Objects.requireNonNull(parenthesizedNode.getExpression(), "parenthesizedNode.expression must not be null.");
        final Object object = this.visit(parenthesizedNode.getExpression());
        if (!(object instanceof LLVMValueRef)) {
            // TODO: 6/4/20 Throw error
            return null;
        }
        return (LLVMValueRef) object;
    }
    
    @Override
    public PostfixNode visit(@NotNull final PostfixNode postfixNode) {
        Objects.requireNonNull(postfixNode.getLeftHandSide(), "postfixNode.leftHandSide must not be null.");
        this.visit(postfixNode.getLeftHandSide());
        return postfixNode;
    }
    
    @Override
    public LLVMValueRef visit(@NotNull final PrefixNode prefixNode) {
        Objects.requireNonNull(prefixNode.getRightHandSide(), "prefixNode.rightHandSide must not be null.");
        
        final Object rhsObject = this.visit(prefixNode.getRightHandSide());
        if (!(rhsObject instanceof LLVMValueRef)) {
            // TODO: 6/4/20 Throw error
            return null;
        }
        
        final LLVMValueRef rhsValue = (LLVMValueRef) rhsObject;
        if (prefixNode.getOperatorType() == PrefixOperators.NEGATE)
            return LLVM.LLVMBuildNeg(this.getBuilderGen().getBuilderRef(), rhsValue, "");
        return null;
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
            } else if (index > causeIndex && (currentChar == '\n' || currentChar == '\r')) {
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
