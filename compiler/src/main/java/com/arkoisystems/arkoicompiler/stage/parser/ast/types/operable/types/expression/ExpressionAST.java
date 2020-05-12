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
package com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression;

import com.arkoisystems.arkoicompiler.api.IASTNode;
import com.arkoisystems.arkoicompiler.api.IVisitor;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.parser.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.OperableAST;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.*;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.AssignmentOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.BinaryOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.PostfixOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.operable.types.expression.types.operators.PrefixOperatorType;
import com.arkoisystems.arkoicompiler.stage.parser.ast.utils.ASTType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TypeKind;
import com.arkoisystems.arkoicompiler.stage.parser.parsers.ExpressionParser;
import lombok.Builder;
import lombok.Getter;
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
    @Nullable
    private final OperableAST operableAST;
    
    @Builder(builderMethodName = "expressionBuilder")
    protected ExpressionAST(
            @Nullable final SyntaxAnalyzer syntaxAnalyzer,
            @Nullable final OperableAST operableAST,
            @Nullable final ArkoiToken startToken,
            @Nullable final ArkoiToken endToken,
            @NotNull final ASTType astType
    ) {
        super(syntaxAnalyzer, startToken, endToken, astType);
        
        this.operableAST = operableAST;
    }
    
    @NotNull
    @Override
    public OperableAST parseAST(@NotNull final IASTNode parentAST) {
        return this.parseAssignment(parentAST);
    }
    
    @Override
    public void accept(@NotNull final IVisitor<?> visitor) { }
    
    @Override
    public @NotNull TypeKind getTypeKind() {
        throw new NullPointerException(this.toString());
    }
    
    @NotNull
    public OperableAST parseAssignment(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        OperableAST operableAST = this.parseAdditive(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .assignmentOperatorType(AssignmentOperatorType.ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .assignmentOperatorType(AssignmentOperatorType.ADD_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .assignmentOperatorType(AssignmentOperatorType.SUB_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .assignmentOperatorType(AssignmentOperatorType.MUL_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.SLASH_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .assignmentOperatorType(AssignmentOperatorType.DIV_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PERCENT_EQUALS) != null) {
                operableAST = AssignmentExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .assignmentOperatorType(AssignmentOperatorType.MOD_ASSIGN)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    public OperableAST parseAdditive(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        OperableAST operableAST = this.parseMultiplicative(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS) != null) {
                operableAST = BinaryExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .binaryOperatorType(BinaryOperatorType.ADDITION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS) != null) {
                operableAST = BinaryExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .binaryOperatorType(BinaryOperatorType.SUBTRACTION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    protected OperableAST parseMultiplicative(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        OperableAST operableAST = this.parseExponential(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK) != null) {
                operableAST = BinaryExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .binaryOperatorType(BinaryOperatorType.MULTIPLICATION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.SLASH) != null) {
                operableAST = BinaryExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .binaryOperatorType(BinaryOperatorType.DIVISION)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PERCENT) != null) {
                operableAST = BinaryExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .binaryOperatorType(BinaryOperatorType.MODULO)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    @NotNull
    private OperableAST parseExponential(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        OperableAST operableAST = this.parseOperable(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        while (true) {
            if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.ASTERISK_ASTERISK) != null) {
                operableAST = BinaryExpressionAST.builder()
                        .syntaxAnalyzer(this.getSyntaxAnalyzer())
                        .leftSideOperable(operableAST)
                        .binaryOperatorType(BinaryOperatorType.EXPONENTIAL)
                        .startToken(operableAST.getStartToken())
                        .build()
                        .parseAST(parentAST);
            } else return operableAST;
        }
    }
    
    // TODO: Change parenthesized expression and cast expression
    @NotNull
    public OperableAST parseOperable(@NotNull final IASTNode parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer(), "syntaxAnalyzer must not be null.");
        
        OperableAST operableAST = null;
        if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.MINUS_MINUS) != null)
            operableAST = PrefixExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .prefixOperatorType(PrefixOperatorType.PREFIX_SUB)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.PLUS_PLUS) != null)
            operableAST = PrefixExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .prefixOperatorType(PrefixOperatorType.PREFIX_ADD)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.MINUS) != null)
            operableAST = PrefixExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .prefixOperatorType(PrefixOperatorType.NEGATE)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(OperatorType.PLUS) != null)
            operableAST = PrefixExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .prefixOperatorType(PrefixOperatorType.AFFIRM)
                    .build()
                    .parseAST(parentAST);
        else if (this.getSyntaxAnalyzer().matchesCurrentToken(SymbolType.OPENING_PARENTHESIS) != null) {
            operableAST = ParenthesizedExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .build()
                    .parseAST(parentAST);
        }
        
        if (operableAST == null)
            operableAST = OperableAST.operableBuilder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .astType(ASTType.OPERABLE)
                    .build()
                    .parseAST(parentAST);
        if (operableAST.isFailed())
            return operableAST;
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.MINUS_MINUS) != null) {
            return PostfixExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .leftSideOperable(operableAST)
                    .postfixOperatorType(PostfixOperatorType.POSTFIX_SUB)
                    .startToken(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, OperatorType.PLUS_PLUS) != null) {
            return PostfixExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .leftSideOperable(operableAST)
                    .postfixOperatorType(PostfixOperatorType.POSTFIX_ADD)
                    .startToken(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        } else if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) != null) {
            return CastExpressionAST.builder()
                    .syntaxAnalyzer(this.getSyntaxAnalyzer())
                    .leftSideOperable(operableAST)
                    .startToken(operableAST.getStartToken())
                    .build()
                    .parseAST(parentAST);
        }
        return operableAST;
    }
    
}
