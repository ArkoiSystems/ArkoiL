package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

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
public class ReturnStatementAST extends AbstractStatementAST
{
    
    @Expose
    private AbstractExpressionAST abstractExpressionAST;
    
    /**
     * This constructor is used to initialize the AST-Type "RETURN_STATEMENT_AST" for this
     * class. This will help to debug problems or check the AST for correct Syntax.
     */
    public ReturnStatementAST() {
        this.setAstType(ASTType.RETURN_STATEMENT_AST);
    }
    
    /**
     * This method will parse the "return" statement and checks it for the correct syntax.
     * This statement can just be used inside a BlockAST because it will define the return
     * type of it. You can't use a "this" statement in-front of it and it needs to end
     * with a semicolon.
     * <p>
     * An example for correct usage:
     * <p>
     * fun main<int>(args: string[]) = 0;
     *
     * @param parentAST
     *         The parent of this AST which just can be a BlockAST.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an ReturnStatementAST if it
     *         parsed until to the end.
     */
    @Override
    public ReturnStatementAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof BlockAST)) {
            syntaxAnalyzer.errorHandler().addError(new ASTError(parentAST, "Couldn't parse the \"return\" statement because it isn't declared inside a block."));
            return null;
        }
    
        if (syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER) == null || !syntaxAnalyzer.currentToken().getTokenContent().equals("return")) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because the parsing doesn't start with the \"return\" keyword."));
            return null;
        } else {
            this.setStart(syntaxAnalyzer.currentToken().getStart());
            syntaxAnalyzer.nextToken(); // This will skip to the followed token after the "return" keyword, so we can check if the next token is an expression.
        }
        
        if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because the keyword isn't followed by an valid expression."));
            return null;
        }
        
        final AbstractExpressionAST abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
        if (abstractExpressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because an error occurred during the parsing of the expression."));
            return null;
        } else this.abstractExpressionAST = abstractExpressionAST;
        
        if (syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.SEMICOLON) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the \"return\" statement because it doesn't end with a semicolon."));
            return null;
        }
        
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
}
