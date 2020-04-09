/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.api.ICompilerSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ExpressionParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;

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
public class ExpressionSyntaxAST extends OperableSyntaxAST
{
    
    public static ExpressionParser EXPRESSION_PARSER = new ExpressionParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableSyntaxAST operableSyntaxAST;
    
    
    public ExpressionSyntaxAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @NotNull
    @Override
    public OperableSyntaxAST parseAST(@NotNull final ICompilerSyntaxAST parentAST) {
        return this.parseAssignment(parentAST);
    }
    
    
    public OperableSyntaxAST parseAssignment(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableSyntaxAST operableSyntaxAST = this.parseAdditive(parentAST);
        if (operableSyntaxAST.isFailed())
            return operableSyntaxAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) != null) {
                operableSyntaxAST = AssignmentExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(AssignmentOperatorType.ASSIGN)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS_EQUALS) != null) {
                operableSyntaxAST = AssignmentExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(AssignmentOperatorType.ADD_ASSIGN)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS_EQUALS) != null) {
                operableSyntaxAST = AssignmentExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(AssignmentOperatorType.SUB_ASSIGN)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK_EQUALS) != null) {
                operableSyntaxAST = AssignmentExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(AssignmentOperatorType.MUL_ASSIGN)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.DIV_EQUALS) != null) {
                operableSyntaxAST = AssignmentExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(AssignmentOperatorType.DIV_ASSIGN)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PERCENT_EQUALS) != null) {
                operableSyntaxAST = AssignmentExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(AssignmentOperatorType.MOD_ASSIGN)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableSyntaxAST;
        }
    }
    
    public OperableSyntaxAST parseAdditive(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableSyntaxAST operableSyntaxAST = this.parseMultiplicative(parentAST);
        if (operableSyntaxAST.isFailed())
            return operableSyntaxAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS) != null) {
                operableSyntaxAST = BinaryExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(BinaryOperatorType.ADDITION)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS) != null) {
                operableSyntaxAST = BinaryExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(BinaryOperatorType.SUBTRACTION)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableSyntaxAST;
        }
    }
    
    protected OperableSyntaxAST parseMultiplicative(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableSyntaxAST operableSyntaxAST = this.parseExponential(parentAST);
        if (operableSyntaxAST.isFailed())
            return operableSyntaxAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK) != null) {
                operableSyntaxAST = BinaryExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(BinaryOperatorType.MULTIPLICATION)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.DIV) != null) {
                operableSyntaxAST = BinaryExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(BinaryOperatorType.DIVISION)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PERCENT) != null) {
                operableSyntaxAST = BinaryExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(BinaryOperatorType.MODULO)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableSyntaxAST;
        }
    }
    
    private OperableSyntaxAST parseExponential(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableSyntaxAST operableSyntaxAST = this.parseOperable(parentAST);
        if (operableSyntaxAST.isFailed())
            return operableSyntaxAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK_ASTERISK) != null) {
                operableSyntaxAST = BinaryExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                        .left(operableSyntaxAST)
                        .operator(BinaryOperatorType.EXPONENTIAL)
                        .start(operableSyntaxAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableSyntaxAST;
        }
    }
    
    
    // TODO: Change parenthesized expression and cast expression
    public OperableSyntaxAST parseOperable(@NotNull final ICompilerSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableSyntaxAST operableSyntaxAST = null;
        if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.MINUS_MINUS) != null)
            operableSyntaxAST = PrefixExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.PREFIX_SUB)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.PLUS_PLUS) != null)
            operableSyntaxAST = PrefixExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.PREFIX_ADD)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.MINUS) != null)
            operableSyntaxAST = PrefixExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.NEGATE)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.PLUS) != null)
            operableSyntaxAST = PrefixExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.AFFIRM)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null) {
            operableSyntaxAST = ParenthesizedExpressionSyntaxAST
                    .builder(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(parentAST);
        }
        
        if (operableSyntaxAST == null)
            operableSyntaxAST = new OperableSyntaxAST(this.getSyntaxAnalyzer(), ASTType.OPERABLE).parseAST(parentAST);
        if (operableSyntaxAST.isFailed())
            return operableSyntaxAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS_MINUS) != null) {
            return PostfixExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .left(operableSyntaxAST)
                    .operator(PostfixOperatorType.POSTFIX_SUB)
                    .start(operableSyntaxAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS_PLUS) != null) {
            return PostfixExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .left(operableSyntaxAST)
                    .operator(PostfixOperatorType.POSTFIX_ADD)
                    .start(operableSyntaxAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) != null) {
            return CastExpressionSyntaxAST.builder(this.getSyntaxAnalyzer())
                    .left(operableSyntaxAST)
                    .start(operableSyntaxAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        }
        return operableSyntaxAST;
    }
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) { }
    
}
