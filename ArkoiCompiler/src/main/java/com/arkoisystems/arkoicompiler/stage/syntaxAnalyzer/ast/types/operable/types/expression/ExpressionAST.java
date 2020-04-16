/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author єхcsє#5543 aka timo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parsers.ExpressionParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class ExpressionAST extends OperableAST
{
    
    public static ExpressionParser EXPRESSION_PARSER = new ExpressionParser();
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperableAST operableAST;
    
    
    public ExpressionAST(@Nullable final SyntaxAnalyzer syntaxAnalyzer, @NotNull final ASTType astType) {
        super(syntaxAnalyzer, astType);
    }
    
    
    @NotNull
    @Override
    public OperableAST parseAST(@NotNull final IASTNode parentAST) {
        return this.parseAssignment(parentAST);
    }
    
    
    @Override
    public void accept(@NotNull final IVisitor visitor) { }
    
    
    public OperableAST parseAssignment(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableAST operableAST = this.parseAdditive(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(AssignmentOperatorType.ASSIGN)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(AssignmentOperatorType.ADD_ASSIGN)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(AssignmentOperatorType.SUB_ASSIGN)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(AssignmentOperatorType.MUL_ASSIGN)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.DIV_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(AssignmentOperatorType.DIV_ASSIGN)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PERCENT_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(AssignmentOperatorType.MOD_ASSIGN)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    public OperableAST parseAdditive(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableAST operableAST = this.parseMultiplicative(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS) != null) {
                operableAST = BinaryExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(BinaryOperatorType.ADDITION)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS) != null) {
                operableAST = BinaryExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(BinaryOperatorType.SUBTRACTION)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    protected OperableAST parseMultiplicative(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableAST operableAST = this.parseExponential(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK) != null) {
                operableAST = BinaryExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(BinaryOperatorType.MULTIPLICATION)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.DIV) != null) {
                operableAST = BinaryExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(BinaryOperatorType.DIVISION)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PERCENT) != null) {
                operableAST = BinaryExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(BinaryOperatorType.MODULO)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    private OperableAST parseExponential(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableAST operableAST = this.parseOperable(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK_ASTERISK) != null) {
                operableAST = BinaryExpressionAST.builder(this.getSyntaxAnalyzer())
                        .left(operableAST)
                        .operator(BinaryOperatorType.EXPONENTIAL)
                        .start(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    
    // TODO: Change parenthesized expression and cast expression
    public OperableAST parseOperable(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        OperableAST operableAST = null;
        if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.MINUS_MINUS) != null)
            operableAST = PrefixExpressionAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.PREFIX_SUB)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.PLUS_PLUS) != null)
            operableAST = PrefixExpressionAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.PREFIX_ADD)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.MINUS) != null)
            operableAST = PrefixExpressionAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.NEGATE)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.PLUS) != null)
            operableAST = PrefixExpressionAST.builder(this.getSyntaxAnalyzer())
                    .operator(PrefixOperatorType.AFFIRM)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null) {
            operableAST = ParenthesizedExpressionAST
                    .builder(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(parentAST);
        }
        
        if (operableAST == null)
            operableAST = new OperableAST(this.getSyntaxAnalyzer(), ASTType.OPERABLE).parseAST(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS_MINUS) != null) {
            return PostfixExpressionAST.builder(this.getSyntaxAnalyzer())
                    .left(operableAST)
                    .operator(PostfixOperatorType.POSTFIX_SUB)
                    .start(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS_PLUS) != null) {
            return PostfixExpressionAST.builder(this.getSyntaxAnalyzer())
                    .left(operableAST)
                    .operator(PostfixOperatorType.POSTFIX_ADD)
                    .start(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) != null) {
            return CastExpressionAST.builder(this.getSyntaxAnalyzer())
                    .left(operableAST)
                    .start(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        }
        return operableAST;
    }
    
}
