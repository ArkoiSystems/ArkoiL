package com.arkoisystems.arkoicompiler.compileStage.errorHandler.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;

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
public class ParserError extends AbstractError
{
    
    @Expose
    private final Parser<?> parser;
    
    public ParserError(final Parser<?> parser, final int start, final int end, final String message, final Object... arguments) {
        super(start, end, "Couldn't parse the \"" + parser.childName() + "\" because an error occurred. " + message, arguments);
        
        this.parser = parser;
    }
    
    public ParserError(final Parser<?> parser, final AbstractToken abstractToken, final String message, final Object... arguments) {
        super(abstractToken.getStart(), abstractToken.getEnd(), "Couldn't parse the \"" + parser.childName() + "\" because an error occurred. " + message, arguments);
        
        this.parser = parser;
    }
    
    public ParserError(final Parser<?> parser, final AbstractAST abstractAST, final String message, final Object... arguments) {
        super(abstractAST.getStart(), abstractAST.getEnd(), "Couldn't parse the \"" + parser.childName() + "\" because an error occurred. " + message, arguments);
        
        this.parser = parser;
    }
    
    public ParserError(final Parser<?> parser, final AbstractToken abstractToken) {
        super(abstractToken.getStart(), abstractToken.getEnd(), "Couldn't parse the \"" + parser.childName() + "\" because an error occurred.");
        
        this.parser = parser;
    }
    
    public ParserError(final Parser<?> parser, final int start, final int end) {
        super(start, end, "Couldn't parse the \"" + parser.childName() + "\" because an error occurred.");
        
        this.parser = parser;
    }
    
}
