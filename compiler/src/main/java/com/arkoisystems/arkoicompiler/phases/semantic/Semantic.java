/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
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
package com.arkoisystems.arkoicompiler.phases.semantic;

import com.arkoisystems.arkoicompiler.CompilerClass;
import com.arkoisystems.arkoicompiler.api.IStage;
import com.arkoisystems.arkoicompiler.phases.semantic.routines.ScopeVisitor;
import com.arkoisystems.arkoicompiler.phases.semantic.routines.TypeVisitor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

@Getter
public class Semantic implements IStage
{
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @Setter
    private boolean failed;
    
    public Semantic(final @NotNull CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.reset();
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(this);
        scopeVisitor.visit(this.getCompilerClass().getParser().getRootNodeAST());
        
        final TypeVisitor typeVisitor = new TypeVisitor(this, scopeVisitor);
        typeVisitor.visit(this.getCompilerClass().getParser().getRootNodeAST());
        
        return !scopeVisitor.isFailed() && !typeVisitor.isFailed();
    }
    
    @Override
    public void reset() {
        this.failed = false;
    }
    
}
