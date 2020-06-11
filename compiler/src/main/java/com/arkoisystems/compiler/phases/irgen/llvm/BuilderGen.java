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
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class BuilderGen
{
    
    @NotNull
    private final LLVMBuilderRef builderRef;
    
    @Builder
    private BuilderGen(@Nullable final ContextGen contextGen) {
        this.builderRef = contextGen == null ?
                LLVM.LLVMCreateBuilder() :
                LLVM.LLVMCreateBuilderInContext(contextGen.getContextRef());
    }
    
    public void setPositionAtEnd(@NotNull final LLVMBasicBlockRef basicBlockRef) {
        LLVM.LLVMPositionBuilderAtEnd(this.getBuilderRef(), basicBlockRef);
    }
    
    public void returnVoid() {
        LLVM.LLVMBuildRetVoid(this.getBuilderRef());
    }
    
    public void returnValue(@NotNull final LLVMValueRef valueRef) {
        LLVM.LLVMBuildRet(this.getBuilderRef(), valueRef);
    }
    
    @NotNull
    public LLVMValueRef buildInboundsGEP(
            @NotNull final LLVMValueRef targetValue,
            @NotNull final LLVMValueRef... indices
    ) {
        return LLVM.LLVMBuildInBoundsGEP(
                this.getBuilderRef(),
                targetValue,
                new PointerPointer<>(indices),
                indices.length,
                ""
        );
    }
    
    @NotNull
    public LLVMValueRef buildBitCast(
            @NotNull final LLVMValueRef value,
            @NotNull final LLVMTypeRef toType
    ) {
        return LLVM.LLVMBuildBitCast(this.getBuilderRef(), value, toType, "");
    }
    
    @NotNull
    public LLVMValueRef buildFunctionCall(
            @NotNull final LLVMValueRef functionRef,
            @NotNull final LLVMValueRef... arguments
    ) {
        return LLVM.LLVMBuildCall(
                this.getBuilderRef(),
                functionRef,
                new PointerPointer<>(arguments),
                arguments.length,
                ""
        );
    }
    
    @NotNull
    public LLVMValueRef buildGlobalStringPtr(@NotNull final String content) {
        return LLVM.LLVMBuildGlobalStringPtr(this.getBuilderRef(), content, "");
    }
    
    @NotNull
    public LLVMValueRef buildAlloca(@NotNull final LLVMTypeRef typeRef) {
        return LLVM.LLVMBuildAlloca(
                this.getBuilderRef(),
                typeRef,
                ""
        );
    }
    
    @NotNull
    public LLVMValueRef buildLoad(@NonNull final LLVMValueRef valueRef) {
        return LLVM.LLVMBuildLoad(this.getBuilderRef(), valueRef, "");
    }
    
}
