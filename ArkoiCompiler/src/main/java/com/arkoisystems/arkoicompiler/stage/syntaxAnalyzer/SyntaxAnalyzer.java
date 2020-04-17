/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on March 7, 2020
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
package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.api.ICompilerClass;
import com.arkoisystems.arkoicompiler.api.ICompilerStage;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.KeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.OperatorToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootAST;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private RootAST rootAST = new RootAST(this);
    
    
    @Getter
    @NotNull
    private ArkoiToken[] tokens = new ArkoiToken[0];
    
    
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
        return !this.rootAST.parseAST(this.rootAST).isFailed();
    }
    
    
    @Override
    public void reset() {
        this.rootAST = new RootAST(this);
        this.errorHandler = new SyntaxErrorHandler();
        this.tokens = new ArkoiToken[0];
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
        final ArkoiToken currentToken = this.currentToken(advance);
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
        final ArkoiToken nextToken = this.nextToken(advance);
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
        
        final ArkoiToken peekToken = this.peekToken(offset, advance);
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
        final ArkoiToken currentToken = this.currentToken(advance);
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
        final ArkoiToken nextToken = this.nextToken(advance);
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
        
        final ArkoiToken peekToken = this.peekToken(offset, advance);
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
        final ArkoiToken currentToken = this.currentToken(advance);
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
        final ArkoiToken nextToken = this.nextToken(advance);
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
        
        final ArkoiToken peekToken = this.peekToken(offset, advance);
        if (!(peekToken instanceof KeywordToken))
            return null;
        
        final KeywordToken keywordToken = (KeywordToken) peekToken;
        if (keywordToken.getKeywordType() != keywordType)
            return null;
        return keywordToken;
    }
    
    
    @Nullable
    public ArkoiToken matchesCurrentToken(@NotNull final TokenType tokenType) {
        return this.matchesCurrentToken(tokenType, true);
    }
    
    
    @Nullable
    public ArkoiToken matchesCurrentToken(@NotNull final TokenType tokenType, final boolean advance) {
        final ArkoiToken currentToken = this.currentToken(advance);
        if (currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    
    @Nullable
    public ArkoiToken matchesNextToken(@NotNull final TokenType tokenType) {
        return this.matchesNextToken(tokenType, true);
    }
    
    
    @Nullable
    public ArkoiToken matchesNextToken(@NotNull final TokenType tokenType, final boolean advance) {
        final ArkoiToken nextToken = this.nextToken(advance);
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    
    @Nullable
    public ArkoiToken matchesPeekToken(final int offset, @NotNull final TokenType tokenType) {
        return this.matchesPeekToken(offset, tokenType, true);
    }
    
    
    @Nullable
    public ArkoiToken matchesPeekToken(final int offset, @NotNull final TokenType tokenType, final boolean advance) {
        if (offset == 0)
            return this.matchesCurrentToken(tokenType, advance);
        
        final ArkoiToken peekToken = this.peekToken(offset, advance);
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    
    @NotNull
    public ArkoiToken peekToken(final int offset) {
        return this.peekToken(offset, true);
    }
    
    
    @NotNull
    public ArkoiToken peekToken(final int offset, final boolean advance) {
        ArkoiToken arkoiToken = this.nextToken(offset, advance);
        this.undoToken(offset, advance);
        return arkoiToken;
    }
    
    
    @NotNull
    public ArkoiToken currentToken() {
        return this.currentToken(true);
    }
    
    
    @NotNull
    public ArkoiToken currentToken(final boolean advance) {
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
    public ArkoiToken nextToken(final int offset) {
        return this.nextToken(offset, true);
    }
    
    
    @NotNull
    public ArkoiToken nextToken(final int offset, final boolean advance) {
        ArkoiToken arkoiToken = this.nextToken(advance);
        for (int index = 1; index < offset; index++)
            arkoiToken = this.nextToken(advance);
        return arkoiToken;
    }
    
    
    @NotNull
    public ArkoiToken nextToken() {
        return this.nextToken(true);
    }
    
    
    @NotNull
    public ArkoiToken nextToken(final boolean advance) {
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
    public ArkoiToken undoToken(final boolean advance) {
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
    public ArkoiToken undoToken(final int offset, final boolean advance) {
        ArkoiToken arkoiToken = null;
        for (int index = 0; index < offset; index++)
            arkoiToken = this.undoToken(advance);
        return arkoiToken;
    }
    
}
