/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 7, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class is an implementation of the {@link AbstractStage}. It will parse an AST
 * (Abstract Syntax Tree) with help of the parsed {@link AbstractToken} list of the {@link
 * LexicalAnalyzer}.
 */
public class SyntaxAnalyzer extends AbstractStage
{
    
    /**
     * This defines the {@link ArkoiClass} in which the {@link SyntaxAnalyzer} got
     * created.
     */
    @Getter
    @NotNull
    private final ArkoiClass arkoiClass;
    
    
    /**
     * The {@link SyntaxErrorHandler} is used for errors which are happening through the
     * process of parsing the {@link AbstractSyntaxAST}s.
     */
    @Getter
    @NotNull
    private final SyntaxErrorHandler errorHandler = new SyntaxErrorHandler();
    
    
    /**
     * The {@link RootSyntaxAST} which is getting parsed directly at the beginning.
     */
    @Getter
    @NotNull
    private final RootSyntaxAST rootSyntaxAST = new RootSyntaxAST(this);
    
    
    /**
     * The {@link AbstractToken[]} which is used to get current, peeked, next tokens
     * without needing to use a {@link List} or something else.
     */
    @Getter
    @NotNull
    private AbstractToken[] tokens = new AbstractToken[0];
    
    
    /**
     * The current token position of the {@link SyntaxAnalyzer}. Used for methods like
     * {@link SyntaxAnalyzer#currentToken()} or {@link SyntaxAnalyzer#peekToken(int)}.
     */
    @Getter
    private int position;
    
    
    /**
     * Constructs a new {@link SyntaxAnalyzer} with the given parameters. It will set the
     * {@link ArkoiClass} it got created in and also the {@link SyntaxErrorHandler} and
     * {@link RootSyntaxAST} are getting created.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} in which the {@link SyntaxAnalyzer} got created.
     */
    public SyntaxAnalyzer(@NotNull final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will run this stage.
     * It will reset the current position and the tokens array. Also it will return the
     * {@link RootSyntaxAST} if the parsing went well or {@code null} if an error
     * occurred.
     *
     * @return the {@link RootSyntaxAST} if everything went well or {@code null} if an
     *         error occurred.
     */
    @Override
    public boolean processStage() {
        this.tokens = this.arkoiClass.getLexicalAnalyzer().getTokens();
        this.position = 0;
        
        return this.rootSyntaxAST.parseAST(null).isPresent();
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will return the
     * created {@link SyntaxErrorHandler}.
     *
     * @return the {@link SyntaxErrorHandler} which got created in the constructor.
     */
    @NotNull
    @Override
    public SyntaxErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    
    @Nullable
    public SymbolToken matchesCurrentToken(@NotNull final SymbolType symbolType) {
        return this.matchesCurrentToken(symbolType, true);
    }
    
    
    @Nullable
    public SymbolToken matchesCurrentToken(@NotNull final SymbolType symbolType, final boolean skipWhitespaces) {
        final AbstractToken currentToken = this.currentToken(skipWhitespaces);
        if (!(currentToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) currentToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    @Nullable
    public SymbolToken matchesNextToken(@NotNull final SymbolType symbolType) {
        return this.matchesNextToken(symbolType, true);
    }
    
    
    @Nullable
    public SymbolToken matchesNextToken(@NotNull final SymbolType symbolType, final boolean skipWhitespaces) {
        final AbstractToken nextToken = this.nextToken(skipWhitespaces);
        if (!(nextToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) nextToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    @Nullable
    public SymbolToken matchesPeekToken(final int offset, @NotNull final SymbolType symbolType) {
        return this.matchesPeekToken(offset, symbolType, true);
    }
    
    
    @Nullable
    public SymbolToken matchesPeekToken(final int offset, @NotNull final SymbolType symbolType, final boolean skipWhitespaces) {
        if (offset == 0)
            return this.matchesCurrentToken(symbolType, skipWhitespaces);
        
        final AbstractToken peekToken = this.peekToken(offset, skipWhitespaces);
        if (!(peekToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) peekToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    @Nullable
    public AbstractToken matchesCurrentToken(@NotNull final TokenType tokenType) {
        return this.matchesCurrentToken(tokenType, true);
    }
    
    
    @Nullable
    public AbstractToken matchesCurrentToken(@NotNull final TokenType tokenType, final boolean skipWhitespaces) {
        final AbstractToken currentToken = this.currentToken(skipWhitespaces);
        if (currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    
    @Nullable
    public AbstractToken matchesNextToken(@NotNull final TokenType tokenType) {
        return this.matchesNextToken(tokenType, true);
    }
    
    
    @Nullable
    public AbstractToken matchesNextToken(@NotNull final TokenType tokenType, final boolean skipWhitespaces) {
        final AbstractToken nextToken = this.nextToken(skipWhitespaces);
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    
    @Nullable
    public AbstractToken matchesPeekToken(final int offset, @NotNull final TokenType tokenType) {
        return this.matchesPeekToken(offset, tokenType, true);
    }
    
    
    @Nullable
    public AbstractToken matchesPeekToken(final int offset, @NotNull final TokenType tokenType, final boolean skipWhitespaces) {
        if (offset == 0)
            return this.matchesCurrentToken(tokenType, skipWhitespaces);
        
        final AbstractToken peekToken = this.peekToken(offset, skipWhitespaces);
        if (peekToken == null || peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    
    @NotNull
    public AbstractToken peekToken(final int offset) {
        return this.peekToken(offset, true);
    }
    
    
    @NotNull
    public AbstractToken peekToken(final int offset, final boolean skipWhitespaces) {
        AbstractToken abstractToken = this.nextToken(offset, skipWhitespaces);
        this.undoToken(offset, skipWhitespaces);
        return abstractToken;
    }
    
    
    @NotNull
    public AbstractToken currentToken() {
        return this.currentToken(true);
    }
    
    
    @NotNull
    public AbstractToken currentToken(final boolean skipWhitespaces) {
        if (skipWhitespaces) {
            while (this.position < this.tokens.length) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE)
                    break;
                this.position++;
            }
        }
        
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[position];
    }
    
    
    @Nullable
    public AbstractToken nextToken(final int offset) {
        return this.nextToken(offset, true);
    }
    
    
    @NotNull
    public AbstractToken nextToken(final int offset, final boolean skipWhitespaces) {
        AbstractToken abstractToken = this.nextToken(skipWhitespaces);
        for (int index = 1; index < offset; index++)
            abstractToken = this.nextToken(skipWhitespaces);
        return abstractToken;
    }
    
    
    @NotNull
    public AbstractToken nextToken() {
        return this.nextToken(true);
    }
    
    
    @NotNull
    public AbstractToken nextToken(final boolean skipWhitespaces) {
        this.position++;
        
        if (skipWhitespaces) {
            while (this.position < this.tokens.length) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE)
                    break;
                this.position++;
            }
        }
        
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[this.position];
    }
    
    
    @NotNull
    public AbstractToken undoToken() {
        return this.undoToken(true);
    }
    
    
    @NotNull
    public AbstractToken undoToken(final boolean skipWhitespaces) {
        this.position--;
        
        if (skipWhitespaces) {
            while (this.position > 0) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE)
                    break;
                this.position--;
            }
        }
        
        if (this.position < 0)
            return this.tokens[0];
        return this.tokens[this.position];
    }
    
    
    @Nullable
    public AbstractToken undoToken(final int offset) {
        return this.undoToken(offset, true);
    }
    
    
    @Nullable
    public AbstractToken undoToken(final int offset, final boolean skipWhitespaces) {
        AbstractToken abstractToken = null;
        for (int index = 0; index < offset; index++)
            abstractToken = this.undoToken(skipWhitespaces);
        return abstractToken;
    }
    
}
