package com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.stage.syntaxAnalyzer.ast.types.RootSyntaxAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka Timo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * <p>
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
@Setter
@Getter
public class SyntaxAnalyzer extends AbstractStage
{
    
    private final ArkoiClass arkoiClass;
    
    
    @Expose
    private final SyntaxErrorHandler errorHandler;
    
    @Expose
    private int position;
    
    private AbstractToken[] tokens;
    
    @Expose
    private RootSyntaxAST rootSyntaxAST;
    
    
    public SyntaxAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new SyntaxErrorHandler(this);
        this.rootSyntaxAST = new RootSyntaxAST(this);
    }
    
    @Override
    public boolean processStage() {
        this.tokens = this.arkoiClass.getLexicalAnalyzer().getTokens();
        this.position = 0;
    
        return this.rootSyntaxAST.parseAST(null, this) != null;
    }
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    public AbstractToken matchesCurrentToken(final AbstractNumberToken.NumberType numberType) {
        final AbstractToken currentToken = this.currentToken();
        if (!(currentToken instanceof AbstractNumberToken))
            return null;
        
        final AbstractNumberToken numberToken = (AbstractNumberToken) currentToken;
        if (numberToken.getNumberType() != numberType)
            return null;
        return numberToken;
    }
    
    public AbstractToken matchesNextToken(final AbstractNumberToken.NumberType numberType) {
        final AbstractToken nextToken = this.nextToken();
        if (!(nextToken instanceof AbstractNumberToken))
            return null;
        
        final AbstractNumberToken numberToken = (AbstractNumberToken) nextToken;
        if (numberToken.getNumberType() != numberType)
            return null;
        return numberToken;
    }
    
    public AbstractToken matchesPeekToken(final int peek, final AbstractNumberToken.NumberType numberType) {
        if (peek == 0)
            return this.matchesCurrentToken(numberType);
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (!(peekToken instanceof AbstractNumberToken))
            return null;
        
        final AbstractNumberToken numberToken = (AbstractNumberToken) peekToken;
        if (numberToken.getNumberType() != numberType)
            return null;
        return numberToken;
    }
    
    public AbstractToken matchesPeekToken(final int peek, final TokenType tokenType) {
        if (peek == 0)
            return this.matchesCurrentToken(tokenType);
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    public AbstractToken matchesCurrentToken(final SymbolToken.SymbolType symbolType) {
        final AbstractToken currentToken = this.currentToken();
        if (!(currentToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) currentToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    public AbstractToken matchesNextToken(final SymbolToken.SymbolType symbolType) {
        final AbstractToken nextToken = this.nextToken();
        if (!(nextToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) nextToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    public AbstractToken matchesPeekToken(final int peek, final SymbolToken.SymbolType symbolType) {
        if (peek == 0)
            return this.matchesCurrentToken(symbolType);
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (!(peekToken instanceof SymbolToken))
            return null;
        
        final SymbolToken symbolToken = (SymbolToken) peekToken;
        if (symbolToken.getSymbolType() != symbolType)
            return null;
        return symbolToken;
    }
    
    public AbstractToken matchesCurrentToken(final TokenType tokenType) {
        final AbstractToken currentToken = this.currentToken();
        if (currentToken.getTokenType() != tokenType)
            return null;
        return currentToken;
    }
    
    public AbstractToken matchesNextToken(final TokenType tokenType) {
        final AbstractToken nextToken = this.nextToken();
        if (nextToken.getTokenType() != tokenType)
            return null;
        return nextToken;
    }
    
    public AbstractToken matchesNextToken(final int peek, final TokenType tokenType) {
        if (peek == 0)
            return this.matchesCurrentToken(tokenType);
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (peekToken.getTokenType() != tokenType)
            return null;
        return peekToken;
    }
    
    public AbstractToken peekToken(final int peek) {
        if (this.position + peek > this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[this.position + peek];
    }
    
    public AbstractToken currentToken() {
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.tokens[position];
    }
    
    public AbstractToken nextToken(final int positions) {
        this.position += positions;
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.currentToken();
    }
    
    public AbstractToken nextToken() {
        this.position++;
        if (this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.currentToken();
    }
    
}
