/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
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
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.ArkoiToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.OperatorType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class OperatorToken extends ArkoiToken
{
    
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private OperatorType operatorType;
    
    
    @Builder
    public OperatorToken(
            @Nullable final LexicalAnalyzer lexicalAnalyzer,
            @NotNull final String tokenContent,
            @NotNull final OperatorType operatorType,
            final int startLine,
            final int charStart,
            final int endLine,
            final int charEnd) {
        super(lexicalAnalyzer, TokenType.OPERATOR, tokenContent, startLine, charStart, endLine, charEnd);
        
        this.setOperatorType(operatorType);
    }
    
    
    @Override
    public @NotNull ArkoiToken parseToken() {
        Objects.requireNonNull(this.getLexicalAnalyzer(), "lexicalAnalyzer must not be null.");
        
        this.setCharStart(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex());
        if (this.getLexicalAnalyzer().currentChar() == '=') {
            this.setOperatorType(OperatorType.EQUALS);
            this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '+') {
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.PLUS_EQUALS);
            } else if (this.getLexicalAnalyzer().peekChar(1) == '+') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.PLUS_PLUS);
            } else this.setOperatorType(OperatorType.PLUS);
            
            this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '-') {
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.MINUS_EQUALS);
            } else if (this.getLexicalAnalyzer().peekChar(1) == '-') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.MINUS_MINUS);
            } else this.setOperatorType(OperatorType.MINUS);
            
            this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '*') {
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.ASTERISK_EQUALS);
            } else if (this.getLexicalAnalyzer().peekChar(1) == '*') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.ASTERISK_ASTERISK);
            } else this.setOperatorType(OperatorType.ASTERISK);
            
            this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '/') {
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.DIV_EQUALS);
            } else this.setOperatorType(OperatorType.DIV);
            
            this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        } else if (this.getLexicalAnalyzer().currentChar() == '%') {
            if (this.getLexicalAnalyzer().peekChar(1) == '=') {
                this.getLexicalAnalyzer().next();
                this.setOperatorType(OperatorType.PERCENT_EQUALS);
            } else this.setOperatorType(OperatorType.PERCENT);
            
            this.setCharEnd(this.getLexicalAnalyzer().getLineIndex(), this.getLexicalAnalyzer().getCharIndex() + 1);
        } else return this.addError(
                BadToken.builder()
                        .lexicalAnalyzer(this.getLexicalAnalyzer())
                        .startLine(this.getLexicalAnalyzer().getLineIndex())
                        .charStart(this.getLexicalAnalyzer().getCharIndex())
                        .endLine(this.getLexicalAnalyzer().getLineIndex())
                        .charEnd(this.getLexicalAnalyzer().getCharIndex() + 1)
                        .build()
                        .parseToken(),
                
                this.getLexicalAnalyzer().getCompilerClass(),
                
                this.getLexicalAnalyzer().getCharIndex(),
                this.getLexicalAnalyzer().getLineIndex(),
                
                "Couldn't lex this operator because the character is unknown."
        );
    
        this.getLexicalAnalyzer().next();
        return this;
    }
    
}