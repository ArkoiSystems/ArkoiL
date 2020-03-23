/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PrefixOperatorType;
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
        return Optional.empty();
    }
    
    
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAssignment(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseAdditive(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.EQUAL) != null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.ASSIGN).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) != null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.ADD_ASSIGN).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) != null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.SUB_ASSIGN).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) != null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.MUL_ASSIGN).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.SLASH) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) != null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.DIV_ASSIGN).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERCENT) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) != null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new AssignmentExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), AssignmentOperatorType.MOD_ASSIGN).parseAST(parentAST);
            } else return optionalLeftSideAST;
        }
    }
    
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAdditive(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseMultiplicative(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.ADDITION).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.SUBTRACTION).parseAST(parentAST);
            } else return optionalLeftSideAST;
        }
    }
    
    protected Optional<? extends AbstractOperableSyntaxAST<?>> parseMultiplicative(final AbstractSyntaxAST parentAST) {
        Optional<? extends AbstractOperableSyntaxAST<?>> optionalLeftSideAST = this.parseExponential(parentAST);
        if (optionalLeftSideAST.isEmpty())
            return Optional.empty();
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.ASTERISK) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.ASTERISK) == null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.MULTIPLICATION).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.SLASH) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
                if(optionalLeftSideAST.isEmpty())
                    return Optional.empty();
                optionalLeftSideAST = new BinaryExpressionSyntaxAST(this.getSyntaxAnalyzer(), optionalLeftSideAST.get(), BinaryOperatorType.DIVISION).parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PERCENT) != null &&
                    this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.EQUAL) == null) {
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
    
        if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null) {
            abstractOperableSyntaxAST = ParenthesizedExpressionSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(parentAST);
            this.getSyntaxAnalyzer().nextToken(); // TODO: Check if I need to remove this
        }
    
        if (abstractOperableSyntaxAST.isEmpty())
            abstractOperableSyntaxAST = new AbstractOperableSyntaxAST<>(this.getSyntaxAnalyzer(), null).parseAST(parentAST);
        if (abstractOperableSyntaxAST.isEmpty())
            return Optional.empty();
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.MINUS) != null &&
                this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.MINUS) != null) {
            abstractOperableSyntaxAST = new PostfixExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST.get(), PostfixOperatorType.POSTFIX_SUB).parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, SymbolType.PLUS) != null &&
                this.getSyntaxAnalyzer().matchesPeekToken(2, SymbolType.PLUS) != null) {
            abstractOperableSyntaxAST = new PostfixExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST.get(), PostfixOperatorType.POSTFIX_ADD).parseAST(parentAST);
        } // TODO: Check if I need to change this
    
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) != null)
            return new CastExpressionSyntaxAST(this.getSyntaxAnalyzer(), abstractOperableSyntaxAST.get()).parseAST(parentAST);
        return abstractOperableSyntaxAST;
    }
    
    @Override
    public void printSyntaxAST(@NonNull final PrintStream printStream, @NonNull final String indents) { }
    
}
