package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
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
    private final FunctionDefinitionAST invokedFunction;
    
    @Expose
    private final List<AbstractExpressionAST> invokeArguments;
    
    private int argumentsPosition;
    
    public FunctionInvokeAST(final FunctionDefinitionAST invokedFunction) {
        super(ASTType.FUNCTION_INVOKE);
        
        this.invokeArguments = new ArrayList<>();
        this.invokedFunction = invokedFunction;
    }
    
    @Override
    public AbstractStatementAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the function invoke because it isn't inside a block."));
            return null;
        }
        
        if (syntaxAnalyzer.currentToken().getTokenType() != TokenType.IDENTIFIER) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function invoke because the parsing doesn't start with the function IdentifierToken."));
            return null;
        } else this.setStart(syntaxAnalyzer.currentToken().getStart());
        
        if (this.invokedFunction == null) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the function invoke because the target function is null."));
            return null;
        }
    
        // TODO: 1/15/2020 This wont work with function invokes with no arguments.
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.OPENING_PARENTHESIS) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function invoke because the function name doesn't get followed by an opening parenthesis."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        this.argumentsPosition = syntaxAnalyzer.getPosition();
        
        if (!syntaxAnalyzer.findMatchingSeparator(this, SeparatorToken.SeparatorType.OPENING_PARENTHESIS)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function invoke because there is no closing parenthesis."));
            return null;
        }
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function invoke because the statement doesn't end with a semicolon."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
    
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public boolean initialize(SyntaxAnalyzer syntaxAnalyzer) {
        final int lastPosition = syntaxAnalyzer.getPosition();
        syntaxAnalyzer.setPosition(this.argumentsPosition);
        
        for (int index = 0; index < this.invokedFunction.getFunctionArguments().size(); index++) {
            if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the function invoke because an error occured during the parsing of the expression of the %s argument", index));
                return false;
            }
    
            // TODO: 1/17/2020 Fix double insertions into arguments list
            
            final AbstractExpressionAST abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the function invoke because an error occured during the parsing of the expression of the %s argument", index));
                return false;
            } else this.invokeArguments.add(abstractExpressionAST);
            
            if (index != this.invokedFunction.getFunctionArguments().size() - 1)
                if(syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.COMMA) == null) {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the function invoke because thethere is no comma between the current and next argument."));
                    return false;
                }
        }
        
        syntaxAnalyzer.setPosition(lastPosition);
        return true;
    }
    
}
