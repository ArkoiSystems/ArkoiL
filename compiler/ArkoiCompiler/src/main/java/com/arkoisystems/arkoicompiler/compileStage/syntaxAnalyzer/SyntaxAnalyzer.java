package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.ArkoiCompiler;
import com.arkoisystems.arkoicompiler.compileStage.ICompileStage;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.operators.types.AssignmentOperatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.RootAST;
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
public class SyntaxAnalyzer implements ICompileStage
{
    
    private final ArkoiClass arkoiClass;
    
    
    @Expose
    private final SyntaxErrorHandler errorHandler;
    
    @Expose
    private int position;
    
    private AbstractToken[] tokens;
    
    @Expose
    private RootAST rootAST;
    
    
    public SyntaxAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
        
        this.errorHandler = new SyntaxErrorHandler();
        this.rootAST = new RootAST(this);
    }
    
    @Override
    public boolean processStage() {
        this.tokens = this.arkoiClass.getLexicalAnalyzer().getTokens().stream().filter(abstractToken -> abstractToken.getTokenType() != TokenType.WHITESPACE).toArray(AbstractToken[]::new);
        this.position = 0;
        
        return this.rootAST.parseAST(null, this) != null;
    }
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
    public boolean findSeparator(final SeparatorToken.SeparatorType separatorType) {
        while (this.position < this.tokens.length) {
            final AbstractToken nextToken = this.nextToken();
            if (nextToken.getTokenType() != TokenType.SEPARATOR)
                continue;
            
            final SeparatorToken separatorToken = (SeparatorToken) nextToken;
            if (separatorToken.getSeparatorType() == separatorType)
                return true;
        }
        return false;
    }
    
    public boolean findMatchingSeparator(final AbstractAST parentAST, final SeparatorToken.SeparatorType separatorType) {
        final SeparatorToken.SeparatorType siblingType;
        switch (separatorType) {
            case OPENING_PARENTHESIS:
                siblingType = SeparatorToken.SeparatorType.CLOSING_PARENTHESIS;
                break;
            case OPENING_BRACE:
                siblingType = SeparatorToken.SeparatorType.CLOSING_BRACE;
                break;
            case OPENING_BRACKET:
                siblingType = SeparatorToken.SeparatorType.CLOSING_BRACKET;
                break;
            case LESS_THAN_SIGN:
                siblingType = SeparatorToken.SeparatorType.GREATER_THAN_SIGN;
                break;
            default:
                this.errorHandler.addError(new ASTError(parentAST, "Couldn't match the next separator because the SeparatorType isn't supported."));
                return false;
        }
        
        int openedSeparators = 1;
        while (this.position < this.tokens.length) {
            final AbstractToken nextToken = this.nextToken();
            if (nextToken.getTokenType() != TokenType.SEPARATOR)
                continue;
            
            final SeparatorToken separatorToken = (SeparatorToken) nextToken;
            if (separatorToken.getSeparatorType() == separatorType)
                openedSeparators++;
            else if (separatorToken.getSeparatorType() == siblingType) {
                openedSeparators--;
                
                if (openedSeparators == 0)
                    return true;
            }
        }
        return false;
    }
    
    public AbstractToken matchesCurrentToken(final AbstractNumberToken.NumberType numberType) {
        final AbstractToken currentToken = this.currentToken();
        if (!(currentToken instanceof AbstractNumberToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AbstractNumberToken numberToken = (AbstractNumberToken) currentToken;
        if (numberToken.getNumberType() != numberType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return numberToken;
    }
    
    public AbstractToken matchesNextToken(final AbstractNumberToken.NumberType numberType) {
        final AbstractToken nextToken = this.nextToken();
        if (!(nextToken instanceof AbstractNumberToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AbstractNumberToken numberToken = (AbstractNumberToken) nextToken;
        if (numberToken.getNumberType() != numberType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return numberToken;
    }
    
    public AbstractToken matchesPeekToken(final int peek, final AbstractNumberToken.NumberType numberType) {
        if (peek == 0) {
            // TODO: 1/2/2020 Throw error
            return this.matchesCurrentToken(numberType);
        }
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (!(peekToken instanceof AbstractNumberToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AbstractNumberToken numberToken = (AbstractNumberToken) peekToken;
        if (numberToken.getNumberType() != numberType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return numberToken;
    }
    
    public AbstractToken matchesCurrentToken(final AssignmentOperatorToken.AssignmentOperatorType assignmentOperatorType) {
        final AbstractToken currentToken = this.currentToken();
        if (!(currentToken instanceof AssignmentOperatorToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AssignmentOperatorToken assignmentOperatorToken = (AssignmentOperatorToken) currentToken;
        if (assignmentOperatorToken.getAssignmentOperatorType() != assignmentOperatorType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return assignmentOperatorToken;
    }
    
    public AbstractToken matchesNextToken(final AssignmentOperatorToken.AssignmentOperatorType assignmentOperatorType) {
        final AbstractToken nextToken = this.nextToken();
        if (!(nextToken instanceof AssignmentOperatorToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AssignmentOperatorToken assignmentOperatorToken = (AssignmentOperatorToken) nextToken;
        if (assignmentOperatorToken.getAssignmentOperatorType() != assignmentOperatorType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return assignmentOperatorToken;
    }
    
    public AbstractToken matchesPeekToken(final int peek, final AssignmentOperatorToken.AssignmentOperatorType assignmentOperatorType) {
        if (peek == 0) {
            // TODO: 1/2/2020 Throw error
            return this.matchesCurrentToken(assignmentOperatorType);
        }
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (!(peekToken instanceof AssignmentOperatorToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final AssignmentOperatorToken assignmentOperatorToken = (AssignmentOperatorToken) peekToken;
        if (assignmentOperatorToken.getAssignmentOperatorType() != assignmentOperatorType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return assignmentOperatorToken;
    }
    
    public AbstractToken matchesCurrentToken(final SeparatorToken.SeparatorType separatorType) {
        final AbstractToken currentToken = this.currentToken();
        if (!(currentToken instanceof SeparatorToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final SeparatorToken separatorToken = (SeparatorToken) currentToken;
        if (separatorToken.getSeparatorType() != separatorType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return separatorToken;
    }
    
    public AbstractToken matchesNextToken(final SeparatorToken.SeparatorType separatorType) {
        final AbstractToken nextToken = this.nextToken();
        if (!(nextToken instanceof SeparatorToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final SeparatorToken separatorToken = (SeparatorToken) nextToken;
        if (separatorToken.getSeparatorType() != separatorType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return separatorToken;
    }
    
    public AbstractToken matchesPeekToken(final int peek, final SeparatorToken.SeparatorType separatorType) {
        if (peek == 0) {
            // TODO: 1/2/2020 Throw error
            return this.matchesCurrentToken(separatorType);
        }
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (!(peekToken instanceof SeparatorToken)) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        
        final SeparatorToken separatorToken = (SeparatorToken) peekToken;
        if (separatorToken.getSeparatorType() != separatorType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return separatorToken;
    }
    
    public AbstractToken matchesCurrentToken(final TokenType tokenType) {
        final AbstractToken currentToken = this.currentToken();
        if (currentToken.getTokenType() != tokenType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return currentToken;
    }
    
    public AbstractToken matchesNextToken(final TokenType tokenType) {
        final AbstractToken nextToken = this.nextToken();
        if (nextToken.getTokenType() != tokenType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
        return nextToken;
    }
    
    public AbstractToken matchesNextToken(final int peek, final TokenType tokenType) {
        if (peek == 0) {
            // TODO: 1/2/2020 Throw error
            return this.matchesCurrentToken(tokenType);
        }
        
        final AbstractToken peekToken = this.peekToken(peek);
        if (peekToken.getTokenType() != tokenType) {
            // TODO: 1/2/2020 Throw error
            return null;
        }
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
    
    public AbstractToken nextToken() {
        this.position++;
        if(this.position >= this.tokens.length)
            return this.tokens[this.tokens.length - 1];
        return this.currentToken();
    }
    
}
