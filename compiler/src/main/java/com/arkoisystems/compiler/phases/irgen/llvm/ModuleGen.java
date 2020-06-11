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
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class ModuleGen
{
    
    @NotNull
    private final LLVMModuleRef moduleRef;
    
    @Builder
    private ModuleGen(
            @Nullable final ContextGen contextGen,
            @NonNull
            @NotNull final String name
    ) {
        this.moduleRef = contextGen == null ?
                LLVM.LLVMModuleCreateWithName(name) :
                LLVM.LLVMModuleCreateWithNameInContext(name, contextGen.getContextRef());
    }
    
    @NotNull
    public String verify() {
        final BytePointer errorPointer = new BytePointer();
        LLVM.LLVMVerifyModule(this.getModuleRef(), LLVM.LLVMReturnStatusAction, errorPointer);
        final String message = errorPointer.getString();
        LLVM.LLVMDisposeMessage(errorPointer);
        return message;
    }
    
    public void dump() {
        LLVM.LLVMDumpModule(this.getModuleRef());
    }
    
    @Override
    protected void finalize() throws Throwable {
        LLVM.LLVMDisposeModule(this.getModuleRef());
        super.finalize();
    }
    
}
