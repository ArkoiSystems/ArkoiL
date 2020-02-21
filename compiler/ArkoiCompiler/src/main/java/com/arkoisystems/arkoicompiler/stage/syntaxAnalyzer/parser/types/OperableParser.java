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
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.operable.AbstractOperableSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.statement.AbstractStatementSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;

/**
 * A {@link AbstractParser} for the {@link AbstractOperableSyntaxAST} with which you can
 * easily parse the {@link AbstractOperableSyntaxAST} or check if the current {@link
 * AbstractToken} and is capable to parse a new {@link AbstractStatementSyntaxAST}.
 */
public class OperableParser extends AbstractParser<AbstractOperableSyntaxAST<?>>
{
    
    /**
     * Parses a new {@link AbstractOperableSyntaxAST} with the given {@link
     * AbstractSyntaxAST} as the parent and the {@link SyntaxAnalyzer} as a useful class
     * to check the syntax of the AST.
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code null} if an error occurred during the parsing of the {@link
     *         AbstractOperableSyntaxAST} or simply returns the parsed result.
     */
    @Override
    public AbstractOperableSyntaxAST<?> parse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return new AbstractOperableSyntaxAST<>(null).parseAST(parentAST, syntaxAnalyzer);
    }
    
    
    /**
     * Tests if the current {@link AbstractToken} is capable to parse a new {@link
     * AbstractOperableSyntaxAST}. Depending on the current {@link AbstractToken}s type
     * it will return {@code true} if it's would work or {@code false} if it wouldn't.
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code false} if the current {@link AbstractToken}s type isn't capable to
     *         parse a new {@link AbstractOperableSyntaxAST} or {@code true} if it is.
     */
    @Override
    public boolean canParse(final AbstractSyntaxAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        switch (syntaxAnalyzer.currentToken().getTokenType()) {
            case STRING_LITERAL:
            case NUMBER_LITERAL:
                return true;
            case SYMBOL:
                return syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) != null;
            case IDENTIFIER:
                return AbstractStatementSyntaxAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer);
            default:
                return false;
        }
    }
    
}