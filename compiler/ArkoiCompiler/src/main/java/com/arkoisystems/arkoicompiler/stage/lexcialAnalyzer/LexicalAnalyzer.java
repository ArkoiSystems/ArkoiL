package com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer;

import com.arkoisystems.arkoicompiler.ArkoiClass;
import com.arkoisystems.arkoicompiler.stage.AbstractStage;
import com.arkoisystems.arkoicompiler.stage.errorHandler.ErrorHandler;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.stage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

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
@Getter
public class LexicalAnalyzer extends AbstractStage
{
    
    
    private final ArkoiClass arkoiClass;
    
    @Expose
    private final LexicalErrorHandler errorHandler;
    
    @Expose
    private AbstractToken[] tokens;
    
    public LexicalAnalyzer(final ArkoiClass arkoiClass) {
        this.arkoiClass = arkoiClass;
    
        this.errorHandler = new LexicalErrorHandler();
    }
    
    @SneakyThrows
    @Override
    public boolean processStage() {
        final List<AbstractToken> tokens = new ArrayList<>();
        final Matcher matcher = TokenType.PATTERN_ENGINE.matcher(this.arkoiClass.getContent());
        while (matcher.find()) {
            final AbstractToken abstractToken = TokenType.createToken(matcher);
            if (abstractToken == null) {
                // TODO: Make it better ty
                return false;
            } else tokens.add(abstractToken);
        }
        tokens.add(new EndOfFileToken(this.arkoiClass.getContent(), 0, this.arkoiClass.getContent().length()));
    
        this.tokens = tokens.stream()
                .filter(abstractToken -> abstractToken.getTokenType() != TokenType.WHITESPACE && abstractToken.getTokenType() != TokenType.COMMENT)
                .toArray(AbstractToken[]::new);
        return true;
    }
    
    @Override
    public ErrorHandler errorHandler() {
        return this.errorHandler;
    }
    
}
