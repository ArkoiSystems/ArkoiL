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
package com.arkoisystems.arkoicompiler.stage.semantic;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.IStage;
import com.arkoisystems.arkoicompiler.stage.semantic.routines.ScopeVisitor;
import com.arkoisystems.arkoicompiler.stage.semantic.routines.TypeVisitor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class Semantic implements IStage
{
    
    @Getter
    @NotNull
    private final ArkoiClass compilerClass;
    
    @Getter
    @NotNull
    private SemanticErrorHandler errorHandler = new SemanticErrorHandler();
    
    @Getter
    private final boolean detailed;
    
    @Getter
    @Setter
    private boolean failed;
    
    public Semantic(final @NotNull ArkoiClass compilerClass, final boolean detailed) {
        this.compilerClass = compilerClass;
        this.detailed = detailed;
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.reset();
    
        final ScopeVisitor scopeVisitor = new ScopeVisitor(this);
        scopeVisitor.visit(this.getCompilerClass().getParser().getRootAST());
    
        final TypeVisitor typeVisitor = new TypeVisitor(this);
        typeVisitor.visit(this.getCompilerClass().getParser().getRootAST());
    
        return !scopeVisitor.isFailed() && !typeVisitor.isFailed();
    }
    
    @Override
    public void reset() {
        this.errorHandler = new SemanticErrorHandler();
        this.failed = false;
    }
    
}
