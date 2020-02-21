/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.errorHandler.AbstractError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class SemanticASTError<T extends AbstractSemanticAST<?>> extends AbstractError
{
    
    @Getter
    private final ArkoiClass arkoiClass;
    
    
    @Getter
    private final T[] semanticASTs;
    
    
    public SemanticASTError(final ArkoiClass arkoiClass, final T[] semanticASTs, final String message, final Object... arguments) {
        super(semanticASTs[0].getStart(), semanticASTs[semanticASTs.length - 1].getEnd(), message, arguments);
        
        this.semanticASTs = semanticASTs;
        this.arkoiClass = arkoiClass;
    }
    
    
    public SemanticASTError(final ArkoiClass arkoiClass, final T[] semanticASTs, final int start, final int end, final String message, final Object... arguments) {
        super(start, end, message, arguments);
        
        this.semanticASTs = semanticASTs;
        this.arkoiClass = arkoiClass;
    }
    
    
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("[" + ErrorHandler.DATE_FORMAT.format(new Date()) + "|INFO] " + this.getMessage() + "\r\n");
        
        for(final AbstractSemanticAST<?> abstractSemanticAST : this.semanticASTs) {
            stringBuilder.append("  >>> ");
            int start = abstractSemanticAST.getStart();
            for (; start > 0; start--) {
                if (this.getArkoiClass().getContent()[start] == 0x0A) {
                    start++;
                    break;
                }
            }
            int end = abstractSemanticAST.getEnd();
            for (; end < this.getArkoiClass().getContent().length; end++) {
                if (this.getArkoiClass().getContent()[end] == 0x0A)
                    break;
            }
    
            final String line = new String(Arrays.copyOfRange(this.getArkoiClass().getContent(), start, end));
            final String trimmedLine = line.trim();
            stringBuilder.append(trimmedLine).append("\r\n");
    
            for (int index = 0; index < 6 + (abstractSemanticAST.getStart() - start) + (line.length() - trimmedLine.length()); index++)
                stringBuilder.append(" ");
            stringBuilder.append("^");
            for (int index = 0; index < (abstractSemanticAST.getEnd() - abstractSemanticAST.getStart()); index++) {
                if (index == (abstractSemanticAST.getEnd() - abstractSemanticAST.getStart()) - 1)
                    stringBuilder.append("^");
                else
                    stringBuilder.append("~");
            }
            stringBuilder.append("\r\n");
        }
        
        return stringBuilder.toString();
    }
    
}
