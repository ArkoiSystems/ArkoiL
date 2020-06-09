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
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.llvm.LLVM.LLVMMemoryBufferRef;
import org.bytedeco.llvm.LLVM.LLVMTargetRef;
import org.bytedeco.llvm.LLVM.lto_code_gen_t;
import org.bytedeco.llvm.LLVM.lto_module_t;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// https://github.com/intel/cm-compiler/blob/master/llvm/tools/llvm-lto/llvm-lto.cpp
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
        final BytePointer errorPointer = new BytePointer();
        
        LLVM.LLVMInitializeAllTargetInfos();
        LLVM.LLVMInitializeAllTargets();
        LLVM.LLVMInitializeAllTargetMCs();
        LLVM.LLVMInitializeAllAsmPrinters();
        LLVM.LLVMInitializeAllAsmParsers();
        
        final BytePointer targetTriple = LLVM.LLVMGetDefaultTargetTriple();
        final LLVMTargetRef targetRef = new LLVMTargetRef();
        LLVM.LLVMGetTargetFromTriple(targetTriple, targetRef, errorPointer);
        if (!Pointer.isNull(errorPointer))
            throw new NullPointerException(errorPointer.getString());
        
        final lto_code_gen_t codeGen = LLVM.lto_codegen_create();
        LLVM.lto_codegen_set_pic_model(codeGen, LLVM.LTO_CODEGEN_PIC_MODEL_DEFAULT);
        LLVM.lto_codegen_set_debug_model(codeGen, LLVM.LTO_DEBUG_MODEL_DWARF);
        
        final List<lto_module_t> ltoModules = new ArrayList<>();
        for (final Module module : this.getModules()) {
            final LLVMMemoryBufferRef memoryBuffer = LLVM.LLVMWriteBitcodeToMemoryBuffer(module.getModuleRef());
            final BytePointer start = LLVM.LLVMGetBufferStart(memoryBuffer);
            final long size = LLVM.LLVMGetBufferSize(memoryBuffer);
            if (!LLVM.lto_module_is_object_file_in_memory(start, size))
                return false;
            
            final lto_module_t ltoModule = LLVM.lto_module_create_from_memory(start, size);
            LLVM.lto_module_set_target_triple(ltoModule, targetTriple);
            LLVM.LLVMDisposeMemoryBuffer(memoryBuffer);
            
            if (LLVM.lto_codegen_add_module(codeGen, ltoModule))
                return false;
            
            ltoModules.add(ltoModule);
        }
        
        LLVM.lto_codegen_set_cpu(codeGen, LLVM.LLVMGetHostCPUName());
        LLVM.lto_codegen_add_must_preserve_symbol(codeGen, "main");
        
        final File file = new File(String.format(
                "%s/output.o",
                this.getCompiler().getOutputPath()
        ));
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        
        final SizeTPointer sizeT = new SizeTPointer(0);
        final BytePointer bytePointer = new BytePointer(LLVM.lto_codegen_compile(codeGen, sizeT));
        
        final int size = (int) sizeT.get();
        final byte[] bytes = new byte[size];
        for (int index = 0; index < size; index++)
            bytes[index] = bytePointer.get(index);
        
        Files.write(file.toPath(), bytes);
        
        for (final lto_module_t ltoModule : ltoModules)
            LLVM.lto_module_dispose(ltoModule);
        LLVM.lto_codegen_dispose(codeGen);
        return true;
    }
    
}
