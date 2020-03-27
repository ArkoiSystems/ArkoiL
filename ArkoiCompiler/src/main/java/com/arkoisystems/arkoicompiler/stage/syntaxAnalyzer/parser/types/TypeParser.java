/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.IdentifierToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.TypeSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.parser.AbstractParser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A {@link AbstractParser} for the {@link TypeSyntaxAST} with which you can easily parse
 * the {@link TypeSyntaxAST} or check if the current {@link AbstractToken} is capable to
 * parse the {@link TypeSyntaxAST}.
 */
public class TypeParser extends AbstractParser
{
    
    /**
     * Parses a new {@link TypeSyntaxAST} with the given {@link AbstractSyntaxAST} as the
     * parent and the {@link SyntaxAnalyzer} as a useful class to check the syntax of the
     * AST.
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
    @NotNull
    @Override
    public Optional<TypeSyntaxAST> parse(@NotNull final AbstractSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return TypeSyntaxAST
                .builder(syntaxAnalyzer)
                .build()
                .parseAST(parentAST);
    }
    
    
    /**
     * Tests if the current {@link AbstractToken} is capable to parse this AST. It only
     * checks if the current {@link AbstractToken} is an {@link IdentifierToken}.
     * Depending on that it will return {@code false} or {@code true}.
     *
     * @param parentAST
     *         the {@link AbstractSyntaxAST} in which this AST is getting parsed.
     * @param syntaxAnalyzer
     *         the {@link SyntaxAnalyzer} which is used to call methods like {@link
     *         SyntaxAnalyzer#matchesCurrentToken(TokenType)} etc.
     *
     * @return {@code false} if the current {@link AbstractToken} isn't an {@link
     *         IdentifierToken} or {@code true} if it is.
     */
    @Override
    public boolean canParse(@NotNull final AbstractSyntaxAST parentAST, @NotNull final SyntaxAnalyzer syntaxAnalyzer) {
        return syntaxAnalyzer.currentToken().getTokenType() == TokenType.TYPE_KEYWORD;
    }
    
}