/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.AbstractExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.types.expression.types.ExpressionSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import lombok.NonNull;

import java.util.Optional;

/**
 * A {@link AbstractParser} for the {@link ExpressionSyntaxAST} with which you can easily
 * parse the {@link ExpressionSyntaxAST} or check if the current {@link AbstractToken} is
 * capable to parse the {@link ExpressionSyntaxAST}.
 */
public class ExpressionParser extends AbstractParser
{
    
    /**
     * Parses a new {@link ExpressionSyntaxAST} with the given {@link AbstractSyntaxAST}
     * as the parent and the {@link SyntaxAnalyzer} as a useful class to check the syntax
     * of the AST. Also it will return {@code null} if the output isn't and {@link
     * ExpressionSyntaxAST} (which should never happens).
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code null} if an error occurred during the parsing of the {@link
     *         TypeSyntaxAST} or simply returns the parsed result.
     */
    @Override
    public Optional<ExpressionSyntaxAST> parse(@NonNull final AbstractSyntaxAST parentAST, @NonNull final SyntaxAnalyzer syntaxAnalyzer) {
        final Optional<? extends AbstractOperableSyntaxAST<?>> optionalAbstractOperableSyntaxAST = new AbstractExpressionSyntaxAST(syntaxAnalyzer, null).parseAST(parentAST);
        if (optionalAbstractOperableSyntaxAST.isEmpty())
            return Optional.empty();
        
        final AbstractOperableSyntaxAST<?> abstractOperableSyntaxAST = optionalAbstractOperableSyntaxAST.get();
        if (!(abstractOperableSyntaxAST instanceof ExpressionSyntaxAST)) {
            abstractOperableSyntaxAST.addError(
                    syntaxAnalyzer.getArkoiClass(),
                    abstractOperableSyntaxAST,
                    "Couldn't parse the expression because the result isn't an ExpressionAST."
            );
            return Optional.empty();
        }
        return Optional.of((ExpressionSyntaxAST) abstractOperableSyntaxAST);
    }
    
    
    /**
     * Tests if the current {@link AbstractToken} is capable to parse this AST. It will
     * check for the type of the current {@link AbstractToken} and depending on that it
     * will return {@code false} or {@code true}.
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code false} if the current {@link AbstractToken}s type isn't supported
     *         or {@code true} if it is.
     */
    @Override
    public boolean canParse(@NonNull final AbstractSyntaxAST parentAST, @NonNull final SyntaxAnalyzer syntaxAnalyzer) {
        switch (syntaxAnalyzer.currentToken().getTokenType()) {
            case STRING_LITERAL:
            case NUMBER_LITERAL:
                return true;
            case SYMBOL:
                final SymbolToken symbolToken = (SymbolToken) syntaxAnalyzer.currentToken();
                switch (symbolToken.getSymbolType()) {
                    case OPENING_PARENTHESIS:
                    case PLUS:
                    case MINUS:
                    case EXCLAMATION_MARK:
                        return true;
                    default:
                        return false;
                }
            case IDENTIFIER:
                return AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer);
            default:
                return false;
        }
    }
    
}