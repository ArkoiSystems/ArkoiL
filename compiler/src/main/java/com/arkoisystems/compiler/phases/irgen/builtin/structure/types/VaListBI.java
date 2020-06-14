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
package com.arkoisystems.compiler.phases.irgen.builtin.structure.types;

import com.arkoisystems.compiler.phases.irgen.IRVisitor;
import com.arkoisystems.compiler.phases.irgen.builtin.structure.BIStructure;
import com.arkoisystems.compiler.phases.parser.ast.types.StructNode;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.global.LLVM;
import org.jetbrains.annotations.NotNull;

public class VaListBI extends BIStructure
{
    
    @Override
    public void generateIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final StructNode structNode
    ) {
        // TODO: 6/14/20 Make compatible with other systems
        LLVM.LLVMStructSetBody(structNode.getStructRef(), new PointerPointer<>(
                irVisitor.getContextGen().makeIntType(32),
                irVisitor.getContextGen().makeIntType(32),
                LLVM.LLVMPointerType(irVisitor.getContextGen().makeIntType(8), 0),
                LLVM.LLVMPointerType(irVisitor.getContextGen().makeIntType(8), 0)
        ), 4, 0);
    }
    
    @Override
    public @NotNull String identifier() {
        return "va_list";
    }
    
}
