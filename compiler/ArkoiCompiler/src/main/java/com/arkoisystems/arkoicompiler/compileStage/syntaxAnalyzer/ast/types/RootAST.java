package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.TokenType;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.EndOfFileToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.ASTType;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.IInitializeable;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.variableStatements.VariableDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.Parser;
import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
public class RootAST extends AbstractAST implements IInitializeable
{
    
    private static Parser<?>[] ROOT_PARSERS = new Parser<?>[] {
            AnnotationAST.ANNOTATION_PARSER,
            AbstractStatementAST.STATEMENT_PARSER,
    };
    
    
    @Expose
    private final List<VariableDefinitionAST> variableStorage;
    
    @Expose
    private final List<FunctionDefinitionAST> functionStorage;
    
    
    public RootAST(final SyntaxAnalyzer syntaxAnalyzer) {
        super(ASTType.ROOT);
        
        this.setEnd(syntaxAnalyzer.getArkoiClass().getContent().length());
        this.setStart(0);
        
        this.variableStorage = new ArrayList<>();
        this.functionStorage = new ArrayList<>();
    }
    
    @Override
    public RootAST parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
            final AbstractToken startToken = syntaxAnalyzer.currentToken();
            if(startToken instanceof EndOfFileToken)
                break;
            
            Parser<?> usedParser = null;
            for (final Parser<?> parser : ROOT_PARSERS) {
                if (!parser.canParse(parentAST, syntaxAnalyzer))
                    continue;
                
                final AbstractAST abstractAST = parser.parse(this, syntaxAnalyzer);
                if (abstractAST == null) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError(parser, startToken.getStart(), syntaxAnalyzer.currentToken().getEnd()));
                    return null;
                } else {
                    usedParser = parser;
                    break;
                }
            }
            
            if (usedParser == null) {
                final AbstractToken parserEndToken = syntaxAnalyzer.currentToken();
                if (parserEndToken.getTokenType() != TokenType.SEPARATOR) {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(parserEndToken, "Couldn't parse the statement because there is no ending separator for the parser (for statements \";\" and for a block \"}\")."));
                    return null;
                }
                
                final SeparatorToken separatorToken = (SeparatorToken) parserEndToken;
                if (separatorToken.getSeparatorType() != SeparatorToken.SeparatorType.SEMICOLON && separatorToken.getSeparatorType() != SeparatorToken.SeparatorType.CLOSING_BRACE) {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(parserEndToken, "Couldn't parse the statement because there is no ending separator for the parser (for statements \";\" and for a block \"}\")."));
                    return null;
                } else syntaxAnalyzer.nextToken();
            } else syntaxAnalyzer.nextToken();
        }
        
        return this;
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        if (toAddAST instanceof VariableDefinitionAST) {
            final VariableDefinitionAST variableDefinitionAST = (VariableDefinitionAST) toAddAST;
            // TODO: 1/6/2020 Make name check etc
            this.variableStorage.add(variableDefinitionAST);
        } else if (toAddAST instanceof FunctionDefinitionAST) {
            final FunctionDefinitionAST functionDefinitionAST = (FunctionDefinitionAST) toAddAST;
            // TODO: 1/6/2020 Make name check etc
            this.functionStorage.add(functionDefinitionAST);
        }
        return toAddAST;
    }
    
    @Override
    public boolean initialize(final SyntaxAnalyzer syntaxAnalyzer) {
        for (final VariableDefinitionAST variableDefinitionAST : this.variableStorage)
            if (!variableDefinitionAST.initialize(syntaxAnalyzer))
                return false;
        for (final FunctionDefinitionAST functionDefinitionAST : this.functionStorage)
            if (!functionDefinitionAST.initialize(syntaxAnalyzer))
                return false;
        return true;
    }
    
    public VariableDefinitionAST getVariableByName(final AbstractToken identifierToken) {
        for (final VariableDefinitionAST variableDefinitionAST : this.variableStorage)
            if (variableDefinitionAST.getNameIdentifierToken().getTokenContent().equals(identifierToken.getTokenContent()))
                return variableDefinitionAST;
        return null;
    }
    
    public FunctionDefinitionAST getFunctionByName(final AbstractToken identifierToken) {
        for (final FunctionDefinitionAST functionDefinitionAST : this.functionStorage)
            if (functionDefinitionAST.getFunctionName().getTokenContent().equals(identifierToken.getTokenContent()))
                return functionDefinitionAST;
        return null;
    }
    
}
