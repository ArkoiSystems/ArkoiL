package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.AssignmentOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.ArgumentDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.ThisStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements.*;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import lombok.Getter;

@Getter
public class StatementParser extends Parser<AbstractStatementAST>
{
    
    @Override
    public AbstractStatementAST parse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractStatementAST statementAST = new AbstractStatementAST().parseAST(parentAST, syntaxAnalyzer);
        if (statementAST != null)
            return statementAST;
        
        // TODO: 1/5/2020 Throw error
        return null;
    }
    
    @Override
    public boolean canParse(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        if (currentToken == null || currentToken.getTokenType() != TokenType.IDENTIFIER)
            return false;
    
        if(parentAST instanceof ThisStatementAST) {
            // TODO: 1/15/2020 Just search methods in THIS RootFile
        
            switch (currentToken.getTokenContent()) {
                case "val":
                case "fun":
                case "this":
                case "return":
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the statement because you can't use it with the \"this\" keyword. The \"this\" keyword can just be followed by a function or variable."));
                    break;
                default:
                    final BlockAST blockAST = ((ThisStatementAST) parentAST).getBlockAST();
                
                    VariableDefinitionAST variableDefinitionAST = blockAST.getVariableByName(currentToken);
                    if(variableDefinitionAST == null)
                        variableDefinitionAST = syntaxAnalyzer.getRootAST().getVariableByName(currentToken);
                
                    if (variableDefinitionAST != null) {
                        if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.ADDITION_ASSIGNMENT) != null)
                            return true;
                        else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.SUBTRACTION_ASSIGNMENT) != null)
                            return true;
                        else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.MULTIPLICATION_ASSIGNMENT) != null)
                            return true;
                        else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.DIVISION_ASSIGNMENT) != null)
                            return true;
                    }
                
                    final FunctionDefinitionAST functionDefinitionAST = syntaxAnalyzer.getRootAST().getFunctionByName(currentToken);
                    if (functionDefinitionAST != null)
                        return true;
                    break;
            }
        } else if (parentAST instanceof AbstractExpressionAST) {
            // TODO: 1/15/2020 Search for every method in every RootFile included (natives too)
            
            if (currentToken.getTokenContent().equals("this"))
                return true;
            else {
                VariableDefinitionAST variableDefinitionAST = syntaxAnalyzer.getRootAST().getVariableByName(currentToken);
                if (variableDefinitionAST != null) {
                    if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.ADDITION_ASSIGNMENT) != null)
                        return true;
                    else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.SUBTRACTION_ASSIGNMENT) != null)
                        return true;
                    else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.MULTIPLICATION_ASSIGNMENT) != null)
                        return true;
                    else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.DIVISION_ASSIGNMENT) != null)
                        return true;
                }
    
                // TODO: 1/16/2020 Add native methods etc
                
                return syntaxAnalyzer.getRootAST().getFunctionByName(currentToken) != null;
            }
        } else {
            // TODO: 1/15/2020 Search for every method in every RootFile included (natives too)
        
            switch (currentToken.getTokenContent()) {
                case "val":
                case "this":
                case "fun":
                case "return":
                    return true;
                default:
                    if(parentAST instanceof BlockAST) {
                        VariableDefinitionAST variableDefinitionAST = ((BlockAST) parentAST).getVariableByName(currentToken);
                        if(variableDefinitionAST == null)
                            variableDefinitionAST = syntaxAnalyzer.getRootAST().getVariableByName(currentToken);
                    
                        if (variableDefinitionAST != null) {
                            if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.ADDITION_ASSIGNMENT) != null)
                                return true;
                            else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.SUBTRACTION_ASSIGNMENT) != null)
                                return true;
                            else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.MULTIPLICATION_ASSIGNMENT) != null)
                                return true;
                            else if (syntaxAnalyzer.matchesPeekToken(1, AssignmentOperatorToken.AssignmentOperatorType.DIVISION_ASSIGNMENT) != null)
                                return true;
                        }
                    }
                    return syntaxAnalyzer.getRootAST().getFunctionByName(currentToken) != null;
            }
        }
        return false;
    }
    
    @Override
    public String childName() {
        return ArgumentDefinitionAST.class.getSimpleName();
    }
    
}