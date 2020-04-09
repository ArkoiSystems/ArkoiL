/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 7, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.KeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.OperatorToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.ArkoiSyntaxAST;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SyntaxAnalyzer implements ICompilerStage
{
    
    @Getter
    @NotNull
    private final ICompilerClass compilerClass;
    
    
    @Getter
    @NotNull
    private SyntaxErrorHandler errorHandler = new SyntaxErrorHandler();
    
    
    @Getter
    @NotNull
    private RootSyntaxAST rootSyntaxAST = new RootSyntaxAST(this);
    
    
    @Getter
    @NotNull
    private AbstractToken[] tokens = new AbstractToken[0];
    
    
    @Getter
    private boolean failed;
    
    
    @Getter
    @Setter
    private int position;
    
    
    public SyntaxAnalyzer(@NotNull final ICompilerClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    
    @Override
    public boolean processStage() {
        this.reset();
        
        this.tokens = this.compilerClass.getLexicalAnalyzer().getTokens();
        return !this.rootSyntaxAST.parseAST(this.rootSyntaxAST).isFailed();
    }
    
    
    @Override
    public void reset() {
        this.rootSyntaxAST = new RootSyntaxAST(this);
        this.errorHandler = new SyntaxErrorHandler();
        this.tokens = new AbstractToken[0];
        this.failed = false;
        this.position = 0;
    }
    
    
    @Override
    public void failed() {
        this.failed = true;
    }
    
    
    @Nullable
    public SymbolToken matchesCurrentToken(@NotNull final SymbolType symbolType) {
        return this.matchesCurrentToken(symbolType, true);
    }
    
    
    @Nullable
    public SymbolToken matchesCurrentToken(@NotNull final SymbolType symbolType, final boolean advance) {
        final AbstractToken currentToken = this.currentToken(advance);
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
    public SymbolToken matchesNextToken(@NotNull final SymbolType symbolType, final boolean advance) {
        final AbstractToken nextToken = this.nextToken(advance);
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
    public SymbolToken matchesPeekToken(final int offset, @NotNull final SymbolType symbolType, final boolean advance) {
        if (offset == 0)
            return this.matchesCurrentToken(symbolType, advance);
        
        final AbstractToken peekToken = this.peekToken(offset, advance);
        if (!(peekToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) peekToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    
    @Nullable
    public OperatorToken matchesCurrentToken(@NotNull final OperatorType operatorType) {
        return this.matchesCurrentToken(operatorType, true);
    }
    
    
    @Nullable
    public OperatorToken matchesCurrentToken(@NotNull final OperatorType operatorType, final boolean advance) {
        final AbstractToken currentToken = this.currentToken(advance);
        if (!(currentToken instanceof OperatorToken))
            return null;
        
        final OperatorToken operatorToken = (OperatorToken) currentToken;
        if (operatorToken.getOperatorType() != operatorType)
            return null;
        return operatorToken;
    }
    
    
    @Nullable
    public OperatorToken matchesNextToken(@NotNull final OperatorType operatorType) {
        return this.matchesNextToken(operatorType, true);
    }
    
    
    @Nullable
    public OperatorToken matchesNextToken(@NotNull final OperatorType operatorType, final boolean advance) {
        final AbstractToken nextToken = this.nextToken(advance);
        if (!(nextToken instanceof OperatorToken))
            return null;
        
        final OperatorToken operatorToken = (OperatorToken) nextToken;
        if (operatorToken.getOperatorType() != operatorType)
            return null;
        return operatorToken;
    }
    
    
    @Nullable
    public OperatorToken matchesPeekToken(final int offset, @NotNull final OperatorType operatorType) {
        return this.matchesPeekToken(offset, operatorType, true);
    }
    
    
    @Nullable
    public OperatorToken matchesPeekToken(final int offset, @NotNull final OperatorType operatorType, final boolean advance) {
        if (offset == 0)
            return this.matchesCurrentToken(operatorType, advance);
        
        final AbstractToken peekToken = this.peekToken(offset, advance);
        if (!(peekToken instanceof OperatorToken))
            return null;
        
        final OperatorToken operatorToken = (OperatorToken) peekToken;
        if (operatorToken.getOperatorType() != operatorType)
            return null;
        return operatorToken;
    }
    
    
    @Nullable
    public KeywordToken matchesCurrentToken(@NotNull final KeywordType keywordType) {
        return this.matchesCurrentToken(keywordType, true);
    }
    
    
    @Nullable
    public KeywordToken matchesCurrentToken(@NotNull final KeywordType keywordType, final boolean advance) {
        final AbstractToken currentToken = this.currentToken(advance);
        if (!(currentToken instanceof KeywordToken))
            return null;
        
        final KeywordToken keywordToken = (KeywordToken) currentToken;
        if (keywordToken.getKeywordType() != keywordType)
            return null;
        return keywordToken;
    }
    
    
    @Nullable
    public KeywordToken matchesNextToken(@NotNull final KeywordType keywordType) {
        return this.matchesNextToken(keywordType, true);
    }
    
    
    @Nullable
    public KeywordToken matchesNextToken(@NotNull final KeywordType keywordType, final boolean advance) {
        final AbstractToken nextToken = this.nextToken(advance);
        if (!(nextToken instanceof KeywordToken))
            return null;
        
        final KeywordToken keywordToken = (KeywordToken) nextToken;
        if (keywordToken.getKeywordType() != keywordType)
            return null;
        return keywordToken;
    }
    
    
    @Nullable
    public KeywordToken matchesPeekToken(final int offset, @NotNull final KeywordType keywordType) {
        return this.matchesPeekToken(offset, keywordType, true);
    }
    
    
    @Nullable
    public KeywordToken matchesPeekToken(final int offset, @NotNull final KeywordType keywordType, final boolean advance) {
        if (offset == 0)
            return this.matchesCurrentToken(keywordType, advance);
        
        final AbstractToken peekToken = this.peekToken(offset, advance);
        if (!(peekToken instanceof KeywordToken))
            return null;
        
        final KeywordToken keywordToken = (KeywordToken) peekToken;
        if (keywordToken.getKeywordType() != keywordType)
            return null;
        return keywordToken;
    }
    
    
    @Nullable
    public AbstractToken matchesCurrentToken(@NotNull final TokenType tokenType) {
        return this.matchesCurrentToken(tokenType, true);
    }
    
    
    @Nullable
    public AbstractToken matchesCurrentToken(@NotNull final TokenType tokenType, final boolean advance) {
        final AbstractToken currentToken = this.currentToken(advance);
        if (currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    
    @Nullable
    public AbstractToken matchesNextToken(@NotNull final TokenType tokenType) {
        return this.matchesNextToken(tokenType, true);
    }
    
    
    @Nullable
    public AbstractToken matchesNextToken(@NotNull final TokenType tokenType, final boolean advance) {
        final AbstractToken nextToken = this.nextToken(advance);
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    
    @Nullable
    public AbstractToken matchesPeekToken(final int offset, @NotNull final TokenType tokenType) {
        return this.matchesPeekToken(offset, tokenType, true);
    }
    
    
    @Nullable
    public AbstractToken matchesPeekToken(final int offset, @NotNull final TokenType tokenType, final boolean advance) {
        if (offset == 0)
            return this.matchesCurrentToken(tokenType, advance);
        
        final AbstractToken peekToken = this.peekToken(offset, advance);
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    
    @NotNull
    public AbstractToken peekToken(final int offset) {
        return this.peekToken(offset, true);
    }
    
    
    @NotNull
    public AbstractToken peekToken(final int offset, final boolean advance) {
        AbstractToken abstractToken = this.nextToken(offset, advance);
        this.undoToken(offset, advance);
        return abstractToken;
    }
    
    
    @NotNull
    public AbstractToken currentToken() {
        return this.currentToken(true);
    }
    
    
    @NotNull
    public AbstractToken currentToken(final boolean advance) {
        if (advance) {
            while (this.position < this.tokens.length) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE && this.tokens[this.position].getTokenType() != TokenType.COMMENT)
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
    public AbstractToken nextToken(final int offset, final boolean advance) {
        AbstractToken abstractToken = this.nextToken(advance);
        for (int index = 1; index < offset; index++)
            abstractToken = this.nextToken(advance);
        return abstractToken;
    }
    
    
    @NotNull
    public AbstractToken nextToken() {
        return this.nextToken(true);
    }
    
    
    @NotNull
    public AbstractToken nextToken(final boolean advance) {
        this.position++;
        
        if (advance) {
            while (this.position < this.tokens.length) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE && this.tokens[this.position].getTokenType() != TokenType.COMMENT)
                    break;
                this.position++;
            }
        }
        
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[this.position];
    }
    
    
    @NotNull
    public AbstractToken undoToken(final boolean advance) {
        this.position--;
        
        if (advance) {
            while (this.position > 0) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE && this.tokens[this.position].getTokenType() != TokenType.COMMENT)
                    break;
                this.position--;
            }
        }
        
        if (this.position < 0)
            return this.tokens[0];
        return this.tokens[this.position];
    }
    
    
    @Nullable
    public AbstractToken undoToken(final int offset, final boolean advance) {
        AbstractToken abstractToken = null;
        for (int index = 0; index < offset; index++)
            abstractToken = this.undoToken(advance);
        return abstractToken;
    }
    
}
