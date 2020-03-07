package com.arkoisystems.arkoicompiler.stage.semanticAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class SemanticAnalyzerTest
{
    
    @Test
    public void parseVariable() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var test = 0;".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        final SemanticAnalyzer semanticAnalyzer = arkoiClass.getSemanticAnalyzer();
        assertTrue(semanticAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        assertEquals("" +
                "├── imports: N/A\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSemanticAST\n" +
                "│       ├── annotations: N/A\n" +
                "│       │\n" +
                "│       ├── name: test\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           └── type: int\n" +
                "│\n" +
                "└── functions: N/A\n",
                semanticAnalyzer.getRootSemanticAST().toString()
        );
    }
    
    
    @Test
    public void parseInvalidStringExpression() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                "var test = \"Hello World\" + 5;".getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        final SemanticAnalyzer semanticAnalyzer = arkoiClass.getSemanticAnalyzer();
        assertFalse(semanticAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        assertEquals("" +
                SemanticErrorType.BINARY_ADDITION_OPERABLE_NOT_SUPPORTED + "\n" +
                " >>> var test = \"Hello World\" + 5;\n" +
                "                ^^^^^^^^^^^^^\n",
                this.getStackTrace(semanticAnalyzer)
        );
    }
    
    
    @Test
    public void parseInvalidMathematicalExpression() throws Exception {
        final ArkoiCompiler arkoiCompiler = new ArkoiCompiler("");
        final ArkoiClass arkoiClass = new ArkoiClass(arkoiCompiler, "",
                ("var test_6 = \"Hello World\";\n" +
                        "var test = (20++ + -10-- * 5f) * 2 ** 3 ** 4 + (test_6 * 2 += 1);").getBytes()
        );
        arkoiCompiler.addClass(arkoiClass);
        
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        final SemanticAnalyzer semanticAnalyzer = arkoiClass.getSemanticAnalyzer();
        assertFalse(semanticAnalyzer.processStage(), this.getStackTrace(arkoiCompiler));
        
        assertEquals("" +
                SemanticErrorType.BINARY_MULTIPLICATION_OPERABLE_NOT_SUPPORTED + "\n" +
                " >>> var test = (20++ + -10-- * 5f) * 2 ** 3 ** 4 + (test_6 * 2 += 1);\n" +
                "                                                     ^^^^^^\n" +
                SemanticErrorType.PREFIX_OPERABLE_NOT_SUPPORTED + "\n" +
                " >>> var test = (20++ + -10-- * 5f) * 2 ** 3 ** 4 + (test_6 * 2 += 1);\n" +
                "                         ^^^^\n",
                this.getStackTrace(semanticAnalyzer)
        );
    }
    
    
    private String getStackTrace(final SemanticAnalyzer semanticAnalyzer) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        semanticAnalyzer.getErrorHandler().printStackTrace(printStream, true);
        return byteArrayOutputStream.toString();
    }
    
    
    private String getStackTrace(final ArkoiCompiler arkoiCompiler) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        arkoiCompiler.printStackTrace(printStream);
        return byteArrayOutputStream.toString();
    }
    
}