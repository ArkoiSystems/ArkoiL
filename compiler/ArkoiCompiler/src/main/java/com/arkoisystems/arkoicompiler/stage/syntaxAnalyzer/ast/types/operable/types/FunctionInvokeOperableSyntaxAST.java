/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.SyntaxASTError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTAccess;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.BlockSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class FunctionInvokeOperableSyntaxAST extends AbstractOperableSyntaxAST<TypeSyntaxAST.TypeKind>
{
    
    @Expose
    private ASTAccess functionAccess;
    
    @Expose
    private final IdentifierToken invokedFunctionName;
    
    @Expose
    private final List<ExpressionSyntaxAST> invokedExpressions;
    
    @Expose
    private final FunctionInvocation invocationType;
    
    /**
     * This constructor will initialize the statement with the AST-Type "FUNCTION_INVOKE"
     * . This will help to debug problems or check the AST for correct syntax. Also it
     * will pass the IdentifierToken to this class which is used for the invoked function
     * name and the FunctionInvocationAccess which is used to check if the statement ends
     * with a semicolon or not.
     *
     * @param invokedFunctionName
     *         The function IdentifierToken which is used for the name of the function.
     */
    public FunctionInvokeOperableSyntaxAST(final IdentifierToken invokedFunctionName, final FunctionInvocation invocationType) {
        super(ASTType.FUNCTION_INVOKE_OPERABLE);
        
        this.invokedFunctionName = invokedFunctionName;
        this.invocationType = invocationType;
    
        this.functionAccess = ASTAccess.GLOBAL_ACCESS;
        this.invokedExpressions = new ArrayList<>();
    }
    
    /**
     * This constructor will initialize the statement with the AST-Type "FUNCTION_INVOKE"
     * . This will help to debug problems or check the AST for correct syntax. Also it
     * will pass the IdentifierToken to this class which is used for the invoked function
     * name.
     *
     * @param invokedFunctionName
     *         The function IdentifierToken which is used for the name of the function.
     */
    public FunctionInvokeOperableSyntaxAST(final IdentifierToken invokedFunctionName) {
        super(ASTType.FUNCTION_INVOKE_OPERABLE);
        
        this.invokedFunctionName = invokedFunctionName;
        
        this.invocationType = FunctionInvocation.BLOCK_INVOCATION;
        this.functionAccess = ASTAccess.GLOBAL_ACCESS;
        this.invokedExpressions = new ArrayList<>();
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
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an FunctionStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public FunctionInvokeOperableSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockSyntaxAST) && !(parentAST instanceof AbstractExpressionSyntaxAST) && !(parentAST instanceof IdentifierInvokeOperableSyntaxAST)) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the \"function invoke\" statement because it isn't declared inside a block, variable invocation or an expression."));
            return null;
        }
    
        if (this.invokedFunctionName == null) {
            syntaxAnalyzer.errorHandler().addError(new SyntaxASTError<>(parentAST, "Couldn't parse the \"function invoke\" statement because the parent tried to parse an function invocation with no function name declared."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals(this.invokedFunctionName.getTokenContent())) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because the parsing doesn't start with the valid function name."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
    
        if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because the function name isn't followed by an opening parenthesis. To invoke function you need to add parenthesis because it is necessary to differentiate between functions and variables."));
            return null;
        } else syntaxAnalyzer.nextToken();
    
    
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) != null)
                break;
    
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because there is incorrect syntax of an expression inside the parenthesis."));
                return null;
            }
    
            final ExpressionSyntaxAST expressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (expressionSyntaxAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the \"function invoke\" statement because an error occurred during the parsing of the expression."));
            } else this.invokedExpressions.add(expressionSyntaxAST);
    
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) != null)
                break;
            else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.COMMA) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because an expression isn't followed by an comma or an closing parenthesis."));
                return null;
            }
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because the expression section isn't ended with an closing parenthesis."));
            return null;
        }
    
        if (this.invocationType.equals(FunctionInvocation.BLOCK_INVOCATION) && syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"function invoke\" statement because it doesn't end with a semicolon but is used as a block invocation."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return this;
    }
    
    public enum FunctionInvocation
    {
        
        BLOCK_INVOCATION,
        EXPRESSION_INVOCATION
        
    }
    
}
