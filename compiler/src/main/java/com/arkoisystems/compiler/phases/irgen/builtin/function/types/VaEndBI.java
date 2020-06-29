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
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VaEndBI extends BIFunction
{
    
    @Override
    public void generateIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionNode functionNode
    ) {
        Objects.requireNonNull(functionNode.getParser());
    
        final FunctionGen functionGen = irVisitor.visit(functionNode);
        final LLVMValueRef functionRef = this.getLLVMVaEnd(irVisitor, functionNode);
    
        irVisitor.getBuilderGen().setPositionAtEnd(irVisitor.getContextGen().appendBasicBlock(functionGen));
    
        irVisitor.getBuilderGen().buildFunctionCall(functionRef, irVisitor.getBuilderGen().buildBitCast(
                functionGen.getParameter(0),
                LLVM.LLVMPointerType(irVisitor.getContextGen().makeIntType(8), 0)
        ));
        irVisitor.getBuilderGen().returnVoid();
    }
    
    @Override
    public @NotNull String identifier() {
        return "va_end";
    }
    
    @NotNull
    private LLVMValueRef getLLVMVaEnd(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionNode functionNode
    ) {
        Objects.requireNonNull(functionNode.getParser());
        
        final LLVMValueRef functionRef = LLVM.LLVMGetNamedFunction(
                irVisitor.getModuleGen().getModuleRef(),
                "llvm.va_end"
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
                .name("llvm.va_end")
                .returnType(irVisitor.getContextGen().makeVoidType())
                .build()
                .getFunctionRef();
    }
    
}
