/*
 * Copyright © 2019-2020 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on February 15, 2020
 * Author timo aka. єхcsє#5543
 */
package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types;

import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.LexicalAnalyzer;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.utils.TokenType;

public class EndOfFileToken extends AbstractToken
{
    
    public EndOfFileToken(final LexicalAnalyzer lexicalAnalyzer) {
        super(lexicalAnalyzer, TokenType.END_OF_FILE);
        
        this.setTokenContent("EOF");
        
        this.setStart(0);
        this.setEnd(lexicalAnalyzer.getArkoiClass().getContent().length);
    }
    
    
    @Override
    public EndOfFileToken parseToken() {
        return this;
    }
    
}