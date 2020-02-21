/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import lombok.Getter;

public class ParserError<T extends AbstractParser<?>> extends AbstractError
{
    
    
    @Getter
    private final T parser;
    
    
    public ParserError(final T parser, final int start, final int end, final String message, final Object... arguments) {
        super(start, end, message, arguments);
        this.parser = parser;
    }
    
    
    public ParserError(final T parser, final AbstractToken abstractToken, final String message, final Object... arguments) {
        super(abstractToken.getStart(), abstractToken.getEnd(), message, arguments);
        this.parser = parser;
    }
    
    
    public ParserError(final T parser, final AbstractSyntaxAST abstractSyntaxAST, final String message, final Object... arguments) {
        super(abstractSyntaxAST.getStart(), abstractSyntaxAST.getEnd(), message, arguments);
        this.parser = parser;
    }
    
    
    public ParserError(final T parser, final AbstractToken abstractToken) {
        super(abstractToken.getStart(), abstractToken.getEnd(), "Couldn't parse the \"" + parser.getParameterName() + "\" because an error occurred.");
        this.parser = parser;
    }
    
    
    public ParserError(final T parser, final int start, final int end) {
        super(start, end, "Couldn't parse the \"" + parser.getParameterName() + "\" because an error occurred.");
        this.parser = parser;
    }
    
}
