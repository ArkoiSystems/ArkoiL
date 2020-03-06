/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 7, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyntaxAnalyzerTest
{
    
    @Test
    public void parseVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = 0;".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: N/A\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSyntaxAST\n" +
                "│       ├── annotations: N/A\n" +
                "│       │\n" +
                "│       ├── name: test\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           └── operable: 0\n" +
                "│\n" +
                "└── functions: N/A\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
    
    @Test
    public void parseFloatingVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = 2.0;".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: N/A\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSyntaxAST\n" +
                "│       ├── annotations: N/A\n" +
                "│       │\n" +
                "│       ├── name: test\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           └── operable: 2.0\n" +
                "│\n" +
                "└── functions: N/A\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
    
    @Test
    public void parseStringVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = \"Hello World :) \\\" okay?\";".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: N/A\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSyntaxAST\n" +
                "│       ├── annotations: N/A\n" +
                "│       │\n" +
                "│       ├── name: test\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           └── operable: Hello World :) \\\" okay?\n" +
                "│\n" +
                "└── functions: N/A\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
    
    @Test
    public void parseAnnotationVariable() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                ("@Test[okay]\n" +
                        "@World[Hello]\n" +
                        "var test = \"Hello World :) \\\" okay?\";").getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: N/A\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSyntaxAST\n" +
                "│       ├── annotations: \n" +
                "│       │   ├── AnnotationSyntaxAST\n" +
                "│       │   │   ├── name: Test\n" +
                "│       │   │   └── arguments: \n" +
                "│       │   │       └── okay\n" +
                "│       │   │\n" +
                "│       │   └── AnnotationSyntaxAST\n" +
                "│       │       ├── name: World\n" +
                "│       │       └── arguments: \n" +
                "│       │           └── Hello\n" +
                "│       │\n" +
                "│       ├── name: test\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           └── operable: Hello World :) \\\" okay?\n" +
                "│\n" +
                "└── functions: N/A\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
    
    @Test
    public void parseImport() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "import \"System\" as system;".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: \n" +
                "│   └── ImportDefinitionSyntaxAST\n" +
                "│       ├── name: system\n" +
                "│       └── path: System\n" +
                "│\n" +
                "├── variables: N/A\n" +
                "│\n" +
                "└── functions: N/A\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
    
    @Test
    public void parseMathematicalExpression() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                "var test = (20++ + -10 * 5f) * 2 ** 3 ** 4 + (test_6 += 1);".getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: N/A\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSyntaxAST\n" +
                "│       ├── annotations: N/A\n" +
                "│       │\n" +
                "│       ├── name: test\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           ├── left:\n" +
                "│           │   └── BinaryExpressionSyntaxAST\n" +
                "│           │        ├── left:\n" +
                "│           │        │   └── ParenthesizedExpressionSyntaxAST\n" +
                "│           │        │        └── operable:\n" +
                "│           │        │            └── ExpressionSyntaxAST\n" +
                "│           │        │                ├── left:\n" +
                "│           │        │                │   └── PostfixExpressionSyntaxAST\n" +
                "│           │        │                │        ├── left:\n" +
                "│           │        │                │        │   └── NumberOperableSyntaxAST\n" +
                "│           │        │                │        │       └── operable: 20\n" +
                "│           │        │                │        └── operator: POSTFIX_ADD\n" +
                "│           │        │                ├── operator: ADDITION\n" +
                "│           │        │                └── right:\n" +
                "│           │        │                    └── BinaryExpressionSyntaxAST\n" +
                "│           │        │                        ├── left:\n" +
                "│           │        │                        │   └── PrefixExpressionSyntaxAST\n" +
                "│           │        │                        │        ├── operator: NEGATE\n" +
                "│           │        │                        │        └── right:\n" +
                "│           │        │                        │            └── NumberOperableSyntaxAST\n" +
                "│           │        │                        │                └── operable: 10\n" +
                "│           │        │                        ├── operator: MULTIPLICATION\n" +
                "│           │        │                        └── right:\n" +
                "│           │        │                            └── CastExpressionSyntaxAST\n" +
                "│           │        │                                ├── left:\n" +
                "│           │        │                                │   └── NumberOperableSyntaxAST\n" +
                "│           │        │                                │       └── operable: 5\n" +
                "│           │        │                                └── operator: FLOAT\n" +
                "│           │        ├── operator: MULTIPLICATION\n" +
                "│           │        └── right:\n" +
                "│           │            └── BinaryExpressionSyntaxAST\n" +
                "│           │                ├── left:\n" +
                "│           │                │   └── BinaryExpressionSyntaxAST\n" +
                "│           │                │        ├── left:\n" +
                "│           │                │        │   └── NumberOperableSyntaxAST\n" +
                "│           │                │        │        └── operable: 2\n" +
                "│           │                │        ├── operator: EXPONENTIAL\n" +
                "│           │                │        └── right:\n" +
                "│           │                │            └── NumberOperableSyntaxAST\n" +
                "│           │                │                └── operable: 3\n" +
                "│           │                ├── operator: EXPONENTIAL\n" +
                "│           │                └── right:\n" +
                "│           │                    └── NumberOperableSyntaxAST\n" +
                "│           │                        └── operable: 4\n" +
                "│           ├── operator: ADDITION\n" +
                "│           └── right:\n" +
                "│               └── ParenthesizedExpressionSyntaxAST\n" +
                "│                   └── operable:\n" +
                "│                       └── ExpressionSyntaxAST\n" +
                "│                           ├── left:\n" +
                "│                           │   └── IdentifierCallOperableSyntaxAST\n" +
                "│                           │        ├── access: GLOBAL_ACCESS\n" +
                "│                           │        └── identifier: test_6\n" +
                "│                           ├── operator: ADD_ASSIGN\n" +
                "│                           └── right:\n" +
                "│                               └── NumberOperableSyntaxAST\n" +
                "│                                   └── operable: 1\n" +
                "│\n" +
                "└── functions: N/A\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
    
    @Test
    public void parseFullClass() throws Exception {
        final ArkoiClass arkoiClass = new ArkoiClass(
                new ArkoiCompiler(""), "",
                ("import \"./test\" as test;\n" +
                        "\n" +
                        "var test_1 = 1;\n" +
                        "\n" +
                        "# this method gets invoked first\n" +
                        "fun main<int>(args: string[]) {\n" +
                        "    return this.test_1;\n" +
                        "}\n" +
                        "\n" +
                        "fun print_greet<>(test_argument: string) = println(\"%s , %s\" % [test_2, test_argument]);\n" +
                        "\n" +
                        "fun greeting<string>() = \"Hello World\";\n").getBytes());
        final LexicalAnalyzer lexicalAnalyzer = arkoiClass.getLexicalAnalyzer();
        assertTrue(lexicalAnalyzer.processStage());
        final SyntaxAnalyzer syntaxAnalyzer = arkoiClass.getSyntaxAnalyzer();
        assertTrue(syntaxAnalyzer.processStage());
        assertEquals("" +
                "├── imports: \n" +
                "│   └── ImportDefinitionSyntaxAST\n" +
                "│       ├── name: test\n" +
                "│       └── path: ./test\n" +
                "│\n" +
                "├── variables: \n" +
                "│   └── VariableDefinitionSyntaxAST\n" +
                "│       ├── annotations: N/A\n" +
                "│       │\n" +
                "│       ├── name: test_1\n" +
                "│       │\n" +
                "│       └── expression:\n" +
                "│           └── operable: 1\n" +
                "│\n" +
                "└── functions: \n" +
                "    ├── FunctionDefinitionSyntaxAST\n" +
                "    │   ├── annotations: N/A\n" +
                "    │   │\n" +
                "    │   ├── name: main\n" +
                "    │   ├── type: void\n" +
                "    │   │\n" +
                "    │   ├── arguments: \n" +
                "    │   │   └── ArgumentDefinitionSyntaxAST\n" +
                "    │   │       ├── name: args\n" +
                "    │   │       └── type: string[]\n" +
                "    │   │\n" +
                "    │   └── block: \n" +
                "    │        ├── type: BLOCK\n" +
                "    │        └── storage: \n" +
                "    │            └── ReturnStatementSyntaxAST\n" +
                "    │                └── expression:\n" +
                "    │                    ├── access: THIS_ACCESS\n" +
                "    │                    └── identifier: test_1\n" +
                "    │   \n" +
                "    ├── FunctionDefinitionSyntaxAST\n" +
                "    │   ├── annotations: N/A\n" +
                "    │   │\n" +
                "    │   ├── name: print_greet\n" +
                "    │   ├── type: void\n" +
                "    │   │\n" +
                "    │   ├── arguments: \n" +
                "    │   │   └── ArgumentDefinitionSyntaxAST\n" +
                "    │   │       ├── name: test_argument\n" +
                "    │   │       └── type: string\n" +
                "    │   │\n" +
                "    │   └── block: \n" +
                "    │        ├── type: INLINE\n" +
                "    │        └── storage: \n" +
                "    │            └── ExpressionSyntaxAST\n" +
                "    │                ├── access: GLOBAL_ACCESS\n" +
                "    │                ├── identifier: println\n" +
                "    │                └── expressions: \n" +
                "    │                    └── BinaryExpressionSyntaxAST\n" +
                "    │                        ├── left:\n" +
                "    │                        │   └── StringOperableSyntaxAST\n" +
                "    │                        │        └── operable: %s , %s\n" +
                "    │                        ├── operator: MODULO\n" +
                "    │                        └── right:\n" +
                "    │                            └── CollectionOperableSyntaxAST\n" +
                "    │                                └── expressions: \n" +
                "    │                                    ├── IdentifierCallOperableSyntaxAST\n" +
                "    │                                    │   ├── access: GLOBAL_ACCESS\n" +
                "    │                                    │   └── identifier: test_2\n" +
                "    │                                    │   \n" +
                "    │                                    └── IdentifierCallOperableSyntaxAST\n" +
                "    │                                        ├── access: GLOBAL_ACCESS\n" +
                "    │                                        └── identifier: test_argument\n" +
                "    │   \n" +
                "    └── FunctionDefinitionSyntaxAST\n" +
                "        ├── annotations: N/A\n" +
                "        │\n" +
                "        ├── name: greeting\n" +
                "        ├── type: void\n" +
                "        │\n" +
                "        ├── arguments: N/A\n" +
                "        │\n" +
                "        └── block: \n" +
                "             ├── type: INLINE\n" +
                "             └── storage: \n" +
                "                 └── ExpressionSyntaxAST\n" +
                "                     └── operable: Hello World\n", syntaxAnalyzer.getRootSyntaxAST().toString());
    }
    
}