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
package com.arkoisystems.compiler.phases.semantic;

import com.arkoisystems.compiler.CompilerClass;
import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import com.arkoisystems.compiler.phases.parser.ast.types.RootNode;
import com.arkoisystems.compiler.phases.parser.ast.types.statement.types.ImportNode;
import com.arkoisystems.compiler.phases.semantic.routines.ScopeVisitor;
import com.arkoisystems.compiler.phases.semantic.routines.TypeVisitor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Semantic
{
    
    @NotNull
    private final CompilerClass compilerClass;
    
    @Setter
    private boolean failed;
    
    public Semantic(@NotNull final CompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @SneakyThrows
    public void processStage() {
        this.importAllClasses(this.getCompilerClass().getParser().getRootNode());
        
        final TypeVisitor typeVisitor = new TypeVisitor(this);
        typeVisitor.visit(this.getCompilerClass().getParser().getRootNode());
        if (typeVisitor.isFailed())
            this.setFailed(true);
        
        final ScopeVisitor scopeVisitor = new ScopeVisitor(this);
        scopeVisitor.visit(this.getCompilerClass().getParser().getRootNode());
        if (scopeVisitor.isFailed())
            this.setFailed(true);
    }
    
    public void importAllClasses(@NotNull final RootNode rootNode) {
        rootNode.getNodes().stream()
                .filter(node -> node instanceof ImportNode)
                .map(node -> (ImportNode) node)
                .forEach(importNode -> {
                    final CompilerClass compilerClass = importNode.resolveClass();
                    if (compilerClass == null)
                        return;
    
                    final HashMap<String, List<ParserNode>> symbolTable = compilerClass.getRootScope().getSymbolTable();
                    for (final Map.Entry<String, List<ParserNode>> entry : symbolTable.entrySet()) {
                        final List<ParserNode> original = entry.getValue();
                        final List<ParserNode> copy = new ArrayList<>();
        
                        for (final ParserNode parserNode : original) {
                            try {
                                copy.add(parserNode.clone());
                            } catch (final CloneNotSupportedException ignored) {
                            }
                        }
        
                        entry.setValue(copy);
                    }
    
                    this.getCompilerClass().getRootScope().getSymbolTable().putAll(symbolTable);
                    this.importAllClasses(compilerClass.getParser().getRootNode());
                });
    }
    
}
