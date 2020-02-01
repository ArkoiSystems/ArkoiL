package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.FunctionStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Getter
public class FunctionInvokeAST extends FunctionStatementAST
{
    
    @Expose
    private final IdentifierToken invokedFunctionNameToken;
    
    @Expose
    private final List<AbstractExpressionAST> invokedArguments;
    
    @Expose
    private final FunctionInvocationAccess functionInvocationAccess;
    
    /**
     * This constructor will initialize the statement with the AST-Type "FUNCTION_INVOKE"
     *. This will help to debug problems or check the AST for correct
     * syntax. Also it will pass the IdentifierToken to this class which is used for the
     * invoked function name and the FunctionInvocationAccess which is used to check if
     * the statement ends with a semicolon or not.
     *
     * @param invokedFunctionNameToken
     *         The function IdentifierToken which is used for the name of the function.
     */
    public FunctionInvokeAST(final IdentifierToken invokedFunctionNameToken, final FunctionInvocationAccess functionInvocationAccess) {
        super(ASTType.FUNCTION_INVOKE);
        
        this.invokedFunctionNameToken = invokedFunctionNameToken;
        this.functionInvocationAccess = functionInvocationAccess;
        
        this.invokedArguments = new ArrayList<>();
    }
    
    /**
     * This constructor will initialize the statement with the AST-Type "FUNCTION_INVOKE"
     *. This will help to debug problems or check the AST for correct
     * syntax. Also it will pass the IdentifierToken to this class which is used for the
     * invoked function name.
     *
     * @param invokedFunctionNameToken
     *         The function IdentifierToken which is used for the name of the function.
     */
    public FunctionInvokeAST(final IdentifierToken invokedFunctionNameToken) {
        super(ASTType.FUNCTION_INVOKE);
        
        this.invokedFunctionNameToken = invokedFunctionNameToken;
        
        this.functionInvocationAccess = FunctionInvocationAccess.BLOCK_INVOCATION;
        this.invokedArguments = new ArrayList<>();
    }
    
    /**
     * This method will parse the "function invoke" statement and checks it for the correct
     * syntax. This statement can just be used inside a BlockAST or inside an
     * AbstractExpressionAST.
     * <p>
     * An example for this statement:
     * <p>
     * var test_string = test();
     * <p>
     * fun main<int>(args: string[]) { println(test()); return 0; }
     * <p>
     * fun test<string>() = "Hello World";
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is needed for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an FunctionStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public FunctionInvokeAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockAST) && !(parentAST instanceof AbstractExpressionAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the \"function invoke\" statement because it isn't declared inside a block or an expression."));
            return null;
        }
        
        if (this.invokedFunctionNameToken == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the \"function invoke\" statement because the parent tried to parse an function invocation with no function name declared."));
            return null;
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals(this.invokedFunctionNameToken.getTokenContent())) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because the parsing doesn't start with the valid function name."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because the function name isn't followed by an opening parenthesis. To invoke function you need to add parenthesis because it is necessary to differentiate between functions and variables."));
            return null;
        }
        
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS) != null)
                break;
            
            if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because there is incorrect syntax of an expression inside the parenthesis."));
                return null;
            }
            
            final AbstractExpressionAST abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function invoke\" statement because an error occurred during the parsing of the expression."));
            } else this.invokedArguments.add(abstractExpressionAST);
            
            if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS) != null)
                break;
            else if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.COMMA) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because an expression isn't followed by an comma or an closing parenthesis."));
                return null;
            }
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because the expression section isn't ended with an closing parenthesis."));
            return null;
        }
        
        if (this.functionInvocationAccess.equals(FunctionInvocationAccess.BLOCK_INVOCATION) && syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because it doesn't end with a semicolon but is used as a block invocation."));
            return null;
        }
        
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    public enum FunctionInvocationAccess
    {
        
        BLOCK_INVOCATION,
        EXPRESSION_INVOCATION;
        
    }
    
}
