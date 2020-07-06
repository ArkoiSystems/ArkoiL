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
import org.bytedeco.llvm.LLVM.LLVMBasicBlockRef;
import org.bytedeco.llvm.LLVM.LLVMContextRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

@Getter
public class ContextGen
{
    
    @NotNull
    private final LLVMContextRef contextRef;
    
    private final boolean usingGlobal;
    
    @Builder
    private ContextGen(final boolean usingGlobal) {
        this.usingGlobal = usingGlobal;
    
        this.contextRef = this.isUsingGlobal()
                ? LLVM.LLVMGetGlobalContext()
                : LLVM.LLVMContextCreate();
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (!this.isUsingGlobal())
            LLVM.LLVMContextDispose(this.getContextRef());
        super.finalize();
    }
    
    @NotNull
    public LLVMTypeRef makeFloatType() {
        return LLVM.LLVMFloatTypeInContext(this.getContextRef());
    }
    
    @NotNull
    public LLVMTypeRef makeIntType(final int bits) {
        return LLVM.LLVMIntTypeInContext(this.getContextRef(), bits);
    }
    
    @NotNull
    public LLVMTypeRef makeDoubleType() {
        return LLVM.LLVMDoubleTypeInContext(this.getContextRef());
    }
    
    @NotNull
    public LLVMTypeRef makeVoidType() {
        return LLVM.LLVMVoidTypeInContext(this.getContextRef());
    }
    
    @NotNull
    public LLVMTypeRef buildStruct() {
        return LLVM.LLVMStructCreateNamed(this.getContextRef(), "");
    }
    
    @NotNull
    public LLVMBasicBlockRef appendBasicBlock(@NotNull final LLVMValueRef functionRef) {
        return LLVM.LLVMAppendBasicBlockInContext(
                this.getContextRef(),
                functionRef,
                ""
        );
    }
    
}
