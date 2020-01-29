package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.AssignmentOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.BlockAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.VariableStatementAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

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
@Setter
public class VariableDefinitionAST extends VariableStatementAST
{
    
    @Expose
    private IdentifierToken nameIdentifierToken;
    
    private int expressionPosition;
    
    @Expose
    private AbstractExpressionAST abstractExpressionAST;
    
    
    public VariableDefinitionAST() {
        super(ASTType.VARIABLE_DEFINITION);
    }
    
    @Override
    public VariableDefinitionAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(parentAST instanceof RootAST) && !(parentAST instanceof BlockAST)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AbstractToken variableIdentifierToken = syntaxAnalyzer.matchesCurrentToken(TokenType.IDENTIFIER);
        if (variableIdentifierToken == null || !variableIdentifierToken.getTokenContent().equals("val")) {
            // TODO: 1/2/2020 Throw error
            return null;
        } else this.setStart(variableIdentifierToken.getStart());
        
        final AbstractToken variableNameIdentifierToken = syntaxAnalyzer.matchesNextToken(TokenType.IDENTIFIER);
        if (variableNameIdentifierToken == null) {
            // TODO: 1/2/2020 Throw error
            return null;
        } else this.nameIdentifierToken = (IdentifierToken) variableNameIdentifierToken;
        
        if (syntaxAnalyzer.matchesNextToken(AssignmentOperatorToken.AssignmentOperatorType.ASSIGNMENT) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the variable definition because the name IdentifierToken isn't followed by an equal sign aka. \"=\"."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        this.expressionPosition = syntaxAnalyzer.getPosition();
        
        if (!syntaxAnalyzer.findSeparator(SeparatorToken.SeparatorType.SEMICOLON)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the variable definition because the statement doesn't end with a semicolon."));
            return null;
        } else this.setEnd(syntaxAnalyzer.currentToken().getEnd());
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public boolean initialize(final SyntaxAnalyzer syntaxAnalyzer) {
        final int lastPosition = syntaxAnalyzer.getPosition();
        syntaxAnalyzer.setPosition(this.expressionPosition);
        
        if(!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the variable definition statement because the expression isn't parsable."));
            return false;
        }
        
        final AbstractExpressionAST abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
        if (abstractExpressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, this.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the variable definition statement because there occurred an error during the parsing of the expression."));
            return false;
        } else this.abstractExpressionAST = abstractExpressionAST;
    
        syntaxAnalyzer.setPosition(lastPosition);
        return true;
    }
    
}
