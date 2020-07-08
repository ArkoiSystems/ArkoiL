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
package com.arkoisystems.compiler.phases.irgen.builtin;

import com.arkoisystems.compiler.phases.irgen.builtin.function.BIFunction;
import com.arkoisystems.compiler.phases.irgen.builtin.function.types.VaEndBI;
import com.arkoisystems.compiler.phases.irgen.builtin.function.types.VaStartBI;
import com.arkoisystems.compiler.phases.irgen.builtin.structure.BIStructure;
import com.arkoisystems.compiler.phases.irgen.builtin.structure.types.VaListBI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class BIManager
{
    
    public static BIManager INSTANCE = new BIManager();
    
    @NotNull
    private final List<Builtin> builtin;
    
    private BIManager() {
        this.builtin = new ArrayList<>();
        
        this.getBuiltin().add(new VaStartBI());
        this.getBuiltin().add(new VaEndBI());
        this.getBuiltin().add(new VaListBI());
    }
    
    @Nullable
    public BIFunction getFunction(@NotNull final String name) {
        return this.getBuiltin().stream()
                .filter(entry -> entry instanceof BIFunction)
                .map(entry -> (BIFunction) entry)
                .filter(entry -> entry.identifier().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    @Nullable
    public BIStructure getStructure(@NotNull final String name) {
        return this.getBuiltin().stream()
                .filter(entry -> entry instanceof BIStructure)
                .map(entry -> (BIStructure) entry)
                .filter(entry -> entry.identifier().equals(name))
                .findFirst()
                .orElse(null);
    }
    
}
