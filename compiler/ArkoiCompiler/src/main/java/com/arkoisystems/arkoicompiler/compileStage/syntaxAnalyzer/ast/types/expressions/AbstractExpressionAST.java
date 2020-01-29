package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.AbstractOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.BinaryOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.types.BinaryExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.types.ParenthesizedExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expressions.types.SimpleExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operables.AbstractOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.ExpressionParser;
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
public class AbstractExpressionAST extends AbstractAST
{
    
    public static ExpressionParser EXPRESSION_PARSER = new ExpressionParser();
    
    
    public AbstractExpressionAST() {
        super(null);
    }
    
    @Override
    public AbstractExpressionAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractOperableAST<?> abstractOperableAST = AbstractOperableAST.OPERABLE_PARSER.parse(this, syntaxAnalyzer);
        if (abstractOperableAST == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the expression because the current token isn't an operable token."));
            return null;
        }
        return parentAST.addAST(this.parseSingleExpression(abstractOperableAST, syntaxAnalyzer), syntaxAnalyzer);
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    private AbstractExpressionAST parseSingleExpression(final AbstractOperableAST<?> abstractOperableAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (!(syntaxAnalyzer.peekToken(1) instanceof AbstractOperatorToken))
            return new SimpleExpressionAST(abstractOperableAST);
        
        final AbstractAST abstractAST = this.parseTerm(syntaxAnalyzer);
        if (!(abstractAST instanceof AbstractExpressionAST))
            return null;
        return (AbstractExpressionAST) abstractAST;
    }
    
    private AbstractAST parseTerm(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST leftSideAST = this.parseFactor(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        term_loop:
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            final AbstractToken abstractToken = syntaxAnalyzer.peekToken(1);
            if (!(abstractToken instanceof BinaryOperatorToken))
                break;
            
            final BinaryOperatorToken binaryOperatorToken = (BinaryOperatorToken) abstractToken;
            switch (binaryOperatorToken.getBinaryOperatorType()) {
                case ADDITION:
                case SUBTRACTION:
                    syntaxAnalyzer.nextToken();
                    syntaxAnalyzer.nextToken();
                    
                    final AbstractAST rightSideAST = this.parseFactor(syntaxAnalyzer);
                    if (rightSideAST == null)
                        return null;
                    
                    leftSideAST = new BinaryExpressionAST(leftSideAST, binaryOperatorToken, rightSideAST);
                    break;
                default:
                    break term_loop;
            }
        }
        
        return leftSideAST;
    }
    
    // TODO: Add parenthesized collection to expressions
    
    private AbstractAST parseFactor(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST leftSideAST = this.parsePrimary(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        factor_loop:
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            final AbstractToken abstractToken = syntaxAnalyzer.peekToken(1);
            if (!(abstractToken instanceof BinaryOperatorToken))
                break;
            
            final BinaryOperatorToken binaryOperatorToken = (BinaryOperatorToken) abstractToken;
            switch (binaryOperatorToken.getBinaryOperatorType()) {
                case MULTIPLICATION:
                case DIVISION:
                case MODULO:
                    syntaxAnalyzer.nextToken();
                    syntaxAnalyzer.nextToken();
                    
                    final AbstractAST rightSideAST = this.parsePrimary(syntaxAnalyzer);
                    if (rightSideAST == null)
                        return null;
                    
                    leftSideAST = new BinaryExpressionAST(leftSideAST, binaryOperatorToken, rightSideAST);
                    break;
                default:
                    break factor_loop;
            }
        }
        
        return leftSideAST;
    }
    
    private AbstractAST parsePrimary(final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken openingParenthesis = syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.OPENING_PARENTHESIS);
        if (openingParenthesis != null) {
            syntaxAnalyzer.nextToken();
            
            final AbstractAST abstractAST = this.parseTerm(syntaxAnalyzer);
            if (!(abstractAST instanceof AbstractExpressionAST))
                return null;
            
            final AbstractToken closingParenthesis = syntaxAnalyzer.matchesNextToken(SeparatorToken.SeparatorType.CLOSING_PARENTHESIS);
            if (closingParenthesis == null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the expression there is no closing parenthesis in the expression."));
                return null;
            }
            return new ParenthesizedExpressionAST((SeparatorToken) openingParenthesis, (AbstractExpressionAST) abstractAST, (SeparatorToken) closingParenthesis);
        }
        
        if (!AbstractOperableAST.OPERABLE_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the operable token because the current token isn't operable."));
            return null;
        }
        
        return AbstractOperableAST.OPERABLE_PARSER.parse(this, syntaxAnalyzer);
    }
    
}
