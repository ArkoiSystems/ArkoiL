/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.errorHandler;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.semanticAnalyzer.ast.AbstractSemanticAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.utils.Variables;
import lombok.Getter;

import java.util.Arrays;
import java.util.Date;

public class ArkoiError
{
    
    @Getter
    private final ArkoiClass arkoiClass;
    
    
    @Getter
    private final Object[] arguments;
    
    
    @Getter
    private final int[][] positions;
    
    
    @Getter
    private final String errorString;
    
    
    @Getter
    private final String message;
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final AbstractSemanticAST<?>[] semanticASTs, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[semanticASTs.length][];
        for (int index = 0; index < semanticASTs.length; index++)
            this.positions[index] = new int[] { semanticASTs[index].getStart(), semanticASTs[index].getEnd() };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final AbstractSemanticAST<?> semanticAST, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[1][];
        this.positions[0] = new int[] { semanticAST.getStart(), semanticAST.getEnd() };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final AbstractSyntaxAST[] syntaxASTs, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[syntaxASTs.length][];
        for (int index = 0; index < syntaxASTs.length; index++)
            this.positions[index] = new int[] { syntaxASTs[index].getStart(), syntaxASTs[index].getEnd() };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final AbstractSyntaxAST syntaxAST, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[1][];
        this.positions[0] = new int[] { syntaxAST.getStart(), syntaxAST.getEnd() };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final AbstractToken[] abstractTokens, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[abstractTokens.length][];
        for (int index = 0; index < abstractTokens.length; index++)
            this.positions[index] = new int[] { abstractTokens[index].getStart(), abstractTokens[index].getEnd() };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final AbstractToken abstractToken, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[1][];
        this.positions[0] = new int[] { abstractToken.getStart(), abstractToken.getEnd() };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final int position, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[1][];
        this.positions[0] = new int[] { position, position };
        this.errorString = this.createError();
    }
    
    
    public ArkoiError(final ArkoiClass arkoiClass, final int start, final int end, final String message, final Object... arguments) {
        this.arkoiClass = arkoiClass;
        this.arguments = arguments;
        this.message = message;
        
        this.positions = new int[1][];
        this.positions[0] = new int[] { start, end };
        this.errorString = this.createError();
    }
    
    
    private String createError() {
        final StringBuilder stringBuilder = new StringBuilder("[" + Variables.DATE_FORMAT.format(new Date()) + "/INFO] " + String.format(this.getMessage(), this.getArguments()) + "\n");
        for (int index = 0; index < this.positions.length; index++) {
            final int[] position = this.positions[index];
            if (position.length != 2)
                continue;
            
            stringBuilder.append(" >>> ");
            int startPosition = position[0], endPosition = position[1];
            for (; startPosition > 0; startPosition--) {
                if (this.getArkoiClass().getContent()[startPosition] != 0x0A)
                    continue;
                startPosition++;
                break;
            }
            for (; endPosition < this.getArkoiClass().getContent().length; endPosition++) {
                if (this.getArkoiClass().getContent()[endPosition] != 0x0A)
                    continue;
                break;
            }
            
            final String realLine = new String(Arrays.copyOfRange(this.getArkoiClass().getContent(), startPosition, endPosition));
            final String line = realLine.replaceAll("\n", "");
            final int difference = realLine.length() - line.length();
            
            stringBuilder.append(line).append("\n");
            stringBuilder.append(" ".repeat(Math.max(0, 5 + (position[0] - startPosition) - difference)));
            stringBuilder.append("^".repeat(Math.max(1, (position[1] - position[0]))));
            
            if (index != this.positions.length - 1)
                stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    
    
    @Override
    public String toString() {
        return this.errorString;
    }
    
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(getArguments());
        result = 31 * result + Arrays.deepHashCode(getPositions());
        result = 31 * result + getMessage().hashCode();
        return result;
    }
    
}
