package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.types.*;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;
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

/*
    Operator Precedence:
    1. parenthesis ( (expr) )
    2. postfix (expr++ expr--)
    3. prefix (++expr --expr +expr -expr ~expr !expr)
    4. multiplicative (* / %)
    5. additive (+ -)
        wip - 5. shift (<< >> >>>)
    6. relational (< > <= >= is)
    7. equality (== !=)
        wip 7. bitwise AND (&)
        wip 8. bitwise inclusive OR (|)
    8. logical AND (&&)
    9. logical OR (||)
        wip 9. ternary (? :)
    10. assignment (= += -= *= /= %=)
*/
@Getter
public class AbstractExpressionAST<S extends AbstractSemantic<?>> extends AbstractAST<S>
{
    
    public static ExpressionParser EXPRESSION_PARSER = new ExpressionParser();
    
    /**
     * This constructor will provide the capability to set the AST-Type for the specified
     * expression type. This will help to debug problems or check the AST for correct
     * syntax.
     *
     * @param astType
     *         The AST-Type which should get set to this class.
     */
    public AbstractExpressionAST(final ASTType astType) {
        super(astType);
    }
    
    /**
     * This method will parse the AbstractExpressionAST and checks it for the correct
     * syntax. This AST can be used by a VariableDefinitionAST or when invoking functions
     * etc.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @param syntaxAnalyzer
     *         The given SyntaxAnalyzer is used for checking the syntax of the current
     *         Token list.
     *
     * @return It will return null if an error occurred or an AbstractExpressionAST if it
     *         parsed until to the end.
     */
    @Override
    public AbstractExpressionAST<?> parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final ExpressionAST expressionAST = this.parseExpression(syntaxAnalyzer);
        if (expressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, this, "Couldn't parse the expression because an error occurred during the parsing of the expression."));
            return null;
        }
        return parentAST.addAST(expressionAST, syntaxAnalyzer);
    }
    
    /**
     * This method is just overwritten because this class extends the AbstractAST class.
     * It will just return the input and doesn't check anything.
     *
     * @param toAddAST
     *         The AST which should get added to the "AbstractExpressionAST".
     * @param syntaxAnalyzer
     *         The SyntaxAnalyzer which should get used if you want to compare Tokens.
     * @param <T>
     *         The Type of the AST which should be added to the "ThisStatementAST".
     *
     * @return It will just return the input "toAddAST" because you can't add ASTs to an
     *         AbstractExpressionAST.
     */
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
    private ExpressionAST parseExpression(final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractAST<?> abstractAST = this.parseAssignment(syntaxAnalyzer);
        if (abstractAST == null)
            return null;
        return new ExpressionAST(abstractAST);
    }
    
    // 10. assignment (= += -= *= /= %=)
    private AbstractAST<?> parseAssignment(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseLogicalOr(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        final boolean equal_second = syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) != null;
        if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new AssignmentExpressionAST(leftSideAST, AssignmentExpressionAST.AssignmentOperator.ADD_ASSIGN, rightSideAST);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new AssignmentExpressionAST(leftSideAST, AssignmentExpressionAST.AssignmentOperator.SUB_ASSIGN, rightSideAST);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.ASTERISK) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new AssignmentExpressionAST(leftSideAST, AssignmentExpressionAST.AssignmentOperator.MUL_ASSIGN, rightSideAST);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.SLASH) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new AssignmentExpressionAST(leftSideAST, AssignmentExpressionAST.AssignmentOperator.DIV_ASSIGN, rightSideAST);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERCENT) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new AssignmentExpressionAST(leftSideAST, AssignmentExpressionAST.AssignmentOperator.MOD_ASSIGN, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.EQUAL) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new AssignmentExpressionAST(leftSideAST, AssignmentExpressionAST.AssignmentOperator.ASSIGN, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 9. logical OR (||)
    private AbstractAST<?> parseLogicalOr(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseLogicalAnd(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.VERTICAL_BAR) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.VERTICAL_BAR) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseLogicalAnd(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new LogicalExpressionAST(leftSideAST, LogicalExpressionAST.LogicalOperator.LOGICAL_OR, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 8. logical AND (&&)
    private AbstractAST<?> parseLogicalAnd(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseEquality(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.AMPERSAND) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.AMPERSAND) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseEquality(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new LogicalExpressionAST(leftSideAST, LogicalExpressionAST.LogicalOperator.LOGICAL_AND, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 7. equality (== !=)
    private AbstractAST<?> parseEquality(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseRelational(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.EQUAL) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseRelational(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new EqualityExpressionAST(leftSideAST, EqualityExpressionAST.EqualityOperator.EQUAL, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.EQUAL) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EXCLAMATION_MARK) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseRelational(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new EqualityExpressionAST(leftSideAST, EqualityExpressionAST.EqualityOperator.NOT_EQUAL, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 6. relational (< > <= >= is)
    private AbstractAST<?> parseRelational(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseAdditive(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        final boolean equal_second = syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) != null;
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.LESS_THAN_SIGN) != null && equal_second) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseAdditive(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new RelationalExpressionAST(leftSideAST, RelationalExpressionAST.RelationalOperator.LESS_EQUAL_THAN, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.GREATER_THAN_SIGN) != null && equal_second) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseAdditive(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new RelationalExpressionAST(leftSideAST, RelationalExpressionAST.RelationalOperator.GREATER_EQUAL_THAN, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.GREATER_THAN_SIGN) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseAdditive(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new RelationalExpressionAST(leftSideAST, RelationalExpressionAST.RelationalOperator.GREATER_THAN, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.GREATER_THAN_SIGN) != null) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseAdditive(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new RelationalExpressionAST(leftSideAST, RelationalExpressionAST.RelationalOperator.LESS_THAN, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, TokenType.IDENTIFIER) != null && syntaxAnalyzer.peekToken(1).getTokenContent().equals("is")) {
            syntaxAnalyzer.nextToken(3);
            
            final AbstractAST<?> rightSideAST = this.parseAdditive(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new RelationalExpressionAST(leftSideAST, RelationalExpressionAST.RelationalOperator.IS, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 5. additive (+ -)
    private AbstractAST<?> parseAdditive(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseMultiplicative(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null) {
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseMultiplicative(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new BinaryExpressionAST(leftSideAST, BinaryExpressionAST.BinaryOperator.ADDITION, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null) {
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseMultiplicative(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new BinaryExpressionAST(leftSideAST, BinaryExpressionAST.BinaryOperator.SUBTRACTION, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 4. multiplicative (* / %)
    private AbstractAST<?> parseMultiplicative(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseUnary(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.ASTERISK) != null) {
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseUnary(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new BinaryExpressionAST(leftSideAST, BinaryExpressionAST.BinaryOperator.MULTIPLICATION, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.SLASH) != null) {
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseUnary(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new BinaryExpressionAST(leftSideAST, BinaryExpressionAST.BinaryOperator.DIVISION, rightSideAST);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERCENT) != null) {
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseUnary(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            
            leftSideAST = new BinaryExpressionAST(leftSideAST, BinaryExpressionAST.BinaryOperator.MODULO, rightSideAST);
        }
        return leftSideAST;
    }
    
    // 3. prefix (++expr --expr +expr -expr ~expr !expr)
    private AbstractAST<?> parseUnary(final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.PLUS) != null && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            return new PrefixUnaryExpressionAST(rightSideAST, PrefixUnaryExpressionAST.PrefixUnaryOperator.PREFIX_ADD, start);
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.MINUS) != null && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            return new PrefixUnaryExpressionAST(rightSideAST, PrefixUnaryExpressionAST.PrefixUnaryOperator.PREFIX_SUB, start);
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.PLUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            return new PrefixUnaryExpressionAST(rightSideAST, PrefixUnaryExpressionAST.PrefixUnaryOperator.AFFIRM, start);
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.MINUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken(2);
            
            final AbstractAST<?> rightSideAST = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideAST == null)
                return null;
            return new PrefixUnaryExpressionAST(rightSideAST, PrefixUnaryExpressionAST.PrefixUnaryOperator.NEGATE, start);
        } else return this.parsePostfix(syntaxAnalyzer);
    }
    
    // brainfuck idk if its right need to test
    // 2. postfix (expr++ expr--)
    private AbstractAST<?> parsePostfix(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractAST<?> leftSideAST = this.parseParenthesis(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.PLUS) != null) {
            syntaxAnalyzer.nextToken(3);
            return new PostfixUnaryExpressionAST(leftSideAST, PostfixUnaryExpressionAST.PostfixUnaryOperator.POSTFIX_ADD, syntaxAnalyzer.currentToken().getEnd());
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.MINUS) != null) {
            syntaxAnalyzer.nextToken(3);
            return new PostfixUnaryExpressionAST(leftSideAST, PostfixUnaryExpressionAST.PostfixUnaryOperator.POSTFIX_SUB, syntaxAnalyzer.currentToken().getEnd());
        }
        return leftSideAST;
    }
    
    // 1. parenthesis ( (expr) )
    private AbstractAST<?> parseParenthesis(final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_PARENTHESIS) != null) {
            final AbstractToken openingParenthesis = syntaxAnalyzer.currentToken();
            syntaxAnalyzer.nextToken();
            
            if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the parenthesized expression because the inner expression isn't valid."));
                return null;
            }
            
            final AbstractExpressionAST<?> abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the parenthesized expression because an error occurred during the parsing of the inner expression."));
                return null;
            }
            
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the parenthesized expression because it doesn't end with a closing parenthesis."));
                return null;
            }
            return new ParenthesizedExpressionAST((SymbolToken) openingParenthesis, abstractExpressionAST, (SymbolToken) syntaxAnalyzer.currentToken());
        } else return this.parseOperable(syntaxAnalyzer);
    }
    
    private AbstractOperableAST<?, ?> parseOperable(final SyntaxAnalyzer syntaxAnalyzer) {
        if (!AbstractOperableAST.OPERABLE_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the operable token because the current token isn't operable."));
            return null;
        } else return AbstractOperableAST.OPERABLE_PARSER.parse(this, syntaxAnalyzer);
    }
    
}
