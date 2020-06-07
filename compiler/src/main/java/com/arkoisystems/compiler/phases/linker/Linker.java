/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on June 07, 2020
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
package com.arkoisystems.compiler.phases.linker;

import com.arkoisystems.compiler.Compiler;
import com.arkoisystems.compiler.api.IStage;
import com.arkoisystems.llvm.Module;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef;
import org.bytedeco.llvm.LLVM.lto_code_gen_t;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Linker implements IStage
{
    
    @NotNull
    private final List<Module> modules;
    
    @NotNull
    private final Compiler compiler;
    
    @Setter
    private boolean failed;
    
    public Linker(@NotNull final Compiler compiler, @NotNull final List<Module> modules) {
        this.compiler = compiler;
        this.modules = modules;
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        final List<LinkerModule> linkerModules = new ArrayList<>();
        
        for (final Module module : this.getModules()) {
            final LLVMMemoryBufferRef memoryBuffer = LLVM.LLVMWriteBitcodeToMemoryBuffer(module.getModuleRef());
            final BytePointer start = LLVM.LLVMGetBufferStart(memoryBuffer);
            final long size = LLVM.LLVMGetBufferSize(memoryBuffer);
            
            if (!LLVM.lto_module_is_object_file_in_memory(start, size))
                return false;
            
            linkerModules.add(new LinkerModule(memoryBuffer, LLVM.lto_module_create_from_memory(start, size)));
        }
        
        final lto_code_gen_t codeGen = LLVM.lto_codegen_create();                // <
        for (final LinkerModule linkerModule : linkerModules)                    // < These lines let the VM crash.
            LLVM.lto_codegen_add_module(codeGen, linkerModule.getLtoModule());   // <
        
        for (final LinkerModule linkerModule : linkerModules) {
            LLVM.lto_module_dispose(linkerModule.getLtoModule());
            LLVM.LLVMDisposeMemoryBuffer(linkerModule.getMemoryBuffer());
        }
        
        LLVM.lto_codegen_dispose(codeGen);
        return true;
    }
    
}
