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
import lombok.Setter;
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
    
    @Setter
    @Nullable
    private LLVMBasicBlockRef currentBlock;
    
    @Builder
    private BuilderGen(@Nullable final ContextGen contextGen) {
        this.builderRef = contextGen == null ?
                LLVM.LLVMCreateBuilder() :
                LLVM.LLVMCreateBuilderInContext(contextGen.getContextRef());
    }
    
    public void setPositionAtEnd(@NotNull final LLVMBasicBlockRef basicBlockRef) {
        LLVM.LLVMPositionBuilderAtEnd(this.getBuilderRef(), basicBlockRef);
        this.setCurrentBlock(basicBlockRef);
    }
    
    public void returnVoid() {
        LLVM.LLVMBuildRetVoid(this.getBuilderRef());
    }
    
    public void returnValue(@NotNull final LLVMValueRef valueRef) {
        LLVM.LLVMBuildRet(this.getBuilderRef(), valueRef);
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
    
    @NotNull
    public LLVMValueRef buildNeg(@NonNull final LLVMValueRef rhsValue) {
        return LLVM.LLVMBuildNeg(this.getBuilderRef(), rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildAdd(
            final boolean floatingPoint,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFAdd(this.getBuilderRef(), lhsValue, rhsValue, "");
        return LLVM.LLVMBuildAdd(this.getBuilderRef(), lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildMul(
            final boolean floatingPoint,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFMul(this.getBuilderRef(), lhsValue, rhsValue, "");
        return LLVM.LLVMBuildMul(this.getBuilderRef(), lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildDiv(
            final boolean floatingPoint,
            final boolean signed,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFDiv(this.getBuilderRef(), lhsValue, rhsValue, "");
        if (signed)
            return LLVM.LLVMBuildSDiv(this.getBuilderRef(), lhsValue, rhsValue, "");
        return LLVM.LLVMBuildUDiv(this.getBuilderRef(), lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildSub(
            final boolean floatingPoint,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFSub(this.getBuilderRef(), lhsValue, rhsValue, "");
        return LLVM.LLVMBuildSub(this.getBuilderRef(), lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildRem(
            final boolean signed,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (signed)
            return LLVM.LLVMBuildSRem(this.getBuilderRef(), lhsValue, rhsValue, "");
        return LLVM.LLVMBuildURem(this.getBuilderRef(), lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildLT(
            final boolean floatingPoint,
            final boolean signed,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFCmp(this.getBuilderRef(), LLVM.LLVMRealOLT, lhsValue, rhsValue, "");
        if (signed)
            return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntSLT, lhsValue, rhsValue, "");
        return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntULT, lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildGT(
            final boolean floatingPoint,
            final boolean signed,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFCmp(this.getBuilderRef(), LLVM.LLVMRealOGT, lhsValue, rhsValue, "");
        if (signed)
            return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntSGT, lhsValue, rhsValue, "");
        return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntUGT, lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildLE(
            final boolean floatingPoint,
            final boolean signed,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFCmp(this.getBuilderRef(), LLVM.LLVMRealOLE, lhsValue, rhsValue, "");
        if (signed)
            return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntSLE, lhsValue, rhsValue, "");
        return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntULE, lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildGE(
            final boolean floatingPoint,
            final boolean signed,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFCmp(this.getBuilderRef(), LLVM.LLVMRealOGE, lhsValue, rhsValue, "");
        if (signed)
            return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntSGE, lhsValue, rhsValue, "");
        return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntUGE, lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildEQ(
            final boolean floatingPoint,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFCmp(this.getBuilderRef(), LLVM.LLVMRealOEQ, lhsValue, rhsValue, "");
        return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntEQ, lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildNE(
            final boolean floatingPoint,
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMValueRef rhsValue
    ) {
        if (floatingPoint)
            return LLVM.LLVMBuildFCmp(this.getBuilderRef(), LLVM.LLVMRealONE, lhsValue, rhsValue, "");
        return LLVM.LLVMBuildICmp(this.getBuilderRef(), LLVM.LLVMIntNE, lhsValue, rhsValue, "");
    }
    
    @NotNull
    public LLVMValueRef buildIntToFP(
            @NotNull final LLVMValueRef lhsValue,
            @NotNull final LLVMTypeRef targetType,
            final boolean signed
    ) {
        if (signed)
            return LLVM.LLVMBuildSIToFP(this.getBuilderRef(), lhsValue, targetType, "");
        return LLVM.LLVMBuildUIToFP(this.getBuilderRef(), lhsValue, targetType, "");
    }
    
}
