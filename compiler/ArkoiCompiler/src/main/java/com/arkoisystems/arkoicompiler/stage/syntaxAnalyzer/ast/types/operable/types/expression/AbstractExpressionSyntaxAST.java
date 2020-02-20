/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.stage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ExpressionParser;
import lombok.Getter;

import java.io.PrintStream;

/*
    Operator Precedence:
    1. parenthesis ( (expr) )
    2. number casting ( expr fFdDsSiIbB )
    3. postfix (expr++ expr--)
    4. prefix (++expr --expr +expr -expr ~expr !expr)
    5. multiplicative (* / %)
    6. additive (+ -)
        wip - 5. shift (<< >> >>>)
    7. relational (< > <= >= is)
    8. equality (== !=)
        wip 7. bitwise AND (&)
        wip 8. bitwise inclusive OR (|)
    9. logical AND (&&)
    10. logical OR (||)
        wip 9. ternary (? :)
    11. assignment (= += -= *= /= %=)
*/
public class AbstractExpressionSyntaxAST extends AbstractOperableSyntaxAST<TypeKind>
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
    public AbstractExpressionSyntaxAST(final ASTType astType) {
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
    public AbstractExpressionSyntaxAST parseAST(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final ExpressionSyntaxAST expressionAST = this.parseExpression(syntaxAnalyzer);
        if (expressionAST == null) {
            syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, this, "Couldn't parse the expression because an error occurred during the parsing of the expression."));
            return null;
        }
        return expressionAST;
    }
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) {
    
    }
    
    private ExpressionSyntaxAST parseExpression(final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractOperableSyntaxAST<?> abstractAST = this.parseAssignment(syntaxAnalyzer);
        if (abstractAST == null)
            return null;
        return new ExpressionSyntaxAST(abstractAST);
    }
    
    // 11. assignment (= += -= *= /= %=)
    private AbstractOperableSyntaxAST<?> parseAssignment(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseLogicalOr(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
    
        final boolean equal_second = syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) != null;
        if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new AssignmentExpressionSyntaxAST(leftSideAST, AssignmentExpressionSyntaxAST.AssignmentOperator.ADD_ASSIGN, rightSideOperable);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new AssignmentExpressionSyntaxAST(leftSideAST, AssignmentExpressionSyntaxAST.AssignmentOperator.SUB_ASSIGN, rightSideOperable);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.ASTERISK) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new AssignmentExpressionSyntaxAST(leftSideAST, AssignmentExpressionSyntaxAST.AssignmentOperator.MUL_ASSIGN, rightSideOperable);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.SLASH) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new AssignmentExpressionSyntaxAST(leftSideAST, AssignmentExpressionSyntaxAST.AssignmentOperator.DIV_ASSIGN, rightSideOperable);
        } else if (equal_second && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERCENT) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new AssignmentExpressionSyntaxAST(leftSideAST, AssignmentExpressionSyntaxAST.AssignmentOperator.MOD_ASSIGN, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.EQUAL) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalOr(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new AssignmentExpressionSyntaxAST(leftSideAST, AssignmentExpressionSyntaxAST.AssignmentOperator.ASSIGN, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 10. logical OR (||)
    private AbstractOperableSyntaxAST<?> parseLogicalOr(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseLogicalAnd(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.VERTICAL_BAR) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.VERTICAL_BAR) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseLogicalAnd(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new LogicalExpressionSyntaxAST(leftSideAST, LogicalExpressionSyntaxAST.LogicalOperator.LOGICAL_OR, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 9. logical AND (&&)
    private AbstractOperableSyntaxAST<?> parseLogicalAnd(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseEquality(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.AMPERSAND) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.AMPERSAND) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseEquality(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new LogicalExpressionSyntaxAST(leftSideAST, LogicalExpressionSyntaxAST.LogicalOperator.LOGICAL_AND, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 8. equality (== !=)
    private AbstractOperableSyntaxAST<?> parseEquality(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseRelational(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.EQUAL) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseRelational(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new EqualityExpressionSyntaxAST(leftSideAST, EqualityExpressionSyntaxAST.EqualityOperator.EQUAL, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.EQUAL) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EXCLAMATION_MARK) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseRelational(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new EqualityExpressionSyntaxAST(leftSideAST, EqualityExpressionSyntaxAST.EqualityOperator.NOT_EQUAL, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 7. relational (< > <= >= is)
    private AbstractOperableSyntaxAST<?> parseRelational(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseAdditive(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        final boolean equal_second = syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) != null;
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.LESS_THAN_SIGN) != null && equal_second) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseAdditive(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new RelationalExpressionSyntaxAST(leftSideAST, RelationalExpressionSyntaxAST.RelationalOperator.LESS_EQUAL_THAN, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.GREATER_THAN_SIGN) != null && equal_second) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseAdditive(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new RelationalExpressionSyntaxAST(leftSideAST, RelationalExpressionSyntaxAST.RelationalOperator.GREATER_EQUAL_THAN, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.GREATER_THAN_SIGN) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseAdditive(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new RelationalExpressionSyntaxAST(leftSideAST, RelationalExpressionSyntaxAST.RelationalOperator.GREATER_THAN, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.GREATER_THAN_SIGN) != null) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseAdditive(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new RelationalExpressionSyntaxAST(leftSideAST, RelationalExpressionSyntaxAST.RelationalOperator.LESS_THAN, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, TokenType.IDENTIFIER) != null && syntaxAnalyzer.peekToken(1).getTokenContent().equals("is")) {
            syntaxAnalyzer.nextToken(3);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseAdditive(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new RelationalExpressionSyntaxAST(leftSideAST, RelationalExpressionSyntaxAST.RelationalOperator.IS, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 6. additive (+ -)
    private AbstractOperableSyntaxAST<?> parseAdditive(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseMultiplicative(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseMultiplicative(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new BinaryExpressionSyntaxAST(leftSideAST, BinaryExpressionSyntaxAST.BinaryOperator.ADDITION, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseMultiplicative(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new BinaryExpressionSyntaxAST(leftSideAST, BinaryExpressionSyntaxAST.BinaryOperator.SUBTRACTION, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 5. multiplicative (* / %)
    private AbstractOperableSyntaxAST<?> parseMultiplicative(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseUnary(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.ASTERISK) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseUnary(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new BinaryExpressionSyntaxAST(leftSideAST, BinaryExpressionSyntaxAST.BinaryOperator.MULTIPLICATION, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.SLASH) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseUnary(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new BinaryExpressionSyntaxAST(leftSideAST, BinaryExpressionSyntaxAST.BinaryOperator.DIVISION, rightSideOperable);
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PERCENT) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.EQUAL) == null) {
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseUnary(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
    
            leftSideAST = new BinaryExpressionSyntaxAST(leftSideAST, BinaryExpressionSyntaxAST.BinaryOperator.MODULO, rightSideOperable);
        }
        return leftSideAST;
    }
    
    // 4. prefix (++expr --expr +expr -expr ~expr !expr)
    private AbstractOperableSyntaxAST<?> parseUnary(final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.PLUS) != null && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
            return new PrefixExpressionSyntaxAST(rightSideOperable, PrefixExpressionSyntaxAST.PrefixUnaryOperator.PREFIX_ADD, start);
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.MINUS) != null && syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken(2);
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
            return new PrefixExpressionSyntaxAST(rightSideOperable, PrefixExpressionSyntaxAST.PrefixUnaryOperator.PREFIX_SUB, start);
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.PLUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken();
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
            return new PrefixExpressionSyntaxAST(rightSideOperable, PrefixExpressionSyntaxAST.PrefixUnaryOperator.AFFIRM, start);
        } else if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.MINUS) != null) {
            final int start = syntaxAnalyzer.currentToken().getStart();
            syntaxAnalyzer.nextToken();
    
            final AbstractOperableSyntaxAST<?> rightSideOperable = this.parseParenthesis(syntaxAnalyzer);
            if (rightSideOperable == null)
                return null;
            return new PrefixExpressionSyntaxAST(rightSideOperable, PrefixExpressionSyntaxAST.PrefixUnaryOperator.NEGATE, start);
        } else return this.parsePostfix(syntaxAnalyzer);
    }
    
    // 3. postfix (expr++ expr--)
    private AbstractOperableSyntaxAST<?> parsePostfix(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseNumberCast(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.PLUS) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.PLUS) != null) {
            syntaxAnalyzer.nextToken(3);
            return new PostfixExpressionSyntaxAST(leftSideAST, PostfixExpressionSyntaxAST.PostfixUnaryOperator.POSTFIX_ADD, syntaxAnalyzer.currentToken().getEnd());
        } else if (syntaxAnalyzer.matchesPeekToken(1, SymbolToken.SymbolType.MINUS) != null && syntaxAnalyzer.matchesPeekToken(2, SymbolToken.SymbolType.MINUS) != null) {
            syntaxAnalyzer.nextToken(3);
            return new PostfixExpressionSyntaxAST(leftSideAST, PostfixExpressionSyntaxAST.PostfixUnaryOperator.POSTFIX_SUB, syntaxAnalyzer.currentToken().getEnd());
        }
        return leftSideAST;
    }
    
    // 2. number casting ( expr-fFdDsSiIbB )
    private AbstractOperableSyntaxAST<?> parseNumberCast(final SyntaxAnalyzer syntaxAnalyzer) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseParenthesis(syntaxAnalyzer);
        if (leftSideAST == null)
            return null;
        
        if(syntaxAnalyzer.matchesPeekToken(1, TokenType.IDENTIFIER) != null) {
            final IdentifierToken identifierToken = (IdentifierToken) syntaxAnalyzer.peekToken(1);
            
            switch (identifierToken.getTokenContent()) {
                case "f":
                case "F":
                    syntaxAnalyzer.nextToken();
                    return new CastExpressionSyntaxAST(leftSideAST, CastExpressionSyntaxAST.CastOperator.FLOAT);
                case "d":
                case "D":
                    syntaxAnalyzer.nextToken();
                    return new CastExpressionSyntaxAST(leftSideAST, CastExpressionSyntaxAST.CastOperator.DOUBLE);
                case "s":
                case "S":
                    syntaxAnalyzer.nextToken();
                    return new CastExpressionSyntaxAST(leftSideAST, CastExpressionSyntaxAST.CastOperator.SHORT);
                case "i":
                case "I":
                    syntaxAnalyzer.nextToken();
                    return new CastExpressionSyntaxAST(leftSideAST, CastExpressionSyntaxAST.CastOperator.INTEGER);
                case "b":
                case "B":
                    syntaxAnalyzer.nextToken();
                    return new CastExpressionSyntaxAST(leftSideAST, CastExpressionSyntaxAST.CastOperator.BYTE);
                default:
                    break;
            }
        }
        return leftSideAST;
    }
    
    // 1. parenthesis ( (expr) )
    private AbstractOperableSyntaxAST<?> parseParenthesis(final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_PARENTHESIS) != null) {
            final AbstractToken openingParenthesis = syntaxAnalyzer.currentToken();
            syntaxAnalyzer.nextToken();
    
            if (!AbstractExpressionSyntaxAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the parenthesized expression because the inner expression isn't valid."));
                return null;
            }
    
            final ExpressionSyntaxAST expressionSyntaxAST = AbstractExpressionSyntaxAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (expressionSyntaxAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ParserError<>(AbstractExpressionSyntaxAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the parenthesized expression because an error occurred during the parsing of the inner expression."));
                return null;
            }
    
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.CLOSING_PARENTHESIS) == null) {
                syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the parenthesized expression because it doesn't end with a closing parenthesis."));
                return null;
            }
            return new ParenthesizedExpressionSyntaxAST((SymbolToken) openingParenthesis, expressionSyntaxAST, (SymbolToken) syntaxAnalyzer.currentToken());
        } else return this.parseOperable(syntaxAnalyzer);
    }
    
    private AbstractOperableSyntaxAST<?> parseOperable(final SyntaxAnalyzer syntaxAnalyzer) {
        if (!AbstractOperableSyntaxAST.OPERABLE_PARSER.canParse(this, syntaxAnalyzer)) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the operable token because the current token isn't operable."));
            return null;
        } else
            return AbstractOperableSyntaxAST.OPERABLE_PARSER.parse(this, syntaxAnalyzer);
    }
    
}
