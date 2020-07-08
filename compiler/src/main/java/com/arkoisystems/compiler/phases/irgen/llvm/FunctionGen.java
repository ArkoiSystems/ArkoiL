/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on June 11, 2020
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
package com.arkoisystems.compiler.phases.irgen.llvm;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class FunctionGen
{
    
    @NotNull
    private final LLVMValueRef functionRef;
    
    @Builder
    private FunctionGen(
            @NonNull
            @NotNull final ModuleGen moduleGen,
            @NonNull
            @NotNull final String name,
            @NonNull
            @NotNull final LLVMTypeRef returnType,
            @Nullable final ParameterGen[] parameters,
            final boolean variadic
    ) {
        final int parameterLength = parameters == null ? 0 : parameters.length;
        final LLVMTypeRef[] parameterTypes = new LLVMTypeRef[parameterLength];
        for (int index = 0; index < parameterTypes.length; index++)
            parameterTypes[index] = Objects.requireNonNull(parameters[index]).getTypeRef();
        
        this.functionRef = LLVM.LLVMAddFunction(moduleGen.getModuleRef(), name, LLVM.LLVMFunctionType(
                returnType,
                new PointerPointer<>(parameterTypes),
                parameterLength,
                variadic ? 1 : 0
        ));
        
        for (int index = 0; index < parameterTypes.length; index++) {
            final LLVMValueRef parameter = LLVM.LLVMGetParam(this.getFunctionRef(), index);
            LLVM.LLVMSetValueName(parameter, Objects.requireNonNull(parameters[index]).getName());
        }
    }
    
    @NotNull
    public LLVMValueRef getParameter(final int index) {
        return LLVM.LLVMGetParam(this.getFunctionRef(), index);
    }
    
    @NotNull
    public LLVMBasicBlockRef getLastBasicBlock() {
        return LLVM.LLVMGetLastBasicBlock(this.getFunctionRef());
    }
    
}
