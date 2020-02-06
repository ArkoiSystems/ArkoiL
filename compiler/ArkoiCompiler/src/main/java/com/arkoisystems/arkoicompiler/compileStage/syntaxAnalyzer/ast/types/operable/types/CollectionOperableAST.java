package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.types.CollectionOperableSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.AbstractOperableAST;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © 2019 ArkoiSystems (https://www.arkoisystems.com/) All Rights Reserved.
 * Created ArkoiCompiler on the Sat Nov 09 2019 Author єхcsє#5543 aka timo
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
public class CollectionOperableAST extends AbstractOperableAST<AbstractExpressionAST<?>[], CollectionOperableSemantic>
{
    
    @Expose
    private final List<AbstractExpressionAST<?>> expressionASTs;
    
    public CollectionOperableAST() {
        this.setAstType(ASTType.COLLECTION_OPERABLE);
        
        this.expressionASTs = new ArrayList<>();
    }
    
    @Override
    public AbstractOperableAST<?, ?> parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the collection operable because the parsing doesn't start with an opening bracket."));
            return null;
        } else syntaxAnalyzer.nextToken();
        
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) != null)
                break;
            
            if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the collection operable because there is an invalid expression inside."));
                return null;
            }
            
            final AbstractExpressionAST<?> abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
            if (abstractExpressionAST == null) {
                syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the collection operable because there occurred an error while parsing the expression inside it."));
                return null;
            } else this.expressionASTs.add(abstractExpressionAST);
            
            if (syntaxAnalyzer.matchesNextToken(SymbolToken.SymbolType.COMMA) != null)
                syntaxAnalyzer.nextToken();
        }
        
        if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.CLOSING_BRACKET) == null) {
            syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the collection operable because it doesn't end with an closing bracket."));
            return null;
        }
        return parentAST.addAST(this, syntaxAnalyzer);
    }
    
    @Override
    public Class<CollectionOperableSemantic> semanticClass() {
        return CollectionOperableSemantic.class;
    }
    
}

