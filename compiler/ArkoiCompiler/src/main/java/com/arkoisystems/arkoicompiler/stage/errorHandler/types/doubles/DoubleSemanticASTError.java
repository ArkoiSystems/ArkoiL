package com.arkoisystems.arkoicompiler.stage.errorHandler.types.doubles;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.DoubleError;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import lombok.Getter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
public class DoubleSemanticASTError<T1 extends AbstractSemanticAST<?>, T2 extends AbstractSemanticAST<?>> extends DoubleError<T1, T2>
{
    
    public DoubleSemanticASTError(final T1 firstAbstractAST, final T2 secondAbstractAST, String message, Object... arguments) {
        super(firstAbstractAST, secondAbstractAST, firstAbstractAST.getStart(), secondAbstractAST.getEnd(), message, arguments);
    }
    
}
