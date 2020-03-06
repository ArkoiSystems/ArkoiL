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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexicalAnalyzerTest
{
    
    @Test
    public void parseIntegerVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = 0;".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        assertArrayEquals(new TokenType[] {
                TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseFloatingVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = 2.0;".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        assertArrayEquals(new TokenType[] {
                TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseStringVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = \"Hello World :) \\\" okay?\";".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        assertArrayEquals(new TokenType[] {
                TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.STRING_LITERAL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseImport() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "import \"System\" as system;".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        assertArrayEquals(new TokenType[] {
                TokenType.IDENTIFIER, TokenType.STRING_LITERAL, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseMathematicalExpression() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = (20++ + -10 * 5f) * 2 ** 3 ** 4 + (test_6 += 1);".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        assertArrayEquals(new TokenType[] {
                TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.SYMBOL,
                TokenType.SYMBOL, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.IDENTIFIER, TokenType.SYMBOL,
                TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.SYMBOL,
                TokenType.NUMBER_LITERAL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.NUMBER_LITERAL,
                TokenType.SYMBOL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
    
    @Test
    public void parseMainMethod() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "fun main<int>(args: string[]) { }".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        assertArrayEquals(new TokenType[] {
                TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.IDENTIFIER, TokenType.SYMBOL,
                TokenType.IDENTIFIER, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.SYMBOL, TokenType.END_OF_FILE
        }, lexicalAnalyzer.getTokenTypes(false));
    }
    
}