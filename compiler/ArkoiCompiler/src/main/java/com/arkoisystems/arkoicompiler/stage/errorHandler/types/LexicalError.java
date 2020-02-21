/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import lombok.Getter;

import java.util.Arrays;
import java.util.Date;

public class LexicalError extends AbstractError
{
 
    @Getter
    private final ArkoiClass arkoiClass;
    
    
    public LexicalError(final ArkoiClass arkoiClass, final int position, final String message) {
        super(position, position, message);
        
        this.arkoiClass = arkoiClass;
    }
    
    
    public LexicalError(final ArkoiClass arkoiClass, final int start, final int end, final String message) {
        super(start, end, message);
        
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
        final String line = new String(Arrays.copyOfRange(arkoiClass.getContent(), start, end));
        final String trimmedLine = line.trim();
        stringBuilder.append(trimmedLine).append("\r\n");
    
        for(int index = 0; index < 6 + (this.getStart() - start) - (line.length() - trimmedLine.length()); index++)
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
