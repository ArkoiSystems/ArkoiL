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

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.arkoicompiler.stage.semantic.visitors.ScopeVisitor;
import com.arkoisystems.arkoicompiler.stage.semantic.visitors.TypeVisitor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class SemanticAnalyzer implements ICompilerStage
{
    
    @Getter
    @NotNull
    private final ICompilerClass compilerClass;
    
    @Getter
    @NotNull
    private SemanticErrorHandler errorHandler = new SemanticErrorHandler();
    
    @Getter
    private final boolean detailed;
    
    @Getter
    private boolean failed;
    
    public SemanticAnalyzer(@NotNull final ICompilerClass compilerClass, final boolean detailed) {
        this.compilerClass = compilerClass;
        this.detailed = detailed;
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        this.reset();
    
        final ScopeVisitor scopeVisitor = new ScopeVisitor(this);
        scopeVisitor.visit(this.getCompilerClass().getSyntaxAnalyzer().getRootAST());
    
        final TypeVisitor typeVisitor = new TypeVisitor(this);
        typeVisitor.visit(this.getCompilerClass().getSyntaxAnalyzer().getRootAST());
    
        return !scopeVisitor.isFailed() && !typeVisitor.isFailed();
    }
    
    @Override
    public void reset() {
        this.errorHandler = new SemanticErrorHandler();
        this.failed = false;
    }
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
}
