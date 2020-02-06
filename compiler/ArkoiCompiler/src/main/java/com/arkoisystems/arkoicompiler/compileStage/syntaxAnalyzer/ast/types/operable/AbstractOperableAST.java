package com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable;

import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ASTError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.ParserError;
import com.arkoisystems.arkoicompiler.compileStage.errorHandler.types.TokenError;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.AbstractToken;
import com.arkoisystems.arkoicompiler.compileStage.lexcialAnalyzer.token.types.SymbolToken;
import com.arkoisystems.arkoicompiler.compileStage.semanticAnalyzer.semantic.AbstractSemantic;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.SyntaxAnalyzer;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.AbstractAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.operable.types.*;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.AbstractStatementAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierCallAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.IdentifierInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.ast.types.statement.types.functionStatements.FunctionInvokeAST;
import com.arkoisystems.arkoicompiler.compileStage.syntaxAnalyzer.parser.types.OperableParser;
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
@Getter
@Setter
public class AbstractOperableAST<OT1, S extends AbstractSemantic<?>> extends AbstractAST<S>
{
    
    public static OperableParser OPERABLE_PARSER = new OperableParser();
    
    @Expose
    private OT1 abstractToken;
    
    public AbstractOperableAST() {
        super(null);
    }
    
    @Override
    public AbstractOperableAST<?, ?> parseAST(final AbstractAST<?> parentAST, final SyntaxAnalyzer syntaxAnalyzer) {
        final AbstractToken currentToken = syntaxAnalyzer.currentToken();
        switch (currentToken.getTokenType()) {
            case STRING_LITERAL:
                return new StringOperableAST().parseAST(parentAST, syntaxAnalyzer);
            case NUMBER_LITERAL:
                return new NumberOperableAST().parseAST(parentAST, syntaxAnalyzer);
            case SYMBOL:
                if (syntaxAnalyzer.matchesCurrentToken(SymbolToken.SymbolType.OPENING_BRACKET) != null)
                    return new CollectionOperableAST().parseAST(parentAST, syntaxAnalyzer);
                else {
                    syntaxAnalyzer.errorHandler().addError(new TokenError(currentToken, "Couldn't parse the operable because the SymbolType isn't supported."));
                    return null;
                }
            case IDENTIFIER:
                if (!AbstractStatementAST.STATEMENT_PARSER.canParse(parentAST, syntaxAnalyzer)) {
                    syntaxAnalyzer.errorHandler().addError(new ParserError(AbstractStatementAST.STATEMENT_PARSER, parentAST.getStart(), syntaxAnalyzer.currentToken().getEnd(), "Couldn't parse the operable statement because it isn't parsable."));
                    return null;
                }
    
                final AbstractStatementAST<?> abstractStatementAST = AbstractStatementAST.STATEMENT_PARSER.parse(parentAST, syntaxAnalyzer);
                if (abstractStatementAST instanceof FunctionInvokeAST)
                    return new FunctionResultOperableAST((FunctionInvokeAST) abstractStatementAST).parseAST(parentAST, syntaxAnalyzer);
                else if(abstractStatementAST instanceof IdentifierCallAST)
                    return new IdentifierCallOperableAST((IdentifierCallAST) abstractStatementAST).parseAST(parentAST, syntaxAnalyzer);
                else if(abstractStatementAST instanceof IdentifierInvokeAST)
                    return new IdentifierInvokeOperableAST((IdentifierInvokeAST) abstractStatementAST).parseAST(parentAST, syntaxAnalyzer);
                else if (abstractStatementAST != null) {
                    syntaxAnalyzer.errorHandler().addError(new ASTError(abstractStatementAST, "Couldn't parse the operable because it isn't a supported statement."));
                    return null;
                }
        }
        return null;
    }
    
    @Override
    public <T extends AbstractAST<?>> T addAST(final T toAddAST, final SyntaxAnalyzer syntaxAnalyzer) {
        return toAddAST;
    }
    
}
