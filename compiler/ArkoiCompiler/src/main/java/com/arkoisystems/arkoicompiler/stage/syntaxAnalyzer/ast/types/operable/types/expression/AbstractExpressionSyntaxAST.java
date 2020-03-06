/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ExpressionParser;

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
     *         The AST-Type which is used set to this class.
     */
    public AbstractExpressionSyntaxAST(final SyntaxAnalyzer syntaxAnalyzer, final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    /**
     * This method will parse the AbstractExpressionAST and checks it for the correct
     * syntax. This AST can be used by a VariableDefinitionAST or when invoking functions
     * etc.
     *
     * @param parentAST
     *         The parent of the AST. With it you can check for correct usage of the
     *         statement.
     * @return It will return null if an error occurred or an AbstractExpressionAST if it
     *         parsed until to the end.
     */
    @Override
    public AbstractOperableSyntaxAST<?> parseAST(final AbstractSyntaxAST parentAST) {
        final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = this.parseAssignment(this);
        if (abstractOperableSyntaxAST == null)
            return null;
        return new ExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST);
    }
    
    private AbstractOperableSyntaxAST<?> parseAssignment(final AbstractSyntaxAST parentAST) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseAdditive(parentAST);
        if (leftSideAST == null)
            return null;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null) {
                this.getSyntaxAnalyzer().nextToken();
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL, false) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            "Couldn't parse the add assignment expression because there is a whitespace between the operators."
                    );
                    return null;
                } else this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseAdditive(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, AssignmentOperatorType.ADD_ASSIGN, rightSideAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null) {
                this.getSyntaxAnalyzer().nextToken();
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL, false) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            "Couldn't parse the sub assignment expression because there is a whitespace between the operators."
                    );
                    return null;
                } else this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseAdditive(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, AssignmentOperatorType.SUB_ASSIGN, rightSideAST);
            } else return leftSideAST;
        }
    }
    
    private AbstractOperableSyntaxAST<?> parseAdditive(final AbstractSyntaxAST parentAST) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseMultiplicative(parentAST);
        if (leftSideAST == null)
            return null;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseMultiplicative(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, BinaryOperatorType.ADDITION, rightSideAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseMultiplicative(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, BinaryOperatorType.SUBTRACTION, rightSideAST);
            } else return leftSideAST;
        }
    }
    
    private AbstractOperableSyntaxAST<?> parseMultiplicative(final AbstractSyntaxAST parentAST) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseExponential(parentAST);
        if (leftSideAST == null)
            return null;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.ASTERISK) == null) {
                this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseExponential(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, BinaryOperatorType.MULTIPLICATION, rightSideAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.SLASH) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseExponential(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, BinaryOperatorType.DIVISION, rightSideAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERCENT) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseExponential(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, BinaryOperatorType.MODULO, rightSideAST);
            } else return leftSideAST;
        }
    }
    
    private AbstractOperableSyntaxAST<?> parseExponential(final AbstractSyntaxAST parentAST) {
        AbstractOperableSyntaxAST<?> leftSideAST = this.parseOperable(parentAST);
        if (leftSideAST == null)
            return null;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.ASTERISK) != null) {
                this.getSyntaxAnalyzer().nextToken();
                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK, false) == null) {
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            this.getSyntaxAnalyzer().currentToken(),
                            "Couldn't parse the exponential expression because there is a whitespace between the operators."
                    );
                    return null;
                } else this.getSyntaxAnalyzer().nextToken(2);
                
                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseOperable(parentAST);
                if (rightSideAST == null)
                    return null;
                
                leftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), leftSideAST, BinaryOperatorType.EXPONENTIAL, rightSideAST);
            } else return leftSideAST;
        }
    }
    
    private AbstractOperableSyntaxAST<?> parseOperable(final AbstractSyntaxAST parentAST) {
        AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = null;
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.MINUS) != null &&
                this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS, false) != null) {
            final int start = this.getSyntaxAnalyzer().currentToken().getStart();
            this.getSyntaxAnalyzer().nextToken(2);
            
            final AbstractOperableSyntaxAST<?> rightSideAST = this.parseOperable(parentAST);
            if (rightSideAST == null)
                return null;
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), start, rightSideAST, PrefixOperatorType.PREFIX_SUB);
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.PLUS) != null &&
                this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS, false) != null) {
            final int start = this.getSyntaxAnalyzer().currentToken().getStart();
            this.getSyntaxAnalyzer().nextToken(2);
            
            final AbstractOperableSyntaxAST<?> rightSideAST = this.parseOperable(parentAST);
            if (rightSideAST == null)
                return null;
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), start, rightSideAST, PrefixOperatorType.PREFIX_ADD);
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.MINUS) != null) {
            final int start = this.getSyntaxAnalyzer().currentToken().getStart();
            this.getSyntaxAnalyzer().nextToken();
            
            final AbstractOperableSyntaxAST<?> rightSideAST = this.parseOperable(parentAST);
            if (rightSideAST == null)
                return null;
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), start, rightSideAST, PrefixOperatorType.NEGATE);
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.PLUS) != null) {
            final int start = this.getSyntaxAnalyzer().currentToken().getStart();
            this.getSyntaxAnalyzer().nextToken();
            
            final AbstractOperableSyntaxAST<?> rightSideAST = this.parseOperable(parentAST);
            if (rightSideAST == null)
                return null;
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), start, rightSideAST, PrefixOperatorType.AFFIRM);
        } else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null) {
            final SymbolToken openingParenthesis = (SymbolToken) this.getSyntaxAnalyzer().currentToken();
            
            this.getSyntaxAnalyzer().nextToken();
            final AbstractOperableSyntaxAST<?> parenthesizedOperable = this.parseAssignment(parentAST);
            if (parenthesizedOperable == null)
                return null;
            final ExpressionSyntaxAST expressionSyntaxAST = new ExpressionSyntaxAST(this.getSyntaxAnalyzer(), parenthesizedOperable);
            
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.CLOSING_PARENTHESIS) == null) {
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        this.getSyntaxAnalyzer().currentToken(),
                        "Couldn't parse the parenthesized expression because it doesn't end with a closing parenthesis."
                );
                return null;
            }
            abstractOperableSyntaxAST = new ParenthesizedExpressionSyntaxAST(this.getSyntaxAnalyzer(), openingParenthesis, expressionSyntaxAST, (SymbolToken) this.getSyntaxAnalyzer().nextToken());
        }
        
        if (abstractOperableSyntaxAST == null)
            abstractOperableSyntaxAST = new AbstractOperableSyntaxAST<>(this.getSyntaxAnalyzer(), null).parseAST(parentAST);
        if (abstractOperableSyntaxAST == null)
            return null;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null) {
            this.getSyntaxAnalyzer().nextToken(1);
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS, false) != null) {
                this.getSyntaxAnalyzer().nextToken();
                return new PostfixExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, PostfixOperatorType.POSTFIX_SUB, this.getSyntaxAnalyzer().currentToken().getEnd());
            } else this.getSyntaxAnalyzer().undoToken();
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null) {
            this.getSyntaxAnalyzer().nextToken(1);
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS, false) != null) {
                this.getSyntaxAnalyzer().nextToken();
                return new PostfixExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, PostfixOperatorType.POSTFIX_ADD, this.getSyntaxAnalyzer().currentToken().getEnd());
            } else this.getSyntaxAnalyzer().undoToken();
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) != null) {
            final IdentifierToken identifierToken = (IdentifierToken) this.getSyntaxAnalyzer().nextToken(false);
            switch (identifierToken.getTokenContent()) {
                case "i":
                case "I":
                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.INTEGER, this.getSyntaxAnalyzer().currentToken().getEnd());
                case "d":
                case "D":
                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.DOUBLE, this.getSyntaxAnalyzer().currentToken().getEnd());
                case "f":
                case "F":
                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.FLOAT, this.getSyntaxAnalyzer().currentToken().getEnd());
                case "b":
                case "B":
                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.BYTE, this.getSyntaxAnalyzer().currentToken().getEnd());
                case "s":
                case "S":
                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.SHORT, this.getSyntaxAnalyzer().currentToken().getEnd());
                default:
                    this.addError(
                            this.getSyntaxAnalyzer().getArkoiClass(),
                            identifierToken,
                            "Couldn't parse the cast expression because it contains a unsupported identifier."
                    );
                    return null;
            }
        }
        return abstractOperableSyntaxAST;
    }
    
    @Override
    public void printSyntaxAST(final PrintStream printStream, final String indents) { }
    
}
