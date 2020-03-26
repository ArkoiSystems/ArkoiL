/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxErrorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.utils.CastOperatorType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.utils.ASTType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public class CastExpressionSyntaxAST extends AbstractExpressionSyntaxAST
{
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private CastOperatorType castOperatorType;
    
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private AbstractOperableSyntaxAST<?> leftSideOperable;
    
    
    public CastExpressionSyntaxAST(@NotNull final SyntaxAnalyzer syntaxAnalyzer, @NotNull final AbstractOperableSyntaxAST<?> leftSideOperable) {
        super(syntaxAnalyzer, ASTType.CAST_EXPRESSION);
        
        this.leftSideOperable = leftSideOperable;
        
        this.setStart(this.leftSideOperable.getStart());
    }
    
    
    @Override
    public Optional<? extends AbstractOperableSyntaxAST<?>> parseAST(@NotNull final AbstractSyntaxAST parentAST) {
        Objects.requireNonNull(this.getSyntaxAnalyzer());
        
        if (this.getSyntaxAnalyzer().matchesPeekToken(1, TokenType.IDENTIFIER, false) == null) {
            this.addError(
                    this.getSyntaxAnalyzer().getArkoiClass(),
                    this.getSyntaxAnalyzer().peekToken(1),
                    "Couldn't parse the cast expression because it doesn't start with an identifier."
            );
            return Optional.empty();
        }
        
        final IdentifierToken identifierToken = (IdentifierToken) this.getSyntaxAnalyzer().nextToken(false);
        switch (identifierToken.getTokenContent()) {
            case "i":
            case "I":
                this.castOperatorType = CastOperatorType.INTEGER;
                break;
            case "d":
            case "D":
                this.castOperatorType = CastOperatorType.DOUBLE;
                break;
            case "f":
            case "F":
                this.castOperatorType = CastOperatorType.FLOAT;
                break;
            case "b":
            case "B":
                this.castOperatorType = CastOperatorType.BYTE;
                break;
            case "s":
            case "S":
                this.castOperatorType = CastOperatorType.SHORT;
                break;
            default:
                this.addError(
                        this.getSyntaxAnalyzer().getArkoiClass(),
                        identifierToken,
                        SyntaxErrorType.EXPRESSION_CAST_WRONG_IDENTIFIER
                );
        }
        
        this.setEnd(identifierToken.getEnd());
        return Optional.of(this);
    }
    
    
    @Override
    public void printSyntaxAST(@NotNull final PrintStream printStream, @NotNull final String indents) {
        printStream.println(indents + "├── left:");
        printStream.println(indents + "│   └── " + (this.getLeftSideOperable() != null ? this.getLeftSideOperable().getClass().getSimpleName() : null));
        if (this.getLeftSideOperable() != null)
            this.getLeftSideOperable().printSyntaxAST(printStream, indents + "│       ");
        printStream.println(indents + "└── operator: " + this.getCastOperatorType());
    }
    
}
