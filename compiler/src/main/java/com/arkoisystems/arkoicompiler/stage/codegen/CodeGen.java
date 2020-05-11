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
package com.arkoisystems.arkoicompiler.stage.codegen;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.llvm4j.api.core.modules.Module;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class CodeGen implements ICompilerStage
{
    
    @Getter
    private final ICompilerClass compilerClass;
    
    @Getter
    private CodeGenErrorHandler errorHandler;
    
    @Getter
    private boolean failed;
    
    public CodeGen(final @NotNull ICompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @Override
    public boolean processStage() {
        final CodeGenVisitor visitor = new CodeGenVisitor();
        final Module module = visitor.visit(this.getCompilerClass().getSyntaxAnalyzer().getRootAST());
        module.dumpModule();
        return module != null;
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    @Override
    public void reset() {
        this.errorHandler = new CodeGenErrorHandler();
    }
    
}
