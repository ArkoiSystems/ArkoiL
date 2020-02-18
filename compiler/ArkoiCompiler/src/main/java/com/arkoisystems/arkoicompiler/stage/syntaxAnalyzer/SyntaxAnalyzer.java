/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.NumberToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.AbstractSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

/**
 * This class is an implementation of the {@link AbstractStage}. It will parse an AST
 * (Abstract Syntax Tree) with help of the parsed {@link AbstractToken} list of the {@link
 * LexicalAnalyzer}.
 */
@Setter
@Getter
public class SyntaxAnalyzer extends AbstractStage
{
    
    /**
     * This defines the {@link ArkoiClass} in which the {@link SyntaxAnalyzer} got
     * created.
     */
    private final ArkoiClass arkoiClass;
    
    
    /**
     * The {@link SyntaxErrorHandler} is used for error which are happening through the
     * process of parsing the {@link AbstractSyntaxAST}'s.
     */
    @Expose
    private final SyntaxErrorHandler errorHandler;
    
    
    /**
     * The {@link AbstractToken[]} which is used to get current, peeked, next tokens
     * without needing to use a {@link List} or something else.
     */
    private AbstractToken[] tokens;
    
    
    /**
     * The current token position of the {@link SyntaxAnalyzer}. Used for methods like
     * {@link SyntaxAnalyzer#currentToken()} or {@link SyntaxAnalyzer#peekToken(int)}.
     */
    @Expose
    private int position;
    
    
    /**
     * The {@link RootSyntaxAST} which is getting parsed directly at the beginning.
     */
    @Expose
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
        
        return this.rootSyntaxAST.parseAST(null, this) != null;
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
    
    /**
     * Checks if the current {@link AbstractToken} matches with the given {@link
     * SymbolToken.SymbolType}. First of all it will check if the {@link AbstractToken}
     * even is an instance of {@link SymbolToken}. If not it will return null or the
     * {@link SymbolToken} which got checked for the {@link SymbolToken.SymbolType}.
     *
     * @param symbolType
     *         the {@link SymbolToken.SymbolType} which should get used to compare with
     *         the current {@link AbstractToken} if it's an instance of {@link
     *         SymbolToken}.
     *
     * @return {@code null} if the current {@link AbstractToken} is not an {@link
     *         SymbolToken} or the {@link SymbolToken.SymbolType} doesn't match.
     */
    public SymbolToken matchesCurrentToken(@NonNull final SymbolToken.SymbolType symbolType) {
        final AbstractToken currentToken = this.currentToken();
        if (!(currentToken instanceof SymbolToken))
            return null;
    
        final SymbolToken symbolToken = (SymbolToken) currentToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    /**
     * Checks if the next {@link AbstractToken} matches with the given {@link
     * SymbolToken.SymbolType}. First of all it will check if the {@link AbstractToken}
     * even is an instance of {@link SymbolToken}. If not it will return null or the
     * {@link SymbolToken} which got checked for the {@link SymbolToken.SymbolType}.
     *
     * @param symbolType
     *         the {@link SymbolToken.SymbolType} which should get used to compare with
     *         the next {@link AbstractToken} if it's an instance of {@link SymbolToken}.
     *
     * @return {@code null} if the next {@link AbstractToken} is not an {@link
     *         SymbolToken} or the {@link SymbolToken.SymbolType} doesn't match.
     */
    public SymbolToken matchesNextToken(@NonNull final SymbolToken.SymbolType symbolType) {
        final AbstractToken nextToken = this.nextToken();
        if (!(nextToken instanceof SymbolToken))
            return null;
    
        final SymbolToken symbolToken = (SymbolToken) nextToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    /**
     * Check if the peeked {@link AbstractToken} matches with the given {@link
     * SymbolToken.SymbolType}. At first it will check if the {@code offset} parameter
     * equal to 0 because it will then just return the result of {@link
     * SyntaxAnalyzer#matchesCurrentToken(SymbolToken.SymbolType)}. If not it will check
     * if the peeked {@link AbstractToken} is an instance of {@link SymbolToken} and
     * depending of that it will check for the match of the {@link SymbolToken.SymbolType}
     * or simply just return null.
     *
     * @param offset
     *         the offset which should get added to the current position.
     * @param symbolType
     *         the {@link SymbolToken.SymbolType} which should get used to compare the
     *         peeked {@link AbstractToken}.
     *
     * @return {@code null} if the peeked {@link AbstractToken} isn't an instance of
     *         {@link SymbolToken} or it doesn't match the given {@link
     *         SymbolToken.SymbolType}. If it does it will just return the peeked {@link
     *         AbstractToken}.
     */
    public SymbolToken matchesPeekToken(final int offset, @NonNull final SymbolToken.SymbolType symbolType) {
        if (offset == 0)
            return this.matchesCurrentToken(symbolType);
    
        final AbstractToken peekToken = this.peekToken(offset);
        if (!(peekToken instanceof SymbolToken))
            return null;
    
        final SymbolToken symbolToken = (SymbolToken) peekToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    /**
     * Checks if the current {@link AbstractToken} matches with the given {@link
     * TokenType}. If not it will return null or the {@link NumberToken} which got checked
     * for the {@link TokenType}.
     *
     * @param tokenType
     *         the {@link TokenType} which should get used to compare with the current
     *         {@link AbstractToken}.
     *
     * @return {@code null} if the current {@link AbstractToken} doesn't match the given
     *         {@link TokenType}. If it does it will just return the peeked {@link
     *         AbstractToken}.
     */
    public AbstractToken matchesCurrentToken(@NonNull final TokenType tokenType) {
        final AbstractToken currentToken = this.currentToken();
        if (currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    
    /**
     * Checks if the next {@link AbstractToken} matches with the given {@link TokenType}.
     * If not it will return null or the {@link SymbolToken} which got checked for the
     * {@link SymbolToken.SymbolType}.
     *
     * @param tokenType
     *         the {@link TokenType} which should get used to compare with the next {@link
     *         AbstractToken}.
     *
     * @return {@code null} if the next {@link AbstractToken} doesn't match the given
     *         {@link TokenType}. If it does it will just return the peeked {@link
     *         AbstractToken}.
     */
    public AbstractToken matchesNextToken(@NonNull final TokenType tokenType) {
        final AbstractToken nextToken = this.nextToken();
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    
    /**
     * Check if the peeked {@link AbstractToken} matches with the given {@link TokenType}.
     * At first it will check if the {@code offset} parameter equal to 0 because it will
     * then just return the result of {@link SyntaxAnalyzer#matchesCurrentToken(TokenType)}.
     * If not it will check if the {@link TokenType} matches and depending of that it will
     * return null or the {@link AbstractToken}.
     *
     * @param offset
     *         the offset which should get added to the current position.
     * @param tokenType
     *         the {@link TokenType} which should get used to compare the peeked {@link
     *         AbstractToken}.
     *
     * @return {@code null} if the peeked {@link AbstractToken} doesn't match the given
     *         {@link TokenType}. If it does it will just return the peeked {@link
     *         AbstractToken}.
     */
    public AbstractToken matchesPeekToken(final int offset, @NonNull final TokenType tokenType) {
        if (offset == 0)
            return this.matchesCurrentToken(tokenType);
    
        final AbstractToken peekToken = this.peekToken(offset);
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    
    /**
     * Returns the peeked {@link AbstractToken} with the offset declared as a parameter
     * {@code offset}. If the offset goes out of bounds, it will just return the last
     * {@link AbstractToken} of the array.
     *
     * @param offset
     *         the offset which should get added to the current position.
     *
     * @return an {@link AbstractToken} which is used in the later process.
     */
    public AbstractToken peekToken(final int offset) {
        if (this.position + offset >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[this.position + offset];
    }
    
    
    /**
     * Returns the current {@link AbstractToken} and returns the last {@link
     * AbstractToken} of the array if it went out of bounds.
     *
     * @return an {@link AbstractToken} which is used in the later process.
     */
    public AbstractToken currentToken() {
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[position];
    }
    
    
    /**
     * Returns the next {@link AbstractToken} with an offset. It will return the last
     * {@link AbstractToken} in the array if it went out of bounds.
     *
     * @param offset
     *         the offset which should get added to the current position.
     *
     * @return an {@link AbstractToken} which is used in the later process.
     */
    public AbstractToken nextToken(final int offset) {
        this.position += offset;
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.currentToken();
    }
    
    
    /**
     * Returns the next {@link AbstractToken} without any offset. It will return the last
     * {@link AbstractToken} in the array if it went out of bounds.
     *
     * @return an {@link AbstractToken} which is used in the later process.
     */
    public AbstractToken nextToken() {
        this.position++;
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.currentToken();
    }
    
}
