package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SeparatorToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.expression.AbstractExpressionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.CollectionOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.FunctionResultOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.NumberOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.StringOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.OperableParser;
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
public class AbstractOperableAST<OT1> extends AbstractAST
{
    
    public static OperableParser OPERABLE_PARSER = new OperableParser();
    
    @Expose
    private final OT1 abstractToken;
    
    public AbstractOperableAST(final OT1 abstractToken) {
        super(null);
        
        this.abstractToken = abstractToken;
    }
    
    @Override
    public AbstractOperableAST<?> parseAST(final AbstractAST parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        switch (currentToken.getTokenType()) {
            case STRING:
                return parentAST.addAST(new StringOperableAST((StringToken) currentToken), syntaxAnalyzer);
            case NUMBER:
                return parentAST.addAST(new NumberOperableAST((AbstractNumberToken) currentToken), syntaxAnalyzer);
            case SEPARATOR:
                if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.OPENING_BRACKET) != null) {
                    syntaxAnalyzer.nextToken();
                    
                    final List<AbstractExpressionAST> expressions = new ArrayList<>();
                    while (syntaxAnalyzer.getPosition() < syntaxAnalyzer.getTokens().length) {
                        if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_BRACKET) != null)
                            break;
                        
                        if (!AbstractExpressionAST.EXPRESSION_PARSER.canParse(this, syntaxAnalyzer)) {
                            syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractExpressionAST.EXPRESSION_PARSER, syntaxAnalyzer.currentToken(), "Couldn't parse the Collection because there is an invalid expression inside."));
                            return null;
                        }
                        
                        final AbstractExpressionAST abstractExpressionAST = AbstractExpressionAST.EXPRESSION_PARSER.parse(this, syntaxAnalyzer);
                        if(abstractExpressionAST == null) {
                            syntaxAnalyzer.errorHandler().addError(new ASTError(this, "Couldn't parse the Collection because there occurred an error while parsing the expression inside it."));
                            return null;
                        }
    
                        expressions.add(abstractExpressionAST);
                    }
                    
                    if (syntaxAnalyzer.matchesCurrentToken(SeparatorToken.SeparatorType.CLOSING_BRACKET) == null) {
                        syntaxAnalyzer.errorHandler().addError(new TokenError(syntaxAnalyzer.currentToken(), "Couldn't parse the Collection because it doesn't end with an closing bracket."));
                        return null;
                    }
                    return new CollectionOperableAST((SeparatorToken) currentToken, expressions.toArray(new AbstractExpressionAST[] { }), (SeparatorToken) syntaxAnalyzer.currentToken());
                } else {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(currentToken, "Couldn't parse the operable because the SeparatorType isn't supported."));
                    return null;
                }
            case IDENTIFIER:
                if (!AbstractStatementAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractStatementAST.STATEMENT_PARSER, parentAST.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the operable statement because it isn't parsable."));
                    return null;
                }
                
                final AbstractStatementAST abstractStatementAST = AbstractStatementAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
                if (abstractStatementAST instanceof FunctionInvokeAST)
                    return new FunctionResultOperableAST((FunctionInvokeAST) abstractStatementAST);
                else {
                    syntaxAnalyzer.errorHandler().addError(new ASTError(abstractStatementAST, "Couldn't parse the operable because it isn't a supported statement."));
                    return null;
                }
        }
        return null;
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
