/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 11, 2020
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
package com.arkoisystems.compiler.phases.irgen;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.llvm.Module;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class IRGenerator
{
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @Setter
    private boolean failed;
    
    @Nullable
    private Module module;
    
    public IRGenerator(@NotNull final CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    public boolean processStage() {
        final IRVisitor visitor = new IRVisitor(this.getCompilerClass());
        this.module = visitor.visit(this.getCompilerClass().getParser().getRootNode());
        return !visitor.isFailed();
    }
    
}
