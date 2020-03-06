/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.AnnotationSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.Getter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class VariableDefinitionSyntaxAST extends AbstractStatementSyntaxAST
{
    
    
    @Getter
    private final List<AnnotationSyntaxAST> variableAnnotations;
    
    
    @Getter
    private IdentifierToken variableName;
    
    
    @Getter
    private ExpressionSyntaxAST variableExpression;
    
    
    public VariableDefinitionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final List<AnnotationSyntaxAST> variableAnnotations) {
        super(syntaxAnalyzer, ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = variableAnnotations;
    }
    
    
    public VariableDefinitionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(syntaxAnalyzer, ASTType.VARIABLE_DEFINITION);
        
        this.variableAnnotations = new ArrayList<>();
    }
    
    
    @Override
    public VariableDefinitionSyntaxAST parseAST(final AbstractSyntaxAST parentAST) {
        if (!(parentAST instanceof RootSyntaxAST) && !(parentAST instanceof BlockSyntaxAST)) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_PARENT
            );
            return null;
        }
        
        if (this.getSyntaxAnalyzer().matchesCurrentToken(TokenType.IDENTIFIER) == null || !this.getSyntaxAnalyzer().currentToken().getTokenContent().equals("var")) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_STAR
            );
            return null;
        }
        this.setStart(this.getSyntaxAnalyzer().currentToken().getStart());
        
        if (this.getSyntaxAnalyzer().matchesNextToken(TokenType.IDENTIFIER) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_NO_NAME
            );
            return null;
        }
        this.variableName = (IdentifierToken) this.getSyntaxAnalyzer().currentToken();
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.EQUAL) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_NO_EQUAL_SIGN
            );
            return null;
        }
        this.getSyntaxAnalyzer().nextToken();
        
        if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, this.getSyntaxAnalyzer())) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_ERROR_DURING_EXPRESSION_PARSING
            );
            return null;
        }
        
        final ExpressionSyntaxAST expressionAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, this.getSyntaxAnalyzer());
        if (expressionAST == null)
            return null;
        this.variableExpression = expressionAST;
        
        if (this.getSyntaxAnalyzer().matchesNextToken(SymbolType.SEMICOLON) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().currentToken(),
                    SyntaxErrorType.VARIABLE_DEFINITION_WRONG_ENDING
            );
            return null;
        }
        
        this.setEnd(this.getSyntaxAnalyzer().currentToken().getEnd());
        return this;
    }
    
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
        printStream.println(indents + "├── annotations: " + (this.getVariableAnnotations().isEmpty() ? "N/A" : ""));
        for (int index = 0; index < this.getVariableAnnotations().size(); index++) {
            final AnnotationSyntaxAST annotationSyntaxAST = this.getVariableAnnotations().get(index);
            if (index == this.getVariableAnnotations().size() - 1) {
                printStream.println(indents + "│   └── " + annotationSyntaxAST.getClass().getSimpleName());
                annotationSyntaxAST.printSyntaxAST(printStream, indents + "│       ");
            } else {
                printStream.println(indents + "│   ├── " + annotationSyntaxAST.getClass().getSimpleName());
                annotationSyntaxAST.printSyntaxAST(printStream, indents + "│   │   ");
                printStream.println(indents + "│   │");
            }
        }
        printStream.println(indents + "│");
        printStream.println(indents + "├── name: " + this.getVariableName().getTokenContent());
        printStream.println(indents + "│");
        printStream.println(indents + "└── expression:");
        this.getVariableExpression().printSyntaxAST(printStream, indents + "    ");
    }
    
}
