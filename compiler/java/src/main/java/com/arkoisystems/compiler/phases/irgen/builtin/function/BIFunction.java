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
package com.arkoisystems.compiler.phases.irgen.builtin.function;

import com.arkoisystems.compiler.phases.irgen.IRVisitor;
import com.arkoisystems.compiler.phases.irgen.builtin.Builtin;
import com.arkoisystems.compiler.phases.parser.ast.types.operable.types.identifier.types.FunctionCallNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.FunctionNode;
import org.bytedeco.llvm.LLVM.LLVMValueRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BIFunction implements Builtin
{
    
    public abstract void generateFunctionIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionNode functionNode
    );
    
    public abstract boolean generatesFunctionIR();
    
    @Nullable
    public abstract LLVMValueRef generateFunctionCallIR(
            @NotNull final IRVisitor irVisitor,
            @NotNull final FunctionCallNode functionCallNode
    );
    
    public abstract boolean generatesFunctionCallIR();
    
}
