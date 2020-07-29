/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on June 14, 2020
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
package com.arkoisystems.compiler.phases.irgen.builtin.function.types;

import com.arkoisystems.compiler.phases.irgen.IRVisitor;
import com.arkoisystems.compiler.phases.irgen.builtin.function.BIFunction;
import com.arkoisystems.compiler.phases.irgen.llvm.FunctionGen;
import com.arkoisystems.compiler.phases.irgen.llvm.ParameterGen;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VaStartBI extends BIFunction
{
    
    @Override
    public void generateFunctionIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionNode functionNode
    ) { }
    
    @Override
    public boolean generatesFunctionIR() {
        return false;
    }
    
    @Nullable
    @Override
    public LLVMValueRef generateFunctionCallIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionCallNode functionCallNode
    ) {
        Objects.requireNonNull(functionCallNode.getParser());
        
        final List<StructNode> structNodes = functionCallNode.getParser()
                .getCompilerClass()
                .getRootScope()
                .lookup("va_list").stream()
                .filter(node -> node instanceof StructNode)
                .map(node -> (StructNode) node)
                .collect(Collectors.toList());
        if (structNodes.size() != 1)
            throw new NullPointerException();
        
        final StructNode targetStruct = structNodes.get(0);
        
        final LLVMValueRef startIntrinsicRef = this.getLLVMVaStart(irVisitor);
        final LLVMValueRef listRef = irVisitor.getBuilderGen().buildAlloca(
                targetStruct,
                irVisitor.visit(targetStruct)
        );
        irVisitor.getBuilderGen().buildFunctionCall(startIntrinsicRef, irVisitor.getBuilderGen().buildBitCast(
                listRef,
                LLVM.LLVMPointerType(irVisitor.getContextGen().makeIntType(8), 0)
        ));
        
        return irVisitor.getBuilderGen().buildLoad(targetStruct, listRef);
    }
    
    @Override
    public boolean generatesFunctionCallIR() {
        return true;
    }
    
    @Override
    public @NotNull String identifier() {
        return "va_start";
    }
    
    @NotNull
    private LLVMValueRef getLLVMVaStart(@NotNull final IRVisitor irVisitor) {
        final LLVMValueRef functionRef = LLVM.LLVMGetNamedFunction(
                irVisitor.getModuleGen().getModuleRef(),
                "llvm.va_start"
        );
        if (functionRef != null)
            return functionRef;
        
        return FunctionGen.builder()
                .moduleGen(irVisitor.getModuleGen())
                .parameters(new ParameterGen[] {
                        ParameterGen.builder()
                                .typeRef(LLVM.LLVMPointerType(irVisitor.getContextGen().makeIntType(8), 0))
                                .name("")
                                .build()
                })
                .name("llvm.va_start")
                .returnType(irVisitor.getContextGen().makeVoidType())
                .build()
                .getFunctionRef();
    }
    
}
