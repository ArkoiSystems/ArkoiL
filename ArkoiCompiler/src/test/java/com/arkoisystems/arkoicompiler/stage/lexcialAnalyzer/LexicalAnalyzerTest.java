/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class LexicalAnalyzerTest
{
    
    @Test
    public void parseInvalidCharacterVariable() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var 'test = 0;".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertFalse(lexicalAnalyzer.processStage(), "\n" + this.getStackTrace(arkoiCompiler));
        
        assertEquals("" +
                "The defined character is unknown for the lexical analyzer:\n" +
                " >>> var 'test = 0;\n" +
                "         ^\n", this.getStackTrace(lexicalAnalyzer));
    }
    
    
    @Test
    public void parseIntegerVariable() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var test = 0;".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), "\n" + this.getStackTrace(arkoiCompiler));
        
        assertArrayEquals(new TokenType[] {
                TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseFloatingVariable() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var test = 2.0;".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), "\n" + this.getStackTrace(arkoiCompiler));
        
        assertArrayEquals(new TokenType[] {
                TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseStringVariable() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var test = \"Hello World :) \\\" okay?\";".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), "\n" + this.getStackTrace(arkoiCompiler));
        
        assertArrayEquals(new TokenType[] {
                TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.OPERATOR, TokenType.STRING_LITERAL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseImport() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "import \"System\" as system;".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), "\n" + this.getStackTrace(arkoiCompiler));
        
        assertArrayEquals(new TokenType[] {
                TokenType.KEYWORD, TokenType.STRING_LITERAL, TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseMathematicalExpression() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var test = (20++ + -10 * 5f) * 2 ** 3 ** 4 + (test_6 += 1);".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        assertArrayEquals(new TokenType[] {
                TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.OPERATOR, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.OPERATOR, TokenType.OPERATOR,
                TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.OPERATOR,
                TokenType.NUMBER_LITERAL, TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.OPERATOR, TokenType.SYMBOL,
                TokenType.IDENTIFIER, TokenType.OPERATOR, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseMainMethod() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "fun main<int>(args: string[]) { }".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        assertArrayEquals(new TokenType[] {
                TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.IDENTIFIER, TokenType.SYMBOL,
                TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    private String getStackTrace(final LexicalAnalyzer lexicalAnalyzer) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        lexicalAnalyzer.getErrorHandler().printStackTrace(printStream, true);
        return byteArrayOutputStream.toString();
    }
    
    
    private String getStackTrace(final ArkoiCompiler arkoiCompiler) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        arkoiCompiler.printStackTrace(printStream);
        return byteArrayOutputStream.toString();
    }
    
}