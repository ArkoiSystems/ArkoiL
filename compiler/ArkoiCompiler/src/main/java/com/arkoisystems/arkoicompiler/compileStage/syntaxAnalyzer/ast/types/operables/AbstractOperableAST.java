package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operables;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.StringToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.numbers.AbstractNumberToken;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operables.types.NumberOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operables.types.StringOperableAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionDefinitionAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.OperableParser;
import com.arkoisystems.arkoicompiler.utils.Variables;
import com.google.gson.annotations.Expose;
import lombok.Getter;

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
public class AbstractOperableAST<OT1 extends AbstractToken> extends AbstractAST
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
        final AbstractToken abstractToken = syntaxAnalyzer.currentToken();
        switch (abstractToken.getTokenType()) {
            case STRING:
                return parentAST.addAST(new StringOperableAST((StringToken) abstractToken), syntaxAnalyzer);
            case NUMBER:
                return parentAST.addAST(new NumberOperableAST((AbstractNumberToken) abstractToken), syntaxAnalyzer);
            case IDENTIFIER:
                if(!AbstractStatementAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractStatementAST.STATEMENT_PARSER, parentAST.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the operable statement because it isn't parsable."));
                    return null;
                }
                
                final AbstractStatementAST abstractStatementAST = AbstractStatementAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
                System.out.println(abstractStatementAST);
                // TODO: 1/5/2020 Test
                break;
        }
        return null;
    }
    
    @Override
    public <T extends AbstractAST> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
