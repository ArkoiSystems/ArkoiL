/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.TypeKind;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types.ExpressionParser;
import lombok.NonNull;

import java.io.PrintStream;
import java.util.Optional;

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
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NonNull final AbstractSyntaxAST parentAST) {
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalAbstractOperableSyntaxAST = this.parseAssignment(this);
        if (optionalAbstractOperableSyntaxAST.isEmpty())
            return Optional.empty();
        return this.isFailed() ? Optional.empty() : Optional.of(new ExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalAbstractOperableSyntaxAST.get()));
    }
    
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAssignment(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseAdditive(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null) {
                //                this.getSyntaxAnalyzer().nextToken();
                //                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL, false) == null) {
                //                    this.addError(
                //                            this.getSyntaxAnalyzer().getArkoiClass(),
                //                            this.getSyntaxAnalyzer().currentToken().getStart(),
                //                            this.getSyntaxAnalyzer().nextToken().getEnd(),
                //                            SyntaxErrorType.EXPRESSION_ADD_ASSIGNMENT_SEPARATED
                //                    );
                //                    this.getSyntaxAnalyzer().nextToken();
                //                } else this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseAdditive(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.ADD_ASSIGN).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null) {
                //                this.getSyntaxAnalyzer().nextToken();
                //                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL, false) == null) {
                //                    this.addError(
                //                            this.getSyntaxAnalyzer().getArkoiClass(),
                //                            this.getSyntaxAnalyzer().currentToken().getStart(),
                //                            this.getSyntaxAnalyzer().nextToken().getEnd(),
                //                            SyntaxErrorType.EXPRESSION_SUB_ASSIGNMENT_SEPARATED
                //                    );
                //                    this.getSyntaxAnalyzer().nextToken();
                //                } else this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseAdditive(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.SUB_ASSIGN).parseAST(parentAST);
            } else return optionalLeftSideAST;
        }
    }
    
    private Optional<? extends AbstractOperableSyntaxAST<?>> parseAdditive(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseMultiplicative(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                //                this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseMultiplicative(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.ADDITION).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                //                this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseMultiplicative(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.SUBTRACTION).parseAST(parentAST);
            } else return optionalLeftSideAST;
        }
    }
    
    private Optional<? extends AbstractOperableSyntaxAST<?>> parseMultiplicative(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseExponential(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.ASTERISK) == null) {
                //                this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseExponential(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.MULTIPLICATION).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.SLASH) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                //                this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseExponential(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.DIVISION).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERCENT) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                //                this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final AbstractOperableSyntaxAST<?> rightSideAST = this.parseExponential(parentAST);
                //                if (rightSideAST == null)
                //                    return null;
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.MODULO).parseAST(parentAST);
            } else return optionalLeftSideAST;
        }
    }
    
    private Optional<? extends AbstractOperableSyntaxAST<?>> parseExponential(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseOperable(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.ASTERISK) != null) {
                //                this.getSyntaxAnalyzer().nextToken();
                //                if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK, false) == null) {
                //                    this.addError(
                //                            this.getSyntaxAnalyzer().getArkoiClass(),
                //                            this.getSyntaxAnalyzer().currentToken().getStart(),
                //                            this.getSyntaxAnalyzer().nextToken().getEnd(),
                //                            SyntaxErrorType.EXPRESSION_EXPONENTIAL_OPERABLE_SEPARATED
                //                    );
                //                    this.getSyntaxAnalyzer().nextToken();
                //                } else this.getSyntaxAnalyzer().nextToken(2);
                //
                //                final Optional<? extends AbstractOperableSyntaxAST<?>> optionalRightSideAST = this.parseOperable(parentAST);
                //                if (optionalRightSideAST.isEmpty())
                //                    return Optional.empty();
                //
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
    
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.EXPONENTIAL).parseAST(parentAST);
            } else return optionalLeftSideAST;
        }
    }
    
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseOperable(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> abstractOperableSyntaxAST = Optional.empty();
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.MINUS) != null &&
                this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null)
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), PrefixOperatorType.PREFIX_SUB).parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.PLUS) != null &&
                this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null)
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), PrefixOperatorType.PREFIX_ADD).parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.MINUS) != null)
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), PrefixOperatorType.NEGATE).parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.PLUS) != null)
            abstractOperableSyntaxAST = new PrefixExpressionSyntaxAST(this.getSyntaxAnalyzer(), PrefixOperatorType.AFFIRM).parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null)
            abstractOperableSyntaxAST = new ParenthesizedExpressionSyntaxAST(this.getSyntaxAnalyzer()).parseOperable(parentAST);
        
        if (abstractOperableSyntaxAST.isEmpty())
            abstractOperableSyntaxAST = new AbstractOperableSyntaxAST<>(this.getSyntaxAnalyzer(), null).parseAST(parentAST);
        if (abstractOperableSyntaxAST.isEmpty())
            return Optional.empty();
        
        //        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null) {
        //            this.getSyntaxAnalyzer().nextToken(1);
        //            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS, false) != null) {
        //                this.getSyntaxAnalyzer().nextToken();
        //                return new PostfixExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, PostfixOperatorType.POSTFIX_SUB, this.getSyntaxAnalyzer().currentToken().getEnd());
        //            } else this.getSyntaxAnalyzer().undoToken();
        //        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null) {
        //            this.getSyntaxAnalyzer().nextToken(1);
        //            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS, false) != null) {
        //                this.getSyntaxAnalyzer().nextToken();
        //                return new PostfixExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, PostfixOperatorType.POSTFIX_ADD, this.getSyntaxAnalyzer().currentToken().getEnd());
        //            } else this.getSyntaxAnalyzer().undoToken();
        //        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) != null) {
        //            final IdentifierToken identifierToken = (IdentifierToken) this.getSyntaxAnalyzer().nextToken(false);
        //            switch (identifierToken.getTokenContent()) {
        //                case "i":
        //                case "I":
        //                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.INTEGER, this.getSyntaxAnalyzer().currentToken().getEnd());
        //                case "d":
        //                case "D":
        //                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.DOUBLE, this.getSyntaxAnalyzer().currentToken().getEnd());
        //                case "f":
        //                case "F":
        //                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.FLOAT, this.getSyntaxAnalyzer().currentToken().getEnd());
        //                case "b":
        //                case "B":
        //                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.BYTE, this.getSyntaxAnalyzer().currentToken().getEnd());
        //                case "s":
        //                case "S":
        //                    return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST, CastOperatorType.SHORT, this.getSyntaxAnalyzer().currentToken().getEnd());
        //                default:
        //                    this.addError(
        //                            this.getSyntaxAnalyzer().getArkoiClass(),
        //                            identifierToken,
        //                            SyntaxErrorType.EXPRESSION_CAST_WRONG_IDENTIFIER
        //                    );
        //            }
        //        }
        return abstractOperableSyntaxAST;
    }
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) { }
    
}
