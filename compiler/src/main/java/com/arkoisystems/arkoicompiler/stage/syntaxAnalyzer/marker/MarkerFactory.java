/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 29, 2020
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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.marker;

import com.arkoisystems.arkoicompiler.api.ICompilerMarker;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class MarkerFactory<T1, T2>
{
    
    @NotNull
    private final List<MarkerFactory<?, ?>> nextMarkerFactories = new ArrayList<>();
    
    @NotNull
    private final ICompilerMarker<T1, T2> currentMarker;
    
    public void addFactory(final MarkerFactory<?, ?> markerFactory) {
        this.nextMarkerFactories.add(markerFactory);
    }
    
    public void mark(final T1 start) {
        this.currentMarker.setStart(start);
    }
    
    public void error(final T1 start, final T2 end, final String message, final Object... arguments) {
        this.currentMarker.setErrorMessage(message);
        this.currentMarker.setErrorArguments(arguments);
        
        this.mark(start);
        this.done(end);
    }
    
    public void done(final T2 end) {
        this.currentMarker.setEnd(end);
    }
    
}
