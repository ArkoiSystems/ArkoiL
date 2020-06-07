/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on May 31, 2020
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
package com.arkoisystems.compiler.phases.parser;

import com.arkoisystems.compiler.phases.parser.ast.ParserNode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class SymbolTable
{
    
    @NotNull
    private final HashMap<String, List<ParserNode>> symbolTable;
    
    @Nullable
    private final SymbolTable parentScope;
    
    public SymbolTable(final @Nullable SymbolTable parentScope) {
        this.parentScope = parentScope;
        
        this.symbolTable = new HashMap<>();
    }
    
    @Nullable
    public List<ParserNode> lookup(final @NotNull String id) {
        return this.getSymbolTable().getOrDefault(id, this.getParentScope() != null ?
                this.getParentScope().lookup(id) :
                null
        );
    }
    
    @Nullable
    public List<ParserNode> lookupScope(final @NotNull String id) {
        return this.getSymbolTable().get(id);
    }
    
    public void insert(final @NotNull String id, final @NotNull ParserNode node) {
        final List<ParserNode> nodes = this.getSymbolTable().getOrDefault(id, new ArrayList<>());
        nodes.add(node);
        this.getSymbolTable().put(id, nodes);
    }
    
}
