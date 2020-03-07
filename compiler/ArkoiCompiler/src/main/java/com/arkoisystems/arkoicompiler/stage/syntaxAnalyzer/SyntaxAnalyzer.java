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
import lombok.NonNull;

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
    private final ArkoiClass arkoiClass;
    
    
    /**
     * The {@link SyntaxErrorHandler} is used for errors which are happening through the
     * process of parsing the {@link AbstractSyntaxAST}s.
     */
    @Getter
    private final SyntaxErrorHandler errorHandler;
    
    
    /**
     * The {@link AbstractToken[]} which is used to get current, peeked, next tokens
     * without needing to use a {@link List} or something else.
     */
    @Getter
    private AbstractToken[] tokens;
    
    
    /**
     * The current token position of the {@link SyntaxAnalyzer}. Used for methods like
     * {@link SyntaxAnalyzer#currentToken()} or {@link SyntaxAnalyzer#peekToken(int)}.
     */
    @Getter
    private int position;
    
    
    /**
     * The {@link RootSyntaxAST} which is getting parsed directly at the beginning.
     */
    @Getter
    private RootSyntaxAST rootSyntaxAST;
    
    
    /**
     * Constructs a new {@link SyntaxAnalyzer} with the given parameters. It will set the
     * {@link ArkoiClass} it got created in and also the {@link SyntaxErrorHandler} and
     * {@link RootSyntaxAST} are getting created.
     *
     * @param arkoiClass
     *         the {@link ArkoiClass} in which the {@link SyntaxAnalyzer} got created.
     */
    public SyntaxAnalyzer(@NonNull final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new SyntaxErrorHandler();
        this.rootSyntaxAST = new RootSyntaxAST(this);
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
        
        return this.rootSyntaxAST.parseAST(null) != null && !this.isFailed();
    }
    
    
    /**
     * Overwritten method from the {@link AbstractStage} class which will return the
     * created {@link SyntaxErrorHandler}.
     *
     * @return the {@link SyntaxErrorHandler} which got created in the constructor.
     */
    @Override
    public SyntaxErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    public SymbolToken matchesCurrentToken(@NonNull final SymbolType symbolType) {
        return this.matchesCurrentToken(symbolType, true);
    }
    
    
    public SymbolToken matchesCurrentToken(@NonNull final SymbolType symbolType, final boolean skipWhitespaces) {
        final AbstractToken currentToken = this.currentToken(skipWhitespaces);
        if (!(currentToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) currentToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    public SymbolToken matchesNextToken(@NonNull final SymbolType symbolType) {
        return this.matchesNextToken(symbolType, true);
    }
    
    
    public SymbolToken matchesNextToken(@NonNull final SymbolType symbolType, final boolean skipWhitespaces) {
        final AbstractToken nextToken = this.nextToken(skipWhitespaces);
        if (!(nextToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) nextToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    public SymbolToken matchesPeekToken(final int offset, @NonNull final SymbolType symbolType) {
        return this.matchesPeekToken(offset, symbolType, true);
    }
    
    
    public SymbolToken matchesPeekToken(final int offset, @NonNull final SymbolType symbolType, final boolean skipWhitespaces) {
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
    
    
    public AbstractToken matchesCurrentToken(@NonNull final TokenType tokenType) {
        return this.matchesCurrentToken(tokenType, true);
    }
    
    
    public AbstractToken matchesCurrentToken(@NonNull final TokenType tokenType, final boolean skipWhitespaces) {
        final AbstractToken currentToken = this.currentToken(skipWhitespaces);
        if (currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    
    public AbstractToken matchesNextToken(@NonNull final TokenType tokenType) {
        return this.matchesNextToken(tokenType, true);
    }
    
    
    public AbstractToken matchesNextToken(@NonNull final TokenType tokenType, final boolean skipWhitespaces) {
        final AbstractToken nextToken = this.nextToken(skipWhitespaces);
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    
    public AbstractToken matchesPeekToken(final int offset, @NonNull final TokenType tokenType) {
        return this.matchesPeekToken(offset, tokenType, true);
    }
    
    
    public AbstractToken matchesPeekToken(final int offset, @NonNull final TokenType tokenType, final boolean skipWhitespaces) {
        if (offset == 0)
            return this.matchesCurrentToken(tokenType, skipWhitespaces);
    
        final AbstractToken peekToken = this.peekToken(offset, skipWhitespaces);
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    
    public AbstractToken peekToken(final int offset) {
        return this.peekToken(offset, true);
    }
    
    
    public AbstractToken peekToken(final int offset, final boolean skipWhitespaces) {
        AbstractToken abstractToken = null;
        for (int index = 0; index < offset; index++)
            abstractToken = this.nextToken(skipWhitespaces);
        for (int index = 0; index < offset; index++)
            this.undoToken(skipWhitespaces);
        return abstractToken;
    }
    
    
    public AbstractToken currentToken() {
        return this.currentToken(true);
    }
    
    
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
    
    
    public AbstractToken nextToken(final int offset) {
        return this.nextToken(offset, true);
    }
    
    
    public AbstractToken nextToken(final int offset, final boolean skipWhitespaces) {
        AbstractToken abstractToken = null;
        for (int index = 0; index < offset; index++)
            abstractToken = this.nextToken(skipWhitespaces);
        return abstractToken;
    }
    
    
    public AbstractToken nextToken() {
        return this.nextToken(true);
    }
    
    
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
    
    
    public AbstractToken undoToken() {
        return this.undoToken(true);
    }
    
    
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
    
    public AbstractToken undoToken(final int offset) {
        return this.undoToken(offset, true);
    }
    
    
    public AbstractToken undoToken(final int offset, final boolean skipWhitespaces) {
        AbstractToken abstractToken = null;
        for (int index = 0; index < offset; index++)
            abstractToken = this.undoToken(skipWhitespaces);
        return abstractToken;
    }
    
}
