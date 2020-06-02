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
package com.arkoisystems.arkoicompiler.phases.codegen;

import com.arkoisystems.arkoicompiler.CompilerClass;
import com.arkoisystems.arkoicompiler.api.IStage;
import com.arkoisystems.llvm4j.api.analysis.VerifierFailureAction;
import com.arkoisystems.llvm4j.api.core.modules.Module;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CodeGen implements IStage
{
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @Setter
    private boolean failed;
    
    public CodeGen(final @NotNull CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @Override
    public boolean processStage() {
        final CodeGenVisitor visitor = new CodeGenVisitor();
        final Module module = visitor.visit(this.getCompilerClass().getParser().getRootNode());
        if (module != null) {
            module.dumpModule();
            module.verifyModule(VerifierFailureAction.PRINT_MESSAGE);
        }
        return module != null;
    }
    
}
