package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.ArkoiClass;
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
        
        if (parentAST instanceof ThisStatementAST) {
            switch (currentToken.getTokenContent()) {
                case "val":
                case "fun":
                case "this":
                case "return":
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the statement because you can't use it with the \"this\" keyword. The \"this\" keyword can just be followed by a function or variable."));
                    break;
                default:
                    final VariableDefinitionAST variableDefinitionAST = syntaxAnalyzer.getRootAST().getVariableByName(currentToken);
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
            if (currentToken.getTokenContent().equals("this"))
                return true;
            else {
                // TODO: Search for internal block variables
                
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
                
                FunctionDefinitionAST functionDefinitionAST = null;
                for (final ArkoiClass arkoiClass : syntaxAnalyzer.getArkoiClass().getArkoiCompiler().getArkoiClasses()) {
                    if ((functionDefinitionAST = arkoiClass.getSyntaxAnalyzer().getRootAST().getFunctionByName(currentToken)) != null)
                        break;
                }
                
                return functionDefinitionAST != null;
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
                    // TODO: Search for internal block variables
                    
                    VariableDefinitionAST variableDefinitionAST = null;
                    if (parentAST instanceof BlockAST)
                        variableDefinitionAST = ((BlockAST) parentAST).getVariableByName(currentToken);
                    
                    if (variableDefinitionAST == null)
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
            
            FunctionDefinitionAST functionDefinitionAST = null;
            for (final ArkoiClass arkoiClass : syntaxAnalyzer.getArkoiClass().getArkoiCompiler().getArkoiClasses()) {
                if ((functionDefinitionAST = arkoiClass.getSyntaxAnalyzer().getRootAST().getFunctionByName(currentToken)) != null)
                    break;
            }
            
            return functionDefinitionAST != null;
        }
        return false;
    }
    
    @Override
    public String childName() {
        return ArgumentDefinitionAST.class.getSimpleName();
    }
    
}