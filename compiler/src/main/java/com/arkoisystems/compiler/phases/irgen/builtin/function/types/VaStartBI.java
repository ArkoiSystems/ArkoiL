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
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VaStartBI extends BIFunction
{
    
    @Override
    public void generateIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionNode functionNode
    ) {
        Objects.requireNonNull(functionNode.getParser());
    
        final List<StructNode> structNodes = Objects.requireNonNullElse(functionNode.getParser()
                .getCompilerClass()
                .getRootScope()
                .lookup("va_list"), new ArrayList<ParserNode>()).stream()
                .filter(node -> node instanceof StructNode)
                .map(node -> (StructNode) node)
                .collect(Collectors.toList());
        if (structNodes.size() != 1)
            throw new NullPointerException();
    
        final FunctionGen functionGen = irVisitor.visit(functionNode);
    
        irVisitor.getBuilderGen().setPositionAtEnd(irVisitor.getContextGen().appendBasicBlock(functionGen.getFunctionRef()));
    
        final LLVMValueRef functionRef = this.getLLVMVaStart(irVisitor, functionNode);
        final LLVMTypeRef listStructure = irVisitor.visit(structNodes.get(0));
        final LLVMValueRef va_list = irVisitor.getBuilderGen().buildAlloca(listStructure);
    
        irVisitor.getBuilderGen().buildFunctionCall(functionRef, irVisitor.getBuilderGen().buildBitCast(
                va_list,
                LLVM.LLVMPointerType(irVisitor.getContextGen().makeIntType(8), 0)
        ));
        irVisitor.getBuilderGen().returnValue(va_list);
    }
    
    @Override
    public @NotNull String identifier() {
        return "va_start";
    }
    
    @NotNull
    private LLVMValueRef getLLVMVaStart(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionNode functionNode
    ) {
        Objects.requireNonNull(functionNode.getParser());
        
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
