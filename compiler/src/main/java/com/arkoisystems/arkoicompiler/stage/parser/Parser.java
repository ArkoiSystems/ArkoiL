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
package com.arkoisystems.arkoicompiler.stage.parser;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.api.IStage;
import com.arkoisystems.arkoicompiler.stage.lexer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.KeywordType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.SymbolType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.enums.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.KeywordToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.OperatorToken;
import com.arkoisystems.arkoicompiler.stage.lexer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.parser.ast.types.Root;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Parser implements IStage
{
    
    @Getter
    @NotNull
    private final ArkoiClass compilerClass;
    
    @Getter
    @NotNull
    private ParserErrorHandler errorHandler = new ParserErrorHandler();
    
    @Getter
    @NotNull
    private Root rootAST = Root.builder()
            .parser(this)
            .build();
    
    @Setter
    @Getter
    @NotNull
    private ArkoiToken[] tokens = new ArkoiToken[0];
    
    @Getter
    @Setter
    private boolean failed;
    
    @Getter
    @Setter
    private int position;
    
    public Parser(final @NotNull ArkoiClass compilerClass) {
        this.compilerClass = compilerClass;
    }
    
    @Override
    public boolean processStage() {
        this.reset();
    
        this.tokens = this.compilerClass.getLexer().getTokens().toArray(ArkoiToken[]::new);
        if (this.tokens.length == 0)
            return true;
    
        return !this.rootAST.parseAST(this.rootAST).isFailed();
    }
    
    @Override
    public void reset() {
        this.rootAST = Root.builder()
                .parser(this)
                .build();
        this.errorHandler = new ParserErrorHandler();
        this.tokens = new ArkoiToken[0];
        this.failed = false;
        this.position = 0;
    }
    
    @Nullable
    public SymbolToken matchesCurrentToken(final @NotNull SymbolType symbolType) {
        return this.matchesCurrentToken(symbolType, true);
    }
    
    @Nullable
    public SymbolToken matchesCurrentToken(final @NotNull SymbolType symbolType, final boolean advance) {
        final ArkoiToken currentToken = this.currentToken(advance);
        if (!(currentToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) currentToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    @Nullable
    public SymbolToken matchesNextToken(final @NotNull SymbolType symbolType) {
        return this.matchesNextToken(symbolType, true);
    }
    
    @Nullable
    public SymbolToken matchesNextToken(final @NotNull SymbolType symbolType, final boolean advance) {
        final ArkoiToken nextToken = this.nextToken(advance);
        if (!(nextToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) nextToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    @Nullable
    public SymbolToken matchesPeekToken(final int offset, final @NotNull SymbolType symbolType) {
        return this.matchesPeekToken(offset, symbolType, true);
    }
    
    @Nullable
    public SymbolToken matchesPeekToken(final int offset, final @NotNull SymbolType symbolType, final boolean advance) {
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
    public OperatorToken matchesCurrentToken(final @NotNull OperatorType operatorType) {
        return this.matchesCurrentToken(operatorType, true);
    }
    
    @Nullable
    public OperatorToken matchesCurrentToken(final @NotNull OperatorType operatorType, final boolean advance) {
        final ArkoiToken currentToken = this.currentToken(advance);
        if (!(currentToken instanceof OperatorToken))
            return null;
        
        final OperatorToken operatorToken = (OperatorToken) currentToken;
        if (operatorToken.getOperatorType() != operatorType)
            return null;
        return operatorToken;
    }
    
    @Nullable
    public OperatorToken matchesNextToken(final @NotNull OperatorType operatorType) {
        return this.matchesNextToken(operatorType, true);
    }
    
    @Nullable
    public OperatorToken matchesNextToken(final @NotNull OperatorType operatorType, final boolean advance) {
        final ArkoiToken nextToken = this.nextToken(advance);
        if (!(nextToken instanceof OperatorToken))
            return null;
        
        final OperatorToken operatorToken = (OperatorToken) nextToken;
        if (operatorToken.getOperatorType() != operatorType)
            return null;
        return operatorToken;
    }
    
    @Nullable
    public OperatorToken matchesPeekToken(final int offset, final @NotNull OperatorType operatorType) {
        return this.matchesPeekToken(offset, operatorType, true);
    }
    
    @Nullable
    public OperatorToken matchesPeekToken(final int offset, final @NotNull OperatorType operatorType, final boolean advance) {
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
    public KeywordToken matchesCurrentToken(final @NotNull KeywordType keywordType) {
        return this.matchesCurrentToken(keywordType, true);
    }
    
    @Nullable
    public KeywordToken matchesCurrentToken(final @NotNull KeywordType keywordType, final boolean advance) {
        final ArkoiToken currentToken = this.currentToken(advance);
        if (!(currentToken instanceof KeywordToken))
            return null;
        
        final KeywordToken keywordToken = (KeywordToken) currentToken;
        if (keywordToken.getKeywordType() != keywordType)
            return null;
        return keywordToken;
    }
    
    @Nullable
    public KeywordToken matchesNextToken(final @NotNull KeywordType keywordType) {
        return this.matchesNextToken(keywordType, true);
    }
    
    @Nullable
    public KeywordToken matchesNextToken(final @NotNull KeywordType keywordType, final boolean advance) {
        final ArkoiToken nextToken = this.nextToken(advance);
        if (!(nextToken instanceof KeywordToken))
            return null;
        
        final KeywordToken keywordToken = (KeywordToken) nextToken;
        if (keywordToken.getKeywordType() != keywordType)
            return null;
        return keywordToken;
    }
    
    @Nullable
    public KeywordToken matchesPeekToken(final int offset, final @NotNull KeywordType keywordType) {
        return this.matchesPeekToken(offset, keywordType, true);
    }
    
    @Nullable
    public KeywordToken matchesPeekToken(final int offset, final @NotNull KeywordType keywordType, final boolean advance) {
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
    public ArkoiToken matchesCurrentToken(final @NotNull TokenType tokenType) {
        return this.matchesCurrentToken(tokenType, true);
    }
    
    @Nullable
    public ArkoiToken matchesCurrentToken(final @NotNull TokenType tokenType, final boolean advance) {
        final ArkoiToken currentToken = this.currentToken(advance);
        if (currentToken == null || currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    @Nullable
    public ArkoiToken matchesNextToken(final @NotNull TokenType tokenType) {
        return this.matchesNextToken(tokenType, true);
    }
    
    @Nullable
    public ArkoiToken matchesNextToken(final @NotNull TokenType tokenType, final boolean advance) {
        final ArkoiToken nextToken = this.nextToken(advance);
        if(nextToken == null)
            return null;
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    @Nullable
    public ArkoiToken matchesPeekToken(final int offset, final @NotNull TokenType tokenType) {
        return this.matchesPeekToken(offset, tokenType, true);
    }
    
    @Nullable
    public ArkoiToken matchesPeekToken(final int offset, final @NotNull TokenType tokenType, final boolean advance) {
        if (offset == 0)
            return this.matchesCurrentToken(tokenType, advance);
        
        final ArkoiToken peekToken = this.peekToken(offset, advance);
        if(peekToken == null)
            return null;
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    @Nullable
    public ArkoiToken peekToken(final int offset) {
        return this.peekToken(offset, true);
    }
    
    @Nullable
    public ArkoiToken peekToken(final int offset, final boolean advance) {
        ArkoiToken arkoiToken = this.nextToken(offset, advance);
        this.undoToken(offset, advance);
        return arkoiToken;
    }
    
    @Nullable
    public ArkoiToken currentToken() {
        return this.currentToken(true);
    }
    
    @Nullable
    public ArkoiToken currentToken(final boolean advance) {
        if (advance) {
            while (this.position < this.tokens.length) {
                if (this.tokens[this.position].getTokenType() != TokenType.WHITESPACE && this.tokens[this.position].getTokenType() != TokenType.COMMENT)
                    break;
                this.position++;
            }
        }
        
        if (this.position >= this.tokens.length)
            return null;
        return this.tokens[position];
    }
    
    @Nullable
    public ArkoiToken nextToken(final int offset) {
        return this.nextToken(offset, true);
    }
    
    @Nullable
    public ArkoiToken nextToken(final int offset, final boolean advance) {
        ArkoiToken arkoiToken = this.nextToken(advance);
        for (int index = 1; index < offset; index++)
            arkoiToken = this.nextToken(advance);
        return arkoiToken;
    }
    
    @Nullable
    public ArkoiToken nextToken() {
        return this.nextToken(true);
    }
    
    @Nullable
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
            return null;
        return this.tokens[this.position];
    }
    
    @Nullable
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
            return null;
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
