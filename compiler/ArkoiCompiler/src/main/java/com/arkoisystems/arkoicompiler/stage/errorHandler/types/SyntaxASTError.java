/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import lombok.Getter;

import java.util.Arrays;
import java.util.Date;

public class SyntaxASTError<T extends AbstractSyntaxAST> extends AbstractError
{
    
    @Getter
    private final ArkoiClass arkoiClass;
    
    
    @Getter
    private final T abstractAST;
    
    
    public SyntaxASTError(final ArkoiClass arkoiClass, final T abstractAST, final String message, final Object... arguments) {
        super(abstractAST.getStart(), abstractAST.getEnd(), message, arguments);
        
        this.abstractAST = abstractAST;
        this.arkoiClass = arkoiClass;
    }
    
    public SyntaxASTError(final ArkoiClass arkoiClass, final T abstractAST, final int start, final int end, final String message, final Object... arguments) {
        super(start, end, message, arguments);
        
        this.abstractAST = abstractAST;
        this.arkoiClass = arkoiClass;
    }
    
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("[" + ErrorHandler.DATE_FORMAT.format(new Date()) + "|INFO] " + this.getMessage() + "\r\n");
        stringBuilder.append("  >>> ");
        
        int start = this.getStart();
        for(; start > 0; start--) {
            if(this.getArkoiClass().getContent()[start] == 0x0A) {
                start++;
                break;
            }
        }
        int end = this.getEnd();
        for(; end < this.getArkoiClass().getContent().length; end++) {
            if(this.getArkoiClass().getContent()[end] == 0x0A)
                break;
        }
        final String line = new String(Arrays.copyOfRange(this.getArkoiClass().getContent(), start, end));
        final String trimmedLine = line.trim();
        stringBuilder.append(trimmedLine).append("\r\n");
    
        for(int index = 0; index < 6 + (this.getStart() - start) + (line.length() - trimmedLine.length()); index++)
            stringBuilder.append(" ");
        stringBuilder.append("^");
        for(int index  = 0; index < (this.getEnd() - this.getStart()); index++) {
            if(index == (this.getEnd() - this.getStart()) - 1)
                stringBuilder.append("^");
            else
                stringBuilder.append("~");
        }
        
        return stringBuilder.toString();
    }
    
}
